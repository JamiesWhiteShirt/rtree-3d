package com.jamieswhiteshirt.rtree3i;

import java.util.Collections;

import org.junit.Test;

public class BranchTest {

    @Test(expected=IllegalArgumentException.class)
    public void testNonLeafPrecondition() {
        new Branch<>(Collections.emptyList(), null);
    }
    
}
