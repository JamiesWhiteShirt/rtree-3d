package com.jamieswhiteshirt.rtree3i;

import java.util.function.*;
import java.util.stream.Collector;

final class EmptySelection<T> implements Selection<T> {
    private static final Selection INSTANCE = new EmptySelection();

    public static <T> Selection<T> create() {
        //noinspection unchecked
        return INSTANCE;
    }

    private EmptySelection() {}

    @Override
    public Selection<T> filter(Predicate<? super T> predicate) {
        return this;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return false;
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return true;
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return true;
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return identity;
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return supplier.get();
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        A container = collector.supplier().get();
        return collector.finisher().apply(container);
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isNotEmpty() {
        return false;
    }
}
