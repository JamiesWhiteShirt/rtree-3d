package com.github.davidmoten.rtree3d;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.davidmoten.rtree3d.geometry.Geometry;
import com.github.davidmoten.rtree3d.geometry.HasGeometry;

interface Node<T, S extends Geometry> extends HasGeometry {

    List<Node<T, S>> add(Entry<? extends T, ? extends S> entry);

    NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all);

    void search(Function<? super Geometry, Boolean> condition,
            Consumer<? super Entry<T, S>> consumer);

    int count();

}
