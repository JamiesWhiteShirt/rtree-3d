package com.github.davidmoten.rtree3d;

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

    public static Comparator<Groups<?>> overlapListPairComparator = Comparator.comparing(Functions.overlapListPair);

    /**
     * Compares the sum of the volumes of two ListPairs.
     */
    public static final Comparator<Groups<?>> volumePairComparator = Comparator.comparingInt(Groups::getVolumeSum);

    /**
     * Returns a {@link Comparator} that is a normal Double comparator for the
     * total of the volumes of overlap of the members of the list with the
     * box r.
     * 
     * @param r
     *            box
     * @param list
     *            geometries to compare with the box
     * @return the total of the volumes of overlap of the geometries in the list
     *         with the box r
     */
    public static Comparator<HasBox> overlapVolumeComparator(
            final Box r, final List<? extends HasBox> list) {
        return Comparator.comparing(Functions.overlapVolume(r, list));
    }

    public static Comparator<HasBox> volumeIncreaseComparator(
            final Box r) {
        return Comparator.comparing(Functions.volumeIncrease(r));
    }

    public static Comparator<HasBox> volumeComparator(final Box r) {
        return Comparator.comparingInt(g -> g.getBox().add(r).getVolume());
    }

    public static <T> Comparator<T> compose(final Comparator<T>... comparators) {
        return (t1, t2) -> {
            for (Comparator<T> comparator : comparators) {
                int value = comparator.compare(t1, t2);
                if (value != 0)
                    return value;
            }
            return 0;
        };
    }
}
