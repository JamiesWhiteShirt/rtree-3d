package com.jamieswhiteshirt.rtree3i;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

interface Node<K, V> {

    List<Node<K, V>> put(EntryBox<K, V> entryBox, Configuration configuration);

    NodeAndEntries<K, V> remove(EntryBox<K, V> entryBox, Configuration configuration);

    NodeAndEntries<K, V> remove(Box box, K key, Configuration configuration);

    Entry<K, V> get(Box box, K key);

    void forEach(Predicate<? super Box> boxPredicate, Consumer<? super Entry<K, V>> consumer);

    boolean anyMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate);

    boolean allMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate);

    <T> T reduce(Predicate<? super Box> boxPredicate, T identity, BiFunction<T, Entry<K, V>, T> operator);

    int count(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate);

    boolean contains(EntryBox<K, V> entryBox);

    int calculateDepth();

    Box getBox();

    int size();

    boolean isLeaf();

    String asString(String margin);

}
