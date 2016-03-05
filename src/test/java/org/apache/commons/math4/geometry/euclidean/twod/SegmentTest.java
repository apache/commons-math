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

import org.apache.commons.math4.geometry.euclidean.twod.Line;
import org.apache.commons.math4.geometry.euclidean.twod.Segment;
import org.apache.commons.math4.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class SegmentTest {

    @Test
    public void testDistance() {
        Vector2D start = new Vector2D(2, 2);
        Vector2D end = new Vector2D(-2, -2);
        Segment segment = new Segment(start, end, new Line(start, end, 1.0e-10));

        // distance to center of segment
        Assert.assertEquals(FastMath.sqrt(2), segment.distance(new Vector2D(1, -1)), 1.0e-10);

        // distance a point on segment
        Assert.assertEquals(FastMath.sin(Math.PI / 4.0), segment.distance(new Vector2D(0, -1)), 1.0e-10);

        // distance to end point
        Assert.assertEquals(FastMath.sqrt(8), segment.distance(new Vector2D(0, 4)), 1.0e-10);

        // distance to start point
        Assert.assertEquals(FastMath.sqrt(8), segment.distance(new Vector2D(0, -4)), 1.0e-10);
    }
}
