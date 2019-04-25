package com.jamieswhiteshirt.rtree3i;

import java.util.List;
import java.util.function.Function;

/**
 * Utility functions for making {@link Selector}s and {@link Splitter}s.
 */
public final class Functions {

    private Functions() {
        // prevent instantiation
    }

    /**
     * A {@link Function} returning the volume of the intersection between groups.
     */
    public static Function<Groups<?>, Integer> groupsIntersectionVolume = pair -> pair.getGroup1().getBox().intersectionVolume(pair.getGroup2().getBox());

    /**
     * Returns a {@link Function} returning the sum of overlap volumes between the list of boxes and the minimum
     * bounding box containing both r and the box.
     * @param r box used to make a minimum bounding box with comparator arguments
     * @param list list of boxes for overlap volumes
     * @return a {@link Function} returning the sum of overlap volumes between the list of boxes and the minimum
     * bounding box containing both r and the comparator argument
     */
    public static Function<Box, Integer> overlapVolume(final Box r, final List<Box> list) {
        return g -> {
            Box gPlusR = g.add(r);
            int m = 0;
            for (Box other : list) {
                m += gPlusR.intersectionVolume(other);
            }
            return m;
        };
    }

    /**
     * Returns a {@link Function} returning the volume of the minimum bounding box containing both r and the box.
     * @param r box with the mimimum volume
     * @return a {@link Function} returning the volume of the minimum bounding box containing both r and the box
     */
    public static Function<Box, Integer> volumeIncrease(final Box r) {
        return g -> g.add(r).getVolume() - g.getVolume();
    }

}
