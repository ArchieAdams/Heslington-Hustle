package com.eng1.heslingtonhustle;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestJunit {

    String message = "Hello World";

    @Test
    public void trueTest() {
        assertEquals(message,"Hello World");
    }

    @Test
    public void falseTest() {
        assertNotEquals(message,"Hello");
    }
}
