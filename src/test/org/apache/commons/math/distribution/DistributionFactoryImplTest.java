/*
 * Copyright 2003-2005 The Apache Software Foundation.
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

package org.apache.commons.math.distribution;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
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
    
    public void testCreateFDistributionNegativePositive(){
        try {
            factory.createFDistribution(-1.0, 1.0);
            fail("negative degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateFDistributionZeroPositive(){
        try {
            factory.createFDistribution(0.0, 1.0);
            fail("zero degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateFDistributionPositiveNegative(){
        try {
            factory.createFDistribution(1.0, -1.0);
            fail("negative degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateFDistributionPositiveZero(){
        try {
            factory.createFDistribution(1.0, 0.0);
            fail("zero degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateFDistributionPositivePositive(){
        try {
            factory.createFDistribution(1.0, 1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive degrees of freedom.  IllegalArgumentException is not expected");
        }
    }
    
    public void testCreateExponentialDistributionNegative(){
        try {
            factory.createExponentialDistribution(-1.0);
            fail("negative mean.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateExponentialDistributionZero(){
        try {
            factory.createExponentialDistribution(0.0);
            fail("zero mean.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateExponentialDistributionPositive(){
        try {
            factory.createExponentialDistribution(1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive mean.  IllegalArgumentException is not expected");
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
    
    public void testCreateTDistributionNegative(){
        try {
            factory.createTDistribution(-1.0);
            fail("negative degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateTDistributionZero(){
        try {
            factory.createTDistribution(0.0);
            fail("zero degrees of freedom.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex) {
            ;
        }
    }
    
    public void testCreateTDistributionPositive(){
        try {
            factory.createTDistribution(1.0);
        } catch (IllegalArgumentException ex) {
            fail("positive degrees of freedom.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionNegativePositive(){
        try {
            factory.createBinomialDistribution(-1, 0.5);
            fail("negative number of trials.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex ) {
        }
    }
    
    public void testBinomialDistributionZeroPositive(){
        try {
            factory.createBinomialDistribution(0, 0.5);
        } catch (IllegalArgumentException ex ) {
            fail("zero number of trials.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionPositivePositive(){
        try {
            factory.createBinomialDistribution(10, 0.5);
        } catch (IllegalArgumentException ex ) {
            fail("positive number of trials.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionPositiveNegative(){
        try {
            factory.createBinomialDistribution(10, -0.5);
            fail("negative probability of success.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex ) {
        }
    }
    
    public void testBinomialDistributionPositiveZero(){
        try {
            factory.createBinomialDistribution(10, 0.0);
        } catch (IllegalArgumentException ex ) {
            fail("zero probability of success.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionPositiveOne(){
        try {
            factory.createBinomialDistribution(10, 1.0);
        } catch (IllegalArgumentException ex ) {
            fail("valid probability of success.  IllegalArgumentException is not expected");
        }
    }
    
    public void testBinomialDistributionPositiveTwo(){
        try {
            factory.createBinomialDistribution(10, 2.0);
            fail("high probability of success.  IllegalArgumentException expected");
        } catch (IllegalArgumentException ex ) {
        }
    }
    
    public void testHypergeometricDistributionNegativePositivePositive(){
        try {
            factory.createHypergeometricDistribution(-1, 10, 10);
            fail("negative population size.  IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testHypergeometricDistributionZeroPositivePositive(){
        try {
            factory.createHypergeometricDistribution(0, 10, 10);
            fail("zero population size.  IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testHypergeometricDistributionPositiveNegativePositive(){
        try {
            factory.createHypergeometricDistribution(20, -1, 10);
            fail("negative number of successes.  IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testHypergeometricDistributionPositiveZeroPositive(){
        try {
            factory.createHypergeometricDistribution(20, 0, 10);
        } catch(IllegalArgumentException ex) {
            fail("valid number of successes.  IllegalArgumentException is not expected");
        }
    }
    
    public void testHypergeometricDistributionPositivePositiveNegative(){
        try {
            factory.createHypergeometricDistribution(20, 10, -1);
            fail("negative sample size.  IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testHypergeometricDistributionPositivePositiveZero(){
        try {
            factory.createHypergeometricDistribution(20, 10, 0);
        } catch(IllegalArgumentException ex) {
            fail("valid sample size.  IllegalArgumentException is not expected");
        }
    }
    
    public void testHypergeometricDistributionSmallPopulationSize() {
        try {
            factory.createHypergeometricDistribution(5, 3, 10);
            fail("sample size larger than population size.  IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testCauchyDistributionNegative() {
        try {
            factory.createCauchyDistribution(0.0, -1.0);
            fail("invalid scale. IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testCauchyDistributionZero() {
        try {
            factory.createCauchyDistribution(0.0, 0.0);
            fail("invalid scale. IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testWeibullDistributionNegativePositive() {
        try {
            factory.createWeibullDistribution(-1.0, 1.0);
            fail("invalid shape. IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testWeibullDistributionZeroPositive() {
        try {
            factory.createWeibullDistribution(0.0, 1.0);
            fail("invalid shape. IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testWeibullDistributionPositiveNegative() {
        try {
            factory.createWeibullDistribution(1.0, -1.0);
            fail("invalid scale. IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
    
    public void testWeibullDistributionPositiveZero() {
        try {
            factory.createWeibullDistribution(1.0, 0.0);
            fail("invalid scale. IllegalArgumentException expected");
        } catch(IllegalArgumentException ex) {
        }
    }
}
