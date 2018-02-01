package com.jamieswhiteshirt.rtree3i;

import java.util.List;
import java.util.function.Function;

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
     * @param keyAccessor
     *            function to access keys
     * @return two lists
     */
    <T> Groups<T> split(List<T> entries, int minSize, Function<T, Box> keyAccessor);
}
