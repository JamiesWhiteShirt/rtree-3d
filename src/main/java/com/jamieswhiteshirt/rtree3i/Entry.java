package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * An entry in the R-tree which has a spatial representation.
 * 
 * @param <T>
 *            the type of Entry
 */
public final class Entry<T> {
    private final T value;
    private final Box box;

    /**
     * Constructor.
     * 
     * @param value
     *            the value of the entry
     * @param box
     *            the getBox of the value
     */
    public Entry(T value, Box box) {
        Preconditions.checkNotNull(box);
        this.value = value;
        this.box = box;
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
        return new Entry<>(value, box);
    }

    /**
     * Returns the value wrapped by this {@link Entry}.
     * 
     * @return the entry value
     */
    public T getValue() {
        return value;
    }

    public Box getBox() {
        return box;
    }

    @Override
    public String toString() {
        return "Entry [value=" + value + ", box=" + box + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value, box);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Entry) {
            Entry o = (Entry) obj;
            return box.equals(o.box) && value.equals(o.value);
        }
        return false;
    }
}
