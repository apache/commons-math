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

/**
 * The ThirdMoment (arithmentic mean) is calculated using the following
 * <a href="http://www.spss.com/tech/stat/Algorithms/11.5/descriptives.pdf">
 * recursive strategy
 * </a>. Both incremental and evaluation strategies currently use this approach.
 * @version $Revision: 1.14 $ $Date: 2004/02/21 21:35:15 $
 */
public class ThirdMoment extends SecondMoment implements Serializable {

    static final long serialVersionUID = -7818711964045118679L;  
      
    /** third moment of values that have been added */
    protected double m3 = Double.NaN;

    /** temporary internal state made availabel for higher order moments */
    protected double v2 = 0.0;

    /** temporary internal state made availabel for higher order moments */
    protected double n2 = 0.0;

    /** temporary internal state made availabel for higher order moments */
    protected double prevM2 = 0.0;

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (n < 1) {
            m3 = m2 = m1 = 0.0;
        }

        /* retain a reference to the last m2*/
        prevM2 = m2;

        /* increment m1 and m2 (and _n0, _n1, _v) */
        super.increment(d);

        v2 = v * v;
        n2 = (double) (n - 2);

        m3 = m3 - (3.0 * v * prevM2) + (n0 * n1 * n2 * v2 * v);

    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return m3;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        super.clear();
        m3 = Double.NaN;
        v2 = 0.0;
        n2 = 0.0;
        prevM2 = 0.0;
    }

}