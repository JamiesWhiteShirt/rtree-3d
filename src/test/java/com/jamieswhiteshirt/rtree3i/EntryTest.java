package com.jamieswhiteshirt.rtree3i;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class EntryTest {

    @Test
    public void testValue() {
        assertEquals(1, (int) Entry.of(Box.create(0, 0, 0, 0, 0, 0), 1).getValue());
    }

    @Test
    public void testEquality() {
        assertEquals(Entry.of(Box.create(0, 0, 0, 0, 0, 0), 1),
                Entry.of(Box.create(0, 0, 0, 0, 0, 0), 1));
    }

    @Test
    public void testEqualityWithGeometry() {
        assertNotEquals(Entry.of(Box.create(0, 0, 0, 0, 0, 0), 1),
                Entry.of(Box.create(0, 1, 0, 0, 1, 0), 1));
    }

    @Test
    public void testInequality() {
        assertNotEquals(Entry.of(Box.create(0, 0, 0, 0, 0, 0), 1),
                Entry.of(Box.create(0, 0, 0, 0, 0, 0), 2));
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(Entry.of(Box.create(0, 0, 0, 0, 0, 0), 1).equals(null));
    }

}
