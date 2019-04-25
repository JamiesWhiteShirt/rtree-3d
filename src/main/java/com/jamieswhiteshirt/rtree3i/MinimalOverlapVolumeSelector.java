package com.jamieswhiteshirt.rtree3i;

import static com.jamieswhiteshirt.rtree3i.Comparators.overlapVolumeComparator;
import static com.jamieswhiteshirt.rtree3i.Comparators.volumeComparator;
import static com.jamieswhiteshirt.rtree3i.Comparators.volumeIncreaseComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A selector that selects the minimum node by {@link Comparators#overlapVolumeComparator},
 * {@link Comparators#volumeComparator(Box)} then {@link Comparators#volumeIncreaseComparator(Box)}.
 */
public final class MinimalOverlapVolumeSelector implements Selector {

    @Override
    public <K, V> Node<K, V> select(Box box, List<Node<K, V>> nodes) {
        List<Box> boxes = nodes.stream().map(Node::getBox).collect(Collectors.toList());
        Comparator<Box> boxComparator = overlapVolumeComparator(box, boxes).thenComparing(volumeIncreaseComparator(box))
                .thenComparing(volumeComparator(box));
        return Collections.min(nodes, Comparator.comparing(Node::getBox, boxComparator));
    }

}
