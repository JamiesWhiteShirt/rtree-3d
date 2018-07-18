package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class Leaf<K, V> implements Node<K, V> {

    private final List<EntryBox<K, V>> entryBoxes;
    private final Box box;

    static <K, V> Leaf<K, V> containing(List<EntryBox<K, V>> entryBoxes) {
        return new Leaf<>(entryBoxes, Util.mbb(entryBoxes.stream().map(EntryBox::getBox).collect(Collectors.toList())));
    }

    static <K, V> Leaf<K, V> containing(EntryBox<K, V> entryBox) {
        return new Leaf<>(Collections.singletonList(entryBox), entryBox.getBox());
    }

    Leaf(List<EntryBox<K, V>> entryBoxes, Box box) {
        Preconditions.checkArgument(!entryBoxes.isEmpty());
        this.entryBoxes = entryBoxes;
        this.box = box;
    }

    private List<Node<K, V>> makeLeaves(Groups<EntryBox<K, V>> pair) {
        List<Node<K, V>> list = new ArrayList<>();
        list.add(containing(pair.getGroup1().getEntries()));
        list.add(containing(pair.getGroup2().getEntries()));
        return list;
    }

    @Override
    public List<Node<K, V>> put(EntryBox<K, V> entryBox, Configuration configuration) {
        for (EntryBox<K, V> existingEntryBox : entryBoxes) {
            if (existingEntryBox.getBox().equals(entryBox.getBox())
                && existingEntryBox.getEntry().getKey().equals(entryBox.getEntry().getKey())) {
                return Collections.singletonList(containing(Util.replace(entryBoxes, existingEntryBox, entryBox)));
            }
        }
        final List<EntryBox<K, V>> newLeafEntries = Util.add(entryBoxes, entryBox);
        if (newLeafEntries.size() <= configuration.getMaxChildren()) {
            return Collections.singletonList(containing(newLeafEntries));
        } else {
            Groups<EntryBox<K, V>> pair = configuration.getSplitter().split(newLeafEntries, configuration.getMinChildren(), EntryBox::getBox);
            return makeLeaves(pair);
        }
    }

    @Override
    public NodeAndEntries<K, V> remove(EntryBox<K, V> entryBox, Configuration configuration) {
        if (!entryBoxes.contains(entryBox)) {
            return new NodeAndEntries<>(this, Collections.emptyList(), 0);
        } else {
            final List<EntryBox<K, V>> newEntryBoxes = Util.remove(entryBoxes, entryBox);

            if (newEntryBoxes.size() >= configuration.getMinChildren()) {
                Leaf<K, V> node = newEntryBoxes.isEmpty() ? null : containing(newEntryBoxes);
                return new NodeAndEntries<>(node, Collections.emptyList(), 1);
            } else {
                return new NodeAndEntries<>(null, newEntryBoxes, 1);
            }
        }
    }

    @Override
    public NodeAndEntries<K, V> remove(Box box, K key, Configuration configuration) {
        for (EntryBox<K, V> entryBox : entryBoxes) {
            if (entryBox.getBox().equals(box) && entryBox.getEntry().getKey().equals(key)) {
                final List<EntryBox<K, V>> newEntryBoxes = Util.remove(entryBoxes, entryBox);

                if (newEntryBoxes.size() >= configuration.getMinChildren()) {
                    Leaf<K, V> node = newEntryBoxes.isEmpty() ? null : containing(newEntryBoxes);
                    return new NodeAndEntries<>(node, Collections.emptyList(), 1);
                } else {
                    return new NodeAndEntries<>(null, newEntryBoxes, 1);
                }
            }
        }
        return new NodeAndEntries<>(this, Collections.emptyList(), 0);
    }

    @Override
    public Entry<K, V> get(Box box, K key) {
        for (EntryBox<K, V> entryBox : entryBoxes) {
            if (entryBox.getBox().equals(box) && entryBox.getEntry().getKey().equals(key)) {
                return entryBox.getEntry();
            }
        }
        return null;
    }

    @Override
    public void forEach(Predicate<? super Box> boxPredicate, Consumer<? super Entry<K, V>> consumer) {
        if (boxPredicate.test(box)) {
            for (final EntryBox<K, V> entryBox : entryBoxes) {
                if (boxPredicate.test(entryBox.getBox())) {
                    consumer.accept(entryBox.getEntry());
                }
            }
        }
    }

    @Override
    public boolean anyMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate) {
        if (boxPredicate.test(box)) {
            for (final EntryBox<K, V> entryBox : entryBoxes) {
                if (boxPredicate.test(entryBox.getBox()) && entryPredicate.test(entryBox.getEntry())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean allMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate) {
        if (boxPredicate.test(box)) {
            for (final EntryBox<K, V> entryBox : entryBoxes) {
                if (!boxPredicate.test(entryBox.getBox()) && !entryPredicate.test(entryBox.getEntry())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public <T> T reduce(Predicate<? super Box> boxPredicate, T identity, BiFunction<T, Entry<K, V>, T> operator) {
        if (boxPredicate.test(box)) {
            T acc = identity;
            for (final EntryBox<K, V> entryBox : entryBoxes) {
                if (boxPredicate.test(entryBox.getBox())) {
                    acc = operator.apply(acc, entryBox.getEntry());
                }
            }
            return acc;
        }
        return identity;
    }

    @Override
    public int count(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate) {
        if (boxPredicate.test(box)) {
            int count = 0;
            for (final EntryBox<K, V> entryBox : entryBoxes) {
                if (boxPredicate.test(entryBox.getBox()) && entryPredicate.test(entryBox.getEntry())) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    @Override
    public boolean contains(EntryBox<K, V> entryBox) {
        return entryBoxes.contains(entryBox);
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
        return entryBoxes.size();
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
        for (EntryBox<K, V> entryBox : entryBoxes) {
            s.append(margin).append("  ").append(entryBox.toString());
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return asString("");
    }
}
