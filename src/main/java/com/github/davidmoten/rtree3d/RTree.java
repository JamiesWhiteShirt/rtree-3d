package com.github.davidmoten.rtree3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.google.common.collect.Lists;

/**
 * Immutable in-memory 2D R-Tree with configurable splitter heuristic.
 * 
 * @param <T>
 *            the entry value type
 */
public final class RTree<T> {

    private final Node<T> root;
    private final Context context;

    /**
     * Benchmarks show that this is a good choice for up to O(10,000) entries
     * when using Quadratic splitter (Guttman).
     */
    public static final int MAX_CHILDREN_DEFAULT_GUTTMAN = 4;

    /**
     * Benchmarks show that this is the sweet spot for up to O(10,000) entries
     * when using R*-tree heuristics.
     */
    public static final int MAX_CHILDREN_DEFAULT_STAR = 4;

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
     * @param context
     *            options for the R-tree
     */
    private RTree(Node<T> root, int size, Context context) {
        this.root = root;
        this.size = size;
        this.context = context;
    }

    /**
     * Constructor.
     * 
     * @param context
     *            specifies parameters and behaviour for the R-tree
     */
    private RTree(Context context) {
        this(null, 0, context);
    }

    /**
     * Returns a new Builder instance for {@link RTree}. Defaults to
     * maxChildren=128, minChildren=64, splitter=QuadraticSplitter.
     * 
     * @param <T>
     *            the value type of the entries in the tree
     * @return a new RTree instance
     */
    public static <T> RTree<T> create() {
        return new Builder().create();
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

    /**
     * When the number of children in an R-tree node drops below this number the
     * node is deleted and the children are added on to the R-tree again.
     * 
     * @param minChildren
     *            less than this number of children in a node triggers a node
     *            deletion and redistribution of its members
     * @return builder
     */
    public static Builder minChildren(int minChildren) {
        return new Builder().minChildren(minChildren);
    }

    /**
     * Sets the max number of children in an R-tree node.
     * 
     * @param maxChildren
     *            max number of children in an R-tree node
     * @return builder
     */
    public static Builder maxChildren(int maxChildren) {
        return new Builder().maxChildren(maxChildren);
    }

    /**
     * Sets the {@link Splitter} to use when maxChildren is reached.
     * 
     * @param splitter
     *            the splitter algorithm to use
     * @return builder
     */
    public static Builder splitter(Splitter splitter) {
        return new Builder().splitter(splitter);
    }

    /**
     * Sets the node {@link Selector} which decides which branches to follow
     * when inserting or searching.
     * 
     * @param selector
     *            determines which branches to follow when inserting or
     *            searching
     * @return builder
     */
    public static Builder selector(Selector selector) {
        return new Builder().selector(selector);
    }

    /**
     * Sets the splitter to {@link SplitterRStar} and selector to
     * {@link SelectorRStar} and defaults to minChildren=10.
     * 
     * @return builder
     */
    public static Builder star() {
        return new Builder().star();
    }

    /**
     * RTree Builder.
     */
    public static class Builder {

        /**
         * According to
         * http://dbs.mathematik.uni-marburg.de/publications/myPapers
         * /1990/BKSS90.pdf (R*-tree paper), best filling ratio is 0.4 for both
         * quadratic split and R*-tree split.
         */
        private static final double DEFAULT_FILLING_FACTOR = 0.4;
        private Optional<Integer> maxChildren = Optional.empty();
        private Optional<Integer> minChildren = Optional.empty();
        private Splitter splitter = new SplitterQuadratic();
        private Selector selector = new SelectorMinimalVolumeIncrease();
        private boolean star = false;

        private Builder() {
        }

        /**
         * When the number of children in an R-tree node drops below this number
         * the node is deleted and the children are added on to the R-tree
         * again.
         * 
         * @param minChildren
         *            less than this number of children in a node triggers a
         *            redistribution of its children.
         * @return builder
         */
        public Builder minChildren(int minChildren) {
            this.minChildren = Optional.of(minChildren);
            return this;
        }

        /**
         * Sets the max number of children in an R-tree node.
         * 
         * @param maxChildren
         *            max number of children in R-tree node.
         * @return builder
         */
        public Builder maxChildren(int maxChildren) {
            this.maxChildren = Optional.of(maxChildren);
            return this;
        }

        /**
         * Sets the {@link Splitter} to use when maxChildren is reached.
         * 
         * @param splitter
         *            node splitting method to use
         * @return builder
         */
        public Builder splitter(Splitter splitter) {
            this.splitter = splitter;
            return this;
        }

        /**
         * Sets the node {@link Selector} which decides which branches to follow
         * when inserting or searching.
         * 
         * @param selector
         *            selects the branch to follow when inserting or searching
         * @return builder
         */
        public Builder selector(Selector selector) {
            this.selector = selector;
            return this;
        }

        /**
         * Sets the splitter to {@link SplitterRStar} and selector to
         * {@link SelectorRStar} and defaults to minChildren=10.
         * 
         * @return builder
         */
        public Builder star() {
            selector = new SelectorRStar();
            splitter = new SplitterRStar();
            star = true;
            return this;
        }

        /**
         * Builds the {@link RTree}.
         * 
         * @param <T>
         *            value type
         * @return RTree
         */
        public <T> RTree<T> create() {
            if (!maxChildren.isPresent())
                if (star)
                    maxChildren = Optional.of(MAX_CHILDREN_DEFAULT_STAR);
                else
                    maxChildren = Optional.of(MAX_CHILDREN_DEFAULT_GUTTMAN);
            if (!minChildren.isPresent())
                minChildren = Optional.of((int) Math.round(maxChildren.get() * DEFAULT_FILLING_FACTOR));
            return new RTree<>(
                    new Context(minChildren.get(), maxChildren.get(), selector, splitter));
        }

    }

    public static <T> RTree<T> create(Node<T> node, Context context) {
        return new RTree<>(node, node != null ? node.countEntries() : 0, context);
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
            List<Node<T>> nodes = root.add(entry, context);
            Node<T> node;
            if (nodes.size() == 1)
                node = nodes.get(0);
            else {
                node = new Branch<>(nodes);
            }
            return new RTree<>(node, size + 1, context);
        } else
            return new RTree<>(new Leaf<>(Lists.newArrayList((Entry<T>) entry)),
                    size + 1, context);
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
            NodeAndEntries<T> nodeAndEntries = root.delete(entry, all, context);
            if (nodeAndEntries.getNode() == root)
                return this;
            else
                return new RTree<>(nodeAndEntries.getNode(),
                        size - nodeAndEntries.countDeleted() - nodeAndEntries.getEntriesToAdd().size(),
                        context).add(nodeAndEntries.getEntriesToAdd());
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
     * intersects with a given rectangle.
     * 
     * @param r
     *            the rectangle to check intersection with
     * @return whether the geometry and the rectangle intersect
     */
    public static Predicate<Box> intersects(final Box r) {
        return g -> g.intersects(r);
    }

    /**
     * Returns the always true predicate. See {@link RTree#getEntries()} for
     * example use.
     */
    private static final Predicate<Box> ALWAYS_TRUE = rectangle -> true;

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
     * returns the minimum bounding rectangle of all entries in the RTree.
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
     * Returns a {@link Context} containing the configuration of the RTree at
     * the time of instantiation.
     * 
     * @return the configuration of the RTree prior to instantiation
     */
    public Context getContext() {
        return context;
    }

    /**
     * Returns a human readable form of the RTree. Here's an example:
     * 
     * <pre>
     * mbb=Rectangle [x1=10.0, y1=4.0, x2=62.0, y2=85.0]
     *   mbb=Rectangle [x1=28.0, y1=4.0, x2=34.0, y2=85.0]
     *     entry=Entry [value=2, geometry=Point [x=29.0, y=4.0]]
     *     entry=Entry [value=1, geometry=Point [x=28.0, y=19.0]]
     *     entry=Entry [value=4, geometry=Point [x=34.0, y=85.0]]
     *   mbb=Rectangle [x1=10.0, y1=45.0, x2=62.0, y2=63.0]
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
