package com.github.davidmoten.rtree3d;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.github.davidmoten.rtree3d.geometry.Box;
import org.junit.Test;

import com.github.davidmoten.rtree3d.geometry.Groups;
import com.google.common.collect.Lists;

public class SplitterRStarTest {

    @Test
    public void testGetPairs() {

        int minSize = 2;
        List<HasBoxDummy> list = Lists.newArrayList();
        list.add(point(1, 1));
        list.add(point(2, 2));
        list.add(point(3, 3));
        list.add(point(4, 4));
        list.add(point(5, 5));
        List<Groups<HasBoxDummy>> pairs = SplitterRStar.getPairs(minSize, list);
        assertEquals(2, pairs.size());
    }
    
    private static HasBoxDummy point(double x, double y) {
        return new HasBoxDummy(Box.create(x, y, 0, x, y, 0));
    }
}
