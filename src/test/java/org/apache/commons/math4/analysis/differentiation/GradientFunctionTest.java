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

package org.apache.commons.math4.analysis.differentiation;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.util.FastMath;
import org.junit.Test;


/**
 * Test for class {@link GradientFunction}.
 */
public class GradientFunctionTest {

    @Test
    public void test2DDistance() {
        EuclideanDistance f = new EuclideanDistance();
        GradientFunction g = new GradientFunction(f);
        for (double x = -10; x < 10; x += 0.5) {
            for (double y = -10; y < 10; y += 0.5) {
                double[] point = new double[] { x, y };
                TestUtils.assertEquals(f.gradient(point), g.value(point), 1.0e-15);
            }
        }
    }

    @Test
    public void test3DDistance() {
        EuclideanDistance f = new EuclideanDistance();
        GradientFunction g = new GradientFunction(f);
        for (double x = -10; x < 10; x += 0.5) {
            for (double y = -10; y < 10; y += 0.5) {
                for (double z = -10; z < 10; z += 0.5) {
                    double[] point = new double[] { x, y, z };
                    TestUtils.assertEquals(f.gradient(point), g.value(point), 1.0e-15);
                }
            }
        }
    }

    private static class EuclideanDistance implements MultivariateDifferentiableFunction {

        @Override
        public double value(double[] point) {
            double d2 = 0;
            for (double x : point) {
                d2 += x * x;
            }
            return FastMath.sqrt(d2);
        }

        @Override
        public DerivativeStructure value(DerivativeStructure[] point)
            throws DimensionMismatchException, MathIllegalArgumentException {
            DerivativeStructure d2 = point[0].getField().getZero();
            for (DerivativeStructure x : point) {
                d2 = d2.add(x.multiply(x));
            }
            return d2.sqrt();
        }

        public double[] gradient(double[] point) {
            double[] gradient = new double[point.length];
            double d = value(point);
            for (int i = 0; i < point.length; ++i) {
                gradient[i] = point[i] / d;
            }
            return gradient;
        }

    }

}
