package com.github.davidmoten.rtree3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.Groups;

final class Leaf<T> implements Node<T> {

    private final List<Entry<T>> entries;
    private final Box box;

    Leaf(List<Entry<T>> entries) {
        this(entries, Util.mbb(entries));
    }
    
    Leaf(List<Entry<T>> entries, Box box) {
        this.entries = entries;
        this.box = box;
    }

    List<Entry<T>> getEntries() {
        return entries;
    }

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public void search(Function<Box, Boolean> condition, Consumer<? super Entry<T>> consumer) {
        if (!condition.apply(box))
            return;

        for (final Entry<T> entry : entries) {
            if (condition.apply(entry.getBox()))
                consumer.accept(entry);
        }
    }

    @Override
    public int countEntries() {
        return entries.size();
    }

    @Override
    public int calculateDepth() {
        return 1;
    }

    @Override
    public List<Node<T>> add(Entry<? extends T> entry, Context context) {
        @SuppressWarnings("unchecked")
        final List<Entry<T>> entries2 = Util.add(entries, (Entry<T>) entry);
        if (entries2.size() <= context.getMaxChildren())
            return Collections.singletonList(new Leaf<>(entries2));
        else {
            Groups<Entry<T>> pair = context.getSplitter().split(entries2, context.getMinChildren());
            return makeLeaves(pair);
        }
    }

    private List<Node<T>> makeLeaves(Groups<Entry<T>> pair) {
        List<Node<T>> list = new ArrayList<>();
        list.add(new Leaf<>(pair.getGroup1().getEntries()));
        list.add(new Leaf<>(pair.getGroup2().getEntries()));
        return list;
    }

    @Override
    public NodeAndEntries<T> delete(Entry<? extends T> entry, boolean all, Context context) {
        if (!entries.contains(entry)) {
            return new NodeAndEntries<>(this, Collections.emptyList(), 0);
        } else {
            final List<Entry<T>> entries2 = new ArrayList<>(entries);
            entries2.remove(entry);
            int numDeleted = 1;
            // keep deleting if all specified
            while (all && entries2.remove(entry))
                numDeleted += 1;

            if (entries2.size() >= context.getMinChildren()) {
                Leaf<T> node = new Leaf<>(entries2);
                return new NodeAndEntries<>(node, Collections.emptyList(), numDeleted);
            } else {
                return new NodeAndEntries<>(null, entries2, numDeleted);
            }
        }
    }

}
