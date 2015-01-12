/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math3.userguide.sofm;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

/**
 * Class that creates two intertwined rings.
 * Each ring is composed of a cloud of points.
 */
public class ChineseRings {
    /** Points in the two rings. */
    private final Vector3D[] points;

    /**
     * @param orientationRing1 Vector othogonal to the plane containing the
     * first ring.
     * @param radiusRing1 Radius of the first ring.
     * @param halfWidthRing1 Half-width of the first ring.
     * @param radiusRing2 Radius of the second ring.
     * @param halfWidthRing2 Half-width of the second ring.
     * @param numPointsRing1 Number of points in the first ring.
     * @param numPointsRing2 Number of points in the second ring.
     */
    public ChineseRings(Vector3D orientationRing1,
                        double radiusRing1,
                        double halfWidthRing1,
                        double radiusRing2,
                        double halfWidthRing2,
                        int numPointsRing1,
                        int numPointsRing2) {
        // First ring (centered at the origin).
        final Vector3D[] firstRing = new Vector3D[numPointsRing1];
        // Second ring (centered around the first ring).
        final Vector3D[] secondRing = new Vector3D[numPointsRing2];

        // Create two rings lying in xy-plane.
        final UnitSphereRandomVectorGenerator unit
            = new UnitSphereRandomVectorGenerator(2);

        final RealDistribution radius1
            = new UniformRealDistribution(radiusRing1 - halfWidthRing1,
                                          radiusRing1 + halfWidthRing1);
        final RealDistribution widthRing1
            = new UniformRealDistribution(-halfWidthRing1, halfWidthRing1);

        for (int i = 0; i < numPointsRing1; i++) {
            final double[] v = unit.nextVector();
            final double r = radius1.sample();
            // First ring is in the xy-plane, centered at (0, 0, 0).
            firstRing[i] = new Vector3D(v[0] * r,
                                        v[1] * r,
                                        widthRing1.sample());
        }

        final RealDistribution radius2
            = new UniformRealDistribution(radiusRing2 - halfWidthRing2,
                                          radiusRing2 + halfWidthRing2);
        final RealDistribution widthRing2
            = new UniformRealDistribution(-halfWidthRing2, halfWidthRing2);

        for (int i = 0; i < numPointsRing2; i++) {
            final double[] v = unit.nextVector();
            final double r = radius2.sample();
            // Second ring is in the xz-plane, centered at (radiusRing1, 0, 0).
            secondRing[i] = new Vector3D(radiusRing1 + v[0] * r,
                                         widthRing2.sample(),
                                         v[1] * r);
        }

        // Move first and second rings into position.
        final Rotation rot = new Rotation(Vector3D.PLUS_K,
                                          orientationRing1.normalize());
        int count = 0;
        points = new Vector3D[numPointsRing1 + numPointsRing2];
        for (int i = 0; i < numPointsRing1; i++) {
            points[count++] = rot.applyTo(firstRing[i]);
        }
        for (int i = 0; i < numPointsRing2; i++) {
            points[count++] = rot.applyTo(secondRing[i]);
        }
    }

    /**
     * Gets all the points.
     */
    public Vector3D[] getPoints() {
        return points.clone();
    }
}
