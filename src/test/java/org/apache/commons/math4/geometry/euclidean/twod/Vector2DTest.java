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
package org.apache.commons.math4.geometry.euclidean.twod;

import org.apache.commons.math4.geometry.euclidean.twod.Vector2D;
import org.junit.Assert;
import org.junit.Test;

public class Vector2DTest {

    @Test
    public void testCrossProduct() {
        final double epsilon = 1e-10;

        Vector2D p1 = new Vector2D(1, 1);
        Vector2D p2 = new Vector2D(2, 2);

        Vector2D p3 = new Vector2D(3, 3);
        Assert.assertEquals(0.0, p3.crossProduct(p1, p2), epsilon);

        Vector2D p4 = new Vector2D(1, 2);
        Assert.assertEquals(1.0, p4.crossProduct(p1, p2), epsilon);

        Vector2D p5 = new Vector2D(2, 1);
        Assert.assertEquals(-1.0, p5.crossProduct(p1, p2), epsilon);
    }
}
