package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.stat.interval.BinomialConfidenceInterval;
import org.apache.commons.math3.stat.interval.ConfidenceInterval;
import org.apache.commons.math3.stat.interval.WilsonScoreInterval;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the WilsonScoreInterval class.
 *
 * @version $Id$
 */
public class WilsonScoreIntervalTest extends BinomialConfidenceIntervalAbstractTest {

    @Override
    protected BinomialConfidenceInterval createBinomialConfidenceInterval() {
        return new WilsonScoreInterval();
    }
    
    @Test
    public void testStandardInterval() {
        ConfidenceInterval confidenceInterval = createStandardTestInterval();
        Assert.assertEquals(0.08003919, confidenceInterval.getLowerBound(), 1E-5);
        Assert.assertEquals(0.1242664, confidenceInterval.getUpperBound(), 1E-5);
    }

}
