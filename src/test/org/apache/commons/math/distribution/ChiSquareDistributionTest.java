package org.apache.commons.math.stat.distribution;

import junit.framework.TestCase;

/**
 * @author Brent Worden
 */
public class ChiSquareDistributionTest extends TestCase {
    private ChiSquaredDistribution chiSquare;
    
	/**
	 * Constructor for ChiSquareDistributionTest.
	 * @param name
	 */
	public ChiSquareDistributionTest(String name) {
		super(name);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
        chiSquare = DistributionFactory.newInstance().createChiSquareDistribution(5.0);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
        chiSquare = null;
		super.tearDown();
	}

    public void testLowerTailProbability(){
        testProbability( .210, .001);
        testProbability( .554, .010);
        testProbability( .831, .025);
        testProbability(1.145, .050);
        testProbability(1.610, .100);
    }

    public void testUpperTailProbability(){
        testProbability(20.515, .999);
        testProbability(15.086, .990);
        testProbability(12.833, .975);
        testProbability(11.070, .950);
        testProbability( 9.236, .900);
    }
    
    public void testLowerTailValues(){
        testValue(.001,  .210);
        testValue(.010,  .554);
        testValue(.025,  .831);
        testValue(.050, 1.145);
        testValue(.100, 1.610);
    }
    
    public void testUpperTailValues(){
        testValue(.999, 20.515);
        testValue(.990, 15.086);
        testValue(.975, 12.833);
        testValue(.950, 11.070);
        testValue(.900,  9.236);
    }
    
    private void testProbability(double x, double expected){
        double actual = chiSquare.cummulativeProbability(x);
        assertEquals("probability for " + x, expected, actual, 10e-4);
    }
    
    private void testValue(double p, double expected){
        double actual = chiSquare.inverseCummulativeProbability(p);
        assertEquals("value for " + p, expected, actual, 10e-4);
    }
}
