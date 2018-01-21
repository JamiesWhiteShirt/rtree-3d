package com.github.davidmoten.rtree3d;

import com.github.davidmoten.rtree3d.geometry.Box;

import java.util.List;

/**
 * Uses minimal overlap volume selector for leaf nodes and minimal volume increase
 * selector for non-leaf nodes.
 */
public final class SelectorRStar implements Selector {

    private static Selector overlapVolumeSelector = new SelectorMinimalOverlapVolume();
    private static Selector volumeIncreaseSelector = new SelectorMinimalVolumeIncrease();

    @Override
    public <T> Node<T> select(Box box, List<? extends Node<T>> nodes) {
        boolean leafNodes = nodes.get(0) instanceof Leaf;
        if (leafNodes)
            return overlapVolumeSelector.select(box, nodes);
        else
            return volumeIncreaseSelector.select(box, nodes);
    }

}
