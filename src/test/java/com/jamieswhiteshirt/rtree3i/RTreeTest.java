package com.jamieswhiteshirt.rtree3i;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RTreeTest {

    private static final Random random = new Random();

    @Test
    public void testInstantiation() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build());
        assertTrue(tree.getEntries().isEmpty());
    }

    @Test
    public void testSearchEmptyTree() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build());
        assertTrue(tree.search(r(1)).isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchOnOneItem() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build());
        Entry<Object> entry = e(1);
        tree = tree.add(entry);
        assertEquals(Collections.singletonList(entry), tree.search(r(1)));
    }

    @Test
    public void testTreeWithOneItemIsNotEmpty() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build()).add(e(1));
        assertFalse(tree.isEmpty());
    }

    @Test
    public void testTreeWithNoDuplicateEntries() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build()).add(e(1)).add(e(1));
        assertEquals(1, tree.size());
    }

    @Test
    public void testPerformanceAndEntriesCount() {
        long repeats = Long.parseLong(System.getProperty("r", "1"));
        long n = Long.parseLong(System.getProperty("n", "10000"));
        RTree<Object> tree = null;
        while (--repeats >= 0) {
            long t = System.currentTimeMillis();
            tree = createRandomRTree(n);
            long diff = System.currentTimeMillis() - t;
            System.out.println("inserts/second = " + ((double) n / diff * 1000));
        }
        assertEquals(n, tree.getEntries().size());

        long t = System.currentTimeMillis();
        Entry<?> entry = tree.search(box(0, 0, 500, 500)).iterator().next();
        long diff = System.currentTimeMillis() - t;
        System.out.println("found " + entry);
        System.out.println("time to get nearest with " + n + " getEntries=" + diff);

    }

    @Test
    public void testSearchOfPoint() {
        Entry<Object> entry = Entry.entry(new Object(), point(1, 1));
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build()).add(entry);
        Collection<Entry<Object>> list = tree.search(RTree.intersects(point(1, 1)));
        assertEquals(1, list.size());
        assertEquals(Sets.newHashSet(entry), Sets.newHashSet(list));
    }

    static List<Entry<Object>> createRandomEntries(long n) {
        List<Entry<Object>> list = new ArrayList<>();
        for (long i = 0; i < n; i++)
            list.add(randomEntry());
        return list;
    }

    static RTree<Object> createRandomRTree(long n) {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().maxChildren(4).build());
        for (long i = 0; i < n; i++) {
            Entry<Object> entry = randomEntry();
            tree = tree.add(entry);
        }
        return tree;
    }

    static Entry<Object> randomEntry() {
        return Entry.entry(new Object(), randomBox());
    }

    @Test
    public void testDeleteWithGeometry() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().maxChildren(4).build());
        Entry<Object> entry = e(1);
        Entry<Object> entry2 = e2(1);
        tree = tree.add(entry).add(entry2);

        tree = tree.remove(entry.getBox(), entry.getValue());
        assertTrue(tree.contains(entry2) && !tree.contains(entry));
    }

    @Test
    public void testDepthWith0() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build());
        tree = tree.add(createRandomEntries(5));
        Collection<Entry<Object>> entries = tree.getEntries();
        RTree<Object> deletedTree = tree.remove(entries);
        assertTrue(deletedTree.isEmpty());
    }

    @Test
    public void testContext() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build());
        assertNotNull(tree.getConfiguration());
    }

    @Test
    public void testIterableDeletion() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build());
        Entry<Object> entry1 = e(1);
        Entry<Object> entry2 = e(2);
        Entry<Object> entry3 = e(3);
        tree = tree.add(entry1).add(entry2).add(entry3);

        tree = tree.remove(Arrays.asList(entry1, entry3));
        assertTrue(tree.contains(entry2) && !tree.contains(entry1) && !tree.contains(entry3));
    }

    @Test
    public void testDepthWithMaxChildren3Entries1() {
        RTree<Object> tree = create(3, 1);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries2() {
        RTree<Object> tree = create(3, 2);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries3() {
        RTree<Object> tree = create(3, 3);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries4() {
        RTree<Object> tree = create(3, 4);
        assertEquals(2, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries10() {
        RTree<Object> tree = create(3, 10);
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
        RTree<Object> tree = create(3, 8);
        tree = tree.add(e(10));
        // node children are now 1,2 and 3,4
        assertEquals(3, tree.calculateDepth());
        tree = tree.remove(e(10));
        // node children are now 1,2 and 3
        assertEquals(3, tree.calculateDepth());
        assertEquals(Sets.newHashSet(e(1), e(2), e(3), e(4), e(5), e(6), e(7), e(8)),
                Sets.newHashSet(tree.getEntries()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteOfEntryThatDoesNotExistFromTreeOfOneEntry() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build()).add(e(1));
        tree = tree.remove(e(2));
        assertEquals(Lists.newArrayList(e(1)), tree.getEntries());
    }

    @Test
    public void testDeleteFromEmptyTree() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build());
        tree = tree.remove(e(2));
        assertEquals(0, tree.getEntries().size());
    }

    @Test
    public void testBuilder1() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().minChildren(1).maxChildren(4)
                .selector(new SelectorMinimalVolumeIncrease()).splitter(new SplitterQuadratic())
                .build());
        testBuiltTree(tree);
    }

    @Test
    public void testDeletionOfEntryThatDoesNotExistFromNonLeaf() {
        RTree<Object> tree = create(3, 100).remove(e(1000));
        assertEquals(100, tree.getEntries().size());
    }

    @Test
    public void testBuilder2() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder()
                .selector(new SelectorMinimalVolumeIncrease())
                .minChildren(1).maxChildren(4).splitter(new SplitterQuadratic()).build());
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder3() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().maxChildren(4)
                .selector(new SelectorMinimalVolumeIncrease()).minChildren(1)
                .splitter(new SplitterQuadratic()).build());
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder4() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder()
                .splitter(new SplitterQuadratic()).maxChildren(4)
                .selector(new SelectorMinimalVolumeIncrease()).minChildren(1).build());
        testBuiltTree(tree);
    }

    private void testBuiltTree(RTree<Object> tree) {
        for (int i = 1; i <= 1000; i++) {
            tree = tree.add(point(i, i), i);
        }
        assertEquals(1000, tree.getEntries().size());
    }

    private static RTree<Object> create(int maxChildren, int n) {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().maxChildren(maxChildren).build());
        for (int i = 1; i <= n; i++)
            tree = tree.add(e(i));
        return tree;
    }

    @Test(expected = RuntimeException.class)
    public void testSplitterRStarThrowsExceptionOnEmptyList() {
        SplitterRStar spl = new SplitterRStar();
        spl.split(Collections.emptyList(), 4, Function.identity());
    }

    @Test
    public void testDeleteOneFromOne() {
        Entry<Object> e1 = e(1);
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().maxChildren(4).build()).add(e1).remove(e1);
        assertEquals(0, tree.getEntries().size());
    }

    @Test
    public void testDeleteOneFromTreeWithDepthGreaterThanOne() {
        Entry<Object> e1 = e(1);
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().maxChildren(4).build()).add(e1).add(e(2))
                .add(e(3)).add(e(4)).add(e(5)).add(e(6)).add(e(7)).add(e(8)).add(e(9)).add(e(10))
                .remove(e1);
        assertEquals(9, tree.getEntries().size());
        assertFalse(tree.getEntries().contains(e1));
    }

    @Test
    public void testDeleteOneFromLargeTreeThenDeleteAllAndEnsureEmpty() {
        int n = 10000;
        RTree<Object> tree = createRandomRTree(n).add(e(1)).add(e(2)).remove(e(1));
        assertEquals(n + 1, tree.getEntries().size());
        assertFalse(tree.getEntries().contains(e(1)));
        assertTrue(tree.getEntries().contains(e(2)));
        n++;
        assertEquals(n, tree.size());

        for (Entry<Object> entry : tree.getEntries()) {
            tree = tree.remove(entry);
            n--;
            assertEquals(n, tree.size());
        }
        assertEquals(0, tree.getEntries().size());
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testDeleteItemThatIsNotPresentDoesNothing() {
        Entry<Object> e1 = e(1);
        Entry<Object> e2 = e(2);
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build()).add(e1);
        assertEquals(tree, tree.remove(e2));
    }

    @Test
    public void testExampleOnReadMe() {
        RTree<String> tree = RTree.create(new ConfigurationBuilder().maxChildren(5).build());
        tree = tree.add(Entry.entry("DAVE", point(10, 20))).add(Entry.entry("FRED", point(12, 25)))
                .add(Entry.entry("MARY", point(97, 125)));
    }

    @Test
    public void testAddOverload() {
        @SuppressWarnings("unchecked")
        RTree<Object> tree = create(3, 0);
        tree = tree.add(point(1, 2), 123);
        assertEquals(1, tree.getEntries().size());
    }

    @Test
    public void testDeleteOverload() {
        @SuppressWarnings("unchecked")
        RTree<Object> tree = create(3, 0);
        tree = tree.add(point(1, 2), 123).remove(point(1, 2), 123);
        assertEquals(0, tree.getEntries().size());
    }

    @Test
    public void testStandardRTreeSearch() {
        Box r = box(13, 23, 50, 80);
        Box[] points = { point(59, 91), point(86, 14), point(36, 60),
                point(57, 36), point(14, 37) };

        RTree<Integer> tree = RTree.create(new ConfigurationBuilder().build());
        for (int i = 0; i < points.length; i++) {
            Box point = points[i];
            System.out.println("point(" + point.x1() + "," + point.y1() + "), value=" + (i + 1));
            tree = tree.add(point, i + 1);
        }
        System.out.println(tree.asString());
        System.out.println("searching " + r);
        Set<Integer> set = tree.search(r).stream().map(Entry::getValue).collect(Collectors.toSet());
        assertEquals(new HashSet<>(asList(3, 5)), set);
    }

    @Test
    public void testStandardRTreeSearch2() {
        Box r = box(10, 10, 50, 50);
        Box[] points = { point(28, 19), point(29, 4), point(10, 63),
                point(34, 85), point(62, 45) };

        RTree<Integer> tree = RTree.create(new ConfigurationBuilder().build());
        for (int i = 0; i < points.length; i++) {
            Box point = points[i];
            System.out.println("point(" + point.x1() + "," + point.y1() + "), value=" + (i + 1));
            tree = tree.add(point, i + 1);
        }
        System.out.println(tree.asString());
        System.out.println("searching " + r);
        Set<Integer> set = tree.search(r).stream().map(Entry::getValue).collect(Collectors.toSet());
        assertEquals(new HashSet<>(Collections.singletonList(1)), set);
    }

    @Test
    public void testStarTreeReturnsSameAsStandardRTree() {

        RTree<Integer> tree1 = RTree.create(new ConfigurationBuilder().build());
        RTree<Integer> tree2 = RTree.create(new ConfigurationBuilder().star().build());

        Box[] testRects = { box(0, 0, 0, 0), box(0, 0, 100, 100), box(0, 0, 10, 10),
                box(0, 0, 50, 51), box(1, 0, 50, 69),
                box(13, 23, 50, 81), box(10, 10, 50, 50) };

        for (int i = 1; i <= 10000; i++) {
            Box point = randomPoint();
            // System.out.println("point(" + point.x() + "," + point.y() +
            // "),");
            tree1 = tree1.add(point, i);
            tree2 = tree2.add(point, i);
        }

        for (Box r : testRects) {
            Set<Integer> res1 = tree1.search(r)
                    .stream().map(Entry::getValue).collect(Collectors.toSet());
            Set<Integer> res2 = tree2.search(r)
                    .stream().map(Entry::getValue).collect(Collectors.toSet());
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
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build());
        assertEquals(0, tree.calculateDepth());
    }

    @Test
    public void calculateAsStringOfEmptyTree() {
        RTree<Object> tree = RTree.create(new ConfigurationBuilder().build());
        assertEquals("", tree.asString());
    }

    @Test
    public void testForMeiZhao() {
        for (int minChildren = 1; minChildren <= 2; minChildren++) {
            RTree<Integer> tree = RTree.<Integer>create(new ConfigurationBuilder().maxChildren(3).minChildren(minChildren)
                    .build()).add(point(1, 9), 1).add(point(2, 10), 2)
                    .add(point(4, 8), 3).add(point(6, 7), 4).add(point(9, 10), 5)
                    .add(point(7, 5), 6).add(point(5, 6), 7).add(point(4, 3), 8).add(point(3, 2), 9)
                    .add(point(9, 1), 10).add(point(10, 4), 11).add(point(6, 2), 12)
                    .add(point(8, 3), 13);
            System.out.println(tree.asString());
        }
    }

    @Test
    public void testRTreeRootMbbWhenRTreeEmpty() {
        assertTrue(RTree.create(new ConfigurationBuilder().build()).getMbb() == null);
    }

    @Test
    public void testRTreeRootMbrWhenRTreeNonEmpty() {
        Box r = RTree.<Integer> create(new ConfigurationBuilder().build()).add(point(1, 1), 1).add(point(2, 2), 2).getMbb();
        assertTrue(r != null);
        assertEquals(Box.create(1, 1, 0, 2, 2, 0), r);
    }

    private static Box point(int x, int y) {
        return Box.create(x, y, 0, x, y, 0);
    }

    private static Box randomPoint() {
        return point(random.nextInt(100), random.nextInt(100));
    }

    static Entry<Object> e(int n) {
        return Entry. entry(n, r(n));
    }

    static Entry<Object> e2(int n) {
        return Entry. entry(n, r(n - 1));
    }

    private static Box r(int n) {
        return box(n, n, n + 1, n + 1);
    }

    private static Box r(int n, int m) {
        return box(n, m, n + 1, m + 1);
    }

    private static Box randomBox() {
        return r(random.nextInt(1000), random.nextInt(1000));
    }
}
