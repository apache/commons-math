/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.stat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Test cases for the TestStatistic class.
 *
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:08:38 $
 */

public final class BivariateRegressionTest extends TestCase {

    /* 
     * NIST "Norris" refernce data set from 
     * http://www.itl.nist.gov/div898/strd/lls/data/LINKS/DATA/Norris.dat
     * Strangely, order is {y,x}
     */
    private double[][] data = {{0.1,0.2},{338.8,337.4},{118.1,118.2},
        {888.0,884.6},{9.2,10.1},{228.1,226.5},{668.5,666.3},{998.5,996.3},
        {449.1,448.6},{778.9,777.0},{559.2,558.2},{0.3,0.4},{0.1,0.6},
        {778.1,775.5},{668.8,666.9},{339.3,338.0},{448.9,447.5},{10.8,11.6},
        {557.7,556.0},{228.3,228.1},{998.0,995.8},{888.8,887.6},{119.6,120.2},
        {0.3,0.3},{0.6,0.3},{557.6,556.8},{339.3,339.1},{888.0,887.2},
        {998.5,999.0},{778.9,779.0},{10.2,11.1},{117.6,118.3},{228.9,229.2},
        {668.4,669.1},{449.2,448.9},{0.2,0.5}}; 
        
    /* 
     * Correlation example from 
     * http://www.xycoon.com/correlation.htm
     */
    private double[][] corrData = {{101.0,99.2},{100.1,99.0},{100.0,100.0},
        {90.6,111.6},{86.5,122.2},{89.7,117.6},{90.6,121.1},{82.8,136.0},
        {70.1,154.2},{65.4,153.6},{61.3,158.5},{62.5,140.6},{63.6,136.2},
        {52.6,168.0},{59.7,154.3},{59.5,149.0},{61.3,165.5}};
        
    /*
     * From Moore and Mcabe, "Introduction to the Practice of Statistics"
     * Example 10.3 
     */
    private double[][] infData = {{15.6,5.2},{26.8,6.1},{37.8,8.7},{36.4,8.5},
    {35.5,8.8},{18.6,4.9},{15.3,4.5},{7.9,2.5},{0.0,1.1}};
    
    /*
     * From http://www.xycoon.com/simple_linear_regression.htm
     */
    private double[][] infData2 = {{1,3},{2,5},{3,7},{4,14},{5,11}};
    
    public BivariateRegressionTest(String name) {
        super(name);
    }
    
    public void setUp() { 
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BivariateRegressionTest.class);
        suite.setName("BivariateRegression Tests");
        return suite;
    }
    
    public void testNorris() {
       BivariateRegression regression = new BivariateRegression();
       for (int i = 0; i < data.length; i++) {
           regression.addData(data[i][1],data[i][0]);
       }
       assertEquals("slope",1.00211681802045, 
            regression.getSlope(),10E-12);
       assertEquals("slope std err",0.429796848199937E-03, 
            regression.getSlopeStdErr(),10E-12);
       assertEquals("number of observations",36,regression.getN());
       assertEquals("intercept", -0.262323073774029,
            regression.getIntercept(),10E-12);
       assertEquals("std err intercept", 0.232818234301152, 
            regression.getInterceptStdErr(),10E-12);
       assertEquals("r-square",0.999993745883712,
            regression.getRSquare(),10E-12);
       assertEquals("SSR",4255954.13232369, 
            regression.getRegressionSumSquares(),10E-9);
       assertEquals("MSE",0.782864662630069, 
            regression.getMeanSquareError(),10E-10);
       assertEquals("SSE",26.6173985294224, 
            regression.getSumSquaredErrors(),10E-9);
       assertEquals("predict(0)",-0.262323073774029,
            regression.predict(0),10E-12);
       assertEquals("predict(1)",1.00211681802045-0.262323073774029,
            regression.predict(1),10E-12);
    }
    
    public void testCorr() {
       BivariateRegression regression = new BivariateRegression();
       regression.addData(corrData);
       assertEquals("number of observations",17,regression.getN());
       assertEquals("r-square",.896123,
            regression.getRSquare(),10E-6);
       assertEquals("r",-.946638, 
            regression.getR(),10E-6);
    }  
    
    public void testNaNs() {
        
        BivariateRegression regression = new BivariateRegression();
        
        assertTrue("intercept not NaN",Double.isNaN(regression.getIntercept()));
        assertTrue("slope not NaN",Double.isNaN(regression.getSlope()));
        assertTrue("slope std err not NaN",
            Double.isNaN(regression.getSlopeStdErr()));
        assertTrue("intercept std err not NaN",
            Double.isNaN(regression.getInterceptStdErr()));
        assertTrue("MSE not NaN",Double.isNaN(regression.getMeanSquareError()));
        assertTrue("e not NaN",Double.isNaN(regression.getR()));
        assertTrue("r-square not NaN",Double.isNaN(regression.getRSquare()));
        assertTrue("RSS not NaN",
            Double.isNaN(regression.getRegressionSumSquares()));
        assertTrue("SSE not NaN",Double.isNaN(regression.getSumSquaredErrors()));
        assertTrue("SSTO not NaN",Double.isNaN(regression.getTotalSumSquares()));
        assertTrue("predict not NaN",Double.isNaN(regression.predict(0)));
        
        regression.addData(1,2);
        regression.addData(1,3);
        
        // No x variation, so these should still blow...
        assertTrue("intercept not NaN",Double.isNaN(regression.getIntercept()));
        assertTrue("slope not NaN",Double.isNaN(regression.getSlope()));
        assertTrue("slope std err not NaN",
            Double.isNaN(regression.getSlopeStdErr()));
        assertTrue("intercept std err not NaN",
            Double.isNaN(regression.getInterceptStdErr()));
        assertTrue("MSE not NaN",Double.isNaN(regression.getMeanSquareError()));
        assertTrue("e not NaN",Double.isNaN(regression.getR()));
        assertTrue("r-square not NaN",Double.isNaN(regression.getRSquare()));
        assertTrue("RSS not NaN",
            Double.isNaN(regression.getRegressionSumSquares()));
        assertTrue("SSE not NaN",Double.isNaN(regression.getSumSquaredErrors()));
        assertTrue("predict not NaN",Double.isNaN(regression.predict(0)));
        
        // but SSTO should be OK
         assertTrue("SSTO NaN",!Double.isNaN(regression.getTotalSumSquares()));
        
        regression = new BivariateRegression();
        
        regression.addData(1,2);
        regression.addData(3,3);
        
        // All should be OK except MSE, s(b0), s(b1) which need one more df 
        assertTrue("interceptNaN",!Double.isNaN(regression.getIntercept()));
        assertTrue("slope NaN",!Double.isNaN(regression.getSlope()));
        assertTrue("slope std err not NaN",
            Double.isNaN(regression.getSlopeStdErr()));
        assertTrue("intercept std err not NaN",
            Double.isNaN(regression.getInterceptStdErr()));
        assertTrue("MSE not NaN",Double.isNaN(regression.getMeanSquareError()));
        assertTrue("r NaN",!Double.isNaN(regression.getR()));
        assertTrue("r-square NaN",!Double.isNaN(regression.getRSquare()));
        assertTrue("RSS NaN",
            !Double.isNaN(regression.getRegressionSumSquares()));
        assertTrue("SSE NaN",!Double.isNaN(regression.getSumSquaredErrors()));
        assertTrue("SSTO NaN",!Double.isNaN(regression.getTotalSumSquares()));
        assertTrue("predict NaN",!Double.isNaN(regression.predict(0)));
        
        regression.addData(1,4);
        
        // MSE, MSE, s(b0), s(b1) should all be OK now
        assertTrue("MSE NaN",!Double.isNaN(regression.getMeanSquareError()));
        assertTrue("slope std err NaN",
            !Double.isNaN(regression.getSlopeStdErr()));
        assertTrue("intercept std err NaN",
            !Double.isNaN(regression.getInterceptStdErr()));
    }
    
    public void testClear() {
       BivariateRegression regression = new BivariateRegression();
       regression.addData(corrData);
       assertEquals("number of observations",17,regression.getN());
       regression.clear();
       assertEquals("number of observations",0,regression.getN());
       regression.addData(corrData);
       assertEquals("r-square",.896123,regression.getRSquare(),10E-6);
       regression.addData(data);
       assertEquals("number of observations",53,regression.getN());
    }
    
    public void testInference() {
       BivariateRegression regression = new BivariateRegression();
       regression.addData(infData);
       assertEquals("slope confidence interval", 0.0271,
            regression.getSlopeConfidenceInterval(),0.0001);
       assertEquals("slope std err",0.01146,
            regression.getSlopeStdErr(),0.0001);
       
       regression = new BivariateRegression();
       regression.addData(infData2);
       assertEquals("significance", 0.023331,
            regression.getSignificance(),0.0001);
       
       //FIXME: get a real example to test against with alpha = .01
       assertTrue("tighter means wider",
            regression.getSlopeConfidenceInterval() < 
            regression.getSlopeConfidenceInterval(0.01));
       
       try {
           double x = regression.getSlopeConfidenceInterval(1);
           fail("expecting IllegalArgumentException for alpha = 1");
       } catch (IllegalArgumentException ex) {
           ;
       }
       
    }                                        
}

