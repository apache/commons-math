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
 * The FourthMoment is calculated using the following
 * <a href="http://www.spss.com/tech/stat/Algorithms/11.5/descriptives.pdf">
 * recursive strategy
 * </a>. Both incremental and evaluation strategies currently use this approach.
 * @version $Revision: 1.17 $ $Date: 2004/06/23 16:26:15 $
 */
public class FourthMoment extends ThirdMoment implements Serializable{

    /** Serializable version identifier */
    static final long serialVersionUID = 4763990447117157611L;
        
    /** fourth moment of values that have been added */
    protected double m4 = Double.NaN;

    /** temporary internal state made available for higher order moments */
    protected double prevM3 = 0.0;

    /** temporary internal state made available for higher order moments */
    protected double n3 = 0.0;


    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (n < 1) {
            m4 = 0.0;
            m3 = 0.0;
            m2 = 0.0;
            m1 = 0.0;
        }

        /* retain previous m3 */
        prevM3 = m3;

        /* increment m1, m2 and m3 (and prevM2, _n0, _n1, _n2, _v, _v2) */
        super.increment(d);

        n3 = (double) (n - 3);

        m4 = m4 - (4.0 * v * prevM3) + (6.0 * v2 * prevM2) +
            ((n0 * n0) - 3 * n1) * (v2 * v2 * n1 * n0);
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return m4;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        super.clear();
        m4 = Double.NaN;
        prevM3 = 0.0;
        n3 = 0.0;
    }

}
