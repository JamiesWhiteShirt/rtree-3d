package com.jamieswhiteshirt.rtree3i;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A selector that selects the node which has the minimal amount of increase in volume on insertion.
 */
public final class MinimalVolumeIncreaseSelector implements Selector {

    @Override
    public <K, V> Node<K, V> select(Box box, List<Node<K, V>> nodes) {
        Comparator<Box> boxComparator = Comparators.volumeIncreaseComparator(box).thenComparing(Comparators.volumeComparator(box));
        return Collections.min(nodes, Comparator.comparing(Node::getBox, boxComparator));
    }
}
