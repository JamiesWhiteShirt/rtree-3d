package com.jamieswhiteshirt.rtree3i;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

interface Node<K, V> {

    List<Node<K, V>> put(Box box, Entry<K, V> entry, Configuration configuration);

    List<Node<K, V>> putBucket(Bucket<K, V> bucket, Configuration configuration);

    NodeAndEntries<K, V> remove(Box box, Entry<K, V> entry, Configuration configuration);

    NodeAndEntries<K, V> remove(Box box, K key, Configuration configuration);

    Entry<K, V> get(Box box, K key);

    void forEach(Predicate<? super Box> boxPredicate, Consumer<? super Entry<K, V>> action);

    boolean anyMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate);

    boolean allMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate);

    <T> T reduce(Predicate<? super Box> boxPredicate, T identity, BiFunction<T, Entry<K, V>, T> operator);

    int count(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate);

    boolean contains(Box box, Entry<K, V> entry);

    boolean containsBucket(Box box);

    int calculateDepth();

    Box getBox();

    int size();

    boolean isLeaf();

    String asString(String margin);

}
