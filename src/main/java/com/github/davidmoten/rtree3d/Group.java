package com.github.davidmoten.rtree3d;

import java.util.List;

public final class Group<T extends HasBox> {
    private final List<T> entries;
    private final Box box;

    public static <T extends HasBox> Group<T> of(List<T> entries) {
        return new Group<>(entries, Util.mbb(entries));
    }

    public Group(List<T> entries, Box box) {
        this.entries = entries;
        this.box = box;
    }

    public List<T> getEntries() {
        return entries;
    }

    public Box getBox() {
        return box;
    }
}
