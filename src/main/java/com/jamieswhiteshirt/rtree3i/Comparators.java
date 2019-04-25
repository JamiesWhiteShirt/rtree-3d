package com.jamieswhiteshirt.rtree3i;

import java.util.Comparator;
import java.util.List;

/**
 * Utility functions asociated with {@link Comparator}s, especially for use with
 * {@link Selector}s and {@link Splitter}s.
 * 
 */
public final class Comparators {

    private Comparators() {
        // prevent instantiation
    }

    /**
     * A {@link Comparator} comparing the volume of the intersection between groups.
     */
    public static Comparator<Groups<?>> groupsVolumeComparator = Comparator.comparing(Functions.groupsIntersectionVolume);

    /**
     * A {@link Comparator} comparing the sum of the volumes of groups.
     */
    public static final Comparator<Groups<?>> groupsIntersectionVolumeComparator = Comparator.comparingInt(Groups::getVolumeSum);

    /**
     * Returns a {@link Comparator} comparing the sum of overlap volumes between the list of boxes and the minimum
     * bounding box containing both r and the box.
     * @param r box used to make a minimum bounding box with comparator arguments
     * @param list list of boxes for overlap volumes
     * @return a {@link Comparator} comparing the sum of overlap volumes between the list of boxes and the minimum
     * bounding box containing both r and the comparator argument
     */
    public static Comparator<Box> overlapVolumeComparator(final Box r, final List<Box> list) {
        return Comparator.comparing(Functions.overlapVolume(r, list));
    }

    /**
     * Returns a {@link Comparator} comparing the increase in volume between r and the minimum bounding box containing
     * both r and the box.
     * @param r box with the minimum volume used to make a minimum bounding box with comparator arguments
     * @return a {@link Comparator} comparing the increase in volume between r and the minimum bounding box containing
     * both r and the comparator argument
     */
    public static Comparator<Box> volumeIncreaseComparator(final Box r) {
        return Comparator.comparing(Functions.volumeIncrease(r));
    }

    /**
     * Returns a {@link Comparator} comparing the volume of the minimum bounding box containing both r and the box.
     * @param r box with the mimimum volume
     * @return a {@link Comparator} comparing the volume of the minimum bounding box containing both r and the box
     */
    public static Comparator<Box> volumeComparator(final Box r) {
        return Comparator.comparingInt(g -> g.add(r).getVolume());
    }
}
