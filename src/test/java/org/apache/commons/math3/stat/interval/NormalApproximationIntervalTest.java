package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.stat.interval.BinomialConfidenceInterval;
import org.apache.commons.math3.stat.interval.ConfidenceInterval;
import org.apache.commons.math3.stat.interval.NormalApproximationInterval;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the NormalApproximationInterval class.
 *
 * @version $Id$
 */
public class NormalApproximationIntervalTest extends BinomialConfidenceIntervalAbstractTest {

    @Override
    protected BinomialConfidenceInterval createBinomialConfidenceInterval() {
        return new NormalApproximationInterval();
    }
    
    @Test
    public void testStandardInterval() {
        ConfidenceInterval confidenceInterval = createStandardTestInterval();
        Assert.assertEquals(0.07793197, confidenceInterval.getLowerBound(), 1E-5);
        Assert.assertEquals(0.1220680, confidenceInterval.getUpperBound(), 1E-5);
    }

}
