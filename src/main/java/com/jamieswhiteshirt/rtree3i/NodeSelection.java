package com.jamieswhiteshirt.rtree3i;

import java.util.function.*;
import java.util.stream.Collector;

final class NodeSelection<K, V, T> implements Selection<T> {
    public static <K, V, T> Selection<T> create(Node<K, V> root, Predicate<? super Box> boxPredicate,
                                                Function<Entry<K, V>, T> entryMapper) {
        return new NodeSelection<>(root, boxPredicate, entryMapper);
    }

    private final Node<K, V> node;
    private final Predicate<? super Box> boxPredicate;
    private final Function<Entry<K, V>, T> entryValueMapper;

    private NodeSelection(Node<K, V> node, Predicate<? super Box> boxPredicate, Function<Entry<K, V>, T> entryMappper) {
        this.node = node;
        this.boxPredicate = boxPredicate;
        this.entryValueMapper = entryMappper;
    }

    @Override
    public Selection<T> filter(Predicate<? super T> predicate) {
        return new FilteredNodeSelection<>(node, boxPredicate, predicate, entryValueMapper);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        node.forEach(boxPredicate, entry -> action.accept(entryValueMapper.apply(entry)));
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return node.anyMatch(boxPredicate, entry -> predicate.test(entryValueMapper.apply(entry)));
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return node.allMatch(boxPredicate, entry -> predicate.test(entryValueMapper.apply(entry)));
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return !node.anyMatch(boxPredicate, entry -> predicate.test(entryValueMapper.apply(entry)));
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return node.reduce(boxPredicate, identity, (acc, entry) -> accumulator.apply(acc,
            entryValueMapper.apply(entry)));
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        R container = supplier.get();
        node.forEach(boxPredicate, entry -> accumulator.accept(container, entryValueMapper.apply(entry)));
        return container;
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        A container = collector.supplier().get();
        node.forEach(boxPredicate, entry -> collector.accumulator().accept(container, entryValueMapper.apply(entry)));
        return collector.finisher().apply(container);
    }

    @Override
    public int count() {
        return node.count(boxPredicate, entry -> true);
    }

    @Override
    public boolean isEmpty() {
        return !isNotEmpty();
    }

    @Override
    public boolean isNotEmpty() {
        return node.anyMatch(boxPredicate, entry -> true);
    }
}
