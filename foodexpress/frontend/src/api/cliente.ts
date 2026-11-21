import type {
  Actor, CategoriaPlato, CrearPedido, EstadoRestaurante, Notificacion, Pedido, Plato, Restaurante,
} from './tipos';

/** Error de la API con el mensaje "detail" del problem detail que devuelven los servicios. */
export class ApiError extends Error {
  constructor(public readonly status: number, mensaje: string) {
    super(mensaje);
  }
}

async function pedir<T>(ruta: string, opciones: RequestInit = {}): Promise<T> {
  let respuesta: Response;
  try {
    respuesta = await fetch(ruta, {
      ...opciones,
      headers: { 'Content-Type': 'application/json', ...opciones.headers },
    });
  } catch {
    throw new ApiError(0, 'No hay conexión con el servidor.');
  }

  if (!respuesta.ok) {
    let mensaje = `Error ${respuesta.status}`;
    try {
      const problema = await respuesta.json();
      if (problema?.detail) mensaje = problema.detail;
    } catch {
      // La respuesta no traía JSON (por ejemplo, un 502 del proxy).
      if (respuesta.status >= 502) mensaje = 'El servicio no está disponible en este momento.';
    }
    throw new ApiError(respuesta.status, mensaje);
  }
  return respuesta.status === 204 ? (undefined as T) : respuesta.json();
}

const json = (cuerpo: unknown) => JSON.stringify(cuerpo);

// ---------- Restaurantes y Menú (puerto 8081) ----------

export const restaurantesApi = {
  listar: () => pedir<Restaurante[]>('/api/restaurantes'),
  obtener: (id: number) => pedir<Restaurante>(`/api/restaurantes/${id}`),
  cambiarEstado: (id: number, estado: EstadoRestaurante) =>
    pedir<Restaurante>(`/api/restaurantes/${id}/estado`, { method: 'PATCH', body: json({ estado }) }),
  menu: (id: number, categoria?: CategoriaPlato) =>
    pedir<Plato[]>(`/api/restaurantes/${id}/menu${categoria ? `?categoria=${categoria}` : ''}`),
  cambiarDisponibilidad: (platoId: number, habilitado: boolean, unidadesDisponibles: number | null) =>
    pedir<Plato>(`/api/platos/${platoId}/disponibilidad`, {
      method: 'PATCH',
      body: json({ habilitado, unidadesDisponibles }),
    }),
};

// ---------- Pedidos (puerto 8082) ----------

export const pedidosApi = {
  crear: (pedido: CrearPedido) => pedir<Pedido>('/api/pedidos', { method: 'POST', body: json(pedido) }),
  obtener: (id: number) => pedir<Pedido>(`/api/pedidos/${id}`),
  porCliente: (email: string) => pedir<Pedido[]>(`/api/pedidos?clienteEmail=${encodeURIComponent(email)}`),
  porRestaurante: (restauranteId: number) => pedir<Pedido[]>(`/api/pedidos?restauranteId=${restauranteId}`),
  avanzar: (id: number) => pedir<Pedido>(`/api/pedidos/${id}/avanzar`, { method: 'PATCH' }),
  cancelar: (id: number, canceladoPor: Actor, motivo?: string) =>
    pedir<Pedido>(`/api/pedidos/${id}/cancelar`, { method: 'POST', body: json({ canceladoPor, motivo }) }),
};

// ---------- Notificaciones (puerto 8083) ----------

export const notificacionesApi = {
  porPedido: (pedidoId: number) => pedir<Notificacion[]>(`/api/notificaciones?pedidoId=${pedidoId}`),
};
