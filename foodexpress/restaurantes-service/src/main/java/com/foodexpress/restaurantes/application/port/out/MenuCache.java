package com.foodexpress.restaurantes.application.port.out;

/**
 * Puerto de salida para avisar que el menú de un restaurante cambió y que
 * cualquier copia en caché dejó de ser válida.
 */
public interface MenuCache {

    void invalidar(Long restauranteId);
}
