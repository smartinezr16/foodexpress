package com.foodexpress.pedidos.infrastructure.persistence;

import com.foodexpress.pedidos.domain.model.ExtraSeleccionado;
import com.foodexpress.pedidos.domain.model.ItemPedido;
import com.foodexpress.pedidos.domain.model.Pedido;
import com.foodexpress.pedidos.domain.model.PedidoBuilder;
import com.foodexpress.pedidos.domain.model.TipoPedido;

import java.util.List;

/** Convierte entre el modelo de dominio y las entidades JPA. */
final class PedidoMapper {

    private PedidoMapper() {
    }

    static PedidoEntity aEntidadNueva(Pedido pedido) {
        PedidoEntity entidad = new PedidoEntity();
        entidad.setClienteNombre(pedido.getCliente().nombre());
        entidad.setClienteEmail(pedido.getCliente().email());
        entidad.setRestauranteId(pedido.getRestauranteId());
        entidad.setRestauranteNombre(pedido.getRestauranteNombre());
        entidad.setTipo(pedido.getTipo());
        entidad.setDireccionEntrega(pedido.direccionEntrega().orElse(null));
        entidad.setTasaImpuesto(pedido.getTasaImpuesto());
        entidad.setSubtotal(pedido.subtotal());
        entidad.setImpuestos(pedido.impuestos());
        entidad.setCostoEnvio(pedido.costoEnvio());
        entidad.setTotal(pedido.total());
        entidad.setFechaCreacion(pedido.getFechaCreacion());
        for (ItemPedido item : pedido.getItems()) {
            entidad.getItems().add(aEntidad(item, entidad));
        }
        copiarEstado(pedido, entidad);
        return entidad;
    }

    /** Después de creado, lo único que cambia en un pedido es su estado y los datos de cancelación. */
    static void copiarEstado(Pedido pedido, PedidoEntity entidad) {
        entidad.setEstado(pedido.getEstado());
        entidad.setCanceladoPor(pedido.canceladoPor().orElse(null));
        entidad.setMotivoCancelacion(pedido.motivoCancelacion().orElse(null));
    }

    static Pedido aDominio(PedidoEntity entidad) {
        PedidoBuilder builder = Pedido.builder()
                .conId(entidad.getId(), entidad.getVersion())
                .cliente(entidad.getClienteNombre(), entidad.getClienteEmail())
                .restaurante(entidad.getRestauranteId(), entidad.getRestauranteNombre())
                .tasaImpuesto(entidad.getTasaImpuesto())
                .enEstado(entidad.getEstado())
                .creadoEn(entidad.getFechaCreacion())
                .canceladoPor(entidad.getCanceladoPor(), entidad.getMotivoCancelacion());

        for (ItemPedidoEntity itemEntidad : entidad.getItems()) {
            builder.agregarItem(aDominio(itemEntidad));
        }

        if (entidad.getTipo() == TipoPedido.DELIVERY) {
            builder.conDomicilio(entidad.getDireccionEntrega(), entidad.getCostoEnvio());
        } else {
            builder.paraRecoger();
        }
        return builder.construir();
    }

    private static ItemPedidoEntity aEntidad(ItemPedido item, PedidoEntity pedido) {
        List<ExtraSeleccionadoEmbeddable> extras = item.getExtras().stream()
                .map(extra -> new ExtraSeleccionadoEmbeddable(extra.extraId(), extra.nombre(), extra.precioAdicional()))
                .toList();
        return new ItemPedidoEntity(pedido, item.getPlatoId(), item.getNombrePlato(), item.getPrecioUnitario(),
                item.getCantidad(), item.getNotasEspeciales(), item.subtotal(), extras);
    }

    private static ItemPedido aDominio(ItemPedidoEntity entidad) {
        ItemPedido.Builder item = ItemPedido.builder()
                .plato(entidad.getPlatoId(), entidad.getNombrePlato(), entidad.getPrecioUnitario())
                .cantidad(entidad.getCantidad())
                .notas(entidad.getNotasEspeciales());
        for (ExtraSeleccionadoEmbeddable extra : entidad.getExtras()) {
            ExtraSeleccionado seleccionado =
                    new ExtraSeleccionado(extra.getExtraId(), extra.getNombre(), extra.getPrecioAdicional());
            item.conExtra(seleccionado.extraId(), seleccionado.nombre(), seleccionado.precioAdicional());
        }
        return item.construir();
    }
}
