package com.github.davidmoten.rtree3d;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;

import com.github.davidmoten.rtree3d.geometry.Geometry;

public class Serializer {

    public static <T, S extends Geometry> void serialize(RTree<T, S> tree,
             BiConsumer<T, OutputStream> objectSerializer, OutputStream os) {
        // TODO
    }

    public static <T, S extends Geometry> RTree<T, S> deserialize(InputStream is,
            BiConsumer<InputStream, T> objectDeserializer) {
        // TODO
        return null;
    }

}
