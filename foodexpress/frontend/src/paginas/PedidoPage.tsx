import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { notificacionesApi, pedidosApi } from '../api/cliente';
import { useTemaEnVivo } from '../api/tiempoReal';
import type { Notificacion, NotificacionEnVivo, Pedido } from '../api/tipos';
import { Cargando, Encabezado, EtiquetaEstado, IndicadorEnVivo, MensajeError } from '../componentes/Comunes';
import DetallePedido from '../componentes/DetallePedido';
import { clientePuedeCancelar, flujoEstados, horaCorta, nombreEstado } from '../util/formato';

const nombreCanal: Record<Notificacion['canal'], string> = { EMAIL: 'Correo', WEBSOCKET: 'En vivo', PUSH: 'Push' };

interface Aviso {
  clave: string;
  titulo: string;
  mensaje: string;
  fecha: string;
  canales: Notificacion['canal'][];
  fallidos: Notificacion['canal'][];
}

/**
 * Una misma notificación se envía por varios canales (patrón Adapter en el backend):
 * aquí se muestra una sola vez, con los canales por los que salió. Los avisos que
 * llegaron por WebSocket y aún no aparecen en el historial se agregan al inicio.
 */
function agruparAvisos(historial: Notificacion[], enVivo: NotificacionEnVivo[]): Aviso[] {
  const grupos = new Map<string, Aviso>();
  for (const n of historial.filter((h) => h.tipoDestinatario === 'CLIENTE')) {
    const clave = `${n.asunto}|${n.cuerpo}`;
    const grupo = grupos.get(clave)
      ?? { clave, titulo: n.asunto, mensaje: n.cuerpo, fecha: n.fecha, canales: [], fallidos: [] };
    (n.estado === 'ENVIADA' ? grupo.canales : grupo.fallidos).push(n.canal);
    grupos.set(clave, grupo);
  }
  for (const n of enVivo) {
    const clave = `${n.titulo}|${n.mensaje}`;
    if (!grupos.has(clave)) {
      grupos.set(clave, { clave, titulo: n.titulo, mensaje: n.mensaje, fecha: n.fecha, canales: ['WEBSOCKET'], fallidos: [] });
    }
  }
  return [...grupos.values()].sort((a, b) => b.fecha.localeCompare(a.fecha));
}

/** Seguimiento de un pedido por parte del cliente, actualizado en tiempo real. */
export default function PedidoPage() {
  const pedidoId = Number(useParams().id);
  const [pedido, setPedido] = useState<Pedido | null>(null);
  const [historial, setHistorial] = useState<Notificacion[]>([]);
  const [enVivo, setEnVivo] = useState<NotificacionEnVivo[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [errorAccion, setErrorAccion] = useState<string | null>(null);

  const cargar = useCallback(() => {
    pedidosApi.obtener(pedidoId).then(setPedido).catch((e) => setError(e.message));
    notificacionesApi.porPedido(pedidoId).then(setHistorial).catch(() => setHistorial([]));
  }, [pedidoId]);
  useEffect(cargar, [cargar]);

  const conectado = useTemaEnVivo(`/topic/pedidos/${pedidoId}`, (n) => {
    setEnVivo((actuales) => [n, ...actuales]);
    cargar();
  });

  if (error) return <MensajeError mensaje={error} />;
  if (!pedido) return <Cargando texto="Cargando pedido…" />;

  const cancelar = async () => {
    const motivo = window.prompt('¿Por qué cancelas el pedido? (opcional)') ?? undefined;
    setErrorAccion(null);
    try {
      setPedido(await pedidosApi.cancelar(pedido.id, 'CLIENTE', motivo));
    } catch (e) {
      setErrorAccion((e as Error).message);
    }
  };

  const avisos = agruparAvisos(historial, enVivo);
  const titulosEnVivo = new Set(enVivo.map((n) => n.titulo));
  const pasoActual = flujoEstados.indexOf(pedido.estado);
  const cancelado = pedido.estado === 'CANCELADO';

  return (
    <>
      <Link to="/mis-pedidos" className="volver">← Mis pedidos</Link>
      <Encabezado
        titulo={`Pedido #${pedido.id}`}
        subtitulo={<>{pedido.restauranteNombre} · {pedido.tipo === 'DELIVERY' ? 'Domicilio' : 'Para recoger'} · {horaCorta(pedido.fechaCreacion)}</>}
        acciones={<IndicadorEnVivo conectado={conectado} />}
      />

      {cancelado ? (
        <div className="aviso aviso--error">
          Pedido cancelado por {pedido.canceladoPor === 'CLIENTE' ? 'ti' : 'el restaurante'}
          {pedido.motivoCancelacion && <>: “{pedido.motivoCancelacion}”</>}.
        </div>
      ) : (
        <ol className="linea-tiempo" aria-label="Estado del pedido">
          {flujoEstados.map((estado, i) => (
            <li key={estado}
                className={i < pasoActual ? 'hecho' : i === pasoActual ? 'actual' : ''}
                aria-current={i === pasoActual ? 'step' : undefined}>
              <span className="linea-tiempo__punto" aria-hidden="true" />
              {nombreEstado[estado]}
            </li>
          ))}
        </ol>
      )}

      <div className="dos-columnas">
        <section className="tarjeta">
          <div className="fila-titulo">
            <h2>Detalle</h2>
            <EtiquetaEstado estado={pedido.estado} />
          </div>
          <DetallePedido pedido={pedido} />
          {clientePuedeCancelar(pedido.estado) && (
            <button className="boton boton--peligro" onClick={cancelar}>Cancelar pedido</button>
          )}
          {errorAccion && <MensajeError mensaje={errorAccion} />}
        </section>

        <section className="tarjeta">
          <h2>Notificaciones</h2>
          {avisos.length === 0 && <p className="tenue">Aquí verás los avisos del restaurante.</p>}
          <ul className="feed">
            {avisos.map((aviso) => (
              <li key={aviso.clave} className={`feed__item ${titulosEnVivo.has(aviso.titulo) ? 'feed__item--nuevo' : ''}`}>
                <strong>{aviso.titulo}</strong>
                <span>{aviso.mensaje}</span>
                <small>
                  {aviso.canales.map((c) => nombreCanal[c]).join(' · ')} · {horaCorta(aviso.fecha)}
                  {aviso.fallidos.length > 0 && ` · no se pudo enviar por ${aviso.fallidos.map((c) => nombreCanal[c]).join(', ')}`}
                </small>
              </li>
            ))}
          </ul>
        </section>
      </div>
    </>
  );
}
