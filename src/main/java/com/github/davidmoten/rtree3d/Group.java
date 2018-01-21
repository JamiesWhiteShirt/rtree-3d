package com.github.davidmoten.rtree3d;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Group<T> {
    private final List<T> entries;
    private final Box box;

    public static <T> Group<T> of(List<T> entries, Function<T, Box> key) {
        return new Group<>(entries, Util.mbb(entries.stream().map(key).collect(Collectors.toList())));
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
