package com.jamieswhiteshirt.rtree3i;

import java.util.Objects;
import java.util.function.Function;

final class EntryBox<K, V> {
    private final Box box;
    private final Entry<K, V> entry;

    static <K, V> EntryBox<K, V> of(Box box, Entry<K, V> entry) {
        return new EntryBox<>(box, entry);
    }

    static <K, V> EntryBox<K, V> of(Function<? super K, Box> keyBoxMapper, Entry<K, V> entry) {
        return of(keyBoxMapper.apply(entry.getKey()), entry);
    }

    private EntryBox(Box box, Entry<K, V> entry) {
        this.box = box;
        this.entry = entry;
    }

    public Box getBox() {
        return box;
    }

    public Entry<K, V> getEntry() {
        return entry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntryBox<?, ?> that = (EntryBox<?, ?>) o;
        return Objects.equals(box, that.box) &&
            Objects.equals(entry, that.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(box, entry);
    }

    @Override
    public String toString() {
        return "EntryBox{" +
            "box=" + box +
            ", entry=" + entry +
            '}';
    }
}
