package com.jamieswhiteshirt.rtree3i;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class Bucket<K, V> {
    private final Box box;
    private final List<Entry<K, V>> entries;

    static <K, V> Bucket<K, V> of(Box box, Entry<K, V> entry) {
        return of(box, Collections.singletonList(entry));
    }

    static <K, V> Bucket<K, V> of(Box box, List<Entry<K, V>> entries) {
        return new Bucket<>(box, entries);
    }

    private Bucket(Box box, List<Entry<K, V>> entries) {
        this.box = box;
        this.entries = entries;
    }

    public Box getBox() {
        return box;
    }

    public Bucket<K, V> put(Entry<K, V> entry) {
        for (Entry<K, V> existingEntry : entries) {
            if (existingEntry.getKey().equals(entry.getKey())) {
                return new Bucket<>(box, Util.replace(entries, existingEntry, entry));
            }
        }
        return new Bucket<>(box, Util.add(entries, entry));
    }

    public Bucket<K, V> remove(Entry<K, V> entry) {
        if (entries.size() == 1) {
            if (entries.get(0).equals(entry)) return null;
        } else {
            return new Bucket<>(box, Util.remove(entries, entry));
        }
        return this;
    }

    public Bucket<K, V> remove(K key) {
        if (entries.size() == 1) {
            if (entries.get(0).getKey().equals(key)) return null;
        } else {
            for (Entry<K, V> entry : entries) {
                if (entry.getKey().equals(key)) {
                    return new Bucket<>(box, Util.remove(entries, entry));
                }
            }
        }
        return this;
    }

    public Entry<K, V> get(K key) {
        for (Entry<K, V> entry : entries) {
            if (entry.getKey().equals(key)) {
                return entry;
            }
        }
        return null;
    }

    public void forEach(Consumer<? super Entry<K, V>> action) {
        entries.forEach(action);
    }

    public boolean anyMatch(Predicate<? super Entry<K, V>> entryPredicate) {
        for (final Entry<K, V> entry : entries) {
            if (entryPredicate.test(entry)) {
                return true;
            }
        }
        return false;
    }

    public boolean allMatch(Predicate<? super Entry<K, V>> entryPredicate) {
        for (final Entry<K, V> entry : entries) {
            if (!entryPredicate.test(entry)) {
                return false;
            }
        }
        return true;
    }

    public <T> T reduce(T identity, BiFunction<T, Entry<K, V>, T> operator) {
        T acc = identity;
        for (final Entry<K, V> entry : entries) {
            acc = operator.apply(acc, entry);
        }
        return acc;
    }

    public int count(Predicate<? super Entry<K, V>> entryPredicate) {
        int count = 0;
        for (final Entry<K, V> entry : entries) {
            if (entryPredicate.test(entry)) {
                count++;
            }
        }
        return count;
    }

    public boolean contains(Entry<K, V> entry) {
        return entries.contains(entry);
    }

    public int size() {
        return entries.size();
    }

    @Override
    public String toString() {
        return "Bucket{" +
            "box=" + box +
            ", entries=" + entries +
            '}';
    }
}
