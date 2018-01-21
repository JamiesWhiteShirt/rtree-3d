package com.github.davidmoten.rtree3d;

import java.util.List;
import java.util.function.Function;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.Groups;

/**
 * Utility functions for making {@link Selector}s and {@link Splitter}s.
 *
 */
public final class Functions {

    private Functions() {
        // prevent instantiation
    }

    public static Function<Groups<?>, Double> overlapListPair = pair -> (double) pair.getGroup1().getBox().intersectionVolume(pair.getGroup2().getBox());

    public static Function<HasBox, Double> overlapVolume(final Box r,
            final List<? extends HasBox> list) {
        return g -> {
            Box gPlusR = g.getBox().add(r);
            double m = 0;
            for (HasBox other : list) {
                if (other != g) {
                    m += gPlusR.intersectionVolume(other.getBox());
                }
            }
            return m;
        };
    }

    public static <T extends HasBox> Function<T, Double> volumeIncrease(final Box r) {
        return g -> (double) (g.getBox().add(r).volume() - g.getBox().volume());
    }

}
