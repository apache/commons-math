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
package org.apache.commons.math.stat.correlation;

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Bivariate Covariance implementation that does not require input data to be
 * stored in memory.
 *
 * @version $Id$
 * @since 3.0
 */
public class StorelessBivariateCovariance {

    private double deltaX = 0.0;

    private double deltaY = 0.0;

    private double meanX = 0.0;

    private double meanY = 0.0;

    private double n = 0;

    private double covarianceNumerator = 0.0;

    private boolean biasCorrected = true;

    public StorelessBivariateCovariance() {
    }

    public StorelessBivariateCovariance(boolean biasCorrected) {
        this.biasCorrected = biasCorrected;
    }

    public void increment(double x, double y) {
        n++;
        deltaX = x - meanX;
        deltaY = y - meanY;
        meanX += deltaX / n;
        meanY += deltaY / n;
        covarianceNumerator += ((n - 1.0) / n) * deltaX * deltaY;
    }

    public double getN() {
        return n;
    }

    public double getResult() throws IllegalArgumentException {
        if (n < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION,
                                                   n, 2);
        }
        if (biasCorrected) {
            return covarianceNumerator / (n - 1d);
        } else {
            return covarianceNumerator / n;
        }
    }

}

