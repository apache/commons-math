package org.apache.commons.math.stat.distribution;

import junit.framework.TestCase;

/**
 * @author Brent Worden
 */
public class DistributionFactoryImplTest extends TestCase {
    /** */
    private DistributionFactory factory;
    
	/**
	 * Constructor for DistributionFactoryImplTest.
	 * @param name
	 */
	public DistributionFactoryImplTest(String name) {
		super(name);
	}
	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
		super.setUp();
        factory = new DistributionFactoryImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void tearDown() throws Exception {
        factory = null;
		super.tearDown();
	}
    
    public void testCreateChiSquareDistributionNegative(){
        try {
            factory.createChiSquareDistribution(-1.0);
            fail("negative degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateChiSquareDistributionZero(){
        try {
            factory.createChiSquareDistribution(0.0);
            fail("zero degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateChiSquareDistributionPositive(){
        try {
            factory.createChiSquareDistribution(1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive degrees of freedom.  IllegalArgumentException is not expected");
        }
    }
    
    public void testCreateGammaDistributionNegativePositive(){
        try {
            factory.createGammaDistribution(-1.0, 1.0);
            fail("negative alpha.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateGammaDistributionZeroPositive(){
        try {
            factory.createGammaDistribution(0.0, 1.0);
            fail("zero alpha.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateGammaDistributionPositiveNegative(){
        try {
            factory.createGammaDistribution(1.0, -1.0);
            fail("negative beta.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateGammaDistributionPositiveZero(){
        try {
            factory.createGammaDistribution(1.0, 0.0);
            fail("zero beta.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateGammaDistributionPositivePositive(){
        try {
            factory.createGammaDistribution(1.0, 1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive alpah and beta.  IllegalArgumentException is not expected");
        }
    }
//    
//    public void testCreateTDistributionNegative(){
//        try {
//            factory.createTDistribution(-1.0);
//            fail("negative degrees of freedom.  IllegalArgumentException expected");
//        } catch (IllegalArgumentException ex) {
//            ;
//        }
//    }
//    
//    public void testCreateTDistributionZero(){
//        try {
//            factory.createTDistribution(0.0);
//            fail("zero degrees of freedom.  IllegalArgumentException expected");
//        } catch (IllegalArgumentException ex) {
//            ;
//        }
//    }
//    
//    public void testCreateTDistributionPositive(){
//        try {
//            factory.createTDistribution(1.0);
//        } catch (IllegalArgumentException ex) {
//            fail("positive degrees of freedom.  IllegalArgumentException is not expected");
//        }
//    }
}
