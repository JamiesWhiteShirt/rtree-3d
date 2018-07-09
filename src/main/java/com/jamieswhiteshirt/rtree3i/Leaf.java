package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class Leaf<T> implements Node<T> {

    private final List<Entry<T>> entries;
    private final Box box;

    static <T> Leaf<T> containing(List<Entry<T>> entries) {
        return new Leaf<>(entries, Util.mbb(entries.stream().map(Entry::getBox).collect(Collectors.toList())));
    }

    Leaf(List<Entry<T>> entries, Box box) {
        Preconditions.checkArgument(!entries.isEmpty());
        this.entries = entries;
        this.box = box;
    }

    private List<Node<T>> makeLeaves(Groups<Entry<T>> pair) {
        List<Node<T>> list = new ArrayList<>();
        list.add(containing(pair.getGroup1().getEntries()));
        list.add(containing(pair.getGroup2().getEntries()));
        return list;
    }

    @Override
    public List<Node<T>> multimapPut(Entry<T> entry, Configuration configuration) {
        if (!entries.contains(entry)) {
            final List<Entry<T>> entries2 = Util.add(entries, entry);
            if (entries2.size() <= configuration.getMaxChildren()) {
                return Collections.singletonList(containing(entries2));
            } else {
                Groups<Entry<T>> pair = configuration.getSplitter().split(entries2, configuration.getMinChildren(), Entry::getBox);
                return makeLeaves(pair);
            }
        } else {
            return Collections.singletonList(this);
        }
    }

    @Override
    public List<Node<T>> mapPut(Entry<T> entry, Configuration configuration) {
        for (Entry<T> existingEntry : entries) {
            if (existingEntry.getBox().equals(entry.getBox())) {
                return Collections.singletonList(containing(Util.replace(entries, existingEntry, entry)));
            }
        }
        final List<Entry<T>> entries2 = Util.add(entries, entry);
        if (entries2.size() <= configuration.getMaxChildren()) {
            return Collections.singletonList(containing(entries2));
        } else {
            Groups<Entry<T>> pair = configuration.getSplitter().split(entries2, configuration.getMinChildren(), Entry::getBox);
            return makeLeaves(pair);
        }
    }

    @Override
    public NodeAndEntries<T> remove(Entry<T> entry, Configuration configuration) {
        if (!entries.contains(entry)) {
            return new NodeAndEntries<>(this, Collections.emptyList(), 0);
        } else {
            final List<Entry<T>> entries2 = new ArrayList<>(entries);
            entries2.remove(entry);

            if (entries2.size() >= configuration.getMinChildren()) {
                Leaf<T> node = entries2.isEmpty() ? null : containing(entries2);
                return new NodeAndEntries<>(node, Collections.emptyList(), 1);
            } else {
                return new NodeAndEntries<>(null, entries2, 1);
            }
        }
    }

    @Override
    public NodeAndEntries<T> mapRemove(Box box, Configuration configuration) {
        for (Entry<T> entry : entries) {
            if (entry.getBox().equals(box)) {
                final List<Entry<T>> entries2 = new ArrayList<>(entries);
                entries2.remove(entry);

                if (entries2.size() >= configuration.getMinChildren()) {
                    Leaf<T> node = entries2.isEmpty() ? null : containing(entries2);
                    return new NodeAndEntries<>(node, Collections.emptyList(), 1);
                } else {
                    return new NodeAndEntries<>(null, entries2, 1);
                }
            }
        }
        return new NodeAndEntries<>(this, Collections.emptyList(), 0);
    }

    @Override
    public Entry<T> mapGet(Box box) {
        for (Entry<T> entry : entries) {
            if (entry.getBox().equals(box)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public void forEach(Predicate<? super Box> condition, Consumer<? super Entry<T>> consumer) {
        if (condition.test(box)) {
            for (final Entry<T> entry : entries) {
                if (condition.test(entry.getBox())) {
                    consumer.accept(entry);
                }
            }
        }
    }

    @Override
    public boolean any(Predicate<? super Box> condition, Predicate<? super Entry<T>> test) {
        if (condition.test(box)) {
            for (Entry<T> entry : entries) {
                if (test.test(entry)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean all(Predicate<? super Box> condition, Predicate<? super Entry<T>> test) {
        if (condition.test(box)) {
            for (Entry<T> entry : entries) {
                if (!test.test(entry)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean contains(Entry<T> entry) {
        return box.contains(entry.getBox()) && entries.contains(entry);
    }

    @Override
    public int calculateDepth() {
        return 1;
    }

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String asString(String margin) {
        StringBuilder s = new StringBuilder();
        s.append(margin);
        s.append("mbb=");
        s.append(getBox());
        s.append('\n');
        for (Entry<T> entry : entries) {
            s.append(margin).append("  ").append(entry.toString());
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return asString("");
    }
}
