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

/**
 *
 * Abstract Implementation for the {@link StorelessUnivariateStatistic} interface.
 * <p>
 * Provides a default <code>evaluate()</code> implementation.
 * 
 * @version $Revision: 1.14 $ $Date: 2004/03/21 21:57:18 $
 */
public abstract class AbstractStorelessUnivariateStatistic
    extends AbstractUnivariateStatistic
    implements StorelessUnivariateStatistic {

    /**
     * This default implementation just calls {@link #increment} in a loop over the input array and 
     * then {@link #getResult} to compute the return value.  
     * <p>
     * Most implementations will override this method with a more efficient implementation that works
     * directly with the input array.
     * 
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public double evaluate(final double[] values, final int begin, final int length) {
        if (this.test(values, begin, length)) {
            this.clear();
            int l = begin + length;
            for (int i = begin; i < l; i++) {
                increment(values[i]);
            }
        }
        return getResult();
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public abstract void clear();

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public abstract double getResult();

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public abstract void increment(final double d);

}