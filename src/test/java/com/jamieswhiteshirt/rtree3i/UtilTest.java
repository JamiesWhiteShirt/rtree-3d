package com.jamieswhiteshirt.rtree3i;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

public class UtilTest {

    @Test
    public void testMbrWithNegativeValues() {
        Box r = Box.create(-2, -2, 0, -1, -1, 1);
        Box mbr = Util.mbb(Collections.singleton(r));
        assertEquals(r, mbr);
        System.out.println(r);
    }

}
