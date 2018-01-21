package com.github.davidmoten.rtree3d;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.github.davidmoten.rtree3d.geometry.Box;
import com.github.davidmoten.rtree3d.geometry.Groups;
import com.github.davidmoten.util.Pair;
import com.google.common.collect.Sets;

public class QuadraticSplitterTest {

    @Test
    public void testWorstCombinationOn3() {
        final HasBoxDummy r1 = r(1);
        final HasBoxDummy r2 = r(100);
        final HasBoxDummy r3 = r(3);
        final Pair<HasBoxDummy> pair = SplitterQuadratic.worstCombination(Arrays.asList(r1, r2, r3));
        assertEquals(r1, pair.getValue1());
        assertEquals(r2, pair.getValue2());
    }

    @Test
    public void testWorstCombinationOnTwoEntries() {
        final HasBoxDummy r1 = r(1);
        final HasBoxDummy r2 = r(2);
        final Pair<HasBoxDummy> pair = SplitterQuadratic.worstCombination(Arrays.asList(r1, r2));
        assertEquals(r1, pair.getValue1());
        assertEquals(r2, pair.getValue2());
    }

    @Test
    public void testWorstCombinationOn4() {
        final HasBoxDummy r1 = r(2);
        final HasBoxDummy r2 = r(1);
        final HasBoxDummy r3 = r(3);
        final HasBoxDummy r4 = r(4);
        final Pair<HasBoxDummy> pair = SplitterQuadratic.worstCombination(Arrays.asList(r1, r2, r3, r4));
        assertEquals(r2, pair.getValue1());
        assertEquals(r4, pair.getValue2());
    }

    @Test
    public void testGetBestCandidateForGroup1() {
        final HasBoxDummy r1 = r(1);
        final HasBoxDummy r2 = r(2);
        final List<HasBoxDummy> list = Collections.singletonList(r1);
        final List<HasBoxDummy> group = Collections.singletonList(r2);
        final HasBoxDummy r = SplitterQuadratic.getBestCandidateForGroup(list, group, Util.mbb(group));
        assertEquals(r1, r);
    }

    @Test
    public void testGetBestCandidateForGroup2() {
        final HasBoxDummy r1 = r(1);
        final HasBoxDummy r2 = r(2);
        final HasBoxDummy r3 = r(10);
        final List<HasBoxDummy> list = Collections.singletonList(r1);
        final List<HasBoxDummy> group = Arrays.asList(r2, r3);
        final HasBoxDummy r = SplitterQuadratic.getBestCandidateForGroup(list, group, Util.mbb(group));
        assertEquals(r1, r);
    }

    @Test
    public void testGetBestCandidateForGroup3() {
        final HasBoxDummy r1 = r(1);
        final HasBoxDummy r2 = r(2);
        final HasBoxDummy r3 = r(10);
        final List<HasBoxDummy> list = Arrays.asList(r1, r2);
        final List<HasBoxDummy> group = Collections.singletonList(r3);
        final HasBoxDummy r = SplitterQuadratic.getBestCandidateForGroup(list, group, Util.mbb(group));
        assertEquals(r2, r);
    }

    @Test
    public void testSplit() {
        final SplitterQuadratic q = new SplitterQuadratic();
        final HasBoxDummy r1 = r(1);
        final HasBoxDummy r2 = r(2);
        final HasBoxDummy r3 = r(100);
        final HasBoxDummy r4 = r(101);
        final Groups<HasBoxDummy> pair = q.split(Arrays.asList(r1, r2, r3, r4), 2);
        assertEquals(Sets.newHashSet(r1, r2), Sets.newHashSet(pair.getGroup1().getEntries()));
        assertEquals(Sets.newHashSet(r3, r4), Sets.newHashSet(pair.getGroup2().getEntries()));
    }

    @Test
    public void testSplit2() {
        final SplitterQuadratic q = new SplitterQuadratic();
        final HasBoxDummy r1 = r(1);
        final HasBoxDummy r2 = r(2);
        final HasBoxDummy r3 = r(100);
        final HasBoxDummy r4 = r(101);
        final HasBoxDummy r5 = r(103);
        final Groups<HasBoxDummy> pair = q.split(Arrays.asList(r1, r2, r3, r4, r5), 2);
        assertEquals(Sets.newHashSet(r1, r2), Sets.newHashSet(pair.getGroup1().getEntries()));
        assertEquals(Sets.newHashSet(r3, r4, r5), Sets.newHashSet(pair.getGroup2().getEntries()));
    }

    @Test
    public void testSplit3() {
        final SplitterQuadratic q = new SplitterQuadratic();
        final HasBoxDummy r1 = r(1);
        final HasBoxDummy r2 = r(2);
        final HasBoxDummy r3 = r(100);
        final HasBoxDummy r4 = r(101);
        final HasBoxDummy r5 = r(103);
        final HasBoxDummy r6 = r(104);
        final Groups<HasBoxDummy> pair = q.split(Arrays.asList(r1, r2, r3, r4, r5, r6), 3);
        assertEquals(Sets.newHashSet(r1, r2, r3), Sets.newHashSet(pair.getGroup1().getEntries()));
        assertEquals(Sets.newHashSet(r4, r5, r6), Sets.newHashSet(pair.getGroup2().getEntries()));
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionForSplitEmptyList() {
        final SplitterQuadratic q = new SplitterQuadratic();    
        q.split(Collections.emptyList(), 3);
    }
    
    private static HasBoxDummy r(int n) {
        return new HasBoxDummy(Box.create(n, n, 0, n + 1, n + 1, 1));
    }

}
