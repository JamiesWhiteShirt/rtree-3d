package com.github.davidmoten.rtree3d;

public class ConfigurationBuilder {
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
    private Splitter splitter = new SplitterQuadratic();
    private Selector selector = new SelectorMinimalVolumeIncrease();
    private boolean star = false;

    public ConfigurationBuilder() {
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
    public ConfigurationBuilder minChildren(int minChildren) {
        this.minChildren = minChildren;
        return this;
    }

    /**
     * Sets the max number of children in an R-tree node.
     *
     * @param maxChildren
     *            max number of children in R-tree node.
     * @return builder
     */
    public ConfigurationBuilder maxChildren(int maxChildren) {
        this.maxChildren = maxChildren;
        return this;
    }

    /**
     * Sets the {@link Splitter} to use when maxChildren is reached.
     *
     * @param splitter
     *            node splitting method to use
     * @return builder
     */
    public ConfigurationBuilder splitter(Splitter splitter) {
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
    public ConfigurationBuilder selector(Selector selector) {
        this.selector = selector;
        return this;
    }

    /**
     * Sets the splitter to {@link SplitterRStar} and selector to
     * {@link SelectorRStar} and defaults to minChildren=10.
     *
     * @return builder
     */
    public ConfigurationBuilder star() {
        selector = new SelectorRStar();
        splitter = new SplitterRStar();
        star = true;
        return this;
    }

    /**
     * Builds the {@link Configuration}.
     *
     * @return Configuration
     */
    public Configuration build() {
        if (maxChildren == null)
            maxChildren = star ? MAX_CHILDREN_DEFAULT_STAR : MAX_CHILDREN_DEFAULT_GUTTMAN;
        if (minChildren == null)
            minChildren = (int) Math.round(maxChildren * DEFAULT_FILLING_FACTOR);
        return new Configuration(minChildren, maxChildren, selector, splitter);
    }

}
