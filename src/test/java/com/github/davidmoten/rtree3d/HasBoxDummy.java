package com.github.davidmoten.rtree3d;

import com.github.davidmoten.rtree3d.geometry.Box;

public class HasBoxDummy implements HasBox {

    private final Box r;

    public HasBoxDummy(Box r) {
        this.r = r;
    }

    @Override
    public Box getBox() {
        return r;
    }
}
