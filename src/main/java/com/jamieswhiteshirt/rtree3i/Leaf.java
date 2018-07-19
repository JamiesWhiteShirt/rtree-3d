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

    private final List<Bucket<K, V>> buckets;
    private final Box box;

    static <K, V> Leaf<K, V> containing(List<Bucket<K, V>> buckets) {
        return new Leaf<>(buckets, Util.mbb(buckets.stream().map(Bucket::getBox).collect(Collectors.toList())));
    }

    static <K, V> Leaf<K, V> containing(Bucket<K, V> bucket) {
        return new Leaf<>(Collections.singletonList(bucket), bucket.getBox());
    }

    Leaf(List<Bucket<K, V>> buckets, Box box) {
        Preconditions.checkArgument(!buckets.isEmpty());
        this.buckets = buckets;
        this.box = box;
    }

    private List<Node<K, V>> makeLeaves(Groups<Bucket<K, V>> pair) {
        List<Node<K, V>> list = new ArrayList<>();
        list.add(containing(pair.getGroup1().getEntries()));
        list.add(containing(pair.getGroup2().getEntries()));
        return list;
    }

    @Override
    public List<Node<K, V>> put(Box box, Entry<K, V> entry, Configuration configuration) {
        for (Bucket<K, V> bucket : buckets) {
            if (bucket.getBox().equals(box)) {
                return Collections.singletonList(containing(Util.replace(buckets, bucket, bucket.put(entry))));
            }
        }
        Bucket<K, V> bucket = Bucket.of(box, entry);
        final List<Bucket<K, V>> newBuckets = Util.add(buckets, bucket);
        if (newBuckets.size() <= configuration.getMaxChildren()) {
            return Collections.singletonList(containing(newBuckets));
        } else {
            Groups<Bucket<K, V>> pair = configuration.getSplitter().split(newBuckets, configuration.getMinChildren(), Bucket::getBox);
            return makeLeaves(pair);
        }
    }

    @Override
    public List<Node<K, V>> putBucket(Bucket<K, V> bucket, Configuration configuration) {
        for (Bucket<K, V> existingBucket : buckets) {
            if (existingBucket.getBox().equals(bucket.getBox())) {
                return Collections.singletonList(containing(Util.replace(buckets, existingBucket, bucket)));
            }
        }
        final List<Bucket<K, V>> newBuckets = Util.add(buckets, bucket);
        if (newBuckets.size() <= configuration.getMaxChildren()) {
            return Collections.singletonList(containing(newBuckets));
        } else {
            Groups<Bucket<K, V>> pair = configuration.getSplitter().split(newBuckets, configuration.getMinChildren(), Bucket::getBox);
            return makeLeaves(pair);
        }
    }

    @Override
    public NodeAndEntries<K, V> remove(Box box, Entry<K, V> entry, Configuration configuration) {
        for (Bucket<K, V> bucket : buckets) {
            if (bucket.getBox().equals(box)) {
                Bucket<K, V> newBucket = bucket.remove(entry);
                List<Bucket<K, V>> newBuckets;
                if (newBucket == null) {
                    newBuckets = Util.remove(buckets, bucket);
                } else {
                    newBuckets = Util.replace(buckets, bucket, newBucket);
                }

                if (newBuckets.size() >= configuration.getMinChildren()) {
                    Leaf<K, V> node = newBuckets.isEmpty() ? null : containing(newBuckets);
                    return new NodeAndEntries<>(node, Collections.emptyList(), 1);
                } else {
                    return new NodeAndEntries<>(null, newBuckets, 1);
                }
            }
        }
        return new NodeAndEntries<>(this, Collections.emptyList(), 0);
    }

    @Override
    public NodeAndEntries<K, V> remove(Box box, K key, Configuration configuration) {
        for (Bucket<K, V> bucket : buckets) {
            if (bucket.getBox().equals(box)) {
                Bucket<K, V> newBucket = bucket.remove(key);
                List<Bucket<K, V>> newBuckets;
                if (newBucket == null) {
                    newBuckets = Util.remove(buckets, bucket);
                } else {
                    newBuckets = Util.replace(buckets, bucket, newBucket);
                }

                if (newBuckets.size() >= configuration.getMinChildren()) {
                    Leaf<K, V> node = newBuckets.isEmpty() ? null : containing(newBuckets);
                    return new NodeAndEntries<>(node, Collections.emptyList(), 1);
                } else {
                    return new NodeAndEntries<>(null, newBuckets, 1);
                }
            }
        }
        return new NodeAndEntries<>(this, Collections.emptyList(), 0);
    }

    @Override
    public Entry<K, V> get(Box box, K key) {
        for (Bucket<K, V> bucket : buckets) {
            if (bucket.getBox().equals(box)) {
                return bucket.get(key);
            }
        }
        return null;
    }

    @Override
    public void forEach(Predicate<? super Box> boxPredicate, Consumer<? super Entry<K, V>> action) {
        if (boxPredicate.test(box)) {
            for (final Bucket<K, V> bucket : buckets) {
                if (boxPredicate.test(bucket.getBox())) {
                    bucket.forEach(action);
                }
            }
        }
    }

    @Override
    public boolean anyMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate) {
        if (boxPredicate.test(box)) {
            for (final Bucket<K, V> bucket : buckets) {
                if (boxPredicate.test(bucket.getBox()) && bucket.anyMatch(entryPredicate)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean allMatch(Predicate<? super Box> boxPredicate, Predicate<? super Entry<K, V>> entryPredicate) {
        if (boxPredicate.test(box)) {
            for (final Bucket<K, V> bucket : buckets) {
                if (!boxPredicate.test(bucket.getBox()) && !bucket.allMatch(entryPredicate)) {
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
            for (final Bucket<K, V> bucket : buckets) {
                if (boxPredicate.test(bucket.getBox())) {
                    acc = bucket.reduce(acc, operator);
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
            for (final Bucket<K, V> bucket : buckets) {
                if (boxPredicate.test(bucket.getBox())) {
                    count += bucket.count(entryPredicate);
                }
            }
            return count;
        }
        return 0;
    }

    @Override
    public boolean contains(Box box, Entry<K, V> entry) {
        if (this.box.contains(box)) {
            for (final Bucket<K, V> bucket : buckets) {
                if (bucket.getBox().equals(box)) {
                    return bucket.contains(entry);
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsBucket(Box box) {
        if (this.box.contains(box)) {
            for (final Bucket<K, V> bucket : buckets) {
                if (bucket.getBox().equals(box)) return true;
            }
        }
        return false;
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
        int size = 0;
        for (final Bucket<K, V> bucket : buckets) {
            size += bucket.size();
        }
        return size;
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
        for (Bucket<K, V> bucket : buckets) {
            s.append(margin).append("  ").append(bucket.toString());
        }
        return s.toString();
    }

    @Override
    public String toString() {
        return asString("");
    }
}
