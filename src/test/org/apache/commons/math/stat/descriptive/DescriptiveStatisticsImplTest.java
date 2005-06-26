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
package org.apache.commons.math.stat.descriptive;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

/**
 * Test cases for the {@link Univariate} class.
 *
 * @version $Revision$ $Date$
 */

public final class DescriptiveStatisticsImplTest extends TestCase {
    private double one = 1;
    private float two = 2;
    private int three = 3;
    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double std = Math.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double skewness = 0;
    private double kurtosis = 0.5;
    private double tolerance = 10E-15;
    
    public DescriptiveStatisticsImplTest(String name) {
        super(name);
    }
    
    public void setUp() {  
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(DescriptiveStatisticsImplTest.class);
        suite.setName("DescriptiveStatistics Tests");
        return suite;
    }
    
    /** test stats */
    public void testStats() {
        DescriptiveStatistics u = DescriptiveStatistics.newInstance(); 
        assertEquals("total count",0,u.getN(),tolerance);
        u.addValue(one);
        u.addValue(two);
        u.addValue(two);
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
        DescriptiveStatistics u = DescriptiveStatistics.newInstance(); 
                
            assertTrue("Mean of n = 0 set should be NaN", 
                Double.isNaN( u.getMean() ) );
            assertTrue("Standard Deviation of n = 0 set should be NaN", 
                Double.isNaN( u.getStandardDeviation() ) );
            assertTrue("Variance of n = 0 set should be NaN",
                Double.isNaN(u.getVariance() ) );

            u.addValue(one);

            assertTrue( "Mean of n = 1 set should be value of single item n1",
                u.getMean() == one);
            assertTrue( "StdDev of n = 1 set should be zero, instead it is: " 
                + u.getStandardDeviation(), u.getStandardDeviation() == 0);
            assertTrue( "Variance of n = 1 set should be zero", 
                u.getVariance() == 0);  
    }
    
    public void testSkewAndKurtosis() {
        DescriptiveStatistics u = DescriptiveStatistics.newInstance(); 
        
        double[] testArray = 
        { 12.5, 12, 11.8, 14.2, 14.9, 14.5, 21, 8.2, 10.3, 11.3, 14.1,
          9.9, 12.2, 12, 12.1, 11, 19.8, 11, 10, 8.8, 9, 12.3 };
        for( int i = 0; i < testArray.length; i++) {
            u.addValue( testArray[i]);
        }
        
        assertEquals("mean", 12.40455, u.getMean(), 0.0001);
        assertEquals("variance", 10.00236, u.getVariance(), 0.0001);
        assertEquals("skewness", 1.437424, u.getSkewness(), 0.0001);
        assertEquals("kurtosis", 2.37719, u.getKurtosis(), 0.0001);
    }

    public void testProductAndGeometricMean() throws Exception {
        DescriptiveStatistics u = DescriptiveStatistics.newInstance(); 
        u.setWindowSize(10);
                
        u.addValue( 1.0 );
        u.addValue( 2.0 );
        u.addValue( 3.0 );
        u.addValue( 4.0 );

        //assertEquals( "Product not expected", 
        //    24.0, u.getProduct(), Double.MIN_VALUE );
        assertEquals( "Geometric mean not expected", 
            2.213364, u.getGeometricMean(), 0.00001 );

        // Now test rolling - StorelessDescriptiveStatistics should discount the contribution
        // of a discarded element
        for( int i = 0; i < 10; i++ ) {
            u.addValue( i + 2 );
        }
        // Values should be (2,3,4,5,6,7,8,9,10,11)
        
        //assertEquals( "Product not expected", 39916800.0, 
        //    u.getProduct(), 0.00001 );
        assertEquals( "Geometric mean not expected", 5.755931, 
            u.getGeometricMean(), 0.00001 );
    }
    
    public void testGetSortedValues() {
        double[] test1 = {5,4,3,2,1};
        double[] test2 = {5,2,1,3,4,0};
        double[] test3 = {1};
        int[] testi = null;
        double[] test4 = null;
        RandomData rd = new RandomDataImpl();
        tstGetSortedValues(test1);
        tstGetSortedValues(test2);
        tstGetSortedValues(test3);
        for (int i = 0; i < 10; i++) {
            testi = rd.nextPermutation(10,6);
            test4 = new double[6];
            for (int j = 0; j < testi.length; j++) {
                test4[j] = (double) testi[j];
            }
            tstGetSortedValues(test4);
        }
        for (int i = 0; i < 10; i++) {
            testi = rd.nextPermutation(10,5);
            test4 = new double[5];
            for (int j = 0; j < testi.length; j++) {
                test4[j] = (double) testi[j];
            }
            tstGetSortedValues(test4);
        }        
    }
    
        
    private void tstGetSortedValues(double[] test) {
        DescriptiveStatistics u = DescriptiveStatistics.newInstance(); 
        for (int i = 0; i < test.length; i++) {
            u.addValue(test[i]);
        }
        double[] sorted = u.getSortedValues();
        if (sorted.length != test.length) {
            fail("wrong length for sorted values array");
        }
        for (int i = 0; i < sorted.length-1; i++) {
            if (sorted[i] > sorted[i+1]) {
                fail("sorted values out of sequence");
            }
        }
    }
    
    public void testPercentiles() {
        double[] test = {5,4,3,2,1};
        DescriptiveStatistics u = DescriptiveStatistics.newInstance(); 
        for (int i = 0; i < test.length; i++) {
            u.addValue(test[i]);
        }
        assertEquals("expecting min",1,u.getPercentile(5),10E-12);
        assertEquals("expecting max",5,u.getPercentile(99),10E-12);
        assertEquals("expecting middle",3,u.getPercentile(50),10E-12);
        try {
            double x = u.getPercentile(0);
            fail("expecting IllegalArgumentException for getPercentile(0)");
        } catch (IllegalArgumentException ex) {
            ;
        }
        try {
            double x = u.getPercentile(120);
            fail("expecting IllegalArgumentException for getPercentile(120)");
        } catch (IllegalArgumentException ex) {
            ;
        }
        
        u.clear();
        double[] test2 = {1,2,3,4};
        for (int i = 0; i < test2.length; i++) {
            u.addValue(test2[i]);
        }
        assertEquals("Q1",1.25,u.getPercentile(25),10E-12);
        assertEquals("Q3",3.75,u.getPercentile(75),10E-12);
        assertEquals("Q2",2.5,u.getPercentile(50),10E-12);
        
        u.clear();
        double[] test3 = {1};
        for (int i = 0; i < test3.length; i++) {
            u.addValue(test3[i]);
        }
        assertEquals("Q1",1,u.getPercentile(25),10E-12);
        assertEquals("Q3",1,u.getPercentile(75),10E-12);
        assertEquals("Q2",1,u.getPercentile(50),10E-12);
        
        u.clear();
        RandomData rd = new RandomDataImpl();
        int[] testi = rd.nextPermutation(100,100); // will contain 0-99
        for (int j = 0; j < testi.length; j++) {
            u.addValue((double) testi[j]);  //OK, laugh at me for the cast
        }
        for (int i = 1; i < 100; i++) {
            assertEquals("percentile " + i,
                (double) i-1 + (double) i*(.01), u.getPercentile(i),10E-12);
        }
        
        u.clear();
        double[] test4 = {1,2,3,4,100};
        for (int i = 0; i < test4.length; i++) {
            u.addValue(test4[i]);
        }
        assertEquals("80th",80.8,u.getPercentile(80),10E-12);
        
        u.clear();
        assertTrue("empty value set should return NaN",
            Double.isNaN(u.getPercentile(50)));
    }
                                     
}

