/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.analysis.differentiation.finite;

import junit.framework.Assert;

import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math4.analysis.function.Cos;
import org.apache.commons.math4.analysis.function.Exp;
import org.apache.commons.math4.analysis.function.Sin;
import org.junit.Test;

/**
 * Some simple tests for the finite difference derivative.
 */
public class UnivariateFiniteDifferenceDerivativeFunctionTest {
        
    @Test
    public void testSin1() {
	Sin sin = new Sin();
	Cos cos = new Cos();
	
	FiniteDifference finiteDifference = new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 4);
	//UnivariateBandwidthStrategy bandwidth = new PowerOfTwoUnivariateBandwidthStrategy(new RuleOfThumbUnivariateBandwidthStrategy());
	UnivariateBandwidthStrategy bandwidth = new PowerOfTwoUnivariateBandwidthStrategy(new MathurAdaptiveUnivariateBandwidthStrategy());
	
	double twoPi = 2d * Math.PI;
	testCore(sin, finiteDifference, bandwidth, cos, -twoPi, twoPi, 10000, null, 1e-12);
    }
    
    @Test
    public void testExp() {
	Exp exp = new Exp();
	FiniteDifference finiteDifference = new FiniteDifference(FiniteDifferenceType.CENTRAL, 1, 4);
	UnivariateBandwidthStrategy bandwidth = new PowerOfTwoUnivariateBandwidthStrategy(new RuleOfThumbUnivariateBandwidthStrategy());
	
	testCore(exp, finiteDifference, bandwidth, exp, -50, 50, 10000, 1e-12, null);
    }
    
    /**
     * Core test harness function.
     * 
     * @param function The function.
     * @param finiteDifference The finite difference stencil.
     * @param bandwidthStrategy The bandwidth strategy.
     * @param analytical The analytical derivative.
     * @param lower Lower bound.
     * @param upper Upper bound.
     * @param stepCount The step count.
     * @param maxRelativeError The relative error bound.
     * @param maxAbsoluteError The absolute error bound.
     */
    private void testCore(
	    final UnivariateFunction function,
	    final FiniteDifference finiteDifference,
	    final UnivariateBandwidthStrategy bandwidthStrategy,
	    final UnivariateFunction analytical,
	    final double lower, 
	    final double upper,
	    final int stepCount,
	    final Double maxRelativeError,
	    final Double maxAbsoluteError) {
	
	// assemble derivative function.
	UnivariateFiniteDifferenceDerivativeFunction numerical = new UnivariateFiniteDifferenceDerivativeFunction(function, finiteDifference, bandwidthStrategy);
		
	FiniteDifferencesDifferentiator fd = new FiniteDifferencesDifferentiator(5, 1e-8);

	for(double x = lower; x <= upper; x += (upper - lower) / stepCount) {
	    double numericalValue = numerical.value(x);
	    double analyticalValue = analytical.value(x);
	    	    
	    double absoluteError = Math.abs(numericalValue - analyticalValue);
	    double relativeError = ((numericalValue) == 0 && (analyticalValue == 0)) ? 0 : 
		absoluteError / Math.max(Math.abs(numericalValue), Math.abs(analyticalValue));
	    
	    double numericalValue2 = fd.differentiate(function).value(new DerivativeStructure(1, 1, 0, x)).getPartialDerivative(1);
	    double absoluteError2 = Math.abs(numericalValue2 - analyticalValue);
	    double relativeError2 = ((numericalValue2) == 0 && (analyticalValue == 0)) ? 0 : 
		absoluteError2 / Math.max(Math.abs(numericalValue2), Math.abs(analyticalValue));
	    
	    Assert.assertTrue(String.format("Relative error of %1$s > %2$s at %3$s (%4$s, %5$s)",
		    relativeError, 
		    maxRelativeError,
		    x,
		    numericalValue,
		    analyticalValue), 
		    (maxRelativeError == null) || (relativeError < maxRelativeError));

	    Assert.assertTrue(String.format("Absolute error of %1$s > %2$s at %3$s (%4$s, %5$s)",
		    absoluteError, 
		    maxAbsoluteError,
		    x,
		    numericalValue,
		    analyticalValue), 
		    (maxAbsoluteError == null) || (absoluteError < maxAbsoluteError));
	}		
    }

    private static final class Quintic 
    	implements UnivariateFunction {

	/**
	 * @see org.apache.commons.math4.analysis.UnivariateFunction#value(double)
	 */
	@Override
	public double value(double x) {
	    double value = (x-1)*(x-0.5)*x*(x+0.5)*(x+1);
	    
	    return value;
	}	
	
    }
    
}
