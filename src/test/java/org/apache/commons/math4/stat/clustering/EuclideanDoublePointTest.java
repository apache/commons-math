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
package org.apache.commons.math3.stat.clustering;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class EuclideanDoublePointTest {

    @Test
    public void testArrayIsReference() {
        final double[] array = { -3.0, -2.0, -1.0, 0.0, 1.0 };
        Assert.assertArrayEquals(array, new EuclideanDoublePoint(array).getPoint(), 1.0e-15);
    }

    @Test
    public void testDistance() {
        final EuclideanDoublePoint e1 = new EuclideanDoublePoint(new double[] { -3.0, -2.0, -1.0, 0.0, 1.0 });
        final EuclideanDoublePoint e2 = new EuclideanDoublePoint(new double[] { 1.0, 0.0, -1.0, 1.0, 1.0 });
        Assert.assertEquals(FastMath.sqrt(21.0), e1.distanceFrom(e2), 1.0e-15);
        Assert.assertEquals(0.0, e1.distanceFrom(e1), 1.0e-15);
        Assert.assertEquals(0.0, e2.distanceFrom(e2), 1.0e-15);
    }

    @Test
    public void testCentroid() {
        final List<EuclideanDoublePoint> list = new ArrayList<EuclideanDoublePoint>();
        list.add(new EuclideanDoublePoint(new double[] { 1.0, 3.0 }));
        list.add(new EuclideanDoublePoint(new double[] { 2.0, 2.0 }));
        list.add(new EuclideanDoublePoint(new double[] { 3.0, 3.0 }));
        list.add(new EuclideanDoublePoint(new double[] { 2.0, 4.0 }));
        final EuclideanDoublePoint c = list.get(0).centroidOf(list);
        Assert.assertEquals(2.0, c.getPoint()[0], 1.0e-15);
        Assert.assertEquals(3.0, c.getPoint()[1], 1.0e-15);
    }

    @Test
    public void testSerial() {
        final EuclideanDoublePoint p = new EuclideanDoublePoint(new double[] { -3.0, -2.0, -1.0, 0.0, 1.0 });
        Assert.assertEquals(p, TestUtils.serializeAndRecover(p));
    }

}
