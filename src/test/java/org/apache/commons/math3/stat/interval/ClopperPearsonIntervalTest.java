package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.stat.interval.BinomialConfidenceInterval;
import org.apache.commons.math3.stat.interval.ClopperPearsonInterval;
import org.apache.commons.math3.stat.interval.ConfidenceInterval;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the ClopperPearsonInterval class.
 *
 * @version $Id$
 */
public class ClopperPearsonIntervalTest extends BinomialConfidenceIntervalAbstractTest {

    @Override
    protected BinomialConfidenceInterval createBinomialConfidenceInterval() {
        return new ClopperPearsonInterval();
    }
    
    @Test
    public void testStandardInterval() {
        ConfidenceInterval confidenceInterval = createStandardTestInterval();
        Assert.assertEquals(0.07873857, confidenceInterval.getLowerBound(), 1E-5);
        Assert.assertEquals(0.1248658, confidenceInterval.getUpperBound(), 1E-5);
    }

}
