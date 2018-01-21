package com.github.davidmoten.rtree3d;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.util.Pair;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public final class SplitterQuadratic implements Splitter {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HasBox> Groups<T> split(List<T> items, int minSize) {
        Preconditions.checkArgument(items.size() >= 2);

        // according to
        // http://en.wikipedia.org/wiki/R-tree#Splitting_an_overflowing_node

        // find the worst combination pairwise in the list and use them to start
        // the two groups
        final Pair<T> worstCombination = worstCombination(items);

        // worst combination to have in the same node is now e1,e2.

        // establish a group around e1 and another group around e2
        final List<T> group1 = Lists.newArrayList(worstCombination.getValue1());
        final List<T> group2 = Lists.newArrayList(worstCombination.getValue2());

        final List<T> remaining = new ArrayList<>(items);
        remaining.remove(worstCombination.getValue1());
        remaining.remove(worstCombination.getValue2());

        final int minGroupSize = items.size() / 2;

        // now add the remainder to the groups using least mbb area increase
        // except in the case where minimumSize would be contradicted
        while (remaining.size() > 0) {
            assignRemaining(group1, group2, remaining, minGroupSize);
        }
        return new Groups<>(Group.of(group1), Group.of(group2));
    }

    private <T extends HasBox> void assignRemaining(final List<T> group1,
            final List<T> group2, final List<T> remaining, final int minGroupSize) {
        final Box mbb1 = Util.mbb(group1);
        final Box mbb2 = Util.mbb(group2);
        final T item1 = getBestCandidateForGroup(remaining, mbb1);
        final T item2 = getBestCandidateForGroup(remaining, mbb2);
        final boolean volume1LessThanVolume2 = item1.getBox().add(mbb1).getVolume() <= item2
                .getBox().add(mbb2).getVolume();

        if (volume1LessThanVolume2 && (group2.size() + remaining.size() - 1 >= minGroupSize)
                || !volume1LessThanVolume2 && (group1.size() + remaining.size() == minGroupSize)) {
            group1.add(item1);
            remaining.remove(item1);
        } else {
            group2.add(item2);
            remaining.remove(item2);
        }
    }

    @VisibleForTesting
    static <T extends HasBox> T getBestCandidateForGroup(List<T> list, Box groupMbb) {
        T minEntry = null;
        int minVolume = Integer.MAX_VALUE;
        for (final T entry : list) {
            final int volume = groupMbb.add(entry.getBox()).getVolume();
            if (volume < minVolume) {
                minVolume = volume;
                minEntry = entry;
            }
        }
        return minEntry;
    }

    @VisibleForTesting
    static <T extends HasBox> Pair<T> worstCombination(List<T> items) {
        T e1 = null;
        T e2 = null;
        int maxVolume = Integer.MIN_VALUE;
        for (final T entry1 : items) {
            for (final T entry2 : items) {
                if (entry1 != entry2) {
                    final int volume = entry1.getBox().add(entry2.getBox()).getVolume();
                    if (volume > maxVolume) {
                        e1 = entry1;
                        e2 = entry2;
                        maxVolume = volume;
                    }
                }
            }
        }

        return e1 != null ? new Pair<>(e1, e2) : new Pair<>(items.get(0), items.get(1));
    }
}
