package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * An entry in the R-tree which has a spatial representation.
 * 
 * @param <T>
 *            the type of Entry
 */
public final class Entry<T> {
    private final Box box;
    private final T value;

    /**
     * Constructor.
     *
     * @param box
     *            the getBox of the value
     * @param value
 *            the value of the entry
     */
    public Entry(Box box, T value) {
        Preconditions.checkNotNull(box);
        this.box = box;
        this.value = value;
    }

    /**
     * Factory method.
     * 
     * @param <T>
     *            type of value
     * @param value
     *            object being given a spatial context
     * @param box
     *            getBox associated with the value
     * @return entry wrapping value and associated geometry
     */
    public static <T> Entry<T> entry(T value, Box box) {
        return new Entry<>(box, value);
    }

    public Box getBox() {
        return box;
    }

    /**
     * Returns the value wrapped by this {@link Entry}.
     *
     * @return the entry value
     */
    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Entry [value=" + value + ", box=" + box + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry<?> entry = (Entry<?>) o;
        return Objects.equals(value, entry.value) &&
            Objects.equals(box, entry.box);
    }

    @Override
    public int hashCode() {

        return Objects.hash(value, box);
    }
}
