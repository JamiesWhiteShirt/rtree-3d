package com.jamieswhiteshirt.rtree3i;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Immutable map of K to V in an R-Tree.
 * A key-box mapper is applied to keys place entries with {@link Box} in the R-Tree.
 * Keys must be effectively immutable, and the key-box-mapper must be effectively pure.
 * @param <K> the type of keys to be maintained by this map
 * @param <V> the type of mapped values
 */
public final class RTreeMap<K, V> {
    /**
     * Create a new RTreeMap with {@link Box} keys using the given configuration.
     * @param configuration configuration for the R-tree
     * @param <V> the type of mapped values
     * @return a new RTreeMap
     */
    public static <V> RTreeMap<Box, V> create(Configuration configuration) {
        return create(configuration, Function.identity());
    }

    /**
     * Create a new RTreeMap using the given configuration and key-box mapper.
     * @param configuration configuration for the R-tree
     * @param keyBoxMapper key-box mapper
     * @param <K> the key type
     * @param <V> the value type
     * @return a new RTreeMap
     */
    public static <K, V> RTreeMap<K, V> create(Configuration configuration, Function<? super K, Box> keyBoxMapper) {
        return new RTreeMap<>(null, configuration, keyBoxMapper);
    }

    private final Node<K, V> root;
    private final Configuration configuration;
    private final Function<? super K, Box> keyBoxMapper;

    private RTreeMap(Node<K, V> root, Configuration configuration, Function<? super K, Box> keyBoxMapper) {
        this.root = root;
        this.configuration = configuration;
        this.keyBoxMapper = keyBoxMapper;
    }

    /**
     * Returns a {@link Selection} of all keys.
     * @return a {@link Selection} of all keys
     */
    public Selection<K> keys() {
        return keys(box -> true);
    }

    /**
     * Returns a {@link Selection} of keys matching the given box predicate.
     * @param boxPredicate predicate applied to bounding boxes in the RTreeMap. The predicate is expected to have this
     *                     property: For all boxes B, if the predicate matches B, it must match all boxes containing B.
     * @return a {@link Selection} of keys matching the given box predicates
     */
    public Selection<K> keys(Predicate<? super Box> boxPredicate) {
        return root != null ? NodeSelection.create(root, boxPredicate, Entry::getKey) : EmptySelection.create();
    }

    /**
     * Returns a {@link Selection} of all entries.
     * @return a {@link Selection} of all entries
     */
    public Selection<V> values() {
        return values(box -> true);
    }

    /**
     * Returns a {@link Selection} of values matching the given box predicate.
     * @param boxPredicate predicate applied to bounding boxes in the RTreeMap. The predicate is expected to have this
     *                     property: For all boxes B, if the predicate matches B, it must match all boxes containing B.
     * @return a {@link Selection} of values matching the given box predicates
     */
    public Selection<V> values(Predicate<? super Box> boxPredicate) {
        return root != null ? NodeSelection.create(root, boxPredicate, Entry::getValue) : EmptySelection.create();
    }

    /**
     * Returns a {@link Selection} of all entries.
     * @return a {@link Selection} of all entries
     */
    public Selection<Entry<K, V>> entries() {
        return entries(box -> true);
    }

    /**
     * Returns a {@link Selection} of entries matching the given box predicate.
     * @param boxPredicate predicate applied to bounding boxes in the RTreeMap. The predicate is expected to have this
     *                     property: For all boxes B, if the predicate matches B, it must match all boxes containing B.
     * @return a {@link Selection} of entries matching the given box predicates
     */
    public Selection<Entry<K, V>> entries(Predicate<? super Box> boxPredicate) {
        return root != null ? NodeSelection.create(root, boxPredicate, Function.identity()) : EmptySelection.create();
    }

    /**
     * The tree is scanned for depth and the depth returned. This involves recursing down to the leaf level of the tree
     * to get the current depth. Should be <code>log(n)</code> in complexity.
     * @return depth of the R-tree
     */
    public int calculateDepth() {
        return root != null ? root.calculateDepth() : 0;
    }

    public boolean contains(Entry<K, V> entry) {
        return root != null && root.contains(EntryBox.of(keyBoxMapper, entry));
    }

    /**
     * Returns a copy of the RTreeMap including the given entry. If the RTreeMap already contains a mapping for the key
     * of the entry, the old entry is replaced by the specified entry.
     * @param entry entry to be added to the R-tree
     * @return a copy of the RTreeMap including the given entry
     */
    public RTreeMap<K, V> put(Entry<K, V> entry) {
        EntryBox<K, V> entryBox = EntryBox.of(keyBoxMapper, entry);
        if (root != null) {
            List<Node<K, V>> nodes = root.put(entryBox, configuration);
            Node<K, V> node;
            if (nodes.size() == 1)
                node = nodes.get(0);
            else {
                node = Branch.containing(nodes);
            }
            return new RTreeMap<>(node, configuration, keyBoxMapper);
        } else {
            return new RTreeMap<>(Leaf.containing(entryBox), configuration, keyBoxMapper);
        }
    }

    /**
     * Returns a copy of the RTreeMap that associates the specified value with the specified key. If the RTreeMap
     * already contains a mapping for the key, the existing value is replaced by the specified value.
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return a copy of the RTreeMap that associates the specified value with the specified key
     */
    public RTreeMap<K, V> put(K key, V value) {
        return put(Entry.of(key, value));
    }

    /**
     * Returns a copy of the RTreeMap with the current entries and the additional given entries added.
     * @param entries entries to add
     * @return R-tree with entries added
     */
    public RTreeMap<K, V> putAll(Iterable<Entry<K, V>> entries) {
        RTreeMap<K, V> tree = this;
        for (Entry<K, V> entry : entries) {
            tree = tree.put(entry);
        }
        return tree;
    }

    /**
     * Returns a copy of the RTreeMap excluding the given entries.
     * @param entries entries to remove
     * @return a copy of the RTreeMap excluding the given entries
     */
    public RTreeMap<K, V> removeAll(Iterable<Entry<K, V>> entries) {
        RTreeMap<K, V> tree = this;
        for (Entry<K, V> entry : entries) {
            tree = tree.remove(entry);
        }
        return tree;
    }

    /**
     * Returns a copy of the RTreeMap without the entry for the specified key only if it is currently mapped
     * to the specified value.
     * @param key key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return a copy of the RTreeMap without the entry
     */
    public RTreeMap<K, V> remove(K key, V value) {
        return remove(Entry.of(key, value));
    }

    /**
     * Returns a copy of the RTreeMap without the entry.
     * @param entry the entry to be removed
     * @return a copy of the RTreeMap without the entry
     */
    public RTreeMap<K, V> remove(Entry<K, V> entry) {
        if (root != null) {
            EntryBox<K, V> removeEntryBox = EntryBox.of(keyBoxMapper, entry);
            NodeAndEntries<K, V> nodeAndEntries = root.remove(removeEntryBox, configuration);
            if (nodeAndEntries.getNode() == root) {
                return this;
            } else {
                Node<K, V> node = nodeAndEntries.getNode();
                for (EntryBox<K, V> entryBox : nodeAndEntries.getEntriesToAdd()) {
                    if (node != null) {
                        List<Node<K, V>> nodes = node.put(entryBox, configuration);
                        if (nodes.size() == 1) {
                            node = nodes.get(0);
                        } else {
                            node = Branch.containing(nodes);
                        }
                    } else {
                        node = Leaf.containing(entryBox);
                    }
                }
                return new RTreeMap<>(node, configuration, keyBoxMapper);
            }
        }
        return this;
    }

    /**
     * Returns a copy of the RTreeMap without the mapping for the key if it is present.
     * @param key key whose mapping is to be deleted from the RTreeMap
     * @return a copy of the RTreeMap without the mapping
     */
    public RTreeMap<K, V> remove(K key) {
        if (root != null) {
            NodeAndEntries<K, V> nodeAndEntries = root.remove(keyBoxMapper.apply(key), key, configuration);
            if (nodeAndEntries.getNode() == root) {
                return this;
            } else {
                Node<K, V> node = nodeAndEntries.getNode();
                for (EntryBox<K, V> entryBox : nodeAndEntries.getEntriesToAdd()) {
                    if (node != null) {
                        List<Node<K, V>> nodes = node.put(entryBox, configuration);
                        if (nodes.size() == 1) {
                            node = nodes.get(0);
                        } else {
                            node = Branch.containing(nodes);
                        }
                    } else {
                        node = Leaf.containing(entryBox);
                    }
                }
                return new RTreeMap<>(node, configuration, keyBoxMapper);
            }
        }
        return this;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this RTreeMap contains no mapping for
     * the key
     * @param key the key whose associated value will be returned
     * @return the value to which the specified key is mapped, or {@code null} if this RTreeMap contains no mapping for
     *         the key
     */
    public V get(K key) {
        if (root != null) {
            Entry<K, V> entry = root.get(keyBoxMapper.apply(key), key);
            return entry != null ? entry.getValue() : null;
        } else {
            return null;
        }
    }

    /**
     * Returns <tt>true</tt> if this RTreeMap contains a mapping for the specified key.
     * @param key key whose presence in this RTreeMap is to be tested
     * @return <tt>true</tt> if this RTreeMap contains a mapping for the specified key
     */
    public boolean containsKey(K key) {
        return root != null && root.get(keyBoxMapper.apply(key), key) != null;
    }

    /**
     * If the RTreeMap has no entries returns null, otherwise
     * returns the minimum bounding box of all entries in the RTreeMap.
     *
     * @return minimum bounding box of all entries in RTreeMap
     */
    public Box getMbb() {
        return root != null ? root.getBox() : null;
    }

    /**
     * Returns true if and only if the R-tree is empty of entries.
     *
     * @return is R-tree empty
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Returns the number of entries in the RTreeMap.
     *
     * @return the number of entries
     */
    public int size() {
        return root != null ? root.size() : 0;
    }

    /**
     * Returns a {@link Configuration} containing the configuration of the RTreeMap at the time of instantiation.
     *
     * @return the configuration of the RTreeMap at the time of instantiation
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        return root != null ? root.toString() : "";
    }
}
