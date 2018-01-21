package com.github.davidmoten.rtree3d;

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
