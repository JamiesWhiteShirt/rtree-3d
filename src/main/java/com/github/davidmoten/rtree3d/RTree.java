package com.github.davidmoten.rtree3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

/**
 * Immutable in-memory 2D R-Tree with configurable splitter heuristic.
 * 
 * @param <T>
 *            the entry value type
 */
public final class RTree<T> {

    private final Node<T> root;
    private final Configuration configuration;

    /**
     * Current size in Entries of the RTree.
     */
    private final int size;

    /**
     * Constructor.
     * 
     * @param root
     *            the root node of the tree if present
     * @param size
     *            known size of the tree
     * @param configuration
     *            options for the R-tree
     */
    private RTree(Node<T> root, int size, Configuration configuration) {
        this.root = root;
        this.size = size;
        this.configuration = configuration;
    }

    /**
     * The tree is scanned for depth and the depth returned. This involves
     * recursing down to the leaf level of the tree to get the current depth.
     * Should be <code>log(n)</code> in complexity.
     * 
     * @return depth of the R-tree
     */
    public int calculateDepth() {
        return root != null ? root.calculateDepth() : 0;
    }

    public int countEntries() {
        return root != null ? root.countEntries() : 0;
    }

    public static <T> RTree<T> create(Node<T> node, Configuration configuration) {
        return new RTree<>(node, node != null ? node.countEntries() : 0, configuration);
    }

    public static <T> RTree<T> create(Configuration configuration) {
        return create(null, configuration);
    }

    /**
     * Returns an immutable copy of the RTree with the addition of given entry.
     * 
     * @param entry
     *            item to add to the R-tree.
     * @return a new immutable R-tree including the new entry
     */
    @SuppressWarnings("unchecked")
    public RTree<T> add(Entry<? extends T> entry) {
        if (root != null) {
            List<Node<T>> nodes = root.add(entry, configuration);
            Node<T> node;
            if (nodes.size() == 1)
                node = nodes.get(0);
            else {
                node = new Branch<>(nodes);
            }
            return new RTree<>(node, size + 1, configuration);
        } else
            return new RTree<>(new Leaf<>(Lists.newArrayList((Entry<T>) entry)),
                    size + 1, configuration);
    }

    /**
     * Returns an immutable copy of the RTree with the addition of an entry
     * comprised of the given value and Geometry.
     * 
     * @param value
     *            the value of the {@link Entry} to be added
     * @param box
     *            the box of the {@link Entry} to be added
     * @return a new immutable R-tree including the new entry
     */
    public RTree<T> add(T value, Box box) {
        return add(Entry.entry(value, box));
    }

    /**
     * Returns an immutable RTree with the current entries and the additional
     * entries supplied as a parameter.
     * 
     * @param entries
     *            entries to add
     * @return R-tree with entries added
     */
    public RTree<T> add(Iterable<Entry<T>> entries) {
        RTree<T> tree = this;
        for (Entry<T> entry : entries)
            tree = tree.add(entry);
        return tree;
    }

    /**
     * Returns a new R-tree with the given entries deleted. If <code>all</code>
     * is false deletes only one if exists. If <code>all</code> is true deletes
     * all matching entries.
     * 
     * @param entries
     *            entries to delete
     * @param all
     *            if false deletes one if exists else deletes all
     * @return R-tree with entries deleted
     */
    public RTree<T> delete(Iterable<Entry<T>> entries, boolean all) {
        RTree<T> tree = this;
        for (Entry<T> entry : entries)
            tree = tree.delete(entry, all);
        return tree;
    }

    /**
     * Returns a new R-tree with the given entries deleted but only one matching
     * occurence of each entry is deleted.
     * 
     * @param entries
     *            entries to delete
     * @return R-tree with entries deleted up to one matching occurence per
     *         entry
     */
    public RTree<T> delete(Iterable<Entry<T>> entries) {
        RTree<T> tree = this;
        for (Entry<T> entry : entries)
            tree = tree.delete(entry);
        return tree;
    }

    /**
     * If <code>all</code> is false deletes one entry matching the given value
     * and Geometry. If <code>all</code> is true deletes all entries matching
     * the given value and geometry. This method has no effect if the entry is
     * not present. The entry must match on both value and geometry to be
     * deleted.
     * 
     * @param value
     *            the value of the {@link Entry} to be deleted
     * @param box
     *            the geometry of the {@link Entry} to be deleted
     * @param all
     *            if false deletes one if exists else deletes all
     * @return a new immutable R-tree without one or many instances of the
     *         specified entry if it exists otherwise returns the original RTree
     *         object
     */
    public RTree<T> delete(T value, Box box, boolean all) {
        return delete(Entry.entry(value, box), all);
    }

    /**
     * Deletes maximum one entry matching the given value and geometry. This
     * method has no effect if the entry is not present. The entry must match on
     * both value and geometry to be deleted.
     * 
     * @param value
     *            the value to be matched for deletion
     * @param box
     *            the geometry to be matched for deletion
     * @return an immutable RTree without one entry (if found) matching the
     *         given value and geometry
     */
    public RTree<T> delete(T value, Box box) {
        return delete(Entry.entry(value, box), false);
    }

    /**
     * Deletes one or all matching entries depending on the value of
     * <code>all</code>. If multiple copies of the entry are in the R-tree only
     * one will be deleted if all is false otherwise all matching entries will
     * be deleted. The entry must match on both value and geometry to be
     * deleted.
     * 
     * @param entry
     *            the {@link Entry} to be deleted
     * @param all
     *            if true deletes all matches otherwise deletes first found
     * @return a new immutable R-tree without one instance of the specified
     *         entry
     */
    public RTree<T> delete(Entry<? extends T> entry, boolean all) {
        if (root != null) {
            NodeAndEntries<T> nodeAndEntries = root.delete(entry, all, configuration);
            if (nodeAndEntries.getNode() == root)
                return this;
            else
                return new RTree<>(nodeAndEntries.getNode(),
                        size - nodeAndEntries.countDeleted() - nodeAndEntries.getEntriesToAdd().size(),
                        configuration).add(nodeAndEntries.getEntriesToAdd());
        } else
            return this;
    }

    /**
     * Deletes one entry if it exists, returning an immutable copy of the RTree
     * without that entry. If multiple copies of the entry are in the R-tree
     * only one will be deleted. The entry must match on both value and geometry
     * to be deleted.
     * 
     * @param entry
     *            the {@link Entry} to be deleted
     * @return a new immutable R-tree without one instance of the specified
     *         entry
     */
    public RTree<T> delete(Entry<? extends T> entry) {
        return delete(entry, false);
    }

    /**
     * Returns a predicate function that indicates if {@link Box}
     * intersects with a given box.
     * 
     * @param r
     *            the box to check intersection with
     * @return whether the geometry and the box intersect
     */
    public static Predicate<Box> intersects(final Box r) {
        return g -> g.intersects(r);
    }

    /**
     * Returns the always true predicate. See {@link RTree#getEntries()} for
     * example use.
     */
    private static final Predicate<Box> ALWAYS_TRUE = box -> true;

    public List<Entry<T>> search(Box box) {
        return search(intersects(box));
    }

    public List<Entry<T>> search(Predicate<Box> condition) {
        List<Entry<T>> entries = new ArrayList<>();
        if (root != null) {
            root.search(condition, entries::add);
        }
        return entries;
    }

    public List<Entry<T>> getEntries() {
        return search(ALWAYS_TRUE);
    }

    Node<T> getRoot() {
        return root;
    }

    /**
     * If the RTree has no entries returns {@link Optional#empty()} otherwise
     * returns the minimum bounding box of all entries in the RTree.
     * 
     * @return minimum bounding box of all entries in RTree
     */
    public Box getMbb() {
        return root != null ? root.getBox() : null;
    }

    /**
     * Returns true if and only if the R-tree is empty of entries.
     * 
     * @return is R-tree empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of entries in the RTree.
     * 
     * @return the number of entries
     */
    public int size() {
        return size;
    }

    /**
     * Returns a {@link Configuration} containing the configuration of the RTree at
     * the time of instantiation.
     * 
     * @return the configuration of the RTree prior to instantiation
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Returns a human readable form of the RTree. Here's an example:
     * 
     * <pre>
     * mbb=Box [x1=10.0, y1=4.0, x2=62.0, y2=85.0]
     *   mbb=Box [x1=28.0, y1=4.0, x2=34.0, y2=85.0]
     *     entry=Entry [value=2, geometry=Point [x=29.0, y=4.0]]
     *     entry=Entry [value=1, geometry=Point [x=28.0, y=19.0]]
     *     entry=Entry [value=4, geometry=Point [x=34.0, y=85.0]]
     *   mbb=Box [x1=10.0, y1=45.0, x2=62.0, y2=63.0]
     *     entry=Entry [value=5, geometry=Point [x=62.0, y=45.0]]
     *     entry=Entry [value=3, geometry=Point [x=10.0, y=63.0]]
     * </pre>
     * 
     * @return a string representation of the RTree
     */
    public String asString() {
        return asString(Integer.MAX_VALUE);
    }

    public String asString(int maxDepth) {
        return root != null ? asString(root, "", 1, maxDepth) : "";
    }

    private String asString(Node<T> node, String margin, int depth, int maxDepth) {
        if (depth > maxDepth)
            return "";
        final String marginIncrement = "  ";
        StringBuilder s = new StringBuilder();
        if (node instanceof Branch) {
            s.append(margin);
            s.append("mbb=");
            s.append(node.getBox());
            s.append('\n');
            Branch<T> n = (Branch<T>) node;
            for (Node<T> child : n.children()) {
                s.append(asString(child, margin + marginIncrement, depth + 1, maxDepth));
            }
        } else {
            Leaf<T> leaf = (Leaf<T>) node;
            s.append(margin);
            s.append("mbb=");
            s.append(leaf.getBox());
            s.append('\n');
            for (Entry<T> entry : leaf.getEntries()) {
                s.append(margin);
                s.append(marginIncrement);
                s.append("entry=");
                s.append(entry);
                s.append('\n');
            }
        }
        return s.toString();
    }

}
