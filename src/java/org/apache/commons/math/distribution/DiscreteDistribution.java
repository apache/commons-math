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
package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

/**
 * Base interface for various discrete distributions.
 * 
 * @version $Revision: 1.11 $ $Date: 2004/04/08 20:45:59 $
 */
public interface DiscreteDistribution {
    /**
     * For this disbution, X, this method returns P(X = x).
     * @param x the value at which the PMF is evaluated.
     * @return PMF for this distribution. 
     */
    double probability(int x);
    
    /**
     * For this disbution, X, this method returns P(X &le; x).
     * @param x the value at which the PDF is evaluated.
     * @return PDF for this distribution. 
     * @exception MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    double cumulativeProbability(int x) throws MathException;

    /**
     * For this disbution, X, this method returns P(x0 &le; X &le; x1).
     * @param x0 the inclusive, lower bound
     * @param x1 the inclusive, upper bound
     * @return the cumulative probability. 
     * @exception MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    double cumulativeProbability(int x0, int x1) throws MathException;
    
    /**
     * For this disbution, X, this method returns x such that P(X &le; x) <= p.
     * @param p the cumulative probability.
     * @return x. 
     * @exception MathException if the inverse cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    int inverseCumulativeProbability(double p) throws MathException;
}
