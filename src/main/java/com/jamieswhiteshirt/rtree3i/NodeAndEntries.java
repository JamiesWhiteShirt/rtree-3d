package com.jamieswhiteshirt.rtree3i;

import java.util.List;

/**
 * Used for tracking deletions through recursive calls.
 * 
 * @param <T>
 *            entry type
 */
final class NodeAndEntries<T> {

    private final Node<T> node;
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
    NodeAndEntries(Node<T> node, List<Entry<T>> entries, int countDeleted) {
        this.node = node;
        this.entries = entries;
        this.count = countDeleted;
    }

    public Node<T> getNode() {
        return node;
    }

    public List<Entry<T>> getEntriesToAdd() {
        return entries;
    }

    public int countDeleted() {
        return count;
    }

}
