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
 * The SecondMoment is calculated using the following
 * <a href="http://www.spss.com/tech/stat/Algorithms/11.5/descriptives.pdf">
 * recursive strategy
 * </a>. Both incremental and evaluation strategies currently use this approach.
 * @version $Revision: 1.16 $ $Date: 2004/06/23 16:26:15 $
 */
public class SecondMoment extends FirstMoment implements Serializable {

    /** Serializable version identifier */
    static final long serialVersionUID = 3942403127395076445L;  
      
    /** second moment of values that have been added */
    protected double m2 = Double.NaN;

    /** temporary internal state made availabel for higher order moments */
    protected double n1 = 0.0;

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (n < 1) {
            m1 = m2 = 0.0;
        }

        /* increment m1 and _n0, _dev,  _v) */
        super.increment(d);

        n1 = n0 - 1;

        /* increment and return m2 */
        m2 += n1 * dev * v;

    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        super.clear();
        m2 = Double.NaN;
        n1 = 0.0;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return m2;
    }

}
