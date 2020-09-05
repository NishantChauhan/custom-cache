package com.nishant.customcache.model;

import com.nishant.customcache.interfaces.ExpirableItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class KeyTypeCacheEntry<K, V> implements ExpirableItem {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class KeyValuePair<K, V> {
        K key;
        V value;

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyValuePair<K, V> that = (KeyValuePair<K, V>) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    public KeyTypeCacheEntry(Class<?> keyType, Class<?> valueType) {
        this.keyType = keyType;
        this.valueType = getHighestType(valueType);
    }

    private Class<?> keyType;
    private Class<?> valueType;
    private final LinkedList<KeyValuePair<K, V>> children = new LinkedList<>();


    public void addEntry(K key, V value) {
        synchronized (children) {
            KeyValuePair<K, V> pair = new KeyValuePair<>(key, value);
            if (!children.isEmpty()) {
                children.stream()
                        .filter(child -> child.equals(pair))
                        .findFirst()
                        .ifPresent(
                                (child) -> {
                                    child.value = value;
                                    return;
                                });
            }
            children.add(pair);
        }
    }

    public Optional<V> getEntry(K key) {
        synchronized (children) {
            return children.parallelStream()
                    .filter(child -> child.key.equals(key))
                    .map(child -> child.value).findFirst();
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
