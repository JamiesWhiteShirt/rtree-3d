package com.jamieswhiteshirt.rtree3i;

import java.util.List;
import java.util.function.Function;

/**
 * A function that splits a list of items into groups.
 */
public interface Splitter {

    /**
     * Splits a list of items into two groups of at least minSize.
     * @param <T> entry type
     * @param entries list of items to split
     * @param minSize min size of each list
     * @param boxMapper box mapper applied to entries
     * @return groups
     */
    <T> Groups<T> split(List<T> entries, int minSize, Function<T, Box> boxMapper);
}
