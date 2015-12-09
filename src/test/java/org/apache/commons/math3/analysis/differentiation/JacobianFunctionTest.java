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

package org.apache.commons.math3.analysis.differentiation;

import org.junit.Assert;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.util.FastMath;
import org.junit.Test;


/**
 * Test for class {@link JacobianFunction}.
 */
public class JacobianFunctionTest {

    @Test
    public void testSphere() {
        SphereMapping    f = new SphereMapping(10.0);
        JacobianFunction j = new JacobianFunction(f);
        for (double latitude = -1.5; latitude < 1.5; latitude += 0.1) {
            for (double longitude = -3.1; longitude < 3.1; longitude += 0.1) {
                double[] point = new double[] { latitude, longitude };
                double[][] referenceJacobian  = f.jacobian(point);
                double[][] testJacobian       = j.value(point);
                Assert.assertEquals(referenceJacobian.length, testJacobian.length);
                for (int i = 0; i < 3; ++i) {
                    TestUtils.assertEquals(referenceJacobian[i], testJacobian[i], 2.0e-15);
                }
            }
        }
    }

    /* Maps (latitude, longitude) to (x, y, z) */
    private static class SphereMapping implements MultivariateDifferentiableVectorFunction {

        private final double radius;

        public SphereMapping(final double radius) {
            this.radius = radius;
        }

        public double[] value(double[] point) {
            final double cLat = FastMath.cos(point[0]);
            final double sLat = FastMath.sin(point[0]);
            final double cLon = FastMath.cos(point[1]);
            final double sLon = FastMath.sin(point[1]);
            return new double[] {
                radius * cLon * cLat,
                radius * sLon * cLat,
                radius * sLat
            };
        }

        public DerivativeStructure[] value(DerivativeStructure[] point) {
            final DerivativeStructure cLat = point[0].cos();
            final DerivativeStructure sLat = point[0].sin();
            final DerivativeStructure cLon = point[1].cos();
            final DerivativeStructure sLon = point[1].sin();
            return new DerivativeStructure[] {
                cLon.multiply(cLat).multiply(radius),
                sLon.multiply(cLat).multiply(radius),
                sLat.multiply(radius)
            };
        }

        public double[][] jacobian(double[] point) {
            final double cLat = FastMath.cos(point[0]);
            final double sLat = FastMath.sin(point[0]);
            final double cLon = FastMath.cos(point[1]);
            final double sLon = FastMath.sin(point[1]);
            return new double[][] {
                { -radius * cLon * sLat, -radius * sLon * cLat },
                { -radius * sLon * sLat,  radius * cLon * cLat },
                {  radius * cLat,         0  }
            };
        }

    }

}
