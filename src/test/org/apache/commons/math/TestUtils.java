package org.apache.commons.math;

import junit.framework.Assert;

/**
 * @author Brent Worden
 */
public class TestUtils {
    /**
     * 
     */
    private TestUtils() {
        super();
    }

    public static void assertEquals(double expected, double actual, double delta) {
        // check for NaN
        if(Double.isNaN(expected)){
            Assert.assertTrue(Double.isNaN(actual));
        } else {
            Assert.assertEquals(expected, actual, delta);
        }
    }
}
