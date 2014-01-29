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
import java.util.StringTokenizer;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2DFormat;
import org.apache.commons.math3.geometry.euclidean.twod.hull.GrahamScan2D;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class for GrahamScan2D.
 * @version $Id$
 */
public class GrahamScan2DTest extends ConvexHullGenerator2DAbstractTest {

    protected ConvexHullGenerator2D createConvexHullGenerator() {
        return new GrahamScan2D();
    }

    // ------------------------------------------------------------------------------

    @Test
    @Ignore // need to fix the case of points on a line
    public void testMultiplePointsWithSameYCoordinate() {
        Collection<Vector2D> points = new ArrayList<Vector2D>();
        points.add(new Vector2D(1, 1));
        points.add(new Vector2D(2, 2));
        points.add(new Vector2D(2, 4));
        points.add(new Vector2D(4, 1));
        points.add(new Vector2D(10, 1));

        ConvexHull2D hull = generator.generate(points);
        Assert.assertTrue(isConvex(hull));
        checkPointsInsideHullRegion(points, hull);
    }

    @Test
    public void testBug() {
        
        String input = "{0.7886552422; 0.8629523066}{-0.477657659; -0.818633147}{-0.9778256822; 0.4459975439}" +
                       "{0.9967680357; -0.7956341096}{-0.6644522529; 0.5722968681}{-0.9769155504; 0.2676854695}" +
                       "{0.2378256814; -0.0546758337}{-0.3094786038; -0.4877828777}{0.4700714363; 0.2338673804}" +
                       "{0.1172690966; -0.5931228134}{-0.8863820898; 0.630175898}{0.9967680357; -0.7956341096}" +
                       "{0.5974682835; 0.5581237347}{0.0670101247; 0.523515029}{-0.0534546034; -0.608353757}" +
                       "{-0.3527909285; 0.4330755698}{0.6524149298; -0.6353437037}{-0.7189115058; -0.3074849638}";
        
        final List<Vector2D> points = new ArrayList<Vector2D>();
        final StringTokenizer st = new StringTokenizer(input, "{", false);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            Vector2D point = Vector2DFormat.getInstance().parse("{" + token);
            points.add(point);
        }
        
        GrahamScan2D generator = new GrahamScan2D();
        ConvexHull2D hull = generator.generate(points);

        Assert.assertTrue(isConvex(hull));
        checkPointsInsideHullRegion(points, hull);
    }
    
}
