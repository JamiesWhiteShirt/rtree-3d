package com.jamieswhiteshirt.rtree3i;

/**
 * A mutable configuration builder for {@link Configuration}.
 */
public final class ConfigurationBuilder {
    /**
     * Benchmarks show that this is a good choice for up to O(10,000) entries
     * when using Quadratic splitter (Guttman).
     */
    private static final int MAX_CHILDREN_DEFAULT_GUTTMAN = 4;

    /**
     * Benchmarks show that this is the sweet spot for up to O(10,000) entries
     * when using R*-tree heuristics.
     */
    private static final int MAX_CHILDREN_DEFAULT_STAR = 4;
    /**
     * According to
     * http://dbs.mathematik.uni-marburg.de/publications/myPapers
     * /1990/BKSS90.pdf (R*-tree paper), best filling ratio is 0.4 for both
     * quadratic split and R*-tree split.
     */
    private static final double DEFAULT_FILLING_FACTOR = 0.4;
    private Integer maxChildren = null;
    private Integer minChildren = null;
    private Splitter splitter = new QuadraticSplitter();
    private Selector selector = new MinimalVolumeIncreaseSelector();
    private boolean star = false;

    /**
     * Constructs a configuration builder.
     */
    public ConfigurationBuilder() {
    }

    /**
     * Sets the minimum number of children in a node. When the number of children in a node drops below this number, the
     * node is deleted and the children are added on to the R-tree again.
     * @param minChildren the minimum number of children in a node
     * @return builder
     */
    public ConfigurationBuilder minChildren(int minChildren) {
        this.minChildren = minChildren;
        return this;
    }

    /**
     * Sets the maximum number of children in a node.
     * @param maxChildren the maximum number of children in a node
     * @return builder
     */
    public ConfigurationBuilder maxChildren(int maxChildren) {
        this.maxChildren = maxChildren;
        return this;
    }

    /**
     * Sets the splitter to use when the number of children in a node exceeds the maximum number of children.
     * @param splitter the node splitting algorithm
     * @return builder
     */
    public ConfigurationBuilder splitter(Splitter splitter) {
        this.splitter = splitter;
        return this;
    }

    /**
     * Sets the selector which decides which branches to follow when inserting or searching.
     * @param selector selects the branch to follow when inserting or searching
     * @return builder
     */
    public ConfigurationBuilder selector(Selector selector) {
        this.selector = selector;
        return this;
    }

    /**
     * Sets the splitter to {@link RStarSplitter} and selector to {@link RStarSelector} and defaults to minChildren=10.
     * @return builder
     */
    public ConfigurationBuilder star() {
        selector = new RStarSelector();
        splitter = new RStarSplitter();
        star = true;
        return this;
    }

    /**
     * Builds the {@link Configuration}.
     * @return the {@link Configuration}
     */
    public Configuration build() {
        if (maxChildren == null)
            maxChildren = star ? MAX_CHILDREN_DEFAULT_STAR : MAX_CHILDREN_DEFAULT_GUTTMAN;
        if (minChildren == null)
            minChildren = (int) Math.round(maxChildren * DEFAULT_FILLING_FACTOR);
        return new Configuration(minChildren, maxChildren, selector, splitter);
    }

}
