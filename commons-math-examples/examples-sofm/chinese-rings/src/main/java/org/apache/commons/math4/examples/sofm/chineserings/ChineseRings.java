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

package org.apache.commons.math4.examples.sofm.chineserings;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.sampling.UnitSphereSampler;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.geometry.euclidean.threed.rotation.Rotation3D;
import org.apache.commons.geometry.euclidean.threed.rotation.QuaternionRotation;

/**
 * Class that creates two intertwined rings in 3D-space.
 * Each ring is composed of a cloud of points.
 */
class ChineseRings {
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
    ChineseRings(Vector3D orientationRing1,
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

        final UniformRandomProvider rng = RandomSource.create(RandomSource.WELL_19937_C);

        // Create two rings lying in xy-plane.
        final UnitSphereSampler unit = UnitSphereSampler.of(rng, 2);

        final ContinuousUniformSampler radius1
            = new ContinuousUniformSampler(rng,
                                           radiusRing1 - halfWidthRing1,
                                           radiusRing1 + halfWidthRing1);
        final ContinuousUniformSampler widthRing1
            = new ContinuousUniformSampler(rng,
                                           -halfWidthRing1,
                                           halfWidthRing1);

        for (int i = 0; i < numPointsRing1; i++) {
            final double[] v = unit.sample();
            final double r = radius1.sample();
            // First ring is in the xy-plane, centered at (0, 0, 0).
            firstRing[i] = Vector3D.of(v[0] * r,
                                       v[1] * r,
                                       widthRing1.sample());
        }

        final ContinuousUniformSampler radius2
            = new ContinuousUniformSampler(rng,
                                           radiusRing2 - halfWidthRing2,
                                           radiusRing2 + halfWidthRing2);
        final ContinuousUniformSampler widthRing2
            = new ContinuousUniformSampler(rng,
                                           -halfWidthRing2,
                                           halfWidthRing2);

        for (int i = 0; i < numPointsRing2; i++) {
            final double[] v = unit.sample();
            final double r = radius2.sample();
            // Second ring is in the xz-plane, centered at (radiusRing1, 0, 0).
            secondRing[i] = Vector3D.of(radiusRing1 + v[0] * r,
                                        widthRing2.sample(),
                                        v[1] * r);
        }

        // Move first and second rings into position.
        final Rotation3D rot = QuaternionRotation.createVectorRotation(Vector3D.Unit.PLUS_Z,
                                                                       orientationRing1.normalize());
        int count = 0;
        points = new Vector3D[numPointsRing1 + numPointsRing2];
        for (int i = 0; i < numPointsRing1; i++) {
            points[count++] = rot.apply(firstRing[i]);
        }
        for (int i = 0; i < numPointsRing2; i++) {
            points[count++] = rot.apply(secondRing[i]);
        }
    }

    /**
     * Gets all the points.
     *
     * @return the points
     */
    public Vector3D[] getPoints() {
        return points.clone();
    }

    /**
     * Creates an iterable that will present the points coordinates.
     *
     * @return the iterable.
     */
    public Iterable<double[]> createIterable() {
        return () -> new Iterator<double[]>() {
                /** Data. */
                private final Vector3D[] points = getPoints();
                /** Number of samples. */
                private int n;

                /** {@inheritDoc} */
                @Override
                public boolean hasNext() {
                    return n < points.length;
                }

                /** {@inheritDoc} */
                @Override
                public double[] next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return points[n++].toArray();
                }

                /** {@inheritDoc} */
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
    }
}
