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
package org.apache.commons.math4.legacy.stat.descriptive.rank;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class KthSelectorTest {

    @Test
    public void testRandom() {

        final int numIterations = 100000;
        final double[] possibleValues = {Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.MAX_VALUE, Double.MIN_VALUE, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, -0., 0., 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        final Random rnd = new Random(0);
        for (int i = 0; i < numIterations; ++i) {

            final int dataSize = rnd.nextInt(30);

            final double[] data = new double[dataSize];

            for (int j = 0; j < dataSize; ++j) {
                data[j] = possibleValues[rnd.nextInt(possibleValues.length)];
            }

            final double[] dataSorted = Arrays.copyOf(data, data.length);
            Arrays.sort(dataSorted);

            for (int j = 0; j < dataSize; ++j) {

                final double[] dataTmp = Arrays.copyOf(data, data.length);
                final double resultKthSelector = new KthSelector().select(dataTmp, null, j);
                final double resultSort = dataSorted[j];
                assertEquals(Double.doubleToLongBits(resultKthSelector), Double.doubleToLongBits(resultSort));
            }
        }
    }
}
