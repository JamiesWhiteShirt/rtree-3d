package com.github.davidmoten.rtree3d;

import com.google.common.base.Preconditions;

/**
 * Configures an RTree prior to instantiation of an {@link RTree}.
 */
public final class Context {

    private final int maxChildren;
    private final int minChildren;
    private final Splitter splitter;
    private final Selector selector;

    /**
     * Constructor.
     * 
     * @param minChildren
     *            minimum number of children per node (at least 1)
     * @param maxChildren
     *            max number of children per node (minimum is 3)
     * @param selector
     *            algorithm to select search path
     * @param splitter
     *            algorithm to split the children across two new nodes
     */
    public Context(int minChildren, int maxChildren, Selector selector, Splitter splitter) {
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

    public int getMaxChildren() {
        return maxChildren;
    }

    public int getMinChildren() {
        return minChildren;
    }

    public Splitter getSplitter() {
        return splitter;
    }

    public Selector getSelector() {
        return selector;
    }

}
