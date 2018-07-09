package com.jamieswhiteshirt.rtree3i;

import java.util.Objects;

final class Pair<T> {

    private final T value1;
    private final T value2;

    public Pair(T value1, T value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public T getValue1() {
        return value1;
    }

    public T getValue2() {
        return value2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?> pair = (Pair<?>) o;
        return Objects.equals(value1, pair.value1) &&
            Objects.equals(value2, pair.value2);
    }

    @Override
    public int hashCode() {

        return Objects.hash(value1, value2);
    }

    @Override
    public String toString() {
        return "Pair{" +
            "value1=" + value1 +
            ", value2=" + value2 +
            '}';
    }
}
