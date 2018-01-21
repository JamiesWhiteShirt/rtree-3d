package com.github.davidmoten.rtree3d;

import com.github.davidmoten.rtree3d.geometry.Box;

import static com.github.davidmoten.rtree3d.Comparators.compose;
import static com.github.davidmoten.rtree3d.Comparators.volumeComparator;
import static com.github.davidmoten.rtree3d.Comparators.volumeIncreaseComparator;

import java.util.Collections;
import java.util.List;

/**
 * Uses minimal volume increase to select a node from a list.
 *
 */
public final class SelectorMinimalVolumeIncrease implements Selector {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Node<T> select(Box box, List<? extends Node<T>> nodes) {
        return Collections.min(nodes, compose(volumeIncreaseComparator(box), volumeComparator(box)));
    }
}
