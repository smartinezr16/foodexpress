import type { CategoriaPlato, EstadoPedido } from '../api/tipos';

const pesos = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 });

/** 66960 → "$ 66.960" */
export const formatoPesos = (valor: number) => pesos.format(valor);

export const nombreCategoria: Record<CategoriaPlato, string> = {
  ENTRADA: 'Entradas',
  PLATO_FUERTE: 'Platos fuertes',
  BEBIDA: 'Bebidas',
  POSTRE: 'Postres',
};

export const ordenCategorias: CategoriaPlato[] = ['ENTRADA', 'PLATO_FUERTE', 'BEBIDA', 'POSTRE'];

export const nombreEstado: Record<EstadoPedido, string> = {
  RECIBIDO: 'Recibido',
  EN_PREPARACION: 'En preparación',
  LISTO: 'Listo',
  ENTREGADO: 'Entregado',
  CANCELADO: 'Cancelado',
};

/** Flujo normal del pedido, en el mismo orden que la máquina de estados del backend. */
export const flujoEstados: EstadoPedido[] = ['RECIBIDO', 'EN_PREPARACION', 'LISTO', 'ENTREGADO'];

/** Texto del botón que lleva el pedido a su siguiente estado. */
export const accionAvanzar: Partial<Record<EstadoPedido, string>> = {
  RECIBIDO: 'Empezar a preparar',
  EN_PREPARACION: 'Marcar como listo',
  LISTO: 'Marcar como entregado',
};

/** Reglas de cancelación del patrón State, reflejadas en la interfaz. */
export const clientePuedeCancelar = (estado: EstadoPedido) => estado === 'RECIBIDO';
export const restaurantePuedeCancelar = (estado: EstadoPedido) =>
  estado === 'RECIBIDO' || estado === 'EN_PREPARACION';

export const horaCorta = (iso: string) =>
  new Date(iso).toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
