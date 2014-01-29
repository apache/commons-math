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
package org.apache.commons.math3.geometry.euclidean.twod.hull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Region.Location;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract base test class for 2D convex hull generators.
 * 
 * @version $Id$
 */
public abstract class ConvexHullGenerator2DAbstractTest {

    protected ConvexHullGenerator2D generator;

    protected abstract ConvexHullGenerator2D createConvexHullGenerator();

    @Before
    public void setUp() {
        generator = createConvexHullGenerator();
    }

    // ------------------------------------------------------------------------------

    @Test(expected = NullArgumentException.class)
    public void testNullArgument() {
        generator.generate(null);
    }
    
    @Test
    public void testEmpty() {
        ConvexHull2D hull = generator.generate(Collections.<Vector2D>emptyList());
        Assert.assertTrue(hull.getVertices().length == 0);
        Assert.assertTrue(hull.getLineSegments().length == 0);
    }

    @Test
    public void testOnePoint() {
        List<Vector2D> points = createRandomPoints(1);
        ConvexHull2D hull = generator.generate(points);
        Assert.assertTrue(hull.getVertices().length == 1);
        Assert.assertTrue(hull.getLineSegments().length == 0);
    }

    @Test
    public void testTwoPoints() {
        List<Vector2D> points = createRandomPoints(2);
        ConvexHull2D hull = generator.generate(points);
        Assert.assertTrue(hull.getVertices().length == 2);
        Assert.assertTrue(hull.getLineSegments().length == 1);
    }

    @Test
    public void testConvexHull() {
        // randomize the size from 4 to 100
        int size = (int) FastMath.floor(FastMath.random() * 96.0 + 4.0);

        List<Vector2D> points = createRandomPoints(size);

        ConvexHull2D hull = generator.generate(points);

        Assert.assertNotNull(hull);
        Assert.assertTrue(isConvex(hull));
        checkPointsInsideHullRegion(points, hull);
    }

    // ------------------------------------------------------------------------------
    
    protected final List<Vector2D> createRandomPoints(int size) {
        // create the cloud container
        List<Vector2D> points = new ArrayList<Vector2D>(size);
        // fill the cloud with a random distribution of points
        for (int i = 0; i < size; i++) {
            points.add(new Vector2D(FastMath.random() * 2.0 - 1.0, FastMath.random() * 2.0 - 1.0));
        }
        return points;
    }
    
    // verify that the constructed hull is really convex
    protected final boolean isConvex(final ConvexHull2D hull) {
        double sign = 0.0;

        final Vector2D[] points = hull.getVertices();
        for (int i = 0; i < points.length; i++) {
            Vector2D p1 = points[i == 0 ? points.length - 1 : i - 1];
            Vector2D p2 = points[i];
            Vector2D p3 = points[i == points.length - 1 ? 0 : i + 1];

            Vector2D d1 = p2.subtract(p1);
            Vector2D d2 = p3.subtract(p2);
                
            double cross = FastMath.signum(d1.getX() * d2.getY() - d1.getY() * d2.getX());

            if (sign != 0.0 && cross != sign) {
                return false;
            }
            
            sign = cross;
        }
        
        return true;
    }
    
    // verify that all points are inside the convex hull region
    protected final void checkPointsInsideHullRegion(final Collection<Vector2D> points,
                                                     final ConvexHull2D hull) {

        Region<Euclidean2D> region = hull.createRegion();

        for (final Vector2D p : points) {
            Location location = region.checkPoint(p);
            Assert.assertTrue(location != Location.OUTSIDE);
        }
    }
}
