import { useCallback, useEffect, useState } from 'react';
import { restaurantesApi } from '../api/cliente';
import type { Plato } from '../api/tipos';
import { formatoPesos, nombreCategoria } from '../util/formato';
import { Cargando, MensajeError } from './Comunes';

/** El restaurante retira platos de la carta o ajusta sus unidades disponibles. */
export default function MenuCocina({ restauranteId }: { restauranteId: number }) {
  const [platos, setPlatos] = useState<Plato[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const cargar = useCallback(() => {
    restaurantesApi.menu(restauranteId).then(setPlatos).catch((e) => setError(e.message));
  }, [restauranteId]);
  useEffect(cargar, [cargar]);

  const actualizar = async (plato: Plato, habilitado: boolean, unidades: number | null) => {
    setError(null);
    try {
      const actualizado = await restaurantesApi.cambiarDisponibilidad(plato.id, habilitado, unidades);
      setPlatos((actuales) => actuales?.map((p) => (p.id === actualizado.id ? actualizado : p)) ?? null);
    } catch (e) {
      setError((e as Error).message);
    }
  };

  if (!platos) return error ? <MensajeError mensaje={error} /> : <Cargando />;

  return (
    <>
      {error && <MensajeError mensaje={error} />}
      <table className="tabla">
        <thead>
          <tr>
            <th>Plato</th>
            <th>Categoría</th>
            <th className="derecha">Precio</th>
            <th>Unidades</th>
            <th>En la carta</th>
          </tr>
        </thead>
        <tbody>
          {platos.map((plato) => (
            <tr key={plato.id} className={plato.disponible ? '' : 'tabla__inactiva'}>
              <td>
                <strong>{plato.nombre}</strong>
                {!plato.disponible && <span className="etiqueta etiqueta--cerrado">No disponible</span>}
              </td>
              <td>{nombreCategoria[plato.categoria]}</td>
              <td className="derecha">{formatoPesos(plato.precio)}</td>
              <td>
                <input
                  type="number"
                  min={0}
                  className="entrada-corta"
                  aria-label={`Unidades de ${plato.nombre}`}
                  placeholder="Sin límite"
                  defaultValue={plato.unidadesDisponibles ?? ''}
                  key={`${plato.id}-${plato.unidadesDisponibles}`}
                  onBlur={(e) => {
                    const texto = e.target.value.trim();
                    const unidades = texto === '' ? null : Math.max(0, Number(texto));
                    if (unidades !== plato.unidadesDisponibles) void actualizar(plato, plato.habilitado, unidades);
                  }}
                />
              </td>
              <td>
                <label className="interruptor">
                  <input type="checkbox" checked={plato.habilitado}
                         onChange={(e) => actualizar(plato, e.target.checked, plato.unidadesDisponibles)} />
                  <span aria-hidden="true" />
                  <span className="sr">{plato.habilitado ? 'Habilitado' : 'Retirado'}</span>
                </label>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
