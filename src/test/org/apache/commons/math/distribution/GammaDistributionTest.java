package org.apache.commons.math.stat.distribution;

import junit.framework.TestCase;

/**
 * @author Brent Worden
 */
public class GammaDistributionTest extends TestCase {
	/**
	 * Constructor for ChiSquareDistributionTest.
	 * @param name
	 */
	public GammaDistributionTest(String name) {
		super(name);
	}

    public void testProbabilities(){
        testProbability(15.5, 4.0, 2.0, .9499);
        testProbability( 0.5, 4.0, 1.0, .0018);
        testProbability(10.0, 1.0, 2.0, .9933);
        testProbability( 5.0, 2.0, 2.0, .7127);
    }
    
    private void testProbability(double x, double a, double b, double expected){
        double actual = DistributionFactory.newInstance().createGammaDistribution(a, b).cummulativeProbability(x);
        assertEquals("probability for " + x, expected, actual, 10e-4);
    }
}
