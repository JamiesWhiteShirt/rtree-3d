package com.github.davidmoten.rtree3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.davidmoten.rtree3d.geometry.Group;
import com.github.davidmoten.rtree3d.geometry.Groups;
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

        Map<SortType, List<Groups<T>>> map = new HashMap<SortType, List<Groups<T>>>(5, 1.0f);
        map.put(SortType.X_LOWER, getPairs(minSize, sort(items, INCREASING_X_LOWER)));
        map.put(SortType.X_UPPER, getPairs(minSize, sort(items, INCREASING_X_UPPER)));
        map.put(SortType.Y_LOWER, getPairs(minSize, sort(items, INCREASING_Y_LOWER)));
        map.put(SortType.Y_UPPER, getPairs(minSize, sort(items, INCREASING_Y_UPPER)));
        map.put(SortType.Z_LOWER, getPairs(minSize, sort(items, INCREASING_Z_LOWER)));
        map.put(SortType.Z_UPPER, getPairs(minSize, sort(items, INCREASING_Z_UPPER)));

        // compute S the sum of all margin-values of the lists above
        // the list with the least S is then used to find minimum overlap

        SortType leastMarginSumSortType = Collections.min(sortTypes, marginSumComparator(map));
        List<Groups<T>> pairs = map.get(leastMarginSumSortType);

        return Collections.min(pairs, comparator);
    }

    private enum SortType {
        X_LOWER, X_UPPER, Y_LOWER, Y_UPPER, Z_LOWER, Z_UPPER;
    }

    private static final List<SortType> sortTypes = Collections
            .unmodifiableList(Arrays.asList(SortType.values()));

    private static <T extends HasBox> Comparator<SortType> marginSumComparator(
            final Map<SortType, List<Groups<T>>> map) {
        return Comparator.comparing(sortType -> (double) marginValueSum(map.get(sortType)));
    }

    private static <T extends HasBox> float marginValueSum(List<Groups<T>> list) {
        float sum = 0;
        for (Groups<T> p : list)
            sum += p.marginSum();
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

    private static Comparator<HasBox> INCREASING_X_LOWER = (n1, n2) -> Float.compare(n1.getBox().x1(), n2.getBox().x1());

    private static Comparator<HasBox> INCREASING_X_UPPER = (n1, n2) -> Float.compare(n1.getBox().x2(), n2.getBox().x2());

    private static Comparator<HasBox> INCREASING_Y_LOWER = (n1, n2) -> Float.compare(n1.getBox().y1(), n2.getBox().y1());

    private static Comparator<HasBox> INCREASING_Y_UPPER = (n1, n2) -> Float.compare(n1.getBox().y2(), n2.getBox().y2());

    private static Comparator<HasBox> INCREASING_Z_LOWER = (n1, n2) -> Float.compare(n1.getBox().z1(), n2.getBox().z1());

    private static Comparator<HasBox> INCREASING_Z_UPPER = (n1, n2) -> Float.compare(n1.getBox().z2(), n2.getBox().z2());

}
