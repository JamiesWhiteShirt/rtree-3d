package com.github.davidmoten.rtree3d;

import static com.github.davidmoten.rtree3d.Comparators.volumeComparator;
import static com.github.davidmoten.rtree3d.Comparators.volumeIncreaseComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Uses minimal volume increase to select a node from a list.
 *
 */
public final class SelectorMinimalVolumeIncrease implements Selector {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Node<T> select(Box box, List<? extends Node<T>> nodes) {
        Comparator<Box> boxComparator = volumeIncreaseComparator(box).thenComparing(volumeComparator(box));
        return Collections.min(nodes, Comparator.comparing(Node::getBox, boxComparator));
    }
}
