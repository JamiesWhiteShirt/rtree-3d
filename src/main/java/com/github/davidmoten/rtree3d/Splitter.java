package com.github.davidmoten.rtree3d;

import java.util.List;

public interface Splitter {

    /**
     * Splits a list of items into two lists of at least minSize.
     * 
     * @param <T>
     *            entry type
     * @param entries
     *            list of items to split
     * @param minSize
     *            min size of each list
     * @return two lists
     */
    <T extends HasBox> Groups<T> split(List<T> entries, int minSize);
}
