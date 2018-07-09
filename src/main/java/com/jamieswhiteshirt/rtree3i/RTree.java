package com.jamieswhiteshirt.rtree3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Immutable in-memory 2D R-Tree with configurable splitter heuristic.
 *
 * @param <T>
 *            the entry value type
 */
public abstract class RTree<T> {

    protected final Node<T> root;
    protected final Configuration configuration;

    RTree(Node<T> root, Configuration configuration) {
        this.root = root;
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
     * Returns the always true predicate. See {@link RTreeMap#getEntries()} for
     * example use.
     */
    private static final Predicate<Box> ALWAYS_TRUE = box -> true;

    public Collection<Entry<T>> search(Box box) {
        return search(intersects(box));
    }

    public Collection<Entry<T>> search(Predicate<Box> condition) {
        List<Entry<T>> entries = new ArrayList<>();
        forEach(condition, entries::add);
        return entries;
    }

    public void forEach(Predicate<Box> condition, Consumer<? super Entry<T>> consumer) {
        if (root != null) {
            root.forEach(condition, consumer);
        }
    }

    public boolean any(Predicate<Box> condition, Predicate<? super Entry<T>> test) {
        return root != null && root.any(condition, test);
    }

    public boolean all(Predicate<Box> condition, Predicate<? super Entry<T>> test) {
        return root == null || root.all(condition, test);
    }

    public Collection<Entry<T>> getEntries() {
        return search(ALWAYS_TRUE);
    }

    public boolean contains(Entry<T> entry) {
        return root != null && root.contains(entry);
    }

    /**
     * If the RTreeMap has no entries returns null, otherwise
     * returns the minimum bounding box of all entries in the RTreeMap.
     *
     * @return minimum bounding box of all entries in RTreeMap
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
        return root == null;
    }

    /**
     * Returns the number of entries in the RTreeMap.
     *
     * @return the number of entries
     */
    public int size() {
        return root != null ? root.size() : 0;
    }

    /**
     * Returns a {@link Configuration} containing the configuration of the RTreeMap at
     * the time of instantiation.
     *
     * @return the configuration of the RTreeMap prior to instantiation
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        return root != null ? root.toString() : "";
    }
}
