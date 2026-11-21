import { useState } from 'react';

export interface DatosCliente {
  nombre: string;
  email: string;
}

const CLAVE = 'foodexpress.cliente';

function leer(): DatosCliente {
  try {
    const guardado = localStorage.getItem(CLAVE);
    if (guardado) return JSON.parse(guardado);
  } catch {
    // Almacenamiento no disponible: se empieza vacío.
  }
  return { nombre: '', email: '' };
}

/**
 * Recuerda el nombre y el correo del cliente en este navegador. No hay inicio de
 * sesión: el correo es lo que identifica los pedidos de un cliente.
 */
export function useCliente(): [DatosCliente, (datos: DatosCliente) => void] {
  const [cliente, setCliente] = useState<DatosCliente>(leer);

  const guardar = (datos: DatosCliente) => {
    setCliente(datos);
    try {
      localStorage.setItem(CLAVE, JSON.stringify(datos));
    } catch {
      // Si no se puede guardar, solo se pierde al recargar.
    }
  };

  return [cliente, guardar];
}
