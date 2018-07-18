package com.jamieswhiteshirt.rtree3i;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class RTreeMapTest {

    @Test
    public void testInstantiation() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build());
        assertTrue(tree.entries().isEmpty());
    }

    @Test
    public void testSearchEmptyTree() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build());
        assertTrue(tree.entries(r(1)::intersects).isEmpty());
    }

    @Test
    public void testGetOnOneItem() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build());
        Entry<Box, Object> entry = e(1);
        tree = tree.put(entry);
        assertEquals(entry.getValue(), tree.get(r(1)));
    }

    @Test
    public void testTreeWithOneItemIsNotEmpty() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build()).put(e(1));
        assertFalse(tree.isEmpty());
    }

    @Test
    public void testTreeWithNoDuplicateEntries() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build()).put(e(1)).put(e(1));
        assertEquals(1, tree.size());
    }

    @Test
    public void testTreeWithUniqueKey() {
        Box b = r(0);
        Entry<Box, Integer> e1 = Entry.of(b, 0);
        Entry<Box, Integer> e2 = Entry.of(b, 1);
        RTreeMap<Box, Integer> tree = RTreeMap.<Integer>create(new ConfigurationBuilder().build()).put(e1).put(e2);
        assertEquals(tree.get(b), e2.getValue());
        assertEquals(1, tree.size());
    }

    @Test
    public void testTreeWithRemovedItem() {
        Box b = r(0);
        Entry<Box, Integer> e1 = Entry.of(b, 0);
        RTreeMap<Box, Integer> tree = RTreeMap.<Integer>create(new ConfigurationBuilder().build()).put(e1).remove(b);
        assertEquals(tree.get(b), null);
        assertEquals(0, tree.size());
    }

    @Test
    public void testDeleteWithGeometry() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().maxChildren(4).build());
        Entry<Box, Object> entry = e(1);
        Entry<Box, Object> entry2 = e2(1);
        tree = tree.put(entry).put(entry2);

        tree = tree.remove(entry);
        assertTrue(tree.contains(entry2) && !tree.contains(entry));
    }

    @Test
    public void testContext() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build());
        assertNotNull(tree.getConfiguration());
    }

    @Test
    public void testIterableDeletion() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build());
        Entry<Box, Object> entry1 = e(1);
        Entry<Box, Object> entry2 = e(2);
        Entry<Box, Object> entry3 = e(3);
        tree = tree.put(entry1).put(entry2).put(entry3);

        tree = tree.removeAll(Arrays.asList(entry1, entry3));
        assertTrue(tree.contains(entry2) && !tree.contains(entry1) && !tree.contains(entry3));
    }

    @Test
    public void testDepthWithMaxChildren3Entries1() {
        RTreeMap<Box, Object> tree = create(3, 1);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries2() {
        RTreeMap<Box, Object> tree = create(3, 2);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries3() {
        RTreeMap<Box, Object> tree = create(3, 3);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries4() {
        RTreeMap<Box, Object> tree = create(3, 4);
        assertEquals(2, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries10() {
        RTreeMap<Box, Object> tree = create(3, 10);
        assertEquals(3, tree.calculateDepth());
    }

    @Test
    public void testSizeIsZeroIfTreeEmpty() {
        assertEquals(0, create(3, 0).size());
    }

    @Test
    public void testSizeIsOneIfTreeHasOneEntry() {
        assertEquals(1, create(3, 1).size());
    }

    @Test
    public void testSizeIsFiveIfTreeHasFiveEntries() {
        assertEquals(5, create(3, 5).size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeletionThatRemovesAllNodesChildren() {
        RTreeMap<Box, Object> tree = create(3, 8);
        tree = tree.put(e(10));
        // node children are now 1,2 and 3,4
        assertEquals(3, tree.calculateDepth());
        tree = tree.remove(e(10));
        // node children are now 1,2 and 3
        assertEquals(3, tree.calculateDepth());
        assertEquals(Sets.newHashSet(e(1), e(2), e(3), e(4), e(5), e(6), e(7), e(8)),
                tree.entries().collect(Collectors.toSet()));
    }

    @Test
    public void testDeleteOfEntryThatDoesNotExistFromTreeOfOneEntry() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build()).put(e(1));
        tree = tree.remove(e(2));
        assertEquals(Lists.newArrayList(e(1)), tree.entries().collect(Collectors.toList()));
    }

    @Test
    public void testDeleteFromEmptyTree() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build());
        tree = tree.remove(e(2));
        assertEquals(0, tree.entries().count());
    }

    @Test
    public void testBuilder1() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().minChildren(1).maxChildren(4)
                .selector(new SelectorMinimalVolumeIncrease()).splitter(new SplitterQuadratic())
                .build());
        testBuiltTree(tree);
    }

    @Test
    public void testDeletionOfEntryThatDoesNotExistFromNonLeaf() {
        RTreeMap<Box, Object> tree = create(3, 100).remove(e(1000));
        assertEquals(100, tree.entries().count());
    }

    @Test
    public void testBuilder2() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder()
                .selector(new SelectorMinimalVolumeIncrease())
                .minChildren(1).maxChildren(4).splitter(new SplitterQuadratic()).build());
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder3() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().maxChildren(4)
                .selector(new SelectorMinimalVolumeIncrease()).minChildren(1)
                .splitter(new SplitterQuadratic()).build());
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder4() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder()
                .splitter(new SplitterQuadratic()).maxChildren(4)
                .selector(new SelectorMinimalVolumeIncrease()).minChildren(1).build());
        testBuiltTree(tree);
    }

    private void testBuiltTree(RTreeMap<Box, Object> tree) {
        for (int i = 1; i <= 1000; i++) {
            tree = tree.put(point(i, i), i);
        }
        assertEquals(1000, tree.entries().count());
    }

    private static RTreeMap<Box, Object> create(int maxChildren, int n) {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().maxChildren(maxChildren).build());
        for (int i = 1; i <= n; i++)
            tree = tree.put(e(i));
        return tree;
    }

    @Test
    public void testDeleteOneFromOne() {
        Entry<Box, Object> e1 = e(1);
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().maxChildren(4).build()).put(e1).remove(e1);
        assertEquals(0, tree.entries().count());
    }

    @Test
    public void testDeleteOneFromTreeWithDepthGreaterThanOne() {
        Entry<Box, Object> e1 = e(1);
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().maxChildren(4).build()).put(e1).put(e(2))
                .put(e(3)).put(e(4)).put(e(5)).put(e(6)).put(e(7)).put(e(8)).put(e(9)).put(e(10))
                .remove(e1);
        assertEquals(9, tree.entries().count());
        assertFalse(tree.contains(e1));
    }

    @Test
    public void testDeleteItemThatIsNotPresentDoesNothing() {
        Entry<Box, Object> e1 = e(1);
        Entry<Box, Object> e2 = e(2);
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build()).put(e1);
        assertEquals(tree, tree.remove(e2));
    }

    @Test
    public void testExampleOnReadMe() {
        RTreeMap<Box, String> tree = RTreeMap.create(new ConfigurationBuilder().maxChildren(5).build());
        tree = tree.put(Entry.of(point(10, 20), "DAVE")).put(Entry.of(point(12, 25), "FRED"))
                .put(Entry.of(point(97, 125), "MARY"));
    }

    @Test
    public void testAddOverload() {
        RTreeMap<Box, Object> tree = create(3, 0);
        tree = tree.put(point(1, 2), 123);
        assertEquals(1, tree.entries().count());
    }

    @Test
    public void testDeleteOverload() {
        RTreeMap<Box, Object> tree = create(3, 0);
        tree = tree.put(point(1, 2), 123).remove(point(1, 2), 123);
        assertEquals(0, tree.entries().count());
    }

    @Test
    public void testStandardRTreeSearch() {
        Box r = box(13, 23, 50, 80);
        Box[] points = { point(59, 91), point(86, 14), point(36, 60),
                point(57, 36), point(14, 37) };

        RTreeMap<Box, Integer> tree = RTreeMap.create(new ConfigurationBuilder().build());
        for (int i = 0; i < points.length; i++) {
            Box point = points[i];
            System.out.println("point(" + point.x1() + "," + point.y1() + "), value=" + (i + 1));
            tree = tree.put(point, i + 1);
        }
        System.out.println(tree.toString());
        System.out.println("searching " + r);
        Set<Integer> set = tree.values(r::intersects).collect(Collectors.toSet());
        assertEquals(new HashSet<>(asList(3, 5)), set);
    }

    @Test
    public void testStandardRTreeSearch2() {
        Box r = box(10, 10, 50, 50);
        Box[] points = { point(28, 19), point(29, 4), point(10, 63),
                point(34, 85), point(62, 45) };

        RTreeMap<Box, Integer> tree = RTreeMap.create(new ConfigurationBuilder().build());
        for (int i = 0; i < points.length; i++) {
            Box point = points[i];
            System.out.println("point(" + point.x1() + "," + point.y1() + "), value=" + (i + 1));
            tree = tree.put(point, i + 1);
        }
        System.out.println(tree.toString());
        System.out.println("searching " + r);
        Set<Integer> set = tree.values(r::intersects).collect(Collectors.toSet());
        assertEquals(new HashSet<>(Collections.singletonList(1)), set);
    }

    @Test
    public void testStarTreeReturnsSameAsStandardRTree() {

        RTreeMap<Box, Integer> tree1 = RTreeMap.create(new ConfigurationBuilder().build());
        RTreeMap<Box, Integer> tree2 = RTreeMap.create(new ConfigurationBuilder().star().build());

        Box[] testRects = { box(0, 0, 0, 0), box(0, 0, 100, 100), box(0, 0, 10, 10),
                box(0, 0, 50, 51), box(1, 0, 50, 69),
                box(13, 23, 50, 81), box(10, 10, 50, 50) };

        for (int i = 1; i <= 10000; i++) {
            Box point = point(i, i);
            // System.out.println("point(" + point.x() + "," + point.y() +
            // "),");
            tree1 = tree1.put(point, i);
            tree2 = tree2.put(point, i);
        }

        for (Box r : testRects) {
            Predicate<Box> boxPredicate = r::intersects;
            Set<Integer> res1 = tree1.values(boxPredicate).collect(Collectors.toSet());
            Set<Integer> res2 = tree2.values(boxPredicate).collect(Collectors.toSet());
            // System.out.println("searchRect= rectangle(" + r.x1() + "," +
            // r.y1() + "," + r.x2() + "," + r.y2()+ ")");
            // System.out.println("res1.size=" + res1.size() + ",res2.size=" +
            // res2.size());
            // System.out.println("res1=" + res1 + ",res2=" + res2);
            assertEquals(res1.size(), res2.size());
        }
    }

    private static Box box(int x1, int y1, int x2, int y2) {
        return Box.create(x1, y1, 0, x2, y2, 1);
    }

    @Test
    public void calculateDepthOfEmptyTree() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build());
        assertEquals(0, tree.calculateDepth());
    }

    @Test
    public void calculateStringOfEmptyTree() {
        RTreeMap<Box, Object> tree = RTreeMap.create(new ConfigurationBuilder().build());
        assertEquals("", tree.toString());
    }

    @Test
    public void testForMeiZhao() {
        for (int minChildren = 1; minChildren <= 2; minChildren++) {
            RTreeMap<Box, Integer> tree = RTreeMap.<Integer>create(new ConfigurationBuilder().maxChildren(3).minChildren(minChildren)
                    .build()).put(point(1, 9), 1).put(point(2, 10), 2)
                    .put(point(4, 8), 3).put(point(6, 7), 4).put(point(9, 10), 5)
                    .put(point(7, 5), 6).put(point(5, 6), 7).put(point(4, 3), 8).put(point(3, 2), 9)
                    .put(point(9, 1), 10).put(point(10, 4), 11).put(point(6, 2), 12)
                    .put(point(8, 3), 13);
            System.out.println(tree.toString());
        }
    }

    @Test
    public void testRTreeRootMbbWhenRTreeEmpty() {
        assertTrue(RTreeMap.create(new ConfigurationBuilder().build()).getMbb() == null);
    }

    @Test
    public void testRTreeRootMbrWhenRTreeNonEmpty() {
        Box r = RTreeMap.<Integer>create(new ConfigurationBuilder().build()).put(point(1, 1), 1).put(point(2, 2), 2).getMbb();
        assertTrue(r != null);
        assertEquals(Box.create(1, 1, 0, 2, 2, 0), r);
    }

    private static Box point(int x, int y) {
        return Box.create(x, y, 0, x, y, 0);
    }

    static Entry<Box, Object> e(int n) {
        return Entry.of(r(n), n);
    }

    static Entry<Box, Object> e2(int n) {
        return Entry.of(r(n - 1), n);
    }

    private static Box r(int n) {
        return box(n, n, n + 1, n + 1);
    }

    private static Box r(int n, int m) {
        return box(n, m, n + 1, m + 1);
    }
}
