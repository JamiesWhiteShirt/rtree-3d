package com.jamieswhiteshirt.rtree3i;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import com.google.common.collect.Sets;

public class QuadraticSplitterTest {

    @Test
    public void testWorstCombinationOn3() {
        final Box r1 = r(1);
        final Box r2 = r(100);
        final Box r3 = r(3);
        final Pair<Box> pair = QuadraticSplitter.worstCombination(Arrays.asList(r1, r2, r3), Function.identity());
        assertEquals(r1, pair.getValue1());
        assertEquals(r2, pair.getValue2());
    }

    @Test
    public void testWorstCombinationOnTwoEntries() {
        final Box r1 = r(1);
        final Box r2 = r(2);
        final Pair<Box> pair = QuadraticSplitter.worstCombination(Arrays.asList(r1, r2), Function.identity());
        assertEquals(r1, pair.getValue1());
        assertEquals(r2, pair.getValue2());
    }

    @Test
    public void testWorstCombinationOn4() {
        final Box r1 = r(2);
        final Box r2 = r(1);
        final Box r3 = r(3);
        final Box r4 = r(4);
        final Pair<Box> pair = QuadraticSplitter.worstCombination(Arrays.asList(r1, r2, r3, r4), Function.identity());
        assertEquals(r2, pair.getValue1());
        assertEquals(r4, pair.getValue2());
    }

    @Test
    public void testGetBestCandidateForGroup1() {
        final Box r1 = r(1);
        final Box r2 = r(2);
        final List<Box> list = Collections.singletonList(r1);
        final List<Box> group = Collections.singletonList(r2);
        final Box r = QuadraticSplitter.getBestCandidateForGroup(list, Util.mbb(group), Function.identity());
        assertEquals(r1, r);
    }

    @Test
    public void testGetBestCandidateForGroup2() {
        final Box r1 = r(1);
        final Box r2 = r(2);
        final Box r3 = r(10);
        final List<Box> list = Collections.singletonList(r1);
        final List<Box> group = Arrays.asList(r2, r3);
        final Box r = QuadraticSplitter.getBestCandidateForGroup(list, Util.mbb(group), Function.identity());
        assertEquals(r1, r);
    }

    @Test
    public void testGetBestCandidateForGroup3() {
        final Box r1 = r(1);
        final Box r2 = r(2);
        final Box r3 = r(10);
        final List<Box> list = Arrays.asList(r1, r2);
        final List<Box> group = Collections.singletonList(r3);
        final Box r = QuadraticSplitter.getBestCandidateForGroup(list, Util.mbb(group), Function.identity());
        assertEquals(r2, r);
    }

    @Test
    public void testSplit() {
        final QuadraticSplitter q = new QuadraticSplitter();
        final Box r1 = r(1);
        final Box r2 = r(2);
        final Box r3 = r(100);
        final Box r4 = r(101);
        final Groups<Box> pair = q.split(Arrays.asList(r1, r2, r3, r4), 2, Function.identity());
        assertEquals(Sets.newHashSet(r1, r2), Sets.newHashSet(pair.getGroup1().getEntries()));
        assertEquals(Sets.newHashSet(r3, r4), Sets.newHashSet(pair.getGroup2().getEntries()));
    }

    @Test
    public void testSplit2() {
        final QuadraticSplitter q = new QuadraticSplitter();
        final Box r1 = r(1);
        final Box r2 = r(2);
        final Box r3 = r(100);
        final Box r4 = r(101);
        final Box r5 = r(103);
        final Groups<Box> pair = q.split(Arrays.asList(r1, r2, r3, r4, r5), 2, Function.identity());
        assertEquals(Sets.newHashSet(r1, r2), Sets.newHashSet(pair.getGroup1().getEntries()));
        assertEquals(Sets.newHashSet(r3, r4, r5), Sets.newHashSet(pair.getGroup2().getEntries()));
    }

    @Test
    public void testSplit3() {
        final QuadraticSplitter q = new QuadraticSplitter();
        final Box r1 = r(1);
        final Box r2 = r(2);
        final Box r3 = r(100);
        final Box r4 = r(101);
        final Box r5 = r(103);
        final Box r6 = r(104);
        final Groups<Box> pair = q.split(Arrays.asList(r1, r2, r3, r4, r5, r6), 3, Function.identity());
        assertEquals(Sets.newHashSet(r1, r2, r3), Sets.newHashSet(pair.getGroup1().getEntries()));
        assertEquals(Sets.newHashSet(r4, r5, r6), Sets.newHashSet(pair.getGroup2().getEntries()));
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionForSplitEmptyList() {
        final QuadraticSplitter q = new QuadraticSplitter();
        q.split(Collections.emptyList(), 3, Function.identity());
    }
    
    private static Box r(int n) {
        return Box.create(n, n, 0, n + 1, n + 1, 1);
    }

}
