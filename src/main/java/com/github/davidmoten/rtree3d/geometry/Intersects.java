package com.github.davidmoten.rtree3d.geometry;

import java.util.function.BiFunction;

public final class Intersects {

    private Intersects() {
        // prevent instantiation
    }

    public static final BiFunction<Geometry, Box, Boolean> geometryIntersectsRectangle = (geometry, r) -> {
        if (geometry instanceof Box)
            return r.intersects((Box) geometry);
        else if (geometry instanceof Point)
            return ((Point) geometry).intersects(r);
        else
            throw new RuntimeException("unrecognized geometry: " + geometry);
    };

    public static final BiFunction<Box, Geometry, Boolean> rectangleIntersectsGeometry = (r, geometry) -> geometryIntersectsRectangle.apply(geometry, r);

    public static final BiFunction<Geometry, Point, Boolean> geometryIntersectsPoint = (geometry, point) -> geometryIntersectsRectangle.apply(geometry, point.mbb());

    public static final BiFunction<Point, Geometry, Boolean> pointIntersectsGeometry = (point, geometry) -> geometryIntersectsPoint.apply(geometry, point);

}
