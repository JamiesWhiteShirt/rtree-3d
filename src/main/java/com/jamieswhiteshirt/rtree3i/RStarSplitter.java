package com.jamieswhiteshirt.rtree3i;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import com.google.common.base.Preconditions;

/**
 * An R*-tree splitter.
 */
public final class RStarSplitter implements Splitter {

    private final Comparator<Groups<?>> groupsComparator;

    /**
     * Constructs an R*-tree splitter.
     */
    public RStarSplitter() {
        this.groupsComparator = com.jamieswhiteshirt.rtree3i.Comparators.groupsVolumeComparator.thenComparing(com.jamieswhiteshirt.rtree3i.Comparators.groupsIntersectionVolumeComparator);
    }

    @Override
    public <T> Groups<T> split(List<T> items, int minSize, Function<T, Box> boxMapper) {
        Preconditions.checkArgument(!items.isEmpty());
        // sort nodes into increasing x, calculate min overlap where both groups
        // have more than minChildren

        Map<SortType, List<Groups<T>>> map = new EnumMap<>(SortType.class);
        for (SortType sortType : SortType.values()) {
            ToIntFunction<T> accessor = item -> sortType.keyAccessor.applyAsInt(boxMapper.apply(item));
            Comparator<T> comparator = Comparator.comparingInt(accessor);
            map.put(sortType, createPairs(minSize, sort(items, comparator), boxMapper));
        }

        // compute S the sum of all margin-values of the lists above
        // the list with the least S is then used to find minimum overlap

        SortType leastMarginSumSortType = Collections.min(sortTypes, marginSumComparator(map));
        List<Groups<T>> pairs = map.get(leastMarginSumSortType);

        return Collections.min(pairs, groupsComparator);
    }

    private enum SortType {
        X1(Box::x1),
        X2(Box::x2),
        Y1(Box::y1),
        Y2(Box::y2),
        Z1(Box::z1),
        Z2(Box::z2);

        final ToIntFunction<Box> keyAccessor;

        SortType(ToIntFunction<Box> keyAccessor) {
            this.keyAccessor = keyAccessor;
        }
    }

    private static final List<SortType> sortTypes = Collections
            .unmodifiableList(Arrays.asList(SortType.values()));

    private static <T> Comparator<SortType> marginSumComparator(final Map<SortType, List<Groups<T>>> map) {
        return Comparator.comparing(sortType -> marginValueSum(map.get(sortType)));
    }

    private static <T> int marginValueSum(List<Groups<T>> list) {
        int sum = 0;
        for (Groups<T> p : list)
            sum += p.getMarginSum();
        return sum;
    }

    static <T> List<Groups<T>> createPairs(int minSize, List<T> list, Function<T, Box> key) {
        List<Groups<T>> pairs = new ArrayList<Groups<T>>(list.size() - 2 * minSize + 1);
        for (int i = minSize; i < list.size() - minSize + 1; i++) {
            List<T> list1 = list.subList(0, i);
            List<T> list2 = list.subList(i, list.size());
            Groups<T> pair = new Groups<>(Group.of(list1, key), Group.of(list2, key));
            pairs.add(pair);
        }
        return pairs;
    }

    private static <T> List<T> sort(List<T> items, Comparator<T> comparator) {
        ArrayList<T> list = new ArrayList<>(items);
        list.sort(comparator);
        return list;
    }

}
