package com.github.davidmoten.rtree3d;

import com.github.davidmoten.rtree3d.geometry.Box;

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
     * @param <T>
     *            type of value of entry in tree
     * @param box
     *            box
     * @param nodes
     *            nodes to select from
     * @return one of the given nodes
     */
    <T> Node<T> select(Box box, List<? extends Node<T>> nodes);

}
