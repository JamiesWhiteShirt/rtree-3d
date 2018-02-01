package com.jamieswhiteshirt.rtree3i;

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
        Comparator<Box> boxComparator = Comparators.volumeIncreaseComparator(box).thenComparing(Comparators.volumeComparator(box));
        return Collections.min(nodes, Comparator.comparing(Node::getBox, boxComparator));
    }
}
