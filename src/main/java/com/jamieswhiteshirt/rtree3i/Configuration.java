package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Preconditions;

/**
 * Configures an RTreeMultimap prior to instantiation of an {@link RTreeMap}.
 */
public final class Configuration {

    private final int maxChildren;
    private final int minChildren;
    private final Splitter splitter;
    private final Selector selector;

    /**
     * Constructor.
     * @param minChildren minimum number of children per node, at least 1
     * @param maxChildren maximum number of children per node, at least 3
     * @param selector algorithm to select search paths
     * @param splitter algorithm to split children across two new nodes
     */
    public Configuration(int minChildren, int maxChildren, Selector selector, Splitter splitter) {
        Preconditions.checkNotNull(splitter);
        Preconditions.checkNotNull(selector);
        Preconditions.checkArgument(maxChildren > 2);
        Preconditions.checkArgument(minChildren >= 1);
        Preconditions.checkArgument(minChildren < maxChildren);
        this.selector = selector;
        this.maxChildren = maxChildren;
        this.minChildren = minChildren;
        this.splitter = splitter;
    }

    /**
     * Returns the maximum number of children per node.
     * @return the maximum number of children per node
     */
    public int getMaxChildren() {
        return maxChildren;
    }

    /**
     * Returns the minimum number of children per node.
     * @return the minimum number of children per node
     */
    public int getMinChildren() {
        return minChildren;
    }

    /**
     * Returns the algorithm to split children across two new nodes.
     * @return the algorithm to split children across two new nodes
     */
    public Splitter getSplitter() {
        return splitter;
    }

    /**
     * Returns the algorithm to select search paths.
     * @return the algorithm to select search paths
     */
    public Selector getSelector() {
        return selector;
    }

}
