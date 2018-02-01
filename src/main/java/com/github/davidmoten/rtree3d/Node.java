package com.github.davidmoten.rtree3d;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

interface Node<T> {

    List<Node<T>> add(Entry<T> entry, Configuration configuration);

    NodeAndEntries<T> delete(Entry<T> entry, Configuration configuration);

    void search(Predicate<Box> condition, Consumer<? super Entry<T>> consumer);

    boolean contains(Entry<T> entry);

    int calculateDepth();

    Box getBox();

    int size();

}
