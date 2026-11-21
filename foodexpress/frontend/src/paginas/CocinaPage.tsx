import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { pedidosApi, restaurantesApi } from '../api/cliente';
import { useTemaEnVivo } from '../api/tiempoReal';
import type { EstadoPedido, NotificacionEnVivo, Pedido, Restaurante } from '../api/tipos';
import { Cargando, Encabezado, EtiquetaEstado, IndicadorEnVivo, MensajeError } from '../componentes/Comunes';
import DetallePedido from '../componentes/DetallePedido';
import MenuCocina from '../componentes/MenuCocina';
import { accionAvanzar, formatoPesos, horaCorta, nombreEstado, restaurantePuedeCancelar } from '../util/formato';

const COLUMNAS: EstadoPedido[] = ['RECIBIDO', 'EN_PREPARACION', 'LISTO'];

/** Panel del restaurante: elige restaurante o muestra su cocina. */
export default function CocinaPage() {
  const { restauranteId } = useParams();
  return restauranteId ? <PanelCocina restauranteId={Number(restauranteId)} /> : <ElegirRestaurante />;
}

function ElegirRestaurante() {
  const [restaurantes, setRestaurantes] = useState<Restaurante[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    restaurantesApi.listar().then(setRestaurantes).catch((e) => setError(e.message));
  }, []);

  return (
    <>
      <Encabezado titulo="Cocina" subtitulo="Elige el restaurante que vas a operar." />
      {error && <MensajeError mensaje={error} />}
      {!restaurantes && !error && <Cargando />}
      <div className="rejilla">
        {restaurantes?.map((r) => (
          <Link key={r.id} to={`/cocina/${r.id}`} className="tarjeta tarjeta-restaurante">
            <div className="tarjeta-restaurante__inicial" aria-hidden="true">{r.nombre.charAt(0)}</div>
            <div className="tarjeta-restaurante__datos"><h2>{r.nombre}</h2><p>{r.direccion}</p></div>
          </Link>
        ))}
      </div>
    </>
  );
}

function PanelCocina({ restauranteId }: { restauranteId: number }) {
  const [restaurante, setRestaurante] = useState<Restaurante | null>(null);
  const [pedidos, setPedidos] = useState<Pedido[] | null>(null);
  const [avisos, setAvisos] = useState<NotificacionEnVivo[]>([]);
  const [pestana, setPestana] = useState<'pedidos' | 'menu'>('pedidos');
  const [error, setError] = useState<string | null>(null);
  const [ocupado, setOcupado] = useState<number | null>(null);

  const cargar = useCallback(() => {
    setError(null);
    Promise.all([restaurantesApi.obtener(restauranteId), pedidosApi.porRestaurante(restauranteId)])
      .then(([r, p]) => {
        setRestaurante(r);
        setPedidos(p);
      })
      .catch((e) => setError(e.message));
  }, [restauranteId]);
  useEffect(cargar, [cargar]);

  // Pedido nuevo o cancelado por el cliente: llega por WebSocket y se refresca el tablero.
  const conectado = useTemaEnVivo(`/topic/restaurantes/${restauranteId}`, (aviso) => {
    setAvisos((actuales) => [aviso, ...actuales].slice(0, 5));
    cargar();
  });

  const ejecutar = async (pedidoId: number, accion: () => Promise<Pedido>) => {
    setOcupado(pedidoId);
    setError(null);
    try {
      const actualizado = await accion();
      setPedidos((actuales) => actuales?.map((p) => (p.id === actualizado.id ? actualizado : p)) ?? null);
    } catch (e) {
      setError((e as Error).message);
      cargar();
    } finally {
      setOcupado(null);
    }
  };

  const alternarAbierto = async () => {
    if (!restaurante) return;
    try {
      setRestaurante(await restaurantesApi.cambiarEstado(restaurante.id,
        restaurante.estado === 'ABIERTO' ? 'CERRADO' : 'ABIERTO'));
    } catch (e) {
      setError((e as Error).message);
    }
  };

  if (!restaurante || !pedidos) return error ? <MensajeError mensaje={error} alReintentar={cargar} /> : <Cargando />;

  const abierto = restaurante.estado === 'ABIERTO';
  const finalizados = pedidos.filter((p) => p.estado === 'ENTREGADO' || p.estado === 'CANCELADO');

  return (
    <>
      <Link to="/cocina" className="volver">← Restaurantes</Link>
      <Encabezado
        titulo={restaurante.nombre}
        subtitulo="Panel de cocina"
        acciones={
          <>
            <IndicadorEnVivo conectado={conectado} />
            <button className={`boton ${abierto ? 'boton--secundario' : ''}`} onClick={alternarAbierto}>
              {abierto ? 'Cerrar restaurante' : 'Abrir restaurante'}
            </button>
          </>
        }
      />

      {avisos.length > 0 && (
        <ul className="avisos-vivo" aria-live="polite">
          {avisos.map((a, i) => (
            <li key={i}><strong>{a.titulo}</strong> {a.mensaje} <small>{horaCorta(a.fecha)}</small></li>
          ))}
        </ul>
      )}
      {error && <MensajeError mensaje={error} />}

      <div className="pestanas" role="tablist">
        <button role="tab" aria-selected={pestana === 'pedidos'} onClick={() => setPestana('pedidos')}>Pedidos</button>
        <button role="tab" aria-selected={pestana === 'menu'} onClick={() => setPestana('menu')}>Menú y disponibilidad</button>
      </div>

      {pestana === 'menu' ? (
        <MenuCocina restauranteId={restauranteId} />
      ) : (
        <>
          <div className="tablero">
            {COLUMNAS.map((estado) => {
              const columna = pedidos.filter((p) => p.estado === estado);
              return (
                <section key={estado} className="tablero__columna">
                  <h2>{nombreEstado[estado]} <span className="contador-columna">{columna.length}</span></h2>
                  {columna.length === 0 && <p className="tenue pequeno">Sin pedidos.</p>}
                  {[...columna].reverse().map((p) => (
                    <article key={p.id} className="tarjeta comanda">
                      <header className="fila-titulo">
                        <strong>#{p.id} · {p.cliente.nombre}</strong>
                        <small className="tenue">{horaCorta(p.fechaCreacion)}</small>
                      </header>
                      <p className="pequeno tenue">
                        {p.tipo === 'DELIVERY' ? 'Domicilio' : 'Para recoger'} · {formatoPesos(p.total)}
                      </p>
                      <DetallePedido pedido={p} compacto />
                      <div className="comanda__acciones">
                        <button className="boton" disabled={ocupado === p.id}
                                onClick={() => ejecutar(p.id, () => pedidosApi.avanzar(p.id))}>
                          {accionAvanzar[p.estado]}
                        </button>
                        {restaurantePuedeCancelar(p.estado) && (
                          <button className="boton boton--texto" disabled={ocupado === p.id}
                                  onClick={() => {
                                    const motivo = window.prompt('Motivo de la cancelación (opcional)');
                                    if (motivo === null) return;
                                    void ejecutar(p.id, () => pedidosApi.cancelar(p.id, 'RESTAURANTE', motivo));
                                  }}>
                            Cancelar
                          </button>
                        )}
                      </div>
                    </article>
                  ))}
                </section>
              );
            })}
          </div>

          {finalizados.length > 0 && (
            <details className="finalizados">
              <summary>Finalizados ({finalizados.length})</summary>
              <ul className="lista-pedidos">
                {finalizados.map((p) => (
                  <li key={p.id} className="pedido-resumen">
                    <span>#{p.id} · {p.cliente.nombre}</span>
                    <span>{formatoPesos(p.total)}</span>
                    <EtiquetaEstado estado={p.estado} />
                  </li>
                ))}
              </ul>
            </details>
          )}
        </>
      )}
    </>
  );
}
