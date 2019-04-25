package com.jamieswhiteshirt.rtree3i;

/**
 * A set of two {@link Group}s. Not thread safe.
 * @param <T> group entry type
 */
public final class Groups<T> {
    private final Group<T> group1;
    private final Group<T> group2;
    // these non-final variable mean that this class is not thread-safe
    // because access to them is not synchronized
    private int volumeSum = -1;
    private final int marginSum;

    /**
     * Constructs a set of both groups.
     * @param group1 the first group
     * @param group2 the second group
     */
    public Groups(Group<T> group1, Group<T> group2) {
        this.group1 = group1;
        this.group2 = group2;
        this.marginSum = group1.getBox().surfaceArea() + group2.getBox().surfaceArea();
    }

    /**
     * Returns the first group.
     * @return the first group
     */
    public Group<T> getGroup1() {
        return group1;
    }

    /**
     * Returns the second group.
     * @return the second group
     */
    public Group<T> getGroup2() {
        return group2;
    }

    /**
     * Returns the sum of the volumes of the groups.
     * @return
     */
    public int getVolumeSum() {
        if (volumeSum == -1)
            volumeSum = group1.getBox().getVolume() + group2.getBox().getVolume();
        return volumeSum;
    }

    /**
     * Returns the sum of the surface areas of the groups.
     * @return the sum of the surface areas of the groups
     */
    public int getMarginSum() {
        return marginSum;
    }

}
