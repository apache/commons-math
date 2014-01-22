package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.stat.interval.AgrestiCoullInterval;
import org.apache.commons.math3.stat.interval.BinomialConfidenceInterval;
import org.apache.commons.math3.stat.interval.ConfidenceInterval;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the AgrestiCoullInterval class.
 *
 * @version $Id$
 */
public class AgrestiCoullIntervalTest extends BinomialConfidenceIntervalAbstractTest {

    @Override
    protected BinomialConfidenceInterval createBinomialConfidenceInterval() {
        return new AgrestiCoullInterval();
    }
    
    @Test
    public void testStandardInterval() {
        ConfidenceInterval confidenceInterval = createStandardTestInterval();
        Assert.assertEquals(0.07993521, confidenceInterval.getLowerBound(), 1E-5);
        Assert.assertEquals(0.1243704, confidenceInterval.getUpperBound(), 1E-5);
    }

}
