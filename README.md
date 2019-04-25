# rtree-3i-lite
[![Maven Repository](https://img.shields.io/maven-metadata/v/https/repo.maven.apache.org/maven2/com/jamieswhiteshirt/rtree-3i-lite/maven-metadata.xml.svg)](https://repo.maven.apache.org/maven2/com/jamieswhiteshirt/rtree-3i-lite/)

Java implementation of an immutable map applying a spatial index to keys based on R-Trees. The R-Tree supports multiple branch size configurations and multiple selection and splitting heuristics.

Keys are spatially indexed using a function that maps keys to 3D int boxes (Box).

Based on [rtree-3d](https://github.com/davidmoten/rtree-3d) by Dave Moten. rtree-3d is designed for geometry data, builds on RxJava, and has features such as 3D visualization. rtree-3i-lite is designed only for applications that require mapping space to arbitrary values, depends only on Guava, and does nothing more.

Releases are available on Maven central. Snapshots are available on Sonatype's snapshots repository.
