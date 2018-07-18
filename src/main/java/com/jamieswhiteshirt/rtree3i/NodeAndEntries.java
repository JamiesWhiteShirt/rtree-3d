package com.jamieswhiteshirt.rtree3i;

import java.util.List;

/**
 * Used for tracking deletions through recursive calls.
 * 
 * @param <K>
 *     entry key type
 * @param <V>
 *     entry value type
 */
final class NodeAndEntries<K, V> {

    private final Node<K, V> node;
    private final List<EntryBox<K, V>> entries;
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
    NodeAndEntries(Node<K, V> node, List<EntryBox<K, V>> entries, int countDeleted) {
        this.node = node;
        this.entries = entries;
        this.count = countDeleted;
    }

    public Node<K, V> getNode() {
        return node;
    }

    public List<EntryBox<K, V>> getEntriesToAdd() {
        return entries;
    }

    public int countDeleted() {
        return count;
    }

}
