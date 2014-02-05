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
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.GrahamScan;
import org.junit.Test;

/**
 * Test class for GrahamScan2D.
 * @version $Id$
 */
public class GrahamScanTest extends ConvexHullGenerator2DAbstractTest {

    @Override
    protected ConvexHullGenerator2D createConvexHullGenerator(boolean includeCollinearPoints) {
        return new GrahamScan(includeCollinearPoints);
    }

    // ------------------------------------------------------------------------------

    @Test
    public void testIdenticalPointsRandom() {
        final List<Vector2D> points = new ArrayList<Vector2D>();

        points.add(new Vector2D(0.7886552422, 0.8629523066));
        points.add(new Vector2D(-0.477657659, -0.818633147));
        points.add(new Vector2D(-0.9778256822, 0.4459975439));
        points.add(new Vector2D(0.9967680357, -0.7956341096));
        points.add(new Vector2D(-0.6644522529, 0.5722968681));
        points.add(new Vector2D(-0.9769155504, 0.2676854695));
        points.add(new Vector2D(0.2378256814, -0.0546758337));
        points.add(new Vector2D(-0.3094786038, -0.4877828777));
        points.add(new Vector2D(0.4700714363, 0.2338673804));
        points.add(new Vector2D(0.1172690966, -0.5931228134));
        points.add(new Vector2D(-0.8863820898, 0.630175898));
        points.add(new Vector2D(0.9967680357, -0.7956341096));
        points.add(new Vector2D(0.5974682835, 0.5581237347));
        points.add(new Vector2D(0.0670101247, 0.523515029));
        points.add(new Vector2D(-0.0534546034, -0.608353757));
        points.add(new Vector2D(-0.3527909285, 0.4330755698));
        points.add(new Vector2D(0.6524149298, -0.6353437037));
        points.add(new Vector2D(-0.7189115058, -0.3074849638));

        final ConvexHull2D hull = generator.generate(points);
        checkConvexHull(points, hull);
    }

    @Test
    public void testIdenticalPointsAtStart() {
        final Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(2, 2));
        points.add(new Vector2D(2, 4));
        points.add(new Vector2D(4, 1.5));
        points.add(new Vector2D(1, 1));

        final ConvexHull2D hull = createConvexHullGenerator(true).generate(points);
        checkConvexHull(points, hull, true);
    }

}
