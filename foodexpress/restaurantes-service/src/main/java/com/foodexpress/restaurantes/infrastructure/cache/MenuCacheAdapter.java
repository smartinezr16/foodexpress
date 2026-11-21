package com.foodexpress.restaurantes.infrastructure.cache;

import com.foodexpress.restaurantes.application.port.out.MenuCache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/** Adaptador de salida: descarta el menú en caché de un restaurante cuando este cambia. */
@Component
public class MenuCacheAdapter implements MenuCache {

    private final CacheManager cacheManager;

    public MenuCacheAdapter(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void invalidar(Long restauranteId) {
        Cache cache = cacheManager.getCache(CacheConfig.CACHE_MENUS);
        if (cache != null) {
            cache.evict(restauranteId);
        }
    }
}
