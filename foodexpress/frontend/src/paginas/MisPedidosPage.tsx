import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { pedidosApi } from '../api/cliente';
import type { Pedido } from '../api/tipos';
import { Cargando, Encabezado, EtiquetaEstado, MensajeError } from '../componentes/Comunes';
import { formatoPesos, horaCorta } from '../util/formato';
import { useCliente } from '../util/useCliente';

export default function MisPedidosPage() {
  const [cliente, guardarCliente] = useCliente();
  const [correo, setCorreo] = useState(cliente.email);
  const [pedidos, setPedidos] = useState<Pedido[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const buscar = useCallback((email: string) => {
    if (!email) return;
    setError(null);
    setPedidos(null);
    pedidosApi.porCliente(email).then(setPedidos).catch((e) => setError(e.message));
  }, []);
  useEffect(() => buscar(cliente.email), [buscar, cliente.email]);

  return (
    <>
      <Encabezado titulo="Mis pedidos" subtitulo="Tus pedidos se identifican con tu correo." />
      <form
        className="buscador"
        onSubmit={(e) => {
          e.preventDefault();
          guardarCliente({ ...cliente, email: correo.trim() });
        }}
      >
        <input type="email" required value={correo} placeholder="tu@correo.com" aria-label="Correo"
               onChange={(e) => setCorreo(e.target.value)} />
        <button className="boton">Buscar</button>
      </form>

      {error && <MensajeError mensaje={error} alReintentar={() => buscar(cliente.email)} />}
      {cliente.email && !pedidos && !error && <Cargando />}
      {pedidos?.length === 0 && <p className="vacio">No hay pedidos con este correo.</p>}

      <ul className="lista-pedidos">
        {pedidos?.map((p) => (
          <li key={p.id}>
            <Link to={`/pedidos/${p.id}`} className="tarjeta pedido-resumen">
              <div>
                <strong>#{p.id} · {p.restauranteNombre}</strong>
                <p className="tenue pequeno">
                  {new Date(p.fechaCreacion).toLocaleDateString('es-CO')} {horaCorta(p.fechaCreacion)} ·{' '}
                  {p.items.reduce((n, i) => n + i.cantidad, 0)} platos
                </p>
              </div>
              <span>{formatoPesos(p.total)}</span>
              <EtiquetaEstado estado={p.estado} />
            </Link>
          </li>
        ))}
      </ul>
    </>
  );
}
