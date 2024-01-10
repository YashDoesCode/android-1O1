package com.keeper.components.backend;

import androidx.collection.LruCache;

public class LocalLruMemoryCache<K, V> {

    private final LruCache<K, V> cache;

    public LocalLruMemoryCache(int maxSizeBytes) {
        this.cache = new LruCache<K, V>(maxSizeBytes) {
            @Override
            protected int sizeOf(K key, V value) {
                return 1;
            }
        };
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void clear() {
        cache.evictAll();
    }
}
