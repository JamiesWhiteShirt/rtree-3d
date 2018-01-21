package com.github.davidmoten.rtree3d;

import java.util.List;
import java.util.Optional;

/**
 * Used for tracking deletions through recursive calls.
 * 
 * @param <T>
 *            entry type
 */
class NodeAndEntries<T> {

    private final Optional<? extends Node<T>> node;
    private final List<Entry<T>> entries;
    private final int count;

    /**
     * Constructor.
     * 
     * @param node
     *            absent = whole node was deleted present = either an unchanged
     *            node because of no removal or the newly created node without
     *            the deleted entry
     * @param entries
     *            from nodes that dropped below minChildren in size and thus
     *            their entries are to be redistributed (readded to the tree)
     * @param countDeleted
     *            count of the number of entries removed
     */
    NodeAndEntries(Optional<? extends Node<T>> node, List<Entry<T>> entries,
                   int countDeleted) {
        this.node = node;
        this.entries = entries;
        this.count = countDeleted;
    }

    Optional<? extends Node<T>> node() {
        return node;
    }

    List<Entry<T>> entriesToAdd() {
        return entries;
    }

    int countDeleted() {
        return count;
    }

}
