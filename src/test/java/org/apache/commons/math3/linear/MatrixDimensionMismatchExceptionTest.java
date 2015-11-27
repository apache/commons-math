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
package org.apache.commons.math3.linear;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link MatrixDimensionMismatchException}.
 *
 */
public class MatrixDimensionMismatchExceptionTest {
    @Test
    public void testAccessors() {
        final MatrixDimensionMismatchException e
            = new MatrixDimensionMismatchException(1, 2, 3, 4);
        Assert.assertEquals(1, e.getWrongRowDimension());
        Assert.assertEquals(2, e.getWrongColumnDimension());
        Assert.assertEquals(3, e.getExpectedRowDimension());
        Assert.assertEquals(4, e.getExpectedColumnDimension());
    }
}
