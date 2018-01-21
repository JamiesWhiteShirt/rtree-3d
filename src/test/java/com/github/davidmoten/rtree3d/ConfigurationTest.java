package com.github.davidmoten.rtree3d;

import org.junit.Test;

public class ConfigurationTest {

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinChildren() {
        new Configuration(0, 4, new SelectorMinimalVolumeIncrease(), new SplitterQuadratic());
    }
    
    @Test(expected = RuntimeException.class)
    public void testContextIllegalMaxChildren() {
        new Configuration(1, 2, new SelectorMinimalVolumeIncrease(), new SplitterQuadratic());
    }

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinMaxChildren() {
        new Configuration(4, 3, new SelectorMinimalVolumeIncrease(), new SplitterQuadratic());
    }

    @Test
    public void testContextLegalChildren() {
        new Configuration(2, 4, new SelectorMinimalVolumeIncrease(), new SplitterQuadratic());
    }
    
    @Test(expected = NullPointerException.class)
    public void testContextSelectorNullThrowsNPE() {
        new Configuration(2, 4, null, new SplitterQuadratic());
    }
    
    @Test(expected = NullPointerException.class)
    public void testContextSplitterNullThrowsNPE() {
        new Configuration(2, 4, new SelectorMinimalVolumeIncrease(), null);
    }
}
