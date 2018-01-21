package com.github.davidmoten.rtree3d.geometry;

import com.github.davidmoten.util.ObjectsHelper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public final class Box {
    private final float x1, y1, x2, y2, z1, z2;

    private Box(float x1, float y1, float z1, float x2, float y2, float z2) {
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

    public float x1() {
        return x1;
    }

    public float y1() {
        return y1;
    }

    public float x2() {
        return x2;
    }

    public float y2() {
        return y2;
    }

    public float z1() {
        return z1;
    }

    public float z2() {
        return z2;
    }

    public float volume() {
        return (x2 - x1) * (y2 - y1) * (z2 - z1);
    }

    public Box add(Box r) {
        return new Box(Math.min(x1, r.x1), Math.min(y1, r.y1), Math.min(z1, r.z1),
                Math.max(x2, r.x2), Math.max(y2, r.y2), Math.max(z2, r.z2));
    }

    public static Box create(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new Box((float) x1, (float) y1, (float) z1, (float) x2, (float) y2, (float) z2);
    }

    public static Box create(float x1, float y1, float z1, float x2, float y2, float z2) {
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
        return Objects.hashCode(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        Optional<Box> other = ObjectsHelper.asClass(obj, Box.class);
        if (other.isPresent()) {
            return Objects.equal(x1, other.get().x1) && Objects.equal(x2, other.get().x2)
                    && Objects.equal(y1, other.get().y1) && Objects.equal(y2, other.get().y2)
                    && Objects.equal(z1, other.get().z1) && Objects.equal(z2, other.get().z2);
        } else
            return false;
    }

    public float intersectionVolume(Box r) {
        if (!intersects(r))
            return 0;
        else
            return create(Math.max(x1, r.x1), Math.max(y1, r.y1), Math.max(z1, r.z1),
                    Math.min(x2, r.x2), Math.min(y2, r.y2), Math.min(z2, r.z2)).volume();
    }

    public float surfaceArea() {
        return 2 * ((x2 - x1) * (y2 - y1) + (y2 - y1) * (z2 - z1) + (x2 - x1) * (z2 - z1));
    }
}