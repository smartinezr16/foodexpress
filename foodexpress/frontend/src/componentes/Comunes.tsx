import type { ReactNode } from 'react';
import type { EstadoPedido } from '../api/tipos';
import { nombreEstado } from '../util/formato';

export function Cargando({ texto = 'Cargando…' }: { texto?: string }) {
  return <p className="cargando" role="status">{texto}</p>;
}

export function MensajeError({ mensaje, alReintentar }: { mensaje: string; alReintentar?: () => void }) {
  return (
    <div className="aviso aviso--error" role="alert">
      <span>{mensaje}</span>
      {alReintentar && <button className="boton boton--texto" onClick={alReintentar}>Reintentar</button>}
    </div>
  );
}

export function EtiquetaEstado({ estado }: { estado: EstadoPedido }) {
  return <span className={`etiqueta etiqueta--${estado.toLowerCase()}`}>{nombreEstado[estado]}</span>;
}

export function IndicadorEnVivo({ conectado }: { conectado: boolean }) {
  return (
    <span className={`en-vivo ${conectado ? 'en-vivo--activo' : ''}`}>
      <span className="en-vivo__punto" aria-hidden="true" />
      {conectado ? 'En vivo' : 'Conectando…'}
    </span>
  );
}

export function Encabezado({ titulo, subtitulo, acciones }: { titulo: string; subtitulo?: ReactNode; acciones?: ReactNode }) {
  return (
    <div className="encabezado">
      <div>
        <h1>{titulo}</h1>
        {subtitulo && <p className="encabezado__subtitulo">{subtitulo}</p>}
      </div>
      {acciones && <div className="encabezado__acciones">{acciones}</div>}
    </div>
  );
}
