package com.nishant.customcache;

import com.nishant.customcache.interfaces.ExpirableItem;
import com.nishant.customcache.interfaces.Expirable;
import com.nishant.customcache.model.KeyTypeCacheEntry;
import com.nishant.customcache.services.ExpirationService;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class CustomCache<K, V> implements Expirable {

    public CustomCache() {
    }

    private final Set<KeyTypeCacheEntry<K, V>> keyTypeCache = Collections.synchronizedSet(new LinkedHashSet<>());
    private final ExpirationService cacheExpirationService = new ExpirationService();

    @Override
    @SuppressWarnings("unchecked")
    public final void expire(ExpirableItem item) {
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
    /** Return the value associated to key without mutating the state. */
    public V get(K key) {
        synchronized (keyTypeCache) {
            return getKeyCacheEntry(key).flatMap(keyTypeCacheEntry -> keyTypeCacheEntry.getEntry(key))
                    .orElse(null);
        }
    }

    /**  Mutates the state by adding new entry in value cache and/or
     * logic to remove its associated key type cache */
    public void put(K key, V value) {
        synchronized (keyTypeCache) {
            KeyTypeCacheEntry<K, V> cacheEntry =
                    existingKeyEntryHandling(key, value)
                            .orElse(addKeyCacheEntry(key, value));
            cacheEntry.addEntry(key, value);
        }
    }

    /**  Mutates the state by removing an entry in value cache
     * and/or invoking logic to remove its associated  key type cache */
    public boolean remove(K key) {

        Optional<KeyTypeCacheEntry<K, V>> keyValueTypeCacheEntry;
        synchronized (keyTypeCache) {
            keyValueTypeCacheEntry = getKeyCacheEntry(key);
            if (keyValueTypeCacheEntry.isPresent()) {
                boolean removed = keyValueTypeCacheEntry.get().removeEntry(key);
                if (keyValueTypeCacheEntry.get().isEmpty()) {
                    removeCacheEntry(keyValueTypeCacheEntry.get());
                }
                return removed;
            }
            return false;
        }
    }

    /**  Mutates the state by adding entry to key type cache */
    private KeyTypeCacheEntry<K, V> addKeyCacheEntry(K key, V value) {
        KeyTypeCacheEntry<K, V> entry = new KeyTypeCacheEntry<>(key.getClass(), value.getClass());
        keyTypeCache.add(entry);
        cacheExpirationService.schedule(this, entry);
        return entry;
    }

    /**  Returns existing key cache entry without mutating the state*/
    private Optional<KeyTypeCacheEntry<K, V>> existingKeyEntryHandling(K key, V value) {
        Optional<KeyTypeCacheEntry<K, V>> keyTypeEntry = getKeyCacheEntry(key);
        if(keyTypeEntry.isPresent()){
            if (!keyTypeEntry.get().matchesHighestTypeOfValue(value)) {
                throw new RuntimeException(
                        "Object of class [" + value.getClass() + "] not allowable for this Key Type [" + key.getClass() + "]. " +
                                "Allowed types are [" + keyTypeEntry.get().getValueType() + "] or it sub and super types");
            }
            return keyTypeEntry;
        }
        return Optional.empty();
    }

    /**  Returns existing key cache entry without mutating the state*/
    private Optional<KeyTypeCacheEntry<K, V>> getKeyCacheEntry(K key) {
        return keyTypeCache.parallelStream().filter(entry -> entry.getKeyType().equals(key.getClass())).findFirst();
    }

    private void removeCacheEntry(KeyTypeCacheEntry<K, V> keyTypeCacheEntry) {
        synchronized (keyTypeCache) {
            keyTypeCache.remove(keyTypeCacheEntry);
        }
    }

}