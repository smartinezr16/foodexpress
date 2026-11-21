import { describe, expect, it } from 'vitest';
import type { Plato } from '../api/tipos';
import { agregarLinea, armarPedido, cambiarCantidad, estimarTotales, type LineaCarrito } from './carrito';

const queso = { id: 1, nombre: 'Queso', precioAdicional: 3000 };
const tocineta = { id: 2, nombre: 'Tocineta', precioAdicional: 4000 };

const hamburguesa: Plato = {
  id: 10, restauranteId: 1, nombre: 'Hamburguesa', descripcion: null, precio: 25000,
  categoria: 'PLATO_FUERTE', disponible: true, habilitado: true, unidadesDisponibles: null,
  extras: [queso, tocineta],
};
const limonada: Plato = { ...hamburguesa, id: 20, nombre: 'Limonada', precio: 6000, categoria: 'BEBIDA', extras: [] };

function carritoDeEjemplo(): LineaCarrito[] {
  let lineas: LineaCarrito[] = [];
  lineas = agregarLinea(lineas, { plato: hamburguesa, cantidad: 2, extras: [queso, tocineta], notas: 'Sin cebolla' });
  lineas = agregarLinea(lineas, { plato: limonada, cantidad: 1, extras: [], notas: '' });
  return lineas;
}

describe('carrito', () => {
  it('estima los totales con la misma regla que el backend', () => {
    // (25000 + 3000 + 4000) * 2 + 6000 = 70000 ; impuesto 8 % = 5600
    expect(estimarTotales(carritoDeEjemplo(), 'LOCAL')).toEqual({
      subtotal: 70000, impuestos: 5600, envio: 0, total: 75600,
    });
  });

  it('suma el domicilio sin gravarlo', () => {
    expect(estimarTotales(carritoDeEjemplo(), 'DELIVERY').total).toBe(80600);
  });

  it('un carrito vacío no cobra domicilio', () => {
    expect(estimarTotales([], 'DELIVERY').total).toBe(0);
  });

  it('junta las líneas idénticas y separa las que cambian de extras', () => {
    let lineas = carritoDeEjemplo();
    lineas = agregarLinea(lineas, { plato: hamburguesa, cantidad: 1, extras: [tocineta, queso], notas: 'Sin cebolla' });
    lineas = agregarLinea(lineas, { plato: hamburguesa, cantidad: 1, extras: [], notas: '' });

    expect(lineas).toHaveLength(3);
    expect(lineas[0].cantidad).toBe(3);
  });

  it('quita la línea cuando la cantidad llega a cero', () => {
    const lineas = carritoDeEjemplo();
    expect(cambiarCantidad(lineas, lineas[1].clave, 0)).toHaveLength(1);
  });

  it('arma la petición sin precios y sin dirección cuando es para recoger', () => {
    const pedido = armarPedido(1, carritoDeEjemplo(), { nombre: 'Ana', email: 'ana@correo.com' }, 'LOCAL', 'Calle 1');

    expect(pedido.direccionEntrega).toBeUndefined();
    expect(pedido.items).toEqual([
      { platoId: 10, cantidad: 2, extraIds: [1, 2], notasEspeciales: 'Sin cebolla' },
      { platoId: 20, cantidad: 1, extraIds: [], notasEspeciales: undefined },
    ]);
  });
});
