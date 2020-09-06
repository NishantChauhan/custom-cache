package com.nishant.customcache.model;

import com.nishant.customcache.interfaces.ExpirableItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class KeyTypeCacheEntry<K, V> implements ExpirableItem {

    public KeyTypeCacheEntry(Class<?> keyType, Class<?> valueType) {
        this.keyType = keyType;
        this.valueType = getHighestType(valueType);
    }

    private Class<?> keyType;
    private Class<?> valueType;
    private final HashSet<KeyValuePair<K, V>> children = new HashSet<>();


    public void addEntry(K key, V value) {
        synchronized (children) {
            KeyValuePair<K, V> pair = new KeyValuePair<>(key, value);
            children.remove(pair);
            children.add(pair);
        }
    }

    public Optional<V> getEntry(K key) {
        synchronized (children) {
            return children.parallelStream()
                    .filter(child -> child.getKey().equals(key))
                    .map(child -> child.getValue()).findFirst();
        }
    }

    public boolean removeEntry(K key) {
        synchronized (children) {
            return children.remove(new KeyValuePair<K, V>(key, null));
        }
    }

    private Class<?> getHighestType(Class<?> valueClazz) {
        Class<?> valueHighestType = valueClazz;
        while (!valueClazz.equals(Object.class)) {
            valueHighestType = valueClazz;
            valueClazz = valueClazz.getSuperclass();
        }
        return valueHighestType;
    }

    public boolean isSameHierarchy(V value) {
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
}
