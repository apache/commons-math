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

import org.apache.commons.math.util.MathUtils;
import java.io.Serializable;

/**
 *
 * Abstract Implementation for the {@link StorelessUnivariateStatistic} interface.
 * <p>
 * Provides a default <code>evaluate()</code> implementation.
 * 
 * @version $Revision: 1.16 $ $Date: 2004/06/17 22:31:58 $
 */
public abstract class AbstractStorelessUnivariateStatistic
    extends AbstractUnivariateStatistic
    implements StorelessUnivariateStatistic, Serializable {

    /** Serialization UID */
    static final long serialVersionUID = -44915725420072521L;
    
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
    
    /**
     * Returns true iff <code>object</code> is an 
     * <code>AbstractStorelessUnivariateStatistic</code> returning the same
     * values as this for <code>getResult()</code> and <code>getN()</code>
     * 
     * @return true if object returns the same value as this
     */
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
       if (object instanceof AbstractStorelessUnivariateStatistic == false) {
            return false;
        }
        AbstractStorelessUnivariateStatistic stat = (AbstractStorelessUnivariateStatistic) object;
        return (MathUtils.equals(stat.getResult(), this.getResult()) && 
                MathUtils.equals(stat.getN(), this.getN()));
    }
    
    /**
     * Returns hash code based on getResult() and getN()
     * 
     * @return hash code
     */
    public int hashCode() {
        return 31* (31 + MathUtils.hash(getResult())) + MathUtils.hash(getN());
    }

}