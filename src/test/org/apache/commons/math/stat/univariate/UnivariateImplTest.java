/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.univariate;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test cases for the {@link DescriptiveStatistics} class.
 *
 * @version $Revision: 1.4 $ $Date: 2004/04/12 02:27:50 $
 */

public final class UnivariateImplTest extends TestCase {
    private double one = 1;
    private float twoF = 2;
    private long twoL = 2;
    private int three = 3;
    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double std = Math.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double tolerance = 10E-15;
    
    public UnivariateImplTest(String name) {
        super(name);
    }
    
    public void setUp() {  
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UnivariateImplTest.class);
        suite.setName("Frequency Tests");
        return suite;
    }
    
    /** test stats */
    public void testStats() {
        SummaryStatistics u = SummaryStatistics.newInstance();
        assertEquals("total count",0,u.getN(),tolerance);
        u.addValue(one);
        u.addValue(twoF);
        u.addValue(twoL);
        u.addValue(three);
        assertEquals("N",n,u.getN(),tolerance);
        assertEquals("sum",sum,u.getSum(),tolerance);
        assertEquals("sumsq",sumSq,u.getSumsq(),tolerance);
        assertEquals("var",var,u.getVariance(),tolerance);
        assertEquals("std",std,u.getStandardDeviation(),tolerance);
        assertEquals("mean",mean,u.getMean(),tolerance);
        assertEquals("min",min,u.getMin(),tolerance);
        assertEquals("max",max,u.getMax(),tolerance);
        u.clear();
        assertEquals("total count",0,u.getN(),tolerance);    
    }     
    
    public void testN0andN1Conditions() throws Exception {
    	SummaryStatistics u = SummaryStatistics.newInstance();
        assertTrue("Mean of n = 0 set should be NaN", 
            Double.isNaN( u.getMean() ) );
		assertTrue("Standard Deviation of n = 0 set should be NaN", 
            Double.isNaN( u.getStandardDeviation() ) );
		assertTrue("Variance of n = 0 set should be NaN", 
            Double.isNaN(u.getVariance() ) );
		
		/* n=1 */
		u.addValue(one);
		assertTrue("mean should be one (n = 1)", 
			u.getMean() == one);
		assertTrue("geometric should be one (n = 1) instead it is " + u.getGeometricMean(), 
			u.getGeometricMean() == one);
		assertTrue("Std should be zero (n = 1)", 
			u.getStandardDeviation() == 0.0);
		assertTrue("variance should be zero (n = 1)", 
			u.getVariance() == 0.0);
					
		/* n=2 */				
		u.addValue(twoF);
		assertTrue("Std should not be zero (n = 2)", 
			u.getStandardDeviation() != 0.0);
		assertTrue("variance should not be zero (n = 2)", 
			u.getVariance() != 0.0);
            
    }

    public void testProductAndGeometricMean() throws Exception {
    	SummaryStatistics u = SummaryStatistics.newInstance();
    	    	
        u.addValue( 1.0 );
        u.addValue( 2.0 );
        u.addValue( 3.0 );
        u.addValue( 4.0 );

        assertEquals( "Geometric mean not expected", 2.213364, 
            u.getGeometricMean(), 0.00001 );
    }
    
    public void testNaNContracts() {
    	SummaryStatistics u = SummaryStatistics.newInstance();
        double nan = Double.NaN;
        assertTrue("mean not NaN",Double.isNaN(u.getMean())); 
        assertTrue("min not NaN",Double.isNaN(u.getMin())); 
        assertTrue("std dev not NaN",Double.isNaN(u.getStandardDeviation())); 
        assertTrue("var not NaN",Double.isNaN(u.getVariance())); 
        assertTrue("geom mean not NaN",Double.isNaN(u.getGeometricMean()));
        
        u.addValue(1.0);
        
        assertEquals( "mean not expected", 1.0, 
            u.getMean(), Double.MIN_VALUE);
        assertEquals( "variance not expected", 0.0, 
            u.getVariance(), Double.MIN_VALUE);
        assertEquals( "geometric mean not expected", 1.0, 
            u.getGeometricMean(), Double.MIN_VALUE);
        
        u.addValue(-1.0);
        
        assertTrue("geom mean not NaN",Double.isNaN(u.getGeometricMean()));
        
        u.addValue(0.0);
        
        assertTrue("geom mean not NaN",Double.isNaN(u.getGeometricMean()));
        
        //FiXME: test all other NaN contract specs
    }
}
