package com.github.davidmoten.rtree3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.Group;
import com.github.davidmoten.rtree3d.geometry.Groups;
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
        final List<T> group1 = Lists.newArrayList(worstCombination.value1());
        final List<T> group2 = Lists.newArrayList(worstCombination.value2());

        final List<T> remaining = new ArrayList<T>(items);
        remaining.remove(worstCombination.value1());
        remaining.remove(worstCombination.value2());

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
        final Box mbr1 = Util.mbb(group1);
        final Box mbr2 = Util.mbb(group2);
        final T item1 = getBestCandidateForGroup(remaining, group1, mbr1);
        final T item2 = getBestCandidateForGroup(remaining, group2, mbr2);
        final boolean volume1LessThanVolume2 = item1.getBox().add(mbr1).volume() <= item2
                .getBox().add(mbr2).volume();

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
    static <T extends HasBox> T getBestCandidateForGroup(List<T> list, List<T> group,
            Box groupMbr) {
        Optional<T> minEntry = Optional.empty();
        Optional<Double> minVolume = Optional.empty();
        for (final T entry : list) {
            final double volume = groupMbr.add(entry.getBox()).volume();
            if (!minVolume.isPresent() || volume < minVolume.get()) {
                minVolume = Optional.of(volume);
                minEntry = Optional.of(entry);
            }
        }
        return minEntry.get();
    }

    @VisibleForTesting
    static <T extends HasBox> Pair<T> worstCombination(List<T> items) {
        Optional<T> e1 = Optional.empty();
        Optional<T> e2 = Optional.empty();
        {
            Optional<Double> maxVolume = Optional.empty();
            for (final T entry1 : items) {
                for (final T entry2 : items) {
                    if (entry1 != entry2) {
                        final double volume = entry1.getBox().add(entry2.getBox())
                                .volume();
                        if (!maxVolume.isPresent() || volume > maxVolume.get()) {
                            e1 = Optional.of(entry1);
                            e2 = Optional.of(entry2);
                            maxVolume = Optional.of(volume);
                        }
                    }
                }
            }
        }
        if (e1.isPresent())
            return new Pair<>(e1.get(), e2.get());
        else
            // all items are the same item
            return new Pair<>(items.get(0), items.get(1));
    }
}
