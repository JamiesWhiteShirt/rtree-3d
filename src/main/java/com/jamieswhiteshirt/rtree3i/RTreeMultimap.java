package com.jamieswhiteshirt.rtree3i;

import java.util.Collections;
import java.util.List;

/**
 * Immutable set multimap of Box -> T in an R-Tree.
 * 
 * @param <T>
 *            the entry value type
 */
public final class RTreeMultimap<T> extends RTree<T> {

    /**
     * Create a new RTreeMultimap with the configuration.
     *
     * @param configuration
     *            options for the R-tree
     */
    public static <T> RTreeMultimap<T> create(Configuration configuration) {
        return new RTreeMultimap<>(null, configuration);
    }

    private RTreeMultimap(Node<T> root, Configuration configuration) {
        super(root, configuration);
    }

    /**
     * Returns an immutable copy of the RTreeMultimap with the addition of given entry.
     * 
     * @param entry
     *            item to add to the R-tree.
     * @return a new immutable R-tree including the new entry
     */
    @SuppressWarnings("unchecked")
    public RTreeMultimap<T> put(Entry<T> entry) {
        if (root != null) {
            List<Node<T>> nodes = root.multimapPut(entry, configuration);
            Node<T> node;
            if (nodes.size() == 1)
                node = nodes.get(0);
            else {
                node = Branch.containing(nodes);
            }
            return new RTreeMultimap<>(node, configuration);
        } else {
            return new RTreeMultimap<>(Leaf.containing(Collections.singletonList(entry)), configuration);
        }
    }

    /**
     * Returns an immutable copy of the RTreeMultimap with the addition of an entry
     * comprised of the given value and Geometry.
     * 
     * @param box
     *            the box of the {@link Entry} to be added
     * @param value
     *            the value of the {@link Entry} to be added
     * @return a new immutable R-tree including the new entry
     */
    public RTreeMultimap<T> put(Box box, T value) {
        return put(Entry.entry(value, box));
    }

    /**
     * Returns an immutable RTreeMultimap with the current entries and the additional
     * entries supplied as a parameter.
     * 
     * @param entries
     *            entries to add
     * @return R-tree with entries added
     */
    public RTreeMultimap<T> putAll(Iterable<Entry<T>> entries) {
        RTreeMultimap<T> tree = this;
        for (Entry<T> entry : entries) {
            tree = tree.put(entry);
        }
        return tree;
    }

    /**
     * Returns a new R-tree with the given entries deleted but only one matching
     * occurence of each entry is deleted.
     * 
     * @param entries
     *            entries to remove
     * @return R-tree with entries deleted up to one matching occurence per
     *         entry
     */
    public RTreeMultimap<T> removeAll(Iterable<Entry<T>> entries) {
        RTreeMultimap<T> tree = this;
        for (Entry<T> entry : entries) {
            tree = tree.remove(entry);
        }
        return tree;
    }

    /**
     * If <code>all</code> is false deletes one entry matching the given value
     * and Geometry. If <code>all</code> is true deletes all entries matching
     * the given value and geometry. This method has no effect if the entry is
     * not present. The entry must match on both value and geometry to be
     * deleted.
     * 
     * @param box
     *            the geometry of the {@link Entry} to be deleted
     * @param value
     *            the value of the {@link Entry} to be deleted
     * @return a new immutable R-tree without one or many instances of the
     *         specified entry if it exists otherwise returns the original RTreeMultimap
     *         object
     */
    public RTreeMultimap<T> remove(Box box, T value) {
        return remove(Entry.entry(value, box));
    }

    /**
     * Deletes one entry if it exists, returning an immutable copy of the RTreeMultimap
     * without that entry. If multiple copies of the entry are in the R-tree
     * only one will be deleted. The entry must match on both value and geometry
     * to be deleted.
     * 
     * @param entry
     *            the {@link Entry} to be deleted
     * @return a new immutable R-tree without one instance of the specified
     *         entry
     */
    public RTreeMultimap<T> remove(Entry<T> entry) {
        if (root != null) {
            NodeAndEntries<T> nodeAndEntries = root.remove(entry, configuration);
            if (nodeAndEntries.getNode() == root)
                return this;
            else
                return new RTreeMultimap<>(nodeAndEntries.getNode(), configuration).putAll(nodeAndEntries.getEntriesToAdd());
        } else
            return this;
    }

}
