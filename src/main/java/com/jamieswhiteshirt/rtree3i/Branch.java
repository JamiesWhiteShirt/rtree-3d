package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class Branch<K, V> implements Node<K, V> {
    private final List<Node<K, V>> children;
    private final Box box;
    private final int size;

    static <K, V> Branch<K, V> containing(List<Node<K, V>> children) {
        return new Branch<>(children, Util.mbb(children.stream().map(Node::getBox).collect(Collectors.toList())));
    }

    Branch(List<Node<K, V>> children, Box box) {
        Preconditions.checkArgument(!children.isEmpty());
        this.children = children;
        this.box = box;
        int size = 0;
        for (Node<K, V> child : children) {
            size += child.size();
        }
        this.size = size;
    }

    private List<Node<K, V>> makeNonLeaves(Groups<Node<K, V>> pair) {
        List<Node<K, V>> list = new ArrayList<>();
        list.add(containing(pair.getGroup1().getEntries()));
        list.add(containing(pair.getGroup2().getEntries()));
        return list;
    }

    private Node<K, V> selectChildForPut(Box box, Configuration configuration) {
        for (final Node<K, V> child : children) {
            if (child.containsBucket(box)) {
                return child;
            }
        }
        return configuration.getSelector().select(box, children);
    }

    @Override
    public List<Node<K, V>> put(Box box, Entry<K, V> entry, Configuration configuration) {
        final Node<K, V> child = selectChildForPut(box, configuration);
        List<Node<K, V>> list = child.put(box, entry, configuration);
        List<Node<K, V>> children2 = Util.replace(children, child, list);
        if (children2.size() <= configuration.getMaxChildren()) {
            return Collections.singletonList(containing(children2));
        } else {
            Groups<Node<K, V>> pair = configuration.getSplitter().split(children2,
                configuration.getMinChildren(), Node::getBox);
            return makeNonLeaves(pair);
        }
    }

    @Override
    public List<Node<K, V>> putBucket(Bucket<K, V> bucket, Configuration configuration) {
        final Node<K, V> child = selectChildForPut(bucket.getBox(), configuration);
        List<Node<K, V>> list = child.putBucket(bucket, configuration);
        List<Node<K, V>> children2 = Util.replace(children, child, list);
        if (children2.size() <= configuration.getMaxChildren()) {
            return Collections.singletonList(containing(children2));
        } else {
            Groups<Node<K, V>> pair = configuration.getSplitter().split(children2,
                configuration.getMinChildren(), Node::getBox);
            return makeNonLeaves(pair);
        }
    }

    @Override
    public NodeAndEntries<K, V> remove(Box box, Entry<K, V> entry, Configuration configuration) {
        // the result of performing a remove of the given entry from this node
        // will be that zero or more entries will be needed to be added back to
        // the root of the tree (because num entries of their node fell below
        // minChildren),
        // zero or more children will need to be removed from this node,
        // zero or more nodes to be added as children to this node(because
        // entries have been deleted from them and they still have enough
        // members to be active)
        List<Bucket<K, V>> addTheseEntries = new ArrayList<>();
        List<Node<K, V>> removeTheseNodes = new ArrayList<>();
        List<Node<K, V>> addTheseNodes = new ArrayList<>();
        int countDeleted = 0;

        for (final Node<K, V> child : children) {
            if (child.getBox().contains(box)) {
                final NodeAndEntries<K, V> result = child.remove(box, entry, configuration);
                if (result.getNode() != null) {
                    if (result.getNode() != child) {
                        // deletion occurred and child is above minChildren so
                        // we update it
                        addTheseNodes.add(result.getNode());
                        removeTheseNodes.add(child);
                        addTheseEntries.addAll(result.getEntriesToAdd());
                        countDeleted += result.countDeleted();
                    }
                    // else nothing was deleted from that child
                } else {
                    // deletion occurred and brought child below minChildren
                    // so we redistribute its entries
                    removeTheseNodes.add(child);
                    addTheseEntries.addAll(result.getEntriesToAdd());
                    countDeleted += result.countDeleted();
                }
            }
        }
        if (removeTheseNodes.isEmpty()) {
            return new NodeAndEntries<>(this, Collections.emptyList(), 0);
        } else {
            List<Node<K, V>> nodes = Util.remove(children, removeTheseNodes);
            nodes.addAll(addTheseNodes);
            if (nodes.size() == 0) {
                return new NodeAndEntries<>(null, addTheseEntries, countDeleted);
            } else {
                Branch<K, V> node = containing(nodes);
                return new NodeAndEntries<>(node, addTheseEntries, countDeleted);
            }
        }
    }

    @Override
    public NodeAndEntries<K, V> remove(Box box, K key, Configuration configuration) {
        // the result of performing a remove of the given entry from this node
        // will be that zero or more entries will be needed to be added back to
        // the root of the tree (because num entries of their node fell below
        // minChildren),
        // zero or more children will need to be removed from this node,
        // zero or more nodes to be added as children to this node(because
        // entries have been deleted from them and they still have enough
        // members to be active)
        List<Bucket<K, V>> addTheseEntries = new ArrayList<>();
        List<Node<K, V>> removeTheseNodes = new ArrayList<>();
        List<Node<K, V>> addTheseNodes = new ArrayList<>();
        int countDeleted = 0;

        for (final Node<K, V> child : children) {
            if (child.getBox().contains(box)) {
                final NodeAndEntries<K, V> result = child.remove(box, key, configuration);
                if (result.getNode() != null) {
                    if (result.getNode() != child) {
                        // deletion occurred and child is above minChildren so
                        // we update it
                        addTheseNodes.add(result.getNode());
                        removeTheseNodes.add(child);
                        addTheseEntries.addAll(result.getEntriesToAdd());
                        countDeleted += result.countDeleted();
                    }
                    // else nothing was deleted from that child
                } else {
                    // deletion occurred and brought child below minChildren
                    // so we redistribute its entries
                    removeTheseNodes.add(child);
                    addTheseEntries.addAll(result.getEntriesToAdd());
                    countDeleted += result.countDeleted();
                }
            }
        }
        if (removeTheseNodes.isEmpty()) {
            return new NodeAndEntries<>(this, Collections.emptyList(), 0);
        } else {
            List<Node<K, V>> nodes = Util.remove(children, removeTheseNodes);
            nodes.addAll(addTheseNodes);
            if (nodes.size() == 0) {
                return new NodeAndEntries<>(null, addTheseEntries, countDeleted);
            } else {
                Branch<K, V> node = containing(nodes);
                return new NodeAndEntries<>(node, addTheseEntries, countDeleted);
            }
        }
    }

    @Override
    public Entry<K, V> get(Box box, K key) {
        for (final Node<K, V> child : children) {
            if (child.getBox().contains(box)) {
                Entry<K, V> entry = child.get(box, key);
                if (entry != null) return entry;
            }
        }
        return null;
    }

    @Override
    public void forEach(Predicate<? super Box> boxPredicate, Consumer<? super Entry<K, V>> action) {
        if (boxPredicate.test(box)) {
            for (final Node<K, V> child : children) {
                child.forEach(boxPredicate, action);
            }
        }
    }

    @Override
    public boolean anyMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate) {
        if (boxPredicate.test(box)) {
            for (final Node<K, V> child : children) {
                if (child.anyMatch(boxPredicate, entryPredicate)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean allMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate) {
        if (boxPredicate.test(box)) {
            for (final Node<K, V> child : children) {
                if (!child.allMatch(boxPredicate, entryPredicate)) {
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
            for (final Node<K, V> child : children) {
                acc = child.reduce(boxPredicate, acc, operator);
            }
            return acc;
        }
        return identity;
    }

    @Override
    public int count(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate) {
        if (boxPredicate.test(box)) {
            int count = 0;
            for (final Node<K, V> child : children) {
                count += child.count(boxPredicate, entryPredicate);
            }
            return count;
        }
        return 0;
    }

    @Override
    public boolean contains(Box box, Entry<K, V> entry) {
        if (this.box.contains(box)) {
            for (final Node<K, V> child : children) {
                if (child.contains(box, entry)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsBucket(Box box) {
        if (this.box.contains(box)) {
            for (final Node<K, V> child : children) {
                if (child.containsBucket(box)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int calculateDepth() {
        return children.get(0).calculateDepth() + 1;
    }

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public String asString(String margin) {
        StringBuilder s = new StringBuilder();
        s.append(margin);
        s.append("mbb=");
        s.append(getBox());
        s.append('\n');
        for (Node<K, V> child : children) {
            s.append(child.asString("  " + margin));
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return asString("");
    }
}
