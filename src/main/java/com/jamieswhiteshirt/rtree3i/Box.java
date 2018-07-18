package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.function.Predicate;

public final class Box {
    private final int x1, y1, x2, y2, z1, z2;

    private Box(int x1, int y1, int z1, int x2, int y2, int z2) {
        Preconditions.checkArgument(x2 >= x1);
        Preconditions.checkArgument(y2 >= y1);
        Preconditions.checkArgument(z2 >= z1);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;
    }

    public int x1() {
        return x1;
    }

    public int y1() {
        return y1;
    }

    public int x2() {
        return x2;
    }

    public int y2() {
        return y2;
    }

    public int z1() {
        return z1;
    }

    public int z2() {
        return z2;
    }

    public int getVolume() {
        return (x2 - x1) * (y2 - y1) * (z2 - z1);
    }

    public Box add(Box r) {
        return new Box(Math.min(x1, r.x1), Math.min(y1, r.y1), Math.min(z1, r.z1),
                Math.max(x2, r.x2), Math.max(y2, r.y2), Math.max(z2, r.z2));
    }

    public static Box create(int x1, int y1, int z1, int x2, int y2, int z2) {
        return new Box(x1, y1, z1, x2, y2, z2);
    }

    public boolean intersects(Box r) {
        return !(x1 > r.x2 || x2 < r.x1 || y1 > r.y2 || y2 < r.y1 || z1 > r.z2 || z2 < r.z1);
    }

    public boolean contains(Box r) {
        return x1 <= r.x1 && x2 >= r.x2 && y1 <= r.y1 && y2 >= r.y2 && z1 <= r.z1 && z2 >= r.z2;
    }

    public boolean containedBy(Box r) {
        return r.x1 <= x1 && r.x2 >= x2 && r.y1 <= y1 && r.y2 >= y2 && r.z1 <= z1 && r.z2 >= z2;
    }

    public int intersectionVolume(Box r) {
        if (!intersects(r))
            return 0;
        else
            return create(Math.max(x1, r.x1), Math.max(y1, r.y1), Math.max(z1, r.z1),
                    Math.min(x2, r.x2), Math.min(y2, r.y2), Math.min(z2, r.z2)).getVolume();
    }

    public int surfaceArea() {
        return 2 * ((x2 - x1) * (y2 - y1) + (y2 - y1) * (z2 - z1) + (x2 - x1) * (z2 - z1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return x1 == box.x1 &&
            y1 == box.y1 &&
            x2 == box.x2 &&
            y2 == box.y2 &&
            z1 == box.z1 &&
            z2 == box.z2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, x2, y2, z1, z2);
    }

    @Override
    public String toString() {
        return "Box{" +
            "x1=" + x1 +
            ", y1=" + y1 +
            ", x2=" + x2 +
            ", y2=" + y2 +
            ", z1=" + z1 +
            ", z2=" + z2 +
            '}';
    }
}