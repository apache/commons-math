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
package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class UnitSphereRandomVectorGeneratorTest {
    /**
     * Test the distribution of points from {@link UnitSphereRandomVectorGenerator#nextVector()}
     * in two dimensions.
     */
    @Test
    public void test2DDistribution() {

        RandomGenerator rg = new JDKRandomGenerator();
        rg.setSeed(17399225432l);
        UnitSphereRandomVectorGenerator generator = new UnitSphereRandomVectorGenerator(2, rg);

        // In 2D, angles with a given vector should be uniformly distributed
        int[] angleBuckets = new int[100];
        int steps = 1000000;
        for (int i = 0; i < steps; ++i) {
            final double[] v = generator.nextVector();
            Assert.assertEquals(2, v.length);
            Assert.assertEquals(1, length(v), 1e-10);
            // Compute angle formed with vector (1,0)
            // Cosine of angle is their dot product, because both are unit length
            // Dot product here is just the first element of the vector by construction
            final double angle = FastMath.acos(v[0]);
            final int bucket = (int) (angleBuckets.length * (angle / FastMath.PI));
            ++angleBuckets[bucket];
        }

        // Simplistic test for roughly even distribution
        final int expectedBucketSize = steps / angleBuckets.length;
        for (int bucket : angleBuckets) {
            Assert.assertTrue("Bucket count " + bucket + " vs expected " + expectedBucketSize,
                              FastMath.abs(expectedBucketSize - bucket) < 350);
        }
    }

    /**
     * @return length (L2 norm) of given vector
     */
    private static double length(double[] vector) {
        double total = 0;
        for (double d : vector) {
            total += d * d;
        }
        return FastMath.sqrt(total);
    }
}
