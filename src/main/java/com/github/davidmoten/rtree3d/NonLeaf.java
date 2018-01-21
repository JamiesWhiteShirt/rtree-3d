package com.github.davidmoten.rtree3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.Groups;
import com.google.common.base.Preconditions;

final class NonLeaf<T> implements Node<T> {

    private final List<? extends Node<T>> children;
    private final Box box;

    NonLeaf(List<? extends Node<T>> children) {
        this(children, Util.mbr(children));
    }
    
    NonLeaf(List<? extends Node<T>> children, Box box) {
        Preconditions.checkArgument(!children.isEmpty());
        this.children = children;
        this.box = box;
    }

    @Override
    public Box getBox() {
        return box;
    }

    @Override
    public void search(Function<Box, Boolean> criterion,
            Consumer<? super Entry<T>> consumer) {

        if (!criterion.apply(box))
            return;

        for (final Node<T> child : children) {
            child.search(criterion, consumer);
        }
    }

    @Override
    public int count() {
        return children.size();
    }

    List<? extends Node<T>> children() {
        return children;
    }

    @Override
    public List<Node<T>> add(Entry<? extends T> entry, Context context) {
        final Node<T> child = context.selector().select(entry.getBox(), children);
        List<Node<T>> list = child.add(entry, context);
        List<? extends Node<T>> children2 = Util.replace(children, child, list);
        if (children2.size() <= context.maxChildren())
            return Collections.singletonList(new NonLeaf<>(children2));
        else {
            Groups<? extends Node<T>> pair = context.splitter().split(children2,
                    context.minChildren());
            return makeNonLeaves(pair);
        }
    }

    private List<Node<T>> makeNonLeaves(Groups<? extends Node<T>> pair) {
        List<Node<T>> list = new ArrayList<>();
        list.add(new NonLeaf<>(pair.group1().entries()));
        list.add(new NonLeaf<>(pair.group2().entries()));
        return list;
    }

    @Override
    public NodeAndEntries<T> delete(Entry<? extends T> entry, boolean all, Context context) {
        // the result of performing a delete of the given entry from this node
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
            if (entry.getBox().intersects(child.getBox())) {
                final NodeAndEntries<T> result = child.delete(entry, all, context);
                if (result.node() != null) {
                    if (result.node() != child) {
                        // deletion occurred and child is above minChildren so
                        // we update it
                        addTheseNodes.add(result.node());
                        removeTheseNodes.add(child);
                        addTheseEntries.addAll(result.entriesToAdd());
                        countDeleted += result.countDeleted();
                        if (!all)
                            break;
                    }
                    // else nothing was deleted from that child
                } else {
                    // deletion occurred and brought child below minChildren
                    // so we redistribute its entries
                    removeTheseNodes.add(child);
                    addTheseEntries.addAll(result.entriesToAdd());
                    countDeleted += result.countDeleted();
                    if (!all)
                        break;
                }
            }
        }
        if (removeTheseNodes.isEmpty())
            return new NodeAndEntries<>(this, Collections.emptyList(), 0);
        else {
            List<Node<T>> nodes = Util.remove(children, removeTheseNodes);
            nodes.addAll(addTheseNodes);
            if (nodes.size() == 0)
                return new NodeAndEntries<>(null, addTheseEntries,
                        countDeleted);
            else {
                NonLeaf<T> node = new NonLeaf<>(nodes);
                return new NodeAndEntries<>(node, addTheseEntries, countDeleted);
            }
        }
    }
}