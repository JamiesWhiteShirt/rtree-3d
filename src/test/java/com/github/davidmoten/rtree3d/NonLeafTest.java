package com.github.davidmoten.rtree3d;

import java.util.Collections;

import org.junit.Test;

public class NonLeafTest {

    @Test(expected=IllegalArgumentException.class)
    public void testNonLeafPrecondition() {
        new NonLeaf<>(Collections.emptyList(), null);
    }
    
}
