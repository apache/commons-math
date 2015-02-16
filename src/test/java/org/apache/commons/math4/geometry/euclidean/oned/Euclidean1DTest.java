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
package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.geometry.Space;
import org.junit.Assert;
import org.junit.Test;

public class Euclidean1DTest {

    @Test
    public void testDimension() {
        Assert.assertEquals(1, Euclidean1D.getInstance().getDimension());
    }

    @Test(expected=Euclidean1D.NoSubSpaceException.class)
    public void testSubSpace() {
        Euclidean1D.getInstance().getSubSpace();
    }

    @Test
    public void testSerialization() {
        Space e1 = Euclidean1D.getInstance();
        Space deserialized = (Space) TestUtils.serializeAndRecover(e1);
        Assert.assertTrue(e1 == deserialized);
    }

}
