package com.jamieswhiteshirt.rtree3i;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class EntryTest {

    @Test
    public void testValue() {
        assertEquals(1, (int) Entry.entry(1, Box.create(0, 0, 0, 0, 0, 0)).getValue());
    }

    @Test
    public void testEquality() {
        assertEquals(Entry.entry(1, Box.create(0, 0, 0, 0, 0, 0)),
                Entry.entry(1, Box.create(0, 0, 0, 0, 0, 0)));
    }

    @Test
    public void testEqualityWithGeometry() {
        assertNotEquals(Entry.entry(1, Box.create(0, 0, 0, 0, 0, 0)),
                Entry.entry(1, Box.create(0, 1, 0, 0, 1, 0)));
    }

    @Test
    public void testInequality() {
        assertNotEquals(Entry.entry(1, Box.create(0, 0, 0, 0, 0, 0)),
                Entry.entry(2, Box.create(0, 0, 0, 0, 0, 0)));
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(Entry.entry(1, Box.create(0, 0, 0, 0, 0, 0)).equals(null));
    }

}
