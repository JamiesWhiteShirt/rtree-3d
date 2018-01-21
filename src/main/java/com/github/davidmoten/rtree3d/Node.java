package com.github.davidmoten.rtree3d;

import com.github.davidmoten.rtree3d.geometry.Box;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

interface Node<T> extends HasBox {

    List<Node<T>> add(Entry<? extends T> entry, Context context);

    NodeAndEntries<T> delete(Entry<? extends T> entry, boolean all, Context context);

    void search(Predicate<Box> condition,
            Consumer<? super Entry<T>> consumer);

    int countEntries();

    int calculateDepth();

}
