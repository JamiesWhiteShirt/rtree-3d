package com.github.davidmoten.rtree3d;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.Groups;

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
     * Compares the sum of the areas of two ListPairs.
     */
    public static final Comparator<Groups<?>> volumePairComparator = (p1, p2) -> Float.compare(p1.volumeSum(), p2.volumeSum());

    /**
     * Returns a {@link Comparator} that is a normal Double comparator for the
     * total of the areas of overlap of the members of the list with the
     * rectangle r.
     * 
     * @param r
     *            rectangle
     * @param list
     *            geometries to compare with the rectangle
     * @return the total of the areas of overlap of the geometries in the list
     *         with the rectangle r
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
        return (g1, g2) -> Float.compare(g1.getBox().add(r).volume(), g2.getBox().add(r).volume());
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
