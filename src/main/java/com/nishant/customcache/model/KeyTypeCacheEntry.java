package com.nishant.customcache.model;

import com.nishant.customcache.annotations.GuardedBy;
import com.nishant.customcache.annotations.ThreadSafe;
import com.nishant.customcache.interfaces.ExpirableItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
@NoArgsConstructor
@ThreadSafe
public class KeyTypeCacheEntry<K, V> implements ExpirableItem {

    public KeyTypeCacheEntry(Class<?> keyType, Class<?> valueType) {
        this.keyType = keyType;
        this.valueType = getHighestType(valueType);
    }

    private Class<?> keyType;
    private Class<?> valueType;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    @GuardedBy("lock") private final Set<KeyValuePair<K, V>> children = new HashSet<>();

    public void addEntry(K key, V value) {
        lock.writeLock().lock();
        try {
            KeyValuePair<K, V> pair = new KeyValuePair<>(key, value);
            children.remove(pair);
            children.add(pair);
        } finally {
            lock.writeLock().unlock();
        }

    }

    public Optional<V> getEntry(K key) {
        lock.readLock().lock();
        try {
            return children.parallelStream()
                    .filter(child -> child.getKey().equals(key))
                    .map(child -> child.getValue()).findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean removeEntry(K key) {
        lock.writeLock().lock();
        try {
            return children.remove(new KeyValuePair<K, V>(key, null));
        } finally {
            lock.writeLock().unlock();
        }
    }


    private static Class<?> getHighestType(Class<?> valueClazz) {
        Class<?> valueHighestType = valueClazz;
        while (!valueClazz.equals(Object.class)) {
            valueHighestType = valueClazz;
            valueClazz = valueClazz.getSuperclass();
        }
        return valueHighestType;
    }

    public boolean matchesHighestTypeOfValue(V value) {
        return this.valueType.equals(getHighestType(value.getClass()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyTypeCacheEntry<?, ?> that = (KeyTypeCacheEntry<?, ?>) o;
        return Objects.equals(keyType, that.keyType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyType);
    }

    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return children.isEmpty();
        }finally {
            lock.readLock().unlock();
        }
    }
}
