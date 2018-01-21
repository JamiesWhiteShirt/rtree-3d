package com.github.davidmoten.rtree3d;

import static com.github.davidmoten.rtree3d.Entry.entry;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Test;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RTreeTest {

    private static final double PRECISION = 0.000001;

    @Test
    public void testInstantiation() {
        RTree<Object> tree = RTree.create();
        assertTrue(tree.getEntries().isEmpty());
    }

    @Test
    public void testSearchEmptyTree() {
        RTree<Object> tree = RTree.create();
        assertTrue(tree.search(r(1)).isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchOnOneItem() {
        RTree<Object> tree = RTree.create();
        Entry<Object> entry = e(1);
        tree = tree.add(entry);
        assertEquals(Collections.singletonList(entry), tree.search(r(1)));
    }

    @Test
    public void testTreeWithOneItemIsNotEmpty() {
        RTree<Object> tree = RTree.create().add(e(1));
        assertFalse(tree.isEmpty());
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
        Entry<Object> entry = tree.search(box(0, 0, 500, 500)).get(0);
        long diff = System.currentTimeMillis() - t;
        System.out.println("found " + entry);
        System.out.println("time to get nearest with " + n + " getEntries=" + diff);

    }

    @Test
    public void testSearchOfPoint() {
        Object value = new Object();
        RTree<Object> tree = RTree.create().add(value, point(1, 1));
        List<Entry<Object>> list = tree.search(RTree.intersects(point(1, 1)));
        assertEquals(1, list.size());
        assertEquals(value, list.get(0).getValue());
    }

    static List<Entry<Object>> createRandomEntries(long n) {
        List<Entry<Object>> list = new ArrayList<Entry<Object>>();
        for (long i = 0; i < n; i++)
            list.add(randomEntry());
        return list;
    }

    static RTree<Object> createRandomRTree(long n) {
        RTree<Object> tree = RTree.maxChildren(4).create();
        for (long i = 0; i < n; i++) {
            Entry<Object> entry = randomEntry();
            tree = tree.add(entry);
        }
        return tree;
    }

    static Entry<Object> randomEntry() {
        return entry(new Object(), random());
    }

    @Test
    public void testDeleteWithGeometry() {
        RTree<Object> tree = RTree.maxChildren(4).create();
        Entry<Object> entry = e(1);
        Entry<Object> entry2 = e2(1);
        tree = tree.add(entry).add(entry2);

        tree = tree.delete(entry.getValue(), entry.getBox(), true);
        List<Entry<Object>> entries = tree.getEntries();
        assertTrue(entries.contains(entry2) && !entries.contains(entry));
    }

    @Test
    public void testDepthWith0() {
        RTree<Object> tree = RTree.create();
        tree = tree.add(createRandomEntries(5));
        List<Entry<Object>> entries = tree.getEntries();
        RTree<Object> deletedTree = tree.delete(entries, true);
        assertTrue(deletedTree.isEmpty());
    }

    @Test
    public void testContext() {
        RTree<Object> tree = RTree.create();
        assertNotNull(tree.getContext());
    }

    @Test
    public void testIterableDeletion() {
        RTree<Object> tree = RTree.create();
        Entry<Object> entry1 = e(1);
        Entry<Object> entry2 = e(2);
        Entry<Object> entry3 = e(3);
        tree = tree.add(entry1).add(entry2).add(entry3);

        List<Entry<Object>> list = new ArrayList<>();
        list.add(entry1);
        list.add(entry3);
        RTree<Object> deletedTree = tree.delete(list);
        List<Entry<Object>> entries = deletedTree.getEntries();
        assertTrue(
                entries.contains(entry2) && !entries.contains(entry1) && !entries.contains(entry3));
    }

    @Test
    public void testFullDeletion() {
        RTree<Object> tree = RTree.maxChildren(4).create();
        Entry<Object> entry = e(1);
        tree = tree.add(entry).add(entry);
        tree = tree.delete(entry, true);
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testPartialDeletion() {
        RTree<Object> tree = RTree.maxChildren(4).create();
        Entry<Object> entry = e(1);
        tree = tree.add(entry).add(entry);
        tree = tree.delete(entry, false);
        List<Entry<Object>> entries = tree.getEntries();
        int countEntries = tree.getEntries().size();
        assertTrue(countEntries == 1);
        assertTrue(entries.get(0).equals(entry));
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

    @Test
    public void testSizeAfterDelete() {
        Entry<Object> entry = e(1);
        RTree<Object> tree = create(3, 0).add(entry).add(entry).add(entry).delete(entry);
        assertEquals(2, tree.size());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeletionThatRemovesAllNodesChildren() {
        RTree<Object> tree = create(3, 8);
        tree = tree.add(e(10));
        // node children are now 1,2 and 3,4
        assertEquals(3, tree.calculateDepth());
        tree = tree.delete(e(10));
        // node children are now 1,2 and 3
        assertEquals(3, tree.calculateDepth());
        assertEquals(Sets.newHashSet(e(1), e(2), e(3), e(4), e(5), e(6), e(7), e(8)),
                Sets.newHashSet(tree.getEntries()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteOfEntryThatDoesNotExistFromTreeOfOneEntry() {
        RTree<Object> tree = RTree.create().add(e(1));
        tree = tree.delete(e(2));
        assertEquals(Lists.newArrayList(e(1)), tree.getEntries());
    }

    @Test
    public void testDeleteFromEmptyTree() {
        RTree<Object> tree = RTree.create();
        tree = tree.delete(e(2));
        assertEquals(0, tree.getEntries().size());
    }

    @Test
    public void testBuilder1() {
        RTree<Object> tree = RTree.minChildren(1).maxChildren(4)
                .selector(new SelectorMinimalVolumeIncrease()).splitter(new SplitterQuadratic())
                .create();
        testBuiltTree(tree);
    }

    @Test
    public void testDeletionOfEntryThatDoesNotExistFromNonLeaf() {
        RTree<Object> tree = create(3, 100).delete(e(1000));
        assertEquals(100, tree.getEntries().size());
    }

    @Test
    public void testBuilder2() {
        RTree<Object> tree = RTree.selector(new SelectorMinimalVolumeIncrease())
                .minChildren(1).maxChildren(4).splitter(new SplitterQuadratic()).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder3() {
        RTree<Object> tree = RTree.maxChildren(4)
                .selector(new SelectorMinimalVolumeIncrease()).minChildren(1)
                .splitter(new SplitterQuadratic()).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder4() {
        RTree<Object> tree = RTree.splitter(new SplitterQuadratic()).maxChildren(4)
                .selector(new SelectorMinimalVolumeIncrease()).minChildren(1).create();
        testBuiltTree(tree);
    }

    private void testBuiltTree(RTree<Object> tree) {
        for (int i = 1; i <= 1000; i++) {
            tree = tree.add(i, point(i, i));
        }
        assertEquals(1000, tree.getEntries().size());
    }

    private static RTree<Object> create(int maxChildren, int n) {
        RTree<Object> tree = RTree.maxChildren(maxChildren).create();
        for (int i = 1; i <= n; i++)
            tree = tree.add(e(i));
        return tree;
    }

    @Test(expected = RuntimeException.class)
    public void testSplitterRStarThrowsExceptionOnEmptyList() {
        SplitterRStar spl = new SplitterRStar();
        spl.split(Collections.emptyList(), 4);
    }

    @Test
    public void testDeleteOneFromOne() {
        Entry<Object> e1 = e(1);
        RTree<Object> tree = RTree.maxChildren(4).create().add(e1).delete(e1);
        assertEquals(0, tree.getEntries().size());
    }

    @Test
    public void testDeleteOneFromTreeWithDepthGreaterThanOne() {
        Entry<Object> e1 = e(1);
        RTree<Object> tree = RTree.maxChildren(4).create().add(e1).add(e(2))
                .add(e(3)).add(e(4)).add(e(5)).add(e(6)).add(e(7)).add(e(8)).add(e(9)).add(e(10))
                .delete(e1);
        assertEquals(9, tree.getEntries().size());
        assertFalse(tree.getEntries().contains(e1));
    }

    @Test
    public void testDeleteOneFromLargeTreeThenDeleteAllAndEnsureEmpty() {
        int n = 10000;
        RTree<Object> tree = createRandomRTree(n).add(e(1)).add(e(2)).delete(e(1));
        assertEquals(n + 1, tree.getEntries().size());
        assertFalse(tree.getEntries().contains(e(1)));
        assertTrue(tree.getEntries().contains(e(2)));
        n++;
        assertEquals(n, tree.size());

        for (Entry<Object> entry : tree.getEntries()) {
            tree = tree.delete(entry);
            n--;
            assertEquals(n, tree.size());
        }
        assertEquals(0, tree.getEntries().size());
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testDeleteOnlyDeleteOneIfThereAreMoreThanMaxChildren() {
        Entry<Object> e1 = e(1);
        int count = RTree.maxChildren(4).create().add(e1).add(e1).add(e1).add(e1).add(e1).delete(e1)
                .search(e1.getBox()).size();
        assertEquals(4, count);
    }

    @Test
    public void testDeleteAllIfThereAreMoreThanMaxChildren() {
        Entry<Object> e1 = e(1);
        int count = RTree.maxChildren(4).create().add(e1).add(e1).add(e1).add(e1).add(e1)
                .delete(e1, true).search(e1.getBox()).size();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteItemThatIsNotPresentDoesNothing() {
        Entry<Object> e1 = e(1);
        Entry<Object> e2 = e(2);
        RTree<Object> tree = RTree.create().add(e1);
        assertTrue(tree == tree.delete(e2));
    }

    @Test
    public void testExampleOnReadMe() {
        RTree<String> tree = RTree.maxChildren(5).create();
        tree = tree.add(entry("DAVE", point(10, 20))).add(entry("FRED", point(12, 25)))
                .add(entry("MARY", point(97, 125)));
    }

    @Test
    public void testAddOverload() {
        @SuppressWarnings("unchecked")
        RTree<Object> tree = create(3, 0);
        tree = tree.add(123, point(1, 2));
        assertEquals(1, tree.getEntries().size());
    }

    @Test
    public void testDeleteOverload() {
        @SuppressWarnings("unchecked")
        RTree<Object> tree = create(3, 0);
        tree = tree.add(123, point(1, 2)).delete(123, point(1, 2));
        assertEquals(0, tree.getEntries().size());
    }

    @Test
    public void testStandardRTreeSearch() {
        Box r = box(13.0, 23.0, 50.0, 80.0);
        Box[] points = { point(59.0, 91.0), point(86.0, 14.0), point(36.0, 60.0),
                point(57.0, 36.0), point(14.0, 37.0) };

        RTree<Integer> tree = RTree.create();
        for (int i = 0; i < points.length; i++) {
            Box point = points[i];
            System.out.println("point(" + point.x1() + "," + point.y1() + "), value=" + (i + 1));
            tree = tree.add(i + 1, point);
        }
        System.out.println(tree.asString());
        System.out.println("searching " + r);
        Set<Integer> set = tree.search(r).stream().map(Entry::getValue).collect(Collectors.toSet());
        assertEquals(new HashSet<>(asList(3, 5)), set);
    }

    @Test
    public void testStandardRTreeSearch2() {
        Box r = box(10.0, 10.0, 50.0, 50.0);
        Box[] points = { point(28.0, 19.0), point(29.0, 4.0), point(10.0, 63.0),
                point(34.0, 85.0), point(62.0, 45.0) };

        RTree<Integer> tree = RTree.create();
        for (int i = 0; i < points.length; i++) {
            Box point = points[i];
            System.out.println("point(" + point.x1() + "," + point.y1() + "), value=" + (i + 1));
            tree = tree.add(i + 1, point);
        }
        System.out.println(tree.asString());
        System.out.println("searching " + r);
        Set<Integer> set = tree.search(r).stream().map(Entry::getValue).collect(Collectors.toSet());
        assertEquals(new HashSet<>(Collections.singletonList(1)), set);
    }

    @Test
    public void testStarTreeReturnsSameAsStandardRTree() {

        RTree<Integer> tree1 = RTree.create();
        RTree<Integer> tree2 = RTree.star().create();

        Box[] testRects = { box(0, 0, 0, 0), box(0, 0, 100, 100), box(0, 0, 10, 10),
                box(0.12, 0.25, 50.356, 50.756), box(1, 0.252, 50, 69.23),
                box(13.12, 23.123, 50.45, 80.9), box(10, 10, 50, 50) };

        for (int i = 1; i <= 10000; i++) {
            Box point = nextPoint();
            // System.out.println("point(" + point.x() + "," + point.y() +
            // "),");
            tree1 = tree1.add(i, point);
            tree2 = tree2.add(i, point);
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

    private static Box box(double x1, double y1, double x2, double y2) {
        return Box.create(x1, y1, 0, x2, y2, 1);
    }

    @Test
    public void calculateDepthOfEmptyTree() {
        RTree<Object> tree = RTree.create();
        assertEquals(0, tree.calculateDepth());
    }

    @Test
    public void calculateAsStringOfEmptyTree() {
        RTree<Object> tree = RTree.create();
        assertEquals("", tree.asString());
    }

    @Test
    public void testForMeiZhao() {
        for (int minChildren = 1; minChildren <= 2; minChildren++) {
            RTree<Integer> tree = RTree.maxChildren(3).minChildren(minChildren)
                    .<Integer> create().add(1, point(1, 9)).add(2, point(2, 10))
                    .add(3, point(4, 8)).add(4, point(6, 7)).add(5, point(9, 10))
                    .add(6, point(7, 5)).add(7, point(5, 6)).add(8, point(4, 3)).add(9, point(3, 2))
                    .add(10, point(9, 1)).add(11, point(10, 4)).add(12, point(6, 2))
                    .add(13, point(8, 3));
            System.out.println(tree.asString());
        }
    }

    @Test
    public void testRTreeRootMbbWhenRTreeEmpty() {
        assertTrue(RTree.create().getMbb() == null);
    }

    @Test
    public void testRTreeRootMbrWhenRTreeNonEmpty() {
        Box r = RTree.<Integer> create().add(1, point(1, 1)).add(2, point(2, 2)).getMbb();
        assertTrue(r != null);
        assertEquals(Box.create(1, 1, 0, 2, 2, 0), r);
    }

    private static Box point(double x, double y) {
        return Box.create(x, y, 0, x, y, 0);
    }

    private static Box nextPoint() {

        double randomX = Math.round(Math.random() * 100);

        double randomY = Math.round(Math.random() * 100);

        return point(randomX, randomY);

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

    private static Box r(double n, double m) {
        return box(n, m, n + 1, m + 1);
    }

    static Box random() {
        return r(Math.random() * 1000, Math.random() * 1000);
    }
}
