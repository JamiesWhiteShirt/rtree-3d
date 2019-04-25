package com.jamieswhiteshirt.rtree3i;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import com.google.common.collect.Lists;

public class RStarSplitterTest {

    @Test
    public void testGetPairs() {

        int minSize = 2;
        List<Box> list = Lists.newArrayList();
        list.add(point(1, 1));
        list.add(point(2, 2));
        list.add(point(3, 3));
        list.add(point(4, 4));
        list.add(point(5, 5));
        List<Groups<Box>> pairs = RStarSplitter.createPairs(minSize, list, Function.identity());
        assertEquals(2, pairs.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitterRStarThrowsExceptionOnEmptyList() {
        RStarSplitter spl = new RStarSplitter();
        spl.split(Collections.emptyList(), 4, Function.identity());
    }

    private static Box point(int x, int y) {
        return Box.create(x, y, 0, x, y, 0);
    }
}
