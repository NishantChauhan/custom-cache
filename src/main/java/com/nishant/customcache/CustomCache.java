package com.nishant.customcache;

import com.nishant.customcache.interfaces.ExpirableItem;
import com.nishant.customcache.interfaces.Expirable;
import com.nishant.customcache.model.KeyTypeCacheEntry;
import com.nishant.customcache.services.ExpirationService;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class CustomCache<K, V> implements Expirable {

    public CustomCache() {
    }

    private final LinkedHashSet<KeyTypeCacheEntry<K, V>> keyTypeCache = new LinkedHashSet<>();
    private final ExpirationService cacheExpirationService = new ExpirationService();

    public void put(K key, V value) {
        synchronized (keyTypeCache) {
            KeyTypeCacheEntry<K, V> cacheEntry =
                    existingKeyEntryHandling(key, value)
                            .orElse(addKeyCacheEntry(key, value));
            cacheEntry.addEntry(key, value);
        }
    }

    private KeyTypeCacheEntry<K, V> addKeyCacheEntry(K key, V value) {
        KeyTypeCacheEntry<K, V> entry = new KeyTypeCacheEntry<>(key.getClass(), value.getClass());
        keyTypeCache.add(entry);
        cacheExpirationService.schedule(this, entry);
        return entry;
    }

    private Optional<KeyTypeCacheEntry<K, V>> existingKeyEntryHandling(K key, V value) {
        if (keyTypeCache.isEmpty()) {
            return Optional.empty();
        }
        for (KeyTypeCacheEntry<K, V> keyTypeEntry : keyTypeCache) {
            if (keyTypeEntry.getKeyType().equals(key.getClass())) {
                if (keyTypeEntry.isSameHierarchy(value)) {
                    return Optional.of(keyTypeEntry);
                } else {
                    throw new RuntimeException(
                            "Object of class [" + value.getClass() + "] not allowable for this Key Type [" + key.getClass() + "]. " +
                                    "Allowed types are [" + keyTypeEntry.getValueType() + "] or it sub and super types");
                }
            }
        }
        return Optional.empty();
    }

    private Optional<KeyTypeCacheEntry<K, V>> getKeyCacheEntry(K key) {
        return keyTypeCache.parallelStream().filter(entry -> entry.getKeyType().equals(key.getClass())).findFirst();
    }

    public boolean remove(K key) {

        Optional<KeyTypeCacheEntry<K, V>> keyValueTypeCacheEntry;
        synchronized (keyTypeCache) {
            keyValueTypeCacheEntry = getKeyCacheEntry(key);
           if (keyValueTypeCacheEntry.isPresent()) {
                boolean removed = keyValueTypeCacheEntry.get().removeEntry(key);
                if (keyValueTypeCacheEntry.get().getChildren().isEmpty()) {
                    removeCacheEntry(keyValueTypeCacheEntry.get());
                }
                return removed;
            }
            return false;
        }
    }

    public V get(K key) {
        synchronized (this.keyTypeCache) {
            return getKeyCacheEntry(key).flatMap(keyTypeCacheEntry -> keyTypeCacheEntry.getEntry(key))
                    .orElse(null);
        }
    }

    public void removeCacheEntry(KeyTypeCacheEntry<K, V> keyTypeCacheEntry) {
        synchronized (keyTypeCache) {
            keyTypeCache.remove(keyTypeCacheEntry);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public void expire(ExpirableItem item) {
        removeCacheEntry((KeyTypeCacheEntry<K, V>) item);
    }

    @Override
    public long getExpiry() {
        return 10;
    }

    @Override
    public TimeUnit getExpiryTimeUnit() {
        return TimeUnit.SECONDS;
    }
}