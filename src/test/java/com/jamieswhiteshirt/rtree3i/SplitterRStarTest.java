package com.jamieswhiteshirt.rtree3i;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import com.google.common.collect.Lists;

public class SplitterRStarTest {

    @Test
    public void testGetPairs() {

        int minSize = 2;
        List<Box> list = Lists.newArrayList();
        list.add(point(1, 1));
        list.add(point(2, 2));
        list.add(point(3, 3));
        list.add(point(4, 4));
        list.add(point(5, 5));
        List<Groups<Box>> pairs = SplitterRStar.createPairs(minSize, list, Function.identity());
        assertEquals(2, pairs.size());
    }
    
    private static Box point(int x, int y) {
        return Box.create(x, y, 0, x, y, 0);
    }
}
