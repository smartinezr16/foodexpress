// Contratos de las APIs de los tres microservicios, tal como los devuelven.

export type EstadoRestaurante = 'ABIERTO' | 'CERRADO';
export type CategoriaPlato = 'ENTRADA' | 'PLATO_FUERTE' | 'BEBIDA' | 'POSTRE';
export type TipoPedido = 'LOCAL' | 'DELIVERY';
export type EstadoPedido = 'RECIBIDO' | 'EN_PREPARACION' | 'LISTO' | 'ENTREGADO' | 'CANCELADO';
export type Actor = 'CLIENTE' | 'RESTAURANTE';

export interface Restaurante {
  id: number;
  nombre: string;
  direccion: string;
  telefono: string | null;
  horaApertura: string;
  horaCierre: string;
  estado: EstadoRestaurante;
}

export interface Extra {
  id: number;
  nombre: string;
  precioAdicional: number;
}

export interface Plato {
  id: number;
  restauranteId: number;
  nombre: string;
  descripcion: string | null;
  precio: number;
  categoria: CategoriaPlato;
  disponible: boolean;
  habilitado: boolean;
  unidadesDisponibles: number | null;
  extras: Extra[];
}

export interface ItemPedido {
  platoId: number;
  nombrePlato: string;
  precioUnitario: number;
  cantidad: number;
  extras: { extraId: number; nombre: string; precioAdicional: number }[];
  notasEspeciales: string | null;
  subtotal: number;
}

export interface Pedido {
  id: number;
  estado: EstadoPedido;
  tipo: TipoPedido;
  cliente: { nombre: string; email: string };
  restauranteId: number;
  restauranteNombre: string;
  direccionEntrega: string | null;
  items: ItemPedido[];
  subtotal: number;
  impuestos: number;
  costoEnvio: number;
  total: number;
  fechaCreacion: string;
  canceladoPor: Actor | null;
  motivoCancelacion: string | null;
}

export interface CrearPedido {
  restauranteId: number;
  cliente: { nombre: string; email: string };
  tipo: TipoPedido;
  direccionEntrega?: string;
  items: { platoId: number; cantidad: number; extraIds: number[]; notasEspeciales?: string }[];
}

export interface Notificacion {
  id: number;
  pedidoId: number;
  canal: 'EMAIL' | 'WEBSOCKET' | 'PUSH';
  tipoDestinatario: Actor;
  destinatario: string;
  asunto: string;
  cuerpo: string;
  estado: 'ENVIADA' | 'FALLIDA';
  detalleError: string | null;
  fecha: string;
}

/** Mensaje que llega por WebSocket desde el servicio de Notificaciones. */
export interface NotificacionEnVivo {
  pedidoId: number;
  tipoEvento: string;
  estado: EstadoPedido | null;
  titulo: string;
  mensaje: string;
  fecha: string;
}
