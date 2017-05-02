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

import org.apache.commons.math4.fraction.BigFraction;
import org.apache.commons.math4.linear.FieldVector;
import org.junit.Test;

/**
 * Tests for multivariate finite difference generator.
 */
public final class MultivariateFiniteDifferenceCoefficientGeneratorTest {

    @Test
    public void secondOrderMixed1() {
	MultivariateFiniteDifference fd = new MultivariateFiniteDifference(
		FiniteDifference.FIVE_POINT_CENTRAL, 
		FiniteDifference.FIVE_POINT_CENTRAL);
	
	MultivariateFiniteDifferenceCoefficientGenerator generator = new MultivariateFiniteDifferenceCoefficientGenerator(fd);
	FieldVector<BigFraction> coefficients = generator.getCoefficients();
	
	for(int index = 0; index < coefficients.getDimension(); index++) {
	    System.out.println(index + "\t" + coefficients.getEntry(index));
	}
    }

}
