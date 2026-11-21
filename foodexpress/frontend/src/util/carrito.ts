import type { CrearPedido, Extra, Plato, TipoPedido } from '../api/tipos';

export interface LineaCarrito {
  /** Identificador local: el mismo plato con extras distintos es otra línea. */
  clave: string;
  plato: Plato;
  cantidad: number;
  extras: Extra[];
  notas: string;
}

// Mismos valores por defecto que el servicio de Pedidos. Son solo una estimación:
// el total definitivo lo calcula el backend con los precios vigentes del menú.
export const TASA_IMPUESTO = 0.08;
export const COSTO_DOMICILIO = 5000;

export function claveLinea(platoId: number, extras: Extra[], notas: string): string {
  const ids = extras.map((e) => e.id).sort((a, b) => a - b).join(',');
  return `${platoId}|${ids}|${notas.trim()}`;
}

export function precioUnitario(linea: Pick<LineaCarrito, 'plato' | 'extras'>): number {
  return linea.plato.precio + linea.extras.reduce((suma, extra) => suma + extra.precioAdicional, 0);
}

export function subtotalLinea(linea: LineaCarrito): number {
  return precioUnitario(linea) * linea.cantidad;
}

export interface TotalesEstimados {
  subtotal: number;
  impuestos: number;
  envio: number;
  total: number;
}

/** Misma regla que el dominio del backend: el impuesto no grava el domicilio; redondeo a 2 decimales. */
export function estimarTotales(lineas: LineaCarrito[], tipo: TipoPedido): TotalesEstimados {
  const subtotal = lineas.reduce((suma, linea) => suma + subtotalLinea(linea), 0);
  const impuestos = Math.round(subtotal * TASA_IMPUESTO * 100) / 100;
  const envio = tipo === 'DELIVERY' && lineas.length > 0 ? COSTO_DOMICILIO : 0;
  return { subtotal, impuestos, envio, total: subtotal + impuestos + envio };
}

/** Agrega una línea o, si ya existe una idéntica, suma la cantidad. */
export function agregarLinea(lineas: LineaCarrito[], nueva: Omit<LineaCarrito, 'clave'>): LineaCarrito[] {
  const clave = claveLinea(nueva.plato.id, nueva.extras, nueva.notas);
  const existente = lineas.find((l) => l.clave === clave);
  if (existente) {
    return lineas.map((l) => (l.clave === clave ? { ...l, cantidad: l.cantidad + nueva.cantidad } : l));
  }
  return [...lineas, { ...nueva, clave }];
}

export function cambiarCantidad(lineas: LineaCarrito[], clave: string, cantidad: number): LineaCarrito[] {
  if (cantidad < 1) return lineas.filter((l) => l.clave !== clave);
  return lineas.map((l) => (l.clave === clave ? { ...l, cantidad } : l));
}

/** Arma el cuerpo de POST /api/pedidos. No envía precios: el backend los toma del menú. */
export function armarPedido(
  restauranteId: number,
  lineas: LineaCarrito[],
  cliente: { nombre: string; email: string },
  tipo: TipoPedido,
  direccionEntrega: string,
): CrearPedido {
  return {
    restauranteId,
    cliente,
    tipo,
    direccionEntrega: tipo === 'DELIVERY' ? direccionEntrega.trim() : undefined,
    items: lineas.map((linea) => ({
      platoId: linea.plato.id,
      cantidad: linea.cantidad,
      extraIds: linea.extras.map((e) => e.id),
      notasEspeciales: linea.notas.trim() || undefined,
    })),
  };
}
