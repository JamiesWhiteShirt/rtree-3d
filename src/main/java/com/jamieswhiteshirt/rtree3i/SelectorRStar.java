package com.jamieswhiteshirt.rtree3i;

import java.util.List;

/**
 * Uses minimal overlap volume selector for leaf nodes and minimal volume increase
 * selector for non-leaf nodes.
 */
public final class SelectorRStar implements Selector {

    private static Selector overlapVolumeSelector = new SelectorMinimalOverlapVolume();
    private static Selector volumeIncreaseSelector = new SelectorMinimalVolumeIncrease();

    @Override
    public <K, V> Node<K, V> select(Box box, List<Node<K, V>> nodes) {
        boolean leafNodes = nodes.get(0).isLeaf();
        if (leafNodes)
            return overlapVolumeSelector.select(box, nodes);
        else
            return volumeIncreaseSelector.select(box, nodes);
    }

}
