package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * An entry in the R-tree which has a spatial representation.
 *
 * @param <K>
 *     the key type
 * @param <V>
 *     the value type
 */
public final class Entry<K, V> {
    private final K key;
    private final V value;

    private Entry(K key, V value) {
        Preconditions.checkNotNull(key);
        this.key = key;
        this.value = value;
    }

    /**
     * Returns an entry with the specified key and value.
     * @param key key of the entry
     * @param value value of the entry
     * @param <K> key type
     * @param <V> value type
     * @return an entry with the specified key and value
     */
    public static <K, V> Entry<K, V> of(K key, V value) {
        return new Entry<>(key, value);
    }

    /**
     * Returns the key of this {@link Entry}.
     *
     * @return the entry key
     */
    public K getKey() {
        return key;
    }

    /**
     * Returns the value wrapped by this {@link Entry}.
     *
     * @return the entry value
     */
    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Entry{" +
            "key=" + key +
            ", value=" + value +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry<?, ?> entry = (Entry<?, ?>) o;
        return Objects.equals(key, entry.key) &&
            Objects.equals(value, entry.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
