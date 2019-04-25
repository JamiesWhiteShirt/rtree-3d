package com.jamieswhiteshirt.rtree3i;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A list of values with a minimum bounding box containing all entries.
 * @param <T> type of values in the group
 */
public final class Group<T> {
    private final List<T> entries;
    private final Box box;

    /**
     * Returns a group containing the entries with a minimum bounding box by applying the box mapper to entries.
     * @param entries group entries
     * @param boxMapper box mapper applied to entries
     * @param <T> type of values in the group
     * @return a group containing the entries with a minimum bounding box by applying the box mapper to entries
     */
    public static <T> Group<T> of(List<T> entries, Function<T, Box> boxMapper) {
        return new Group<>(entries, Util.mbb(entries.stream().map(boxMapper).collect(Collectors.toList())));
    }

    /**
     * Constructs a group containing the entries with the minimum bounding box.
     * @param entries group entries
     * @param box minimum bounding box
     */
    public Group(List<T> entries, Box box) {
        this.entries = entries;
        this.box = box;
    }

    /**
     * Returns the group entries.
     * @return the group entries
     */
    public List<T> getEntries() {
        return entries;
    }

    /**
     * Returns the minimum bounding box containing all entries.
     * @return the minimum bounding box containing all entries
     */
    public Box getBox() {
        return box;
    }
}
