import { useState } from 'react';
import type { TipoPedido } from '../api/tipos';
import { cambiarCantidad, estimarTotales, subtotalLinea, type LineaCarrito } from '../util/carrito';
import { formatoPesos } from '../util/formato';
import type { DatosCliente } from '../util/useCliente';

export interface DatosConfirmacion {
  cliente: DatosCliente;
  tipo: TipoPedido;
  direccion: string;
}

interface Props {
  lineas: LineaCarrito[];
  alCambiarLineas: (lineas: LineaCarrito[]) => void;
  cliente: DatosCliente;
  enviando: boolean;
  alConfirmar: (datos: DatosConfirmacion) => void;
}

export default function Carrito({ lineas, alCambiarLineas, cliente, enviando, alConfirmar }: Props) {
  const [tipo, setTipo] = useState<TipoPedido>('LOCAL');
  const [direccion, setDireccion] = useState('');
  const [nombre, setNombre] = useState(cliente.nombre);
  const [email, setEmail] = useState(cliente.email);

  const totales = estimarTotales(lineas, tipo);
  const vacio = lineas.length === 0;

  return (
    <aside className="tarjeta carrito" aria-label="Tu pedido">
      <h2>Tu pedido</h2>

      {vacio ? (
        <p className="tenue">Agrega platos del menú.</p>
      ) : (
        <ul className="carrito__lineas">
          {lineas.map((linea) => (
            <li key={linea.clave}>
              <div className="carrito__linea">
                <strong>{linea.plato.nombre}</strong>
                <span>{formatoPesos(subtotalLinea(linea))}</span>
              </div>
              {linea.extras.length > 0 && (
                <p className="tenue pequeno">+ {linea.extras.map((e) => e.nombre).join(', ')}</p>
              )}
              {linea.notas.trim() && <p className="tenue pequeno">“{linea.notas.trim()}”</p>}
              <div className="contador contador--pequeno">
                <button type="button" aria-label="Quitar uno"
                        onClick={() => alCambiarLineas(cambiarCantidad(lineas, linea.clave, linea.cantidad - 1))}>−</button>
                <span>{linea.cantidad}</span>
                <button type="button" aria-label="Agregar uno"
                        onClick={() => alCambiarLineas(cambiarCantidad(lineas, linea.clave, linea.cantidad + 1))}>+</button>
              </div>
            </li>
          ))}
        </ul>
      )}

      <form
        className="carrito__formulario"
        onSubmit={(e) => {
          e.preventDefault();
          alConfirmar({ cliente: { nombre: nombre.trim(), email: email.trim() }, tipo, direccion });
        }}
      >
        <div className="selector" role="radiogroup" aria-label="Entrega">
          <button type="button" role="radio" aria-checked={tipo === 'LOCAL'}
                  className={tipo === 'LOCAL' ? 'activo' : ''} onClick={() => setTipo('LOCAL')}>Recoger</button>
          <button type="button" role="radio" aria-checked={tipo === 'DELIVERY'}
                  className={tipo === 'DELIVERY' ? 'activo' : ''} onClick={() => setTipo('DELIVERY')}>Domicilio</button>
        </div>

        {tipo === 'DELIVERY' && (
          <label className="campo">
            <span>Dirección de entrega</span>
            <input required value={direccion} maxLength={255} onChange={(e) => setDireccion(e.target.value)}
                   placeholder="Calle 10 # 5-20, apto 301" />
          </label>
        )}
        <label className="campo">
          <span>Nombre</span>
          <input required value={nombre} onChange={(e) => setNombre(e.target.value)} />
        </label>
        <label className="campo">
          <span>Correo</span>
          <input required type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </label>

        <dl className="totales">
          <dt>Subtotal</dt><dd>{formatoPesos(totales.subtotal)}</dd>
          <dt>Impuesto (8 %)</dt><dd>{formatoPesos(totales.impuestos)}</dd>
          {tipo === 'DELIVERY' && (<><dt>Domicilio</dt><dd>{formatoPesos(totales.envio)}</dd></>)}
          <dt className="totales__total">Total estimado</dt><dd className="totales__total">{formatoPesos(totales.total)}</dd>
        </dl>

        <button type="submit" className="boton boton--ancho" disabled={vacio || enviando}>
          {enviando ? 'Enviando…' : 'Hacer pedido'}
        </button>
        <p className="tenue pequeno">El total final lo confirma el restaurante con los precios vigentes del menú.</p>
      </form>
    </aside>
  );
}
