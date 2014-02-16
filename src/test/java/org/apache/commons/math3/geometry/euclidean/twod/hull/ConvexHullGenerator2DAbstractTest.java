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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Region.Location;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
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
    protected RandomGenerator random;

    protected abstract ConvexHullGenerator2D createConvexHullGenerator(boolean includeCollinearPoints);

    protected Collection<Vector2D> reducePoints(Collection<Vector2D> points) {
        // do nothing by default, may be overridden by other tests
        return points;
    }

    @Before
    public void setUp() {
        generator = createConvexHullGenerator(false);
        random = new MersenneTwister(10);
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
    public void testAllIdentical() {
        final Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(1, 1));

        final ConvexHull2D hull = generator.generate(points);
        Assert.assertTrue(hull.getVertices().length == 1);
    }

    @Test
    public void testConvexHull() {
        // execute 100 random variations
        for (int i = 0; i < 100; i++) {
            // randomize the size from 4 to 100
            int size = (int) FastMath.floor(random.nextDouble() * 96.0 + 4.0);

            List<Vector2D> points = createRandomPoints(size);
            ConvexHull2D hull = generator.generate(reducePoints(points));
            checkConvexHull(points, hull);
        }
    }

    @Test
    public void testCollinearPoints() {
        final Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(2, 2));
        points.add(new Vector2D(2, 4));
        points.add(new Vector2D(4, 1));
        points.add(new Vector2D(10, 1));

        final ConvexHull2D hull = generator.generate(points);
        checkConvexHull(points, hull);
    }

    @Test
    public void testCollinearPointsReverse() {
        final Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(2, 2));
        points.add(new Vector2D(2, 4));
        points.add(new Vector2D(10, 1));
        points.add(new Vector2D(4, 1));

        final ConvexHull2D hull = generator.generate(points);
        checkConvexHull(points, hull);
    }

    @Test
    public void testCollinearPointsIncluded() {
        final Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(2, 2));
        points.add(new Vector2D(2, 4));
        points.add(new Vector2D(4, 1));
        points.add(new Vector2D(10, 1));

        final ConvexHull2D hull = createConvexHullGenerator(true).generate(points);
        checkConvexHull(points, hull, true);
    }

    @Test
    public void testCollinearPointsIncludedReverse() {
        final Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(2, 2));
        points.add(new Vector2D(2, 4));
        points.add(new Vector2D(10, 1));
        points.add(new Vector2D(4, 1));

        final ConvexHull2D hull = createConvexHullGenerator(true).generate(points);
        checkConvexHull(points, hull, true);
    }

    @Test
    public void testIdenticalPoints() {
        final Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(2, 2));
        points.add(new Vector2D(2, 4));
        points.add(new Vector2D(4, 1));
        points.add(new Vector2D(1, 1));

        final ConvexHull2D hull = generator.generate(points);
        checkConvexHull(points, hull);
    }

    @Test
    public void testIdenticalPoints2() {
        final Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(2, 2));
        points.add(new Vector2D(2, 4));
        points.add(new Vector2D(4, 1));
        points.add(new Vector2D(1, 1));

        final ConvexHull2D hull = createConvexHullGenerator(true).generate(points);
        checkConvexHull(points, hull, true);
    }

    @Test
    public void testClosePoints() {
        final Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(2, 2));
        points.add(new Vector2D(2, 4));
        points.add(new Vector2D(4, 1));
        points.add(new Vector2D(1.00001, 1));

        final ConvexHull2D hull = generator.generate(points);
        checkConvexHull(points, hull);
    }

    // ------------------------------------------------------------------------------
    
    protected final List<Vector2D> createRandomPoints(int size) {
        // create the cloud container
        List<Vector2D> points = new ArrayList<Vector2D>(size);
        // fill the cloud with a random distribution of points
        for (int i = 0; i < size; i++) {
            points.add(new Vector2D(random.nextDouble() * 2.0 - 1.0, random.nextDouble() * 2.0 - 1.0));
        }
        return points;
    }

    protected final void checkConvexHull(final Collection<Vector2D> points, final ConvexHull2D hull) {
        checkConvexHull(points, hull, false);
    }

    protected final void checkConvexHull(final Collection<Vector2D> points, final ConvexHull2D hull,
                                         final boolean includesCollinearPoints) {
        Assert.assertNotNull(hull);
        Assert.assertTrue(isConvex(hull, includesCollinearPoints));
        checkPointsInsideHullRegion(points, hull, includesCollinearPoints);
    }

    // verify that the constructed hull is really convex
    protected final boolean isConvex(final ConvexHull2D hull, final boolean includesCollinearPoints) {
        double sign = 0.0;

        final Vector2D[] points = hull.getVertices();

        for (int i = 0; i < points.length; i++) {
            Vector2D p1 = points[i == 0 ? points.length - 1 : i - 1];
            Vector2D p2 = points[i];
            Vector2D p3 = points[i == points.length - 1 ? 0 : i + 1];

            Vector2D d1 = p2.subtract(p1);
            Vector2D d2 = p3.subtract(p2);

            Assert.assertTrue(d1.getNorm() > 1e-10);
            Assert.assertTrue(d2.getNorm() > 1e-10);

            double cross = FastMath.signum(d1.getX() * d2.getY() - d1.getY() * d2.getX());

            if (sign != 0.0 && cross != sign) {
                if (includesCollinearPoints && cross == 0.0) {
                    // in case of collinear points the cross product will be zero
                } else {
                    return false;
                }
            }
            
            sign = cross;
        }
        
        return true;
    }
    
    // verify that all points are inside the convex hull region
    protected final void checkPointsInsideHullRegion(final Collection<Vector2D> points,
                                                     final ConvexHull2D hull,
                                                     final boolean includesCollinearPoints) {

        final Collection<Vector2D> hullVertices = Arrays.asList(hull.getVertices());
        final Region<Euclidean2D> region = hull.createRegion();

        for (final Vector2D p : points) {
            Location location = region.checkPoint(p);
            Assert.assertTrue(location != Location.OUTSIDE);

            if (location == Location.BOUNDARY && includesCollinearPoints) {
                Assert.assertTrue(hullVertices.contains(p));
            }
        }
    }
}
