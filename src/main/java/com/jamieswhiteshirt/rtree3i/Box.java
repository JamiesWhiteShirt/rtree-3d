package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * Three-dimensional axis aligned box with integer minimum and maximum values for each axis.
 * A bounding box may be flat on any axis, meaning it could be a face, a line or a point.
 */
public final class Box {
    private final int x1, y1, z1, x2, y2, z2;

    private Box(int x1, int y1, int z1, int x2, int y2, int z2) {
        Preconditions.checkArgument(x2 >= x1);
        Preconditions.checkArgument(y2 >= y1);
        Preconditions.checkArgument(z2 >= z1);
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    /**
     * Returns the minimum X value.
     * @return the mimimum X value
     */
    public int x1() {
        return x1;
    }

    /**
     * Returns the minimum Y value.
     * @return the minimum Y value
     */
    public int y1() {
        return y1;
    }

    /**
     * Returns the minimum Z value.
     * @return the mimimum Z value
     */
    public int z1() {
        return z1;
    }

    /**
     * Returns the maximum X value.
     * @return the maximum X value
     */
    public int x2() {
        return x2;
    }

    /**
     * Returns the maximum Y value.
     * @return the maximum Y value
     */
    public int y2() {
        return y2;
    }

    /**
     * Returns the maximum Z value.
     * @return the maximum Z value
     */
    public int z2() {
        return z2;
    }

    /**
     * Returns the volume of the bounding box
     * @return the volume of the bounding box
     */
    public int getVolume() {
        return (x2 - x1) * (y2 - y1) * (z2 - z1);
    }

    /**
     * Returns the minimum bounding box containing both this box and the other bounding box
     * @param r the other bounding box
     * @return the minimum bounding box containing both this box and the other bounding box
     */
    public Box add(Box r) {
        return new Box(Math.min(x1, r.x1), Math.min(y1, r.y1), Math.min(z1, r.z1),
                Math.max(x2, r.x2), Math.max(y2, r.y2), Math.max(z2, r.z2));
    }

    /**
     * Returns a new bounding box with the specified space
     * @param x1 minimum X value, must be less than or equal to x2
     * @param y1 minimum Y value, must be less than or equal to y2
     * @param z1 minimum Z value, must be less than or equal to z2
     * @param x2 maximum X value, must be greater than or equal to x1
     * @param y2 maximum Y value, must be greater than or equal to y1
     * @param z2 maximum Z value, must be greater than or equal to z1
     * @return
     */
    public static Box create(int x1, int y1, int z1, int x2, int y2, int z2) {
        return new Box(x1, y1, z1, x2, y2, z2);
    }

    /**
     * Returns true if this bounding box and the other bounding box have a closed intersection.
     * @param r the other bounding box
     * @return true if this bounding box and the other bounding box have a closed intersection
     */
    public boolean intersectsClosed(Box r) {
        return x1 < r.x2 && x2 > r.x1 && y1 < r.y2 && y2 > r.y1 && z1 < r.z2 && z2 > r.z1;
    }

    /**
     * Returns true if this bounding box and the other bounding box have an open intersection.
     * @param r the other bounding box
     * @return true if this bounding box and the other bounding box have an open intersection
     */
    public boolean intersectsOpen(Box r) {
        return x1 <= r.x2 && x2 >= r.x1 && y1 <= r.y2 && y2 >= r.y1 && z1 <= r.z2 && z2 >= r.z1;
    }

    /**
     * Returns true if this bounding box fully contains the other bounding box.
     * @param r the other bounding box
     * @return true if this bounding box fully contains the other bounding box
     */
    public boolean contains(Box r) {
        return x1 <= r.x1 && x2 >= r.x2 && y1 <= r.y1 && y2 >= r.y2 && z1 <= r.z1 && z2 >= r.z2;
    }

    /**
     * Returns true if this bounding box is fully contained by the other bounding box.
     * @param r the other bounding box
     * @return true if this bounding box is fully contained by the other bounding box
     */
    public boolean containedBy(Box r) {
        return r.x1 <= x1 && r.x2 >= x2 && r.y1 <= y1 && r.y2 >= y2 && r.z1 <= z1 && r.z2 >= z2;
    }

    /**
     * Returns the volume of the intersection of this bounding box and the other bounding box. If the intersection is
     * empty or null, the volume is zero.
     * @param r the other bounding box
     * @return the volume of the intersection of this bounding box and the other bounding box
     */
    public int intersectionVolume(Box r) {
        if (!intersectsClosed(r)) {
            return 0;
        } else {
            return create(Math.max(x1, r.x1), Math.max(y1, r.y1), Math.max(z1, r.z1),
                Math.min(x2, r.x2), Math.min(y2, r.y2), Math.min(z2, r.z2)).getVolume();
        }
    }

    /**
     * Returns the surface area of this bounding box.
     * @return the surface area of this bounding box
     */
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
            z1 == box.z1 &&
            x2 == box.x2 &&
            y2 == box.y2 &&
            z2 == box.z2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public String toString() {
        return "Box{" +
            "x1=" + x1 +
            ", y1=" + y1 +
            ", z1=" + z1 +
            ", x2=" + x2 +
            ", y2=" + y2 +
            ", z2=" + z2 +
            '}';
    }
}