package com.github.davidmoten.rtree3d;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import com.github.davidmoten.rtree3d.geometry.Box;

public class UtilTest {

    @Test
    public void testMbrWithNegativeValues() {
        Box r = Box.create(-2, -2, 0, -1, -1, 1);
        Box mbr = Util.mbb(Collections.singleton(new HasBoxDummy(r)));
        assertEquals(r,mbr);
        System.out.println(r);
    }

}
