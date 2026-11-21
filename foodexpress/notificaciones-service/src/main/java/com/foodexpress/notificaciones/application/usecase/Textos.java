package com.foodexpress.notificaciones.application.usecase;

import com.foodexpress.notificaciones.application.dto.EventoPedido;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.stream.Collectors;

/** Utilidades para redactar los mensajes. */
final class Textos {

    private Textos() {
    }

    /** 66960.00 se muestra como "$66.960". */
    static String pesos(BigDecimal valor) {
        if (valor == null) {
            return "$0";
        }
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        return "$" + new DecimalFormat("#,##0", simbolos).format(valor);
    }

    /** "2 x Hamburguesa, 1 x Limonada" */
    static String resumenItems(EventoPedido evento) {
        return evento.items().stream()
                .map(item -> item.cantidad() + " x " + item.nombre())
                .collect(Collectors.joining(", "));
    }
}
