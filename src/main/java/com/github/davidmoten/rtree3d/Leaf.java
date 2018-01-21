package com.github.davidmoten.rtree3d;

import static com.google.common.base.Optional.of;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.Geometry;
import com.github.davidmoten.rtree3d.geometry.ListPair;
import com.google.common.base.Optional;

final class Leaf<T, S extends Geometry> implements Node<T, S> {

    private final List<Entry<T, S>> entries;
    private final Box mbr;
    private final Context context;

    Leaf(List<Entry<T, S>> entries, Context context) {
        this(entries, Util.mbr(entries), context);
    }
    
    Leaf(List<Entry<T, S>> entries, Box mbr, Context context) {
        this.entries = entries;
        this.context = context;
        this.mbr = mbr;
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    List<Entry<T, S>> entries() {
        return entries;
    }

    @Override
    public void search(Function<? super Geometry, Boolean> condition,
            Consumer<? super Entry<T, S>> consumer) {

        if (!condition.apply(this.geometry().mbb()))
            return;

        for (final Entry<T, S> entry : entries) {
            if (condition.apply(entry.geometry()))
                consumer.accept(entry);
        }
    }

    @Override
    public int count() {
        return entries.size();
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        @SuppressWarnings("unchecked")
        final List<Entry<T, S>> entries2 = Util.add(entries, (Entry<T, S>) entry);
        if (entries2.size() <= context.maxChildren())
            return Collections.singletonList((Node<T, S>) new Leaf<T, S>(entries2, context));
        else {
            ListPair<Entry<T, S>> pair = context.splitter().split(entries2, context.minChildren());
            return makeLeaves(pair);
        }
    }

    private List<Node<T, S>> makeLeaves(ListPair<Entry<T, S>> pair) {
        List<Node<T, S>> list = new ArrayList<Node<T, S>>();
        list.add(new Leaf<T, S>(pair.group1().list(), context));
        list.add(new Leaf<T, S>(pair.group2().list(), context));
        return list;
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        if (!entries.contains(entry)) {
            return new NodeAndEntries<T, S>(java.util.Optional.of(this), Collections.<Entry<T, S>> emptyList(), 0);
        } else {
            final List<Entry<T, S>> entries2 = new ArrayList<Entry<T, S>>(entries);
            entries2.remove(entry);
            int numDeleted = 1;
            // keep deleting if all specified
            while (all && entries2.remove(entry))
                numDeleted += 1;

            if (entries2.size() >= context.minChildren()) {
                Leaf<T, S> node = new Leaf<T, S>(entries2, context);
                return new NodeAndEntries<T, S>(java.util.Optional.of(node), Collections.<Entry<T, S>> emptyList(),
                        numDeleted);
            } else {
                return new NodeAndEntries<T, S>(java.util.Optional.<Node<T, S>>empty(), entries2,
                        numDeleted);
            }
        }
    }

}
