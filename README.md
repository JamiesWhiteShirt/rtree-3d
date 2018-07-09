# rtree-3i-lite

Lightweight immutable map and multimap, mapping 3D int boxes to arbitrary values, with a spatial index based on immutable R-Trees in Java. Supports multiple branch size configurations and multiple selection and splitting heuristics.

Based on [rtree-3d](https://github.com/davidmoten/rtree-3d) by Dave Moten. rtree-3d is designed for geometry data, builds on RxJava, and has features such as 3D visualization. rtree-3i-lite is designed only for applications that require mapping space to arbitrary values, depends only on Guava, and does nothing more.
