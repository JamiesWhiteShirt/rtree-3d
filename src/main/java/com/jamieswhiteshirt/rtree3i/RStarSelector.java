package com.jamieswhiteshirt.rtree3i;

import java.util.List;

/**
 * Uses a {@link MinimalOverlapVolumeSelector} for leaf nodes and a {@link MinimalVolumeIncreaseSelector} for non-leaf nodes.
 */
public final class RStarSelector implements Selector {

    private static Selector overlapVolumeSelector = new MinimalOverlapVolumeSelector();
    private static Selector volumeIncreaseSelector = new MinimalVolumeIncreaseSelector();

    @Override
    public <K, V> Node<K, V> select(Box box, List<Node<K, V>> nodes) {
        boolean leafNodes = nodes.get(0).isLeaf();
        if (leafNodes)
            return overlapVolumeSelector.select(box, nodes);
        else
            return volumeIncreaseSelector.select(box, nodes);
    }

}
