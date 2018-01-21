package com.github.davidmoten.rtree3d;

import java.util.*;
import java.util.function.ToIntFunction;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public final class SplitterRStar implements Splitter {

    private final Comparator<Groups<?>> comparator;

    @SuppressWarnings("unchecked")
    public SplitterRStar() {
        this.comparator = Comparators.compose(Comparators.overlapListPairComparator,
                Comparators.volumePairComparator);
    }

    @Override
    public <T extends HasBox> Groups<T> split(List<T> items, int minSize) {
        Preconditions.checkArgument(!items.isEmpty());
        // sort nodes into increasing x, calculate min overlap where both groups
        // have more than minChildren

        Map<SortType, List<Groups<T>>> map = new EnumMap<>(SortType.class);
        for (SortType sortType : SortType.values()) {
            map.put(sortType, getPairs(minSize, sort(items, sortType.comparator)));
        }

        // compute S the sum of all margin-values of the lists above
        // the list with the least S is then used to find minimum overlap

        SortType leastMarginSumSortType = Collections.min(sortTypes, marginSumComparator(map));
        List<Groups<T>> pairs = map.get(leastMarginSumSortType);

        return Collections.min(pairs, comparator);
    }

    private enum SortType {
        X1(item -> item.getBox().x1()),
        X2(item -> item.getBox().x2()),
        Y1(item -> item.getBox().y1()),
        Y2(item -> item.getBox().y2()),
        Z1(item -> item.getBox().z1()),
        Z2(item -> item.getBox().z2());

        final Comparator<HasBox> comparator;

        SortType(ToIntFunction<HasBox> keyAccessor) {
            this.comparator = Comparator.comparingInt(keyAccessor);
        }
    }

    private static final List<SortType> sortTypes = Collections
            .unmodifiableList(Arrays.asList(SortType.values()));

    private static <T extends HasBox> Comparator<SortType> marginSumComparator(
            final Map<SortType, List<Groups<T>>> map) {
        return Comparator.comparing(sortType -> marginValueSum(map.get(sortType)));
    }

    private static <T extends HasBox> int marginValueSum(List<Groups<T>> list) {
        int sum = 0;
        for (Groups<T> p : list)
            sum += p.getMarginSum();
        return sum;
    }

    @VisibleForTesting
    static <T extends HasBox> List<Groups<T>> getPairs(int minSize, List<T> list) {
        List<Groups<T>> pairs = new ArrayList<Groups<T>>(list.size() - 2 * minSize + 1);
        for (int i = minSize; i < list.size() - minSize + 1; i++) {
            List<T> list1 = list.subList(0, i);
            List<T> list2 = list.subList(i, list.size());
            Groups<T> pair = new Groups<>(Group.of(list1), Group.of(list2));
            pairs.add(pair);
        }
        return pairs;
    }

    private static <T extends HasBox> List<T> sort(List<T> items,
            Comparator<HasBox> comparator) {
        ArrayList<T> list = new ArrayList<>(items);
        Collections.sort(list, comparator);
        return list;
    }

}
