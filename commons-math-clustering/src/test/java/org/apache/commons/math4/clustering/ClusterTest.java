/*
        Licensed to the Apache Software Foundation (ASF) under one or more
        contributor license agreements.  See the NOTICE file distributed with
        this work for additional information regarding copyright ownership.
        The ASF licenses this file to You under the Apache License, Version 2.0
        (the "License"); you may not use this file except in compliance with
        the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
        -*/
package org.apache.commons.math4.clustering;

import org.junit.Assert;
import org.junit.Test;


public class ClusterTest {

    @Test
    public void testAddPoint() {
        Cluster<DoublePoint> cluster = new Cluster<>();
        DoublePoint origin = new DoublePoint(new double[]{0.0, 0.0});
        cluster.addPoint(origin);
        Assert.assertEquals(1,cluster.getPoints().size());
        Assert.assertEquals(origin,cluster.getPoints().get(0));


    }

    @Test
    public void testAddPointWithClusterablePointNull() {
        Cluster<DoublePoint> cluster = new Cluster<>();
        DoublePoint origin = new DoublePoint(new double[]{0.0, 0.0});
        cluster.addPoint(origin);
        Assert.assertEquals(1,cluster.getPoints().size());
        Assert.assertEquals(origin,cluster.getPoints().get(0));


    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPointWithNull() {
        Cluster<DoublePoint> cluster = new Cluster<>();
        cluster.addPoint(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPointWithgetPointsNull() {
        Cluster<Clusterable> cluster = new Cluster<>();
        cluster.addPoint(() -> null);
    }


    @Test
    public void testGetPoints() {
        Cluster<DoublePoint> cluster = new Cluster<>();
        DoublePoint origin = new DoublePoint(new double[]{0.0, 0.0});
        DoublePoint point11 = new DoublePoint(new double[]{1.0, 1.0});
        cluster.addPoint(origin);
        cluster.addPoint(point11);
        Assert.assertEquals(2,cluster.getPoints().size());
        Assert.assertEquals(origin,cluster.getPoints().get(0));
        Assert.assertEquals(point11,cluster.getPoints().get(1));
        Assert.assertNotSame(cluster.getPoints(),cluster.getPoints());
    }

    @Test
    public void testCentroid() {
        Cluster<DoublePoint> cluster = new Cluster<>();
        DoublePoint origin = new DoublePoint(new double[]{0.0, 0.0});
        DoublePoint point11 = new DoublePoint(new double[]{1.0, 1.0});
        DoublePoint point52 = new DoublePoint(new double[]{5.0, 2.0});
        DoublePoint expectedCentroid = new DoublePoint(new double[]{2.0, 1.0});
        cluster.addPoint(origin);
        cluster.addPoint(point11);
        cluster.addPoint(point52);
        Assert.assertEquals(expectedCentroid,cluster.centroid());
    }

    @Test
    public void testCentroidEmptyPointList() {
        Cluster<DoublePoint> cluster = new Cluster<>();
        Assert.assertNull(cluster.centroid());
    }
}