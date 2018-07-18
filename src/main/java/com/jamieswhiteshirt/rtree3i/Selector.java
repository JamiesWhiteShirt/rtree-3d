package com.jamieswhiteshirt.rtree3i;

import java.util.List;

/**
 * The heuristic used on insert to select which node to add an Entry to.
 * 
 */
public interface Selector {

    /**
     * Returns the node from a list of nodes that an object with the given
     * geometry would be added to.
     * 
     * @param <K>
     *            type of key of entry in tree
     * @param <V>
     *            type of value of entry in tree
     * @param box
     *            box
     * @param nodes
     *            nodes to select from
     * @return one of the given nodes
     */
    <K, V> Node<K, V> select(Box box, List<Node<K, V>> nodes);

}
