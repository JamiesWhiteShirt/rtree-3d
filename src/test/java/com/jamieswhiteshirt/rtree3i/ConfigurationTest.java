package com.jamieswhiteshirt.rtree3i;

import org.junit.Test;

public class ConfigurationTest {

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinChildren() {
        new Configuration(0, 4, new MinimalVolumeIncreaseSelector(), new QuadraticSplitter());
    }
    
    @Test(expected = RuntimeException.class)
    public void testContextIllegalMaxChildren() {
        new Configuration(1, 2, new MinimalVolumeIncreaseSelector(), new QuadraticSplitter());
    }

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinMaxChildren() {
        new Configuration(4, 3, new MinimalVolumeIncreaseSelector(), new QuadraticSplitter());
    }

    @Test
    public void testContextLegalChildren() {
        new Configuration(2, 4, new MinimalVolumeIncreaseSelector(), new QuadraticSplitter());
    }
    
    @Test(expected = NullPointerException.class)
    public void testContextSelectorNullThrowsNPE() {
        new Configuration(2, 4, null, new QuadraticSplitter());
    }
    
    @Test(expected = NullPointerException.class)
    public void testContextSplitterNullThrowsNPE() {
        new Configuration(2, 4, new MinimalVolumeIncreaseSelector(), null);
    }
}
