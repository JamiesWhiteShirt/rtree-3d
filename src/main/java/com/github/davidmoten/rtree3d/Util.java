package com.github.davidmoten.rtree3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * @author dxm
 *
 */
public final class Util {

    private Util() {
        // prevent instantiation
    }

    /**
     * Returns the minimum bounding box of a number of items. Benchmarks
     * below indicate that when the number of items is &gt;1 this method is more
     * performant than one using {@link Box#add(Box)}.
     *
     * @param items
     *            items to bound
     * @return the minimum bounding box containings items
     */
    public static Box mbb(Collection<Box> items) {
        Preconditions.checkArgument(!items.isEmpty());
        int minX1 = Integer.MAX_VALUE;
        int minY1 = Integer.MAX_VALUE;
        int minZ1 = Integer.MAX_VALUE;
        int maxX2 = -Integer.MAX_VALUE;
        int maxY2 = -Integer.MAX_VALUE;
        int maxZ2 = -Integer.MAX_VALUE;
        for (final Box r : items) {
            if (r.x1() < minX1)
                minX1 = r.x1();
            if (r.y1() < minY1)
                minY1 = r.y1();
            if (r.z1() < minZ1)
                minZ1 = r.z1();
            if (r.x2() > maxX2)
                maxX2 = r.x2();
            if (r.y2() > maxY2)
                maxY2 = r.y2();
            if (r.z2() > maxZ2)
                maxZ2 = r.z2();
        }
        return Box.create(minX1, minY1, minZ1, maxX2, maxY2, maxZ2);
    }

    static <T> List<T> add(List<T> list, T element) {
        final ArrayList<T> result = new ArrayList<>(list.size() + 2);
        result.addAll(list);
        result.add(element);
        return result;
    }

    static <T> List<T> remove(List<? extends T> list, List<? extends T> elements) {
        final ArrayList<T> result = new ArrayList<>(list);
        result.removeAll(elements);
        return result;
    }

    static <T> List<T> replace(List<T> list, T element, List<T> replacements) {
        List<T> list2 = new ArrayList<>(list.size() + replacements.size());
        for (T node : list)
            if (node != element)
                list2.add(node);
        list2.addAll(replacements);
        return list2;
    }

}
