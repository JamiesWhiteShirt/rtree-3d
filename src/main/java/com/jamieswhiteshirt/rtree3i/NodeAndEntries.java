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
    private final List<Bucket<K, V>> buckets;
    private final int count;

    /**
     * Constructor.
     *
     * @param node
     *            absent = whole node was deleted present = either an unchanged
     *            node because of no removal or the newly created node without
     *            the deleted entry
     * @param buckets
     *            from nodes that dropped below minChildren in size and thus
     *            their entries are to be redistributed (readded to the tree)
     * @param countDeleted
     *            count of the number of entries removed
     */
    NodeAndEntries(Node<K, V> node, List<Bucket<K, V>> buckets, int countDeleted) {
        this.node = node;
        this.buckets = buckets;
        this.count = countDeleted;
    }

    public Node<K, V> getNode() {
        return node;
    }

    public List<Bucket<K, V>> getEntriesToAdd() {
        return buckets;
    }

    public int countDeleted() {
        return count;
    }

}
