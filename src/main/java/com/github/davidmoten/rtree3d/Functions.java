package com.github.davidmoten.rtree3d;

import java.util.List;
import java.util.function.Function;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.HasGeometry;
import com.github.davidmoten.rtree3d.geometry.ListPair;

/**
 * Utility functions for making {@link Selector}s and {@link Splitter}s.
 *
 */
public final class Functions {

    private Functions() {
        // prevent instantiation
    }

    public static final Function<ListPair<? extends HasGeometry>, Double> overlapListPair = pair -> (double) pair.group1().geometry().mbb()
            .intersectionVolume(pair.group2().geometry().mbb());

    public static Function<HasGeometry, Double> overlapVolume(final Box r,
            final List<? extends HasGeometry> list) {
        return g -> {
            Box gPlusR = g.geometry().mbb().add(r);
            double m = 0;
            for (HasGeometry other : list) {
                if (other != g) {
                    m += gPlusR.intersectionVolume(other.geometry().mbb());
                }
            }
            return m;
        };
    }

    public static Function<HasGeometry, Double> volumeIncrease(final Box r) {
        return g -> {
            Box gPlusR = g.geometry().mbb().add(r);
            return (double) (gPlusR.volume() - g.geometry().mbb().volume());
        };
    }

}
