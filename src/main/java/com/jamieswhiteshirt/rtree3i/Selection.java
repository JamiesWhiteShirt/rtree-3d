package com.jamieswhiteshirt.rtree3i;

import java.util.function.*;
import java.util.stream.Collector;

public interface Selection<T> {
    /**
     * Returns a selection consisting of the elements of this selection that match the given predicate.
     * @param predicate a non-interfering, stateless predicate to apply to each element to determine if it should be
     *                  included
     * @return the new selection
     */
    Selection<T> filter(Predicate<? super T> predicate);

    /**
     * Performs an action for each element of this selection.
     * @param action a non-interfering action to perform on the elements
     */
    void forEach(Consumer<? super T> action);

    /**
     * Performs a reduction on the elements of this selection, using the provided identity value and an associative
     * accumulation function, and returns the reduced value.
     * @param identity the identity value for the accumulating function
     * @param accumulator an associative, non-interfering, stateless function for combining two values
     * @return the result of the reduction
     */
    T reduce(T identity, BinaryOperator<T> accumulator);

    /**
     * Performs a mutable reduction operation on the elements of this selection.  A mutable reduction is one in which
     * the reduced value is a mutable result container, such as an {@code ArrayList}, and elements are incorporated by
     * updating the state of the result rather than by replacing the result.
     * @param <R> type of the result
     * @param supplier a function that creates a new result container. For a
     *                 parallel execution, this function may be called
     *                 multiple times and must return a fresh value each time.
     * @param accumulator an associative, non-interfering, stateless function for incorporating an additional element
     *                    into a result
     * @param combiner an associative, non-interfering, stateless function for combining two values, which must be
     *                 compatible with the accumulator function
     * @return the result of the reduction
     */
    <R> R collect(Supplier<R> supplier,
                  BiConsumer<R, ? super T> accumulator,
                  BiConsumer<R, R> combiner);

    /**
     * Performs a mutable reduction operation on the elements of this selection using a {@code Collector}. A
     * {@code Collector} encapsulates the functions used as arguments to
     * {@link #collect(Supplier, BiConsumer, BiConsumer)}, allowing for reuse of collection strategies and composition
     * of collect operations such as multiple-level grouping or partitioning.
     * @param <R> the type of the result
     * @param <A> the intermediate accumulation type of the {@code Collector}
     * @param collector the {@code Collector} describing the reduction
     * @return the result of the reduction
     */
    <R, A> R collect(Collector<? super T, A, R> collector);

    /**
     * Returns the count of elements in this selection.
     * @return the count of elements in this selection
     */
    int count();

    /**
     * Returns whether any elements of this selection match the provided predicate. May not evaluate the predicate on
     * all elements if not necessary for determining the result. If the selection is empty then {@code false} is
     * returned and the predicate is not evaluated.
     * @param predicate a non-interfering, stateless predicate to apply to elements of this selection
     * @return {@code true} if any elements of the selection match the provided predicate, otherwise {@code false}
     */
    boolean anyMatch(Predicate<? super T> predicate);

    /**
     * Returns whether all elements of this selection match the provided predicate. May not evaluate the predicate on
     * all elements if not necessary for determining the result. If the selection is empty then {@code true} is returned
     * and the predicate is not evaluated.
     * @param predicate a non-interfering, stateless predicate to apply to elements of this selection
     * @return {@code true} if either all elements of the selection match the provided predicate or the selection is
     * empty, otherwise {@code false}
     */
    boolean allMatch(Predicate<? super T> predicate);

    /**
     * Returns whether no elements of this selection match the provided predicate. May not evaluate the predicate on
     * all elements if not necessary for determining the result. If the selection is empty then {@code true} is returned
     * and the predicate is not evaluated.
     * @param predicate a non-interfering, stateless predicate to apply to elements of this selection
     * @return {@code true} if either no elements of the selection match the provided predicate or the selection is
     * empty, otherwise {@code false}
     */
    boolean noneMatch(Predicate<? super T> predicate);

    /**
     * Returns whether the count of elements in this selection is zero.
     * @return {@code true} if the count of elements in this selection is zero, otherwise {@code false}
     */
    boolean isEmpty();

    /**
     * Returns whether the count of elements in this selection is not zero.
     * @return {@code true} if the count of elements in this selection is not zero, otherwise {@code false}
     */
    boolean isNotEmpty();
}
