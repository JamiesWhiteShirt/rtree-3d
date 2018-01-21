package com.github.davidmoten.rtree3d.geometry;

import com.github.davidmoten.rtree3d.HasBox;

/**
 *
 * Not thread safe.
 *
 * @param <T>
 *            list type
 */
public final class Groups<T extends HasBox> {
    private final Group<T> group1;
    private final Group<T> group2;
    // these non-final variable mean that this class is not thread-safe
    // because access to them is not synchronized
    private float areaSum = -1;
    private final float marginSum;

    public Groups(Group<T> group1, Group<T> group2) {
        this.group1 = group1;
        this.group2 = group2;
        this.marginSum = group1.box().surfaceArea() + group2.box().surfaceArea();
    }

    public Group<T> group1() {
        return group1;
    }

    public Group<T> group2() {
        return group2;
    }

    public float volumeSum() {
        if (areaSum == -1)
            areaSum = group1.box().volume() + group2.box().volume();
        return areaSum;
    }

    public float marginSum() {
        return marginSum;
    }

}
