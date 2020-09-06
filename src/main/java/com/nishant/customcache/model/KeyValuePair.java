package com.nishant.customcache.model;

import com.nishant.customcache.annotations.Immutable;
import com.nishant.customcache.annotations.ThreadSafe;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;


@Getter
@AllArgsConstructor
@ThreadSafe
@Immutable
public class KeyValuePair<K, V> {
    private K key;
    private V value;

    private KeyValuePair(){

    }

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
