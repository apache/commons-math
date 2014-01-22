package org.apache.commons.math3.stat.interval;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the IntervalUtils class.
 * 
 * @version $Id$
 */
public class IntervalUtilsTest {

    private final int successes = 50;
    private final int trials = 500;
    private final double confidenceLevel = 0.9;
    
    // values to test must be exactly the same
    private final double eps = 0.0;

    @Test
    public void testAgrestiCoull() {
        checkConfidenceIntervals(new AgrestiCoullInterval().createInterval(trials, successes, confidenceLevel),
                                 IntervalUtils.getAgrestiCoullInterval(trials, successes, confidenceLevel));
    }

    @Test
    public void testClopperPearson() {
        checkConfidenceIntervals(new ClopperPearsonInterval().createInterval(trials, successes, confidenceLevel),
                                 IntervalUtils.getClopperPearsonInterval(trials, successes, confidenceLevel));
    }

    @Test
    public void testNormalApproximation() {
        checkConfidenceIntervals(new NormalApproximationInterval().createInterval(trials, successes, confidenceLevel),
                                 IntervalUtils.getNormalApproximationInterval(trials, successes, confidenceLevel));
    }

    @Test
    public void testWilsonScore() {
        checkConfidenceIntervals(new WilsonScoreInterval().createInterval(trials, successes, confidenceLevel),
                                 IntervalUtils.getWilsonScoreInterval(trials, successes, confidenceLevel));
    }

    private void checkConfidenceIntervals(ConfidenceInterval expected, ConfidenceInterval actual) {
        Assert.assertEquals(expected.getLowerBound(), actual.getLowerBound(), eps);
        Assert.assertEquals(expected.getUpperBound(), actual.getUpperBound(), eps);
        Assert.assertEquals(expected.getConfidenceLevel(), actual.getConfidenceLevel(), eps);
    }
}
