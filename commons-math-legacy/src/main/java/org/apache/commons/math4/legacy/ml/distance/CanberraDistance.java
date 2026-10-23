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
package org.apache.commons.math4.legacy.ml.distance;

import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.math4.legacy.core.MathArrays;

/**
 * Calculates the Canberra distance between two points.
 *
 * @since 3.2
 */
public class CanberraDistance implements DistanceMeasure {
    /** {@inheritDoc} */
    @Override
    public double compute(double[] a, double[] b) {
        MathArrays.checkEqualLength(a, b);
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            final double absA = JdkMath.abs(a[i]);
            final double absB = JdkMath.abs(b[i]);
            final double num = JdkMath.abs(a[i] - b[i]);
            final double denom = absA + absB;
            if (Double.isInfinite(denom) && Double.isFinite(a[i]) && Double.isFinite(b[i])) {
                // Rescale large finite coordinates to avoid intermediate overflow.
                sum += JdkMath.abs(a[i] * 0.5 - b[i] * 0.5) / (absA * 0.5 + absB * 0.5);
            } else {
                sum += num == 0.0 && denom == 0.0 ? 0.0 : num / denom;
            }
        }
        return sum;
    }
}
