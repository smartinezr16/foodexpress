import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { restaurantesApi } from '../api/cliente';
import type { Restaurante } from '../api/tipos';
import { Cargando, Encabezado, MensajeError } from '../componentes/Comunes';

export default function RestaurantesPage() {
  const [restaurantes, setRestaurantes] = useState<Restaurante[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const cargar = () => {
    setError(null);
    restaurantesApi.listar().then(setRestaurantes).catch((e) => setError(e.message));
  };
  useEffect(cargar, []);

  return (
    <>
      <Encabezado titulo="¿Qué quieres comer hoy?" subtitulo="Elige un restaurante para ver su menú." />
      {error && <MensajeError mensaje={error} alReintentar={cargar} />}
      {!restaurantes && !error && <Cargando />}
      {restaurantes?.length === 0 && <p className="vacio">Todavía no hay restaurantes registrados.</p>}

      <div className="rejilla">
        {restaurantes?.map((r) => {
          const abierto = r.estado === 'ABIERTO';
          const contenido = (
            <>
              <div className="tarjeta-restaurante__inicial" aria-hidden="true">{r.nombre.charAt(0)}</div>
              <div className="tarjeta-restaurante__datos">
                <h2>{r.nombre}</h2>
                <p>{r.direccion}</p>
                <p className="tenue">Horario {r.horaApertura.slice(0, 5)} a {r.horaCierre.slice(0, 5)}</p>
              </div>
              <span className={`etiqueta ${abierto ? 'etiqueta--abierto' : 'etiqueta--cerrado'}`}>
                {abierto ? 'Abierto' : 'Cerrado'}
              </span>
            </>
          );
          return abierto ? (
            <Link key={r.id} to={`/restaurantes/${r.id}`} className="tarjeta tarjeta-restaurante">{contenido}</Link>
          ) : (
            <div key={r.id} className="tarjeta tarjeta-restaurante tarjeta-restaurante--cerrado">{contenido}</div>
          );
        })}
      </div>
    </>
  );
}
