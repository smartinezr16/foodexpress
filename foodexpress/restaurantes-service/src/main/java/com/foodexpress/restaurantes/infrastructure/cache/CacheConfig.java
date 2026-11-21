package com.foodexpress.restaurantes.infrastructure.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Activa la caché de menús (Caffeine, en memoria). El tamaño y el tiempo de vida
 * se configuran en application.yml (spring.cache.caffeine.spec).
 *
 * Atributo de calidad RENDIMIENTO: el menú se lee muchísimo más de lo que cambia,
 * así que las consultas repetidas no llegan a la base de datos.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHE_MENUS = "menus";
}
