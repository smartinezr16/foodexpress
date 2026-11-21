import { useEffect, useRef, useState } from 'react';
import type { Extra, Plato } from '../api/tipos';
import { precioUnitario } from '../util/carrito';
import { formatoPesos } from '../util/formato';

interface Props {
  plato: Plato;
  alCerrar: () => void;
  alAgregar: (seleccion: { cantidad: number; extras: Extra[]; notas: string }) => void;
}

/** Ventana para elegir cantidad, extras y notas especiales de un plato. */
export default function PlatoModal({ plato, alCerrar, alAgregar }: Props) {
  const [cantidad, setCantidad] = useState(1);
  const [extras, setExtras] = useState<Extra[]>([]);
  const [notas, setNotas] = useState('');
  const dialogo = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    dialogo.current?.showModal();
  }, []);

  const alternarExtra = (extra: Extra) =>
    setExtras((actuales) =>
      actuales.some((e) => e.id === extra.id) ? actuales.filter((e) => e.id !== extra.id) : [...actuales, extra],
    );

  const total = precioUnitario({ plato, extras }) * cantidad;
  const maximo = plato.unidadesDisponibles ?? 99;

  return (
    <dialog ref={dialogo} className="modal" onClose={alCerrar} onClick={(e) => e.target === dialogo.current && alCerrar()}>
      <form
        method="dialog"
        className="modal__contenido"
        onSubmit={(e) => {
          e.preventDefault();
          alAgregar({ cantidad, extras, notas });
        }}
      >
        <h2>{plato.nombre}</h2>
        {plato.descripcion && <p className="tenue">{plato.descripcion}</p>}
        <p className="precio">{formatoPesos(plato.precio)}</p>

        {plato.extras.length > 0 && (
          <fieldset className="modal__grupo">
            <legend>Extras</legend>
            {plato.extras.map((extra) => (
              <label key={extra.id} className="opcion">
                <input
                  type="checkbox"
                  checked={extras.some((e) => e.id === extra.id)}
                  onChange={() => alternarExtra(extra)}
                />
                <span>{extra.nombre}</span>
                <span className="tenue">+ {formatoPesos(extra.precioAdicional)}</span>
              </label>
            ))}
          </fieldset>
        )}

        <label className="campo">
          <span>Notas para la cocina</span>
          <textarea value={notas} maxLength={500} rows={2} placeholder="Ej.: sin cebolla, término medio"
                    onChange={(e) => setNotas(e.target.value)} />
        </label>

        <div className="modal__pie">
          <div className="contador" aria-label="Cantidad">
            <button type="button" onClick={() => setCantidad((c) => Math.max(1, c - 1))} aria-label="Quitar uno">−</button>
            <span>{cantidad}</span>
            <button type="button" onClick={() => setCantidad((c) => Math.min(maximo, c + 1))} aria-label="Agregar uno">+</button>
          </div>
          <button type="button" className="boton boton--texto" onClick={alCerrar}>Cancelar</button>
          <button type="submit" className="boton">Agregar · {formatoPesos(total)}</button>
        </div>
      </form>
    </dialog>
  );
}
