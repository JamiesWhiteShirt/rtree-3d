package com.github.davidmoten.rtree3d;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

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

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Box [x1=");
        b.append(x1);
        b.append(", y1=");
        b.append(y1);
        b.append(", z1=");
        b.append(z1);
        b.append(", x2=");
        b.append(x2);
        b.append(", y2=");
        b.append(y2);
        b.append(", z2=");
        b.append(z2);
        b.append("]");
        return b.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Box) {
            Box o = (Box) obj;
            return x1 == o.x1 && y1 == o.y1 && z1 == o.z1 && x2 == o.x2 && y2 == o.y2 && z2 == o.z2;
        }
        return false;
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
}