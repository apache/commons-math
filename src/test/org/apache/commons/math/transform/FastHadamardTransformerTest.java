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
package org.apache.commons.math.transform;

import junit.framework.TestCase;

/**
 * JUnit Test for HadamardTransformerTest
 * @see org.apache.commons.math.transform.FastHadamardTransformer
 */
public final class FastHadamardTransformerTest extends TestCase {

    /**
     * Test of transformer for the a 8-point FHT (means n=8)
     */
    public void test8Points() {
        checkTransform(new double[] { 1.0, 4.0, -2.0, 3.0, 0.0, 1.0, 4.0, -1.0 },
                       new double[] { 10.0, -4.0, 2.0, -4.0, 2.0, -12.0, 6.0, 8.0 });
    }

    /**
     * Test of transformer for the a 4-points FHT (means n=4)
     */
    public void test4Points() {
        checkTransform(new double[] { 1.0, 2.0, 3.0, 4.0 },
                       new double[] { 10.0, -2.0, -4.0, 0.0 });
    }

    /**
     * Test of transformer for wrong number of points
     */
    public void test3Points() {
        try {
            new FastHadamardTransformer().transform(new double[3]);
            fail("an exception should have been thrown");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    private void checkTransform(double[]x, double[] y) {
        // Initiate the transformer
        FastHadamardTransformer transformer = new FastHadamardTransformer();

        // transform input vector x to output vector
        double result[] = transformer.transform(x);

        for (int i=0;i<result.length;i++) {
            // compare computed results to precomputed results
            assertEquals(y[i], result[i]);
        }
    }
    
}
