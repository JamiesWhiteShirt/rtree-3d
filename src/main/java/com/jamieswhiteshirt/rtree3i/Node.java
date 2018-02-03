package com.jamieswhiteshirt.rtree3i;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

interface Node<T> {

    List<Node<T>> add(Entry<T> entry, Configuration configuration);

    NodeAndEntries<T> remove(Entry<T> entry, Configuration configuration);

    void forEach(Predicate<? super Box> condition, Consumer<? super Entry<T>> consumer);

    boolean any(Predicate<? super Box> condition, Predicate<? super Entry<T>> test);

    boolean all(Predicate<? super Box> condition, Predicate<? super Entry<T>> test);

    boolean contains(Entry<T> entry);

    int calculateDepth();

    Box getBox();

    int size();

}
