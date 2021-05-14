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
package org.apache.commons.math4.legacy.linear;

import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.junit.Assert;
import org.junit.Test;

public final class OpenMapRealMatrixTest {

    @Test(expected=NumberIsTooLargeException.class)
    public void testMath679() {
        new OpenMapRealMatrix(3, Integer.MAX_VALUE);
    }

    @Test
    public void testMath870() {
        // Caveat: This implementation assumes that, for any {@code x},
        // the equality {@code x * 0d == 0d} holds. But it is is not true for
        // {@code NaN}. Moreover, zero entries will lose their sign.
        // Some operations (that involve {@code NaN} and/or infinities) may
        // thus give incorrect results.
        OpenMapRealMatrix a = new OpenMapRealMatrix(3, 3);
        OpenMapRealMatrix x = new OpenMapRealMatrix(3, 1);
        x.setEntry(0, 0, Double.NaN);
        x.setEntry(2, 0, Double.NEGATIVE_INFINITY);
        OpenMapRealMatrix b = a.multiply(x);
        for (int i = 0; i < b.getRowDimension(); ++i) {
            for (int j = 0; j < b.getColumnDimension(); ++j) {
                // NaNs and infinities have disappeared, this is a limitation of our implementation
                Assert.assertEquals(0.0, b.getEntry(i, j), 1.0e-20);
            }
        }
    }
}
