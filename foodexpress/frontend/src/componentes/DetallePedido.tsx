import type { Pedido } from '../api/tipos';
import { formatoPesos } from '../util/formato';

/** Ítems y totales de un pedido, tal como los calculó el servicio de Pedidos. */
export default function DetallePedido({ pedido, compacto = false }: { pedido: Pedido; compacto?: boolean }) {
  return (
    <>
      <ul className="items">
        {pedido.items.map((item, i) => (
          <li key={i}>
            <div className="carrito__linea">
              <span><strong>{item.cantidad} ×</strong> {item.nombrePlato}</span>
              {!compacto && <span>{formatoPesos(item.subtotal)}</span>}
            </div>
            {item.extras.length > 0 && <p className="tenue pequeno">+ {item.extras.map((e) => e.nombre).join(', ')}</p>}
            {item.notasEspeciales && <p className="nota">“{item.notasEspeciales}”</p>}
          </li>
        ))}
      </ul>
      {pedido.direccionEntrega && <p className="pequeno"><strong>Entregar en:</strong> {pedido.direccionEntrega}</p>}
      {!compacto && (
        <dl className="totales">
          <dt>Subtotal</dt><dd>{formatoPesos(pedido.subtotal)}</dd>
          <dt>Impuesto</dt><dd>{formatoPesos(pedido.impuestos)}</dd>
          {pedido.tipo === 'DELIVERY' && (<><dt>Domicilio</dt><dd>{formatoPesos(pedido.costoEnvio)}</dd></>)}
          <dt className="totales__total">Total</dt><dd className="totales__total">{formatoPesos(pedido.total)}</dd>
        </dl>
      )}
    </>
  );
}
