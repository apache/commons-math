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
package org.apache.commons.math.stat.univariate.moment;

import java.io.Serializable;

import org.apache.commons.math.stat.univariate.AbstractStorelessUnivariateStatistic;

/**
 * FirstMoment.java
 *
 * The FirstMoment (arithmentic mean) is calculated using the following
 * <a href="http://www.spss.com/tech/stat/Algorithms/11.5/descriptives.pdf">
 * recursive strategy
 * </a>. Both incremental and evaluation strategies currently use this approach.
 * @version $Revision: 1.14 $ $Date: 2004/04/27 16:42:30 $
 */
public class FirstMoment extends AbstractStorelessUnivariateStatistic implements Serializable{

    /** Serializable version identifier */
    static final long serialVersionUID = -803343206421984070L; 
    
    /** count of values that have been added */
    protected long n = 0;

    /** first moment of values that have been added */
    protected double m1 = Double.NaN;

    /**
     * temporary internal state made available for
     * higher order moments
     */
    protected double dev = 0.0;

    /**
     * temporary internal state made available for
     * higher order moments
     */
    protected double v = 0.0;

    /**
     * temporary internal state made available for
     * higher order moments
     */
    protected double n0 = 0.0;

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (n < 1) {
            m1 = 0.0;
        }

        n++;
        dev = d - m1;
        n0 = (double) n;
        v = dev / n0;

        m1 += v;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        m1 = Double.NaN;
        n = 0;
        dev = 0.0;
        v = 0.0;
        n0 = 0.0;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return m1;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getN()
     */
    public double getN() {
        return n;
    }

}