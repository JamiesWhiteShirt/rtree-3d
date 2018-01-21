package com.github.davidmoten.rtree3d;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.Geometry;
import com.github.davidmoten.rtree3d.geometry.HasGeometry;
import com.github.davidmoten.rtree3d.geometry.ListPair;

/**
 * Utility functions asociated with {@link Comparator}s, especially for use with
 * {@link Selector}s and {@link Splitter}s.
 * 
 */
public final class Comparators {

    private Comparators() {
        // prevent instantiation
    }

    public static final Comparator<ListPair<?>> overlapListPairComparator = toComparator(Functions.overlapListPair);

    /**
     * Compares the sum of the areas of two ListPairs.
     */
    public static final Comparator<ListPair<?>> volumePairComparator = new Comparator<ListPair<?>>() {

        @Override
        public int compare(ListPair<?> p1, ListPair<?> p2) {
            return ((Float) p1.volumeSum()).compareTo(p2.volumeSum());
        }
    };

    /**
     * Returns a {@link Comparator} that is a normal Double comparator for the
     * total of the areas of overlap of the members of the list with the
     * rectangle r.
     * 
     * @param <T>
     *            type of geometry being compared
     * @param r
     *            rectangle
     * @param list
     *            geometries to compare with the rectangle
     * @return the total of the areas of overlap of the geometries in the list
     *         with the rectangle r
     */
    public static <T extends HasGeometry> Comparator<HasGeometry> overlapVolumeComparator(
            final Box r, final List<T> list) {
        return toComparator(Functions.overlapVolume(r, list));
    }

    public static <T extends HasGeometry> Comparator<HasGeometry> volumeIncreaseComparator(
            final Box r) {
        return toComparator(Functions.volumeIncrease(r));
    }

    public static Comparator<HasGeometry> volumeComparator(final Box r) {
        return (g1, g2) -> Float.compare(g1.geometry().mbb().add(r).volume(), g2.geometry().mbb()
                .add(r).volume());
    }

    public static <R, T extends Comparable<T>> Comparator<R> toComparator(final Function<R, T> function) {
        return Comparator.comparing(function);
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

    /**
     * <p>
     * Returns a comparator that can be used to sort entries returned by search
     * methods. For example:
     * </p>
     * <p>
     * <code>search(100).toSortedList(ascendingDistance(r))</code>
     * </p>
     * 
     * @param <T>
     *            the value type
     * @param <S>
     *            the entry type
     * @param r
     *            rectangle to measure distance to
     * @return a comparator to sort by ascending distance from the rectangle
     */
    public static <T, S extends Geometry> Comparator<Entry<T, S>> ascendingDistance(
            final Box r) {
        return Comparator.comparingDouble(e -> e.geometry().distance(r));
    }

}
