package org.axonframework.common.cache;

import javax.cache.Cache;
import javax.cache.event.CacheEntryListener;

/**
 * @author Allard Buijze
 */
public class DefaultStrategy implements CacheStrategy {

    @Override
    public <K, V> void registerCacheEntryListener(Cache<K, V> cache, CacheEntryListener<K, V> cacheListener) {
        cache.registerCacheEntryListener(cacheListener);
    }
}
