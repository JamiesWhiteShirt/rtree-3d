package com.jamieswhiteshirt.rtree3i;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class Branch<T> implements Node<T> {
    private final List<Node<T>> children;
    private final Box box;
    private final int size;

    static <T> Branch<T> containing(List<Node<T>> children) {
        return new Branch<>(children, Util.mbb(children.stream().map(Node::getBox).collect(Collectors.toList())));
    }

    public Branch(List<Node<T>> children, Box box) {
        Preconditions.checkArgument(!children.isEmpty());
        this.children = children;
        this.box = box;
        int size = 0;
        for (Node<T> child : children) {
            size += child.size();
        }
        this.size = size;
    }

    private List<Node<T>> makeNonLeaves(Groups<Node<T>> pair) {
        List<Node<T>> list = new ArrayList<>();
        list.add(containing(pair.getGroup1().getEntries()));
        list.add(containing(pair.getGroup2().getEntries()));
        return list;
    }

    @Override
    public List<Node<T>> multimapPut(Entry<T> entry, Configuration configuration) {
        if (!contains(entry)) {
            final Node<T> child = configuration.getSelector().select(entry.getBox(), children);
            List<Node<T>> list = child.multimapPut(entry, configuration);
            List<Node<T>> children2 = Util.replace(children, child, list);
            if (children2.size() <= configuration.getMaxChildren()) {
                return Collections.singletonList(containing(children2));
            } else {
                Groups<Node<T>> pair = configuration.getSplitter().split(children2,
                    configuration.getMinChildren(), Node::getBox);
                return makeNonLeaves(pair);
            }
        } else {
            return Collections.singletonList(this);
        }
    }

    @Override
    public List<Node<T>> mapPut(Entry<T> entry, Configuration configuration) {
        if (!contains(entry)) {
            final Node<T> child = configuration.getSelector().select(entry.getBox(), children);
            List<Node<T>> list = child.mapPut(entry, configuration);
            List<Node<T>> children2 = Util.replace(children, child, list);
            if (children2.size() <= configuration.getMaxChildren()) {
                return Collections.singletonList(containing(children2));
            } else {
                Groups<Node<T>> pair = configuration.getSplitter().split(children2,
                    configuration.getMinChildren(), Node::getBox);
                return makeNonLeaves(pair);
            }
        } else {
            return Collections.singletonList(this);
        }
    }

    @Override
    public NodeAndEntries<T> remove(Entry<T> entry, Configuration configuration) {
        // the result of performing a remove of the given entry from this node
        // will be that zero or more entries will be needed to be added back to
        // the root of the tree (because num entries of their node fell below
        // minChildren),
        // zero or more children will need to be removed from this node,
        // zero or more nodes to be added as children to this node(because
        // entries have been deleted from them and they still have enough
        // members to be active)
        List<Entry<T>> addTheseEntries = new ArrayList<>();
        List<Node<T>> removeTheseNodes = new ArrayList<>();
        List<Node<T>> addTheseNodes = new ArrayList<>();
        int countDeleted = 0;

        for (final Node<T> child : children) {
            if (child.getBox().contains(entry.getBox())) {
                final NodeAndEntries<T> result = child.remove(entry, configuration);
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
        if (removeTheseNodes.isEmpty())
            return new NodeAndEntries<>(this, Collections.emptyList(), 0);
        else {
            List<Node<T>> nodes = Util.remove(children, removeTheseNodes);
            nodes.addAll(addTheseNodes);
            if (nodes.size() == 0)
                return new NodeAndEntries<>(null, addTheseEntries, countDeleted);
            else {
                Branch<T> node = containing(nodes);
                return new NodeAndEntries<>(node, addTheseEntries, countDeleted);
            }
        }
    }

    @Override
    public NodeAndEntries<T> mapRemove(Box box, Configuration configuration) {
        // the result of performing a remove of the given entry from this node
        // will be that zero or more entries will be needed to be added back to
        // the root of the tree (because num entries of their node fell below
        // minChildren),
        // zero or more children will need to be removed from this node,
        // zero or more nodes to be added as children to this node(because
        // entries have been deleted from them and they still have enough
        // members to be active)
        List<Entry<T>> addTheseEntries = new ArrayList<>();
        List<Node<T>> removeTheseNodes = new ArrayList<>();
        List<Node<T>> addTheseNodes = new ArrayList<>();
        int countDeleted = 0;

        for (final Node<T> child : children) {
            if (child.getBox().contains(box)) {
                final NodeAndEntries<T> result = child.mapRemove(box, configuration);
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
        if (removeTheseNodes.isEmpty())
            return new NodeAndEntries<>(this, Collections.emptyList(), 0);
        else {
            List<Node<T>> nodes = Util.remove(children, removeTheseNodes);
            nodes.addAll(addTheseNodes);
            if (nodes.size() == 0)
                return new NodeAndEntries<>(null, addTheseEntries, countDeleted);
            else {
                Branch<T> node = containing(nodes);
                return new NodeAndEntries<>(node, addTheseEntries, countDeleted);
            }
        }
    }

    @Override
    public Entry<T> mapGet(Box box) {
        for (final Node<T> child : children) {
            if (child.getBox().contains(box)) {
                Entry<T> entry = child.mapGet(box);
                if (entry != null) return entry;
            }
        }
        return null;
    }

    @Override
    public void forEach(Predicate<? super Box> criterion, Consumer<? super Entry<T>> consumer) {
        if (criterion.test(box)) {
            for (final Node<T> child : children) {
                child.forEach(criterion, consumer);
            }
        }
    }

    @Override
    public boolean any(Predicate<? super Box> condition, Predicate<? super Entry<T>> test) {
        if (condition.test(box)) {
            for (final Node<T> child : children) {
                if (child.any(condition, test)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean all(Predicate<? super Box> condition, Predicate<? super Entry<T>> test) {
        if (condition.test(box)) {
            for (final Node<T> child : children) {
                if (!child.all(condition, test)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean contains(Entry<T> entry) {
        if (!box.contains(entry.getBox())) return false;

        for (final Node<T> child : children) {
            if (child.contains(entry)) {
                return true;
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
        for (Node<T> child : children) {
            s.append(child.asString("  " + margin));
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return asString("");
    }
}
