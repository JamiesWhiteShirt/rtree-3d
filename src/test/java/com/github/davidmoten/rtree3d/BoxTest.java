package com.github.davidmoten.rtree3d;

import static org.junit.Assert.*;

import com.github.davidmoten.rtree3d.Box;
import org.junit.Test;

public class BoxTest {

    private static final double PRECISION = 0.00001;

    @Test(expected = IllegalArgumentException.class)
    public void testXParametersWrongOrderThrowsException() {
        box(2, 0, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testYParametersWrongOrderThrowsException() {
        box(0, 2, 1, 1);
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(box(0, 0, 1, 1).equals(null));
    }

    @Test
    public void testSimpleEquality() {
        Box r = box(0, 0, 2, 1);
        Box r2 = box(0, 0, 2, 1);

        assertTrue(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality1() {
        Box r = box(0, 0, 2, 1);
        Box r2 = box(0, 0, 2, 2);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality2() {
        Box r = box(0, 0, 2, 1);
        Box r2 = box(1, 0, 2, 1);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality3() {
        Box r = box(0, 0, 2, 1);
        Box r2 = box(0, 1, 2, 1);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality4() {
        Box r = box(0, 0, 2, 2);
        Box r2 = box(0, 0, 1, 2);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testIntersects() {
        Box a = box(14, 14, 86, 37);
        Box b = box(13, 23, 50, 80);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    public void testIntersectsNoRectangleContainsCornerOfAnother() {
        Box a = box(10, 10, 50, 50);
        Box b = box(28, 4, 34, 85);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    public void testIntersectsOneRectangleContainsTheOther() {
        Box a = box(10, 10, 50, 50);
        Box b = box(20, 20, 40, 40);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }
    
    private static Box box(int x1, int y1, int x2, int y2) {
        return Box.create(x1, y1, 0, x2, y2, 1);
    }
    
}