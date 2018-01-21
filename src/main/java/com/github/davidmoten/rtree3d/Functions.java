package com.github.davidmoten.rtree3d;

import java.util.List;
import java.util.function.Function;

/**
 * Utility functions for making {@link Selector}s and {@link Splitter}s.
 *
 */
public final class Functions {

    private Functions() {
        // prevent instantiation
    }

    public static Function<Groups<?>, Integer> overlapListPair = pair -> pair.getGroup1().getBox().intersectionVolume(pair.getGroup2().getBox());

    public static Function<Box, Integer> overlapVolume(final Box r,
            final List<Box> list) {
        return g -> {
            Box gPlusR = g.add(r);
            int m = 0;
            for (Box other : list) {
                m += gPlusR.intersectionVolume(other);
            }
            return m;
        };
    }

    public static Function<Box, Integer> volumeIncrease(final Box r) {
        return g -> g.add(r).getVolume() - g.getVolume();
    }

}
