package com.jamieswhiteshirt.rtree3i;

import java.util.function.*;
import java.util.stream.Collector;

public class NodeSelection<K, V, T> implements Selection<T> {
    private static final Predicate<Object> ALWAYS_TRUE = o -> true;

    public static <K, V, T> Selection<T> create(Node<K, V> root, Predicate<? super Box> boxPredicate,
                                                Function<Entry<K, V>, T> entryMapper) {
        return new NodeSelection<>(root, boxPredicate, ALWAYS_TRUE, entryMapper);
    }

    private final Node<K, V> node;
    private final Predicate<? super Box> boxPredicate;
    private final Predicate<? super T> filter;
    private final Function<Entry<K, V>, T> entryValueMapper;

    private NodeSelection(Node<K, V> node, Predicate<? super Box> boxPredicate, Predicate<? super T> filter,
                          Function<Entry<K, V>, T> entryMappper) {
        this.node = node;
        this.boxPredicate = boxPredicate;
        this.filter = filter;
        this.entryValueMapper = entryMappper;
    }

    @Override
    public Selection<T> filter(Predicate<? super T> predicate) {
        if (this.filter == ALWAYS_TRUE) {
            return new NodeSelection<>(node, boxPredicate, predicate, entryValueMapper);
        } else {
            Predicate<? super T> filter = this.filter;
            return new NodeSelection<>(node, boxPredicate, t -> filter.test(t) && predicate.test(t), entryValueMapper);
        }
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        node.forEach(boxPredicate, entry -> {
            T value = entryValueMapper.apply(entry);
            if (filter.test(value)) {
                action.accept(entryValueMapper.apply(entry));
            }
        });
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return node.anyMatch(boxPredicate, entry -> {
            T value = entryValueMapper.apply(entry);
            return filter.test(value) && predicate.test(value);
        });
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return node.allMatch(boxPredicate, entry -> {
            T value = entryValueMapper.apply(entry);
            return !filter.test(value) || predicate.test(value);
        });
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return !node.anyMatch(boxPredicate, entry -> {
            T value = entryValueMapper.apply(entry);
            return !filter.test(value) || predicate.test(value);
        });
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return node.reduce(boxPredicate, identity, (acc, entry) -> {
            T value = entryValueMapper.apply(entry);
            return filter.test(value) ? accumulator.apply(acc, value) : acc;
        });
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        R container = supplier.get();
        node.forEach(boxPredicate, entry -> {
            T value = entryValueMapper.apply(entry);
            if (filter.test(value)) {
                accumulator.accept(container, value);
            }
        });
        return container;
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        A container = collector.supplier().get();
        node.forEach(boxPredicate, entry -> {
            T value = entryValueMapper.apply(entry);
            if (filter.test(value)) {
                collector.accumulator().accept(container, value);
            }
        });
        return collector.finisher().apply(container);
    }

    @Override
    public int count() {
        return node.count(boxPredicate, entry -> filter.test(entryValueMapper.apply(entry)));
    }

    @Override
    public boolean isEmpty() {
        return !isNotEmpty();
    }

    @Override
    public boolean isNotEmpty() {
        return node.anyMatch(boxPredicate, entry -> filter.test(entryValueMapper.apply(entry)));
    }
}
