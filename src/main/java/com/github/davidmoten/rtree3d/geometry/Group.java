package com.github.davidmoten.rtree3d.geometry;

import com.github.davidmoten.rtree3d.HasBox;
import com.github.davidmoten.rtree3d.Util;

import java.util.List;

public class Group<T extends HasBox> {
    private final List<T> entries;
    private final Box box;

    public static <T extends HasBox> Group<T> of(List<T> entries) {
        return new Group<>(entries, Util.mbr(entries));
    }

    public Group(List<T> entries, Box box) {
        this.entries = entries;
        this.box = box;
    }

    public List<T> entries() {
        return entries;
    }

    public Box box() {
        return box;
    }
}
