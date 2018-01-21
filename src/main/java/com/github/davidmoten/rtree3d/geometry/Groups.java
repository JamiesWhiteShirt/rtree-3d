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
    private int volumeSum = -1;
    private final int marginSum;

    public Groups(Group<T> group1, Group<T> group2) {
        this.group1 = group1;
        this.group2 = group2;
        this.marginSum = group1.getBox().surfaceArea() + group2.getBox().surfaceArea();
    }

    public Group<T> getGroup1() {
        return group1;
    }

    public Group<T> getGroup2() {
        return group2;
    }

    public int getVolumeSum() {
        if (volumeSum == -1)
            volumeSum = group1.getBox().volume() + group2.getBox().volume();
        return volumeSum;
    }

    public int getMarginSum() {
        return marginSum;
    }

}
