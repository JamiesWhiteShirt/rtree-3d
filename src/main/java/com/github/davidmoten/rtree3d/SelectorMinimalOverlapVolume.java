package com.github.davidmoten.rtree3d;

import static com.github.davidmoten.rtree3d.Comparators.overlapVolumeComparator;
import static com.github.davidmoten.rtree3d.Comparators.volumeComparator;
import static com.github.davidmoten.rtree3d.Comparators.volumeIncreaseComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class SelectorMinimalOverlapVolume implements Selector {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Node<T> select(Box box, List<? extends Node<T>> nodes) {
        List<Box> boxes = nodes.stream().map(Node::getBox).collect(Collectors.toList());
        Comparator<Box> boxComparator = overlapVolumeComparator(box, boxes).thenComparing(volumeIncreaseComparator(box))
                .thenComparing(volumeComparator(box));
        return Collections.min(nodes, Comparator.comparing(Node::getBox, boxComparator));
    }

}
