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
package org.apache.commons.math4.geometry.euclidean.threed;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.util.ExceptionContext;
import org.apache.commons.math4.exception.util.Localizable;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.geometry.GeometryTestUtils;
import org.apache.commons.math4.geometry.euclidean.twod.Cartesian2D;
import org.apache.commons.math4.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math4.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math4.geometry.euclidean.twod.SubLine;
import org.apache.commons.math4.geometry.partitioning.BSPTree;
import org.apache.commons.math4.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math4.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math4.geometry.partitioning.BoundaryProjection;
import org.apache.commons.math4.geometry.partitioning.Region;
import org.apache.commons.math4.geometry.partitioning.RegionDumper;
import org.apache.commons.math4.geometry.partitioning.RegionFactory;
import org.apache.commons.math4.geometry.partitioning.RegionParser;
import org.apache.commons.math4.geometry.partitioning.SubHyperplane;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Test;

public class PolyhedronsSetTest {

    private static final double TEST_TOLERANCE = 1e-10;

    @Test
    public void testWholeSpace() {
        // act
        PolyhedronsSet polySet = new PolyhedronsSet(TEST_TOLERANCE);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        GeometryTestUtils.assertPositiveInfinity(polySet.getSize());
        Assert.assertEquals(0.0, polySet.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(Cartesian3D.NaN, (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertTrue(polySet.isFull());

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE),
                new Cartesian3D(-100, -100, -100),
                new Cartesian3D(0, 0, 0),
                new Cartesian3D(100, 100, 100),
                new Cartesian3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE));
    }

    @Test
    public void testEmptyRegion() {
        // act
        PolyhedronsSet polySet = new PolyhedronsSet(new BSPTree<Euclidean3D>(Boolean.FALSE), TEST_TOLERANCE);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        Assert.assertEquals(0.0, polySet.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(0.0, polySet.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(Cartesian3D.NaN, (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertTrue(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE),
                new Cartesian3D(-100, -100, -100),
                new Cartesian3D(0, 0, 0),
                new Cartesian3D(100, 100, 100),
                new Cartesian3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE));
    }

    @Test
    public void testHalfSpace() {
        // arrange
        List<SubHyperplane<Euclidean3D>> boundaries = new ArrayList<>();
        boundaries.add(new SubPlane(new Plane(Cartesian3D.ZERO, Cartesian3D.PLUS_J, TEST_TOLERANCE),
                new PolygonsSet(TEST_TOLERANCE)));

        // act
        PolyhedronsSet polySet = new PolyhedronsSet(boundaries, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        GeometryTestUtils.assertPositiveInfinity(polySet.getSize());
        GeometryTestUtils.assertPositiveInfinity(polySet.getBoundarySize());
        GeometryTestUtils.assertVectorEquals(Cartesian3D.NaN, (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE),
                new Cartesian3D(-100, -100, -100));
        checkPoints(Region.Location.BOUNDARY, polySet, new Cartesian3D(0, 0, 0));
        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(100, 100, 100),
                new Cartesian3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE));
    }

    @Test
    public void testInvertedRegion() {
        // arrange
        List<SubHyperplane<Euclidean3D>> boundaries = createBoxBoundaries(Cartesian3D.ZERO, 1.0, TEST_TOLERANCE);
        PolyhedronsSet box = new PolyhedronsSet(boundaries, TEST_TOLERANCE);;

        // act
        PolyhedronsSet polySet = (PolyhedronsSet) new RegionFactory<Euclidean3D>().getComplement(box);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        GeometryTestUtils.assertPositiveInfinity(polySet.getSize());
        Assert.assertEquals(6, polySet.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(Cartesian3D.NaN, (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE),
                new Cartesian3D(-100, -100, -100),
                new Cartesian3D(100, 100, 100),
                new Cartesian3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE));
        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(0, 0, 0));
    }

    @Test
    public void testCreateFromBoundaries_noBoundaries_treeRepresentsWholeSpace() {
        // arrange
        List<SubHyperplane<Euclidean3D>> boundaries = new ArrayList<>();

        // act
        PolyhedronsSet polySet = new PolyhedronsSet(boundaries, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        GeometryTestUtils.assertPositiveInfinity(polySet.getSize());
        Assert.assertEquals(0.0, polySet.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(Cartesian3D.NaN, (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertTrue(polySet.isFull());
    }

    @Test
    public void testCreateFromBoundaries_unitBox() {
        // arrange
        List<SubHyperplane<Euclidean3D>> boundaries = createBoxBoundaries(Cartesian3D.ZERO, 1.0, TEST_TOLERANCE);

        // act
        PolyhedronsSet polySet = new PolyhedronsSet(boundaries, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        Assert.assertEquals(1.0, polySet.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(6.0, polySet.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(Cartesian3D.ZERO, (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(-1, 0, 0),
                new Cartesian3D(1, 0, 0),
                new Cartesian3D(0, -1, 0),
                new Cartesian3D(0, 1, 0),
                new Cartesian3D(0, 0, -1),
                new Cartesian3D(0, 0, 1),

                new Cartesian3D(1, 1, 1),
                new Cartesian3D(1, 1, -1),
                new Cartesian3D(1, -1, 1),
                new Cartesian3D(1, -1, -1),
                new Cartesian3D(-1, 1, 1),
                new Cartesian3D(-1, 1, -1),
                new Cartesian3D(-1, -1, 1),
                new Cartesian3D(-1, -1, -1));

        checkPoints(Region.Location.BOUNDARY, polySet,
                new Cartesian3D(0.5, 0, 0),
                new Cartesian3D(-0.5, 0, 0),
                new Cartesian3D(0, 0.5, 0),
                new Cartesian3D(0, -0.5, 0),
                new Cartesian3D(0, 0, 0.5),
                new Cartesian3D(0, 0, -0.5),

                new Cartesian3D(0.5, 0.5, 0.5),
                new Cartesian3D(0.5, 0.5, -0.5),
                new Cartesian3D(0.5, -0.5, 0.5),
                new Cartesian3D(0.5, -0.5, -0.5),
                new Cartesian3D(-0.5, 0.5, 0.5),
                new Cartesian3D(-0.5, 0.5, -0.5),
                new Cartesian3D(-0.5, -0.5, 0.5),
                new Cartesian3D(-0.5, -0.5, -0.5));

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(0, 0, 0),

                new Cartesian3D(0.4, 0.4, 0.4),
                new Cartesian3D(0.4, 0.4, -0.4),
                new Cartesian3D(0.4, -0.4, 0.4),
                new Cartesian3D(0.4, -0.4, -0.4),
                new Cartesian3D(-0.4, 0.4, 0.4),
                new Cartesian3D(-0.4, 0.4, -0.4),
                new Cartesian3D(-0.4, -0.4, 0.4),
                new Cartesian3D(-0.4, -0.4, -0.4));
    }

    @Test
    public void testCreateFromBoundaries_twoBoxes_disjoint() {
        // arrange
        List<SubHyperplane<Euclidean3D>> boundaries = new ArrayList<>();
        boundaries.addAll(createBoxBoundaries(Cartesian3D.ZERO, 1.0, TEST_TOLERANCE));
        boundaries.addAll(createBoxBoundaries(new Cartesian3D(2, 0, 0), 1.0, TEST_TOLERANCE));

        // act
        PolyhedronsSet polySet = new PolyhedronsSet(boundaries, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        Assert.assertEquals(2.0, polySet.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(12.0, polySet.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(1, 0, 0), (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(-1, 0, 0),
                new Cartesian3D(1, 0, 0),
                new Cartesian3D(3, 0, 0));

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(0, 0, 0),
                new Cartesian3D(2, 0, 0));
    }

    @Test
    public void testCreateFromBoundaries_twoBoxes_sharedSide() {
        // arrange
        List<SubHyperplane<Euclidean3D>> boundaries = new ArrayList<>();
        boundaries.addAll(createBoxBoundaries(new Cartesian3D(0, 0, 0), 1.0, TEST_TOLERANCE));
        boundaries.addAll(createBoxBoundaries(new Cartesian3D(1, 0, 0), 1.0, TEST_TOLERANCE));

        // act
        PolyhedronsSet polySet = new PolyhedronsSet(boundaries, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        Assert.assertEquals(2.0, polySet.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(10.0, polySet.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(0.5, 0, 0), (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(-1, 0, 0),
                new Cartesian3D(2, 0, 0));

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(0, 0, 0),
                new Cartesian3D(1, 0, 0));
    }

    @Test
    public void testCreateFromBoundaries_twoBoxes_separationLessThanTolerance() {
        // arrange
        double tolerance = 1e-6;
        List<SubHyperplane<Euclidean3D>> boundaries = new ArrayList<>();
        boundaries.addAll(createBoxBoundaries(new Cartesian3D(0, 0, 0), 1.0, tolerance));
        boundaries.addAll(createBoxBoundaries(new Cartesian3D(1 + 1e-7, 0, 0), 1.0, tolerance));

        // act
        PolyhedronsSet polySet = new PolyhedronsSet(boundaries, tolerance);

        // assert
        Assert.assertEquals(tolerance, polySet.getTolerance(), Precision.EPSILON);
        Assert.assertEquals(2.0, polySet.getSize(), tolerance);
        Assert.assertEquals(10.0, polySet.getBoundarySize(), tolerance);
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(0.5 + 5e-8, 0, 0), (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(-1, 0, 0),
                new Cartesian3D(2, 0, 0));

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(0, 0, 0),
                new Cartesian3D(1, 0, 0));
    }

    @Test
    public void testCreateFromBoundaries_twoBoxes_sharedEdge() {
        // arrange
        List<SubHyperplane<Euclidean3D>> boundaries = new ArrayList<>();
        boundaries.addAll(createBoxBoundaries(new Cartesian3D(0, 0, 0), 1.0, TEST_TOLERANCE));
        boundaries.addAll(createBoxBoundaries(new Cartesian3D(1, 1, 0), 1.0, TEST_TOLERANCE));

        // act
        PolyhedronsSet polySet = new PolyhedronsSet(boundaries, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        Assert.assertEquals(2.0, polySet.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(12.0, polySet.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(0.5, 0.5, 0), (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(-1, 0, 0),
                new Cartesian3D(1, 0, 0),
                new Cartesian3D(0, 1, 0),
                new Cartesian3D(2, 1, 0));

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(0, 0, 0),
                new Cartesian3D(1, 1, 0));
    }

    @Test
    public void testCreateFromBoundaries_twoBoxes_sharedPoint() {
        // arrange
        List<SubHyperplane<Euclidean3D>> boundaries = new ArrayList<>();
        boundaries.addAll(createBoxBoundaries(new Cartesian3D(0, 0, 0), 1.0, TEST_TOLERANCE));
        boundaries.addAll(createBoxBoundaries(new Cartesian3D(1, 1, 1), 1.0, TEST_TOLERANCE));

        // act
        PolyhedronsSet polySet = new PolyhedronsSet(boundaries, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(TEST_TOLERANCE, polySet.getTolerance(), Precision.EPSILON);
        Assert.assertEquals(2.0, polySet.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(12.0, polySet.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(0.5, 0.5, 0.5), (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(-1, 0, 0),
                new Cartesian3D(1, 0, 0),
                new Cartesian3D(0, 1, 1),
                new Cartesian3D(2, 1, 1));

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(0, 0, 0),
                new Cartesian3D(1, 1, 1));
    }

    @Test
    public void testCreateBox() {
        // act
        PolyhedronsSet tree = new PolyhedronsSet(0, 1, 0, 1, 0, 1, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(1.0, tree.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(6.0, tree.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(0.5, 0.5, 0.5), (Cartesian3D) tree.getBarycenter(), TEST_TOLERANCE);

        for (double x = -0.25; x < 1.25; x += 0.1) {
            boolean xOK = (x >= 0.0) && (x <= 1.0);
            for (double y = -0.25; y < 1.25; y += 0.1) {
                boolean yOK = (y >= 0.0) && (y <= 1.0);
                for (double z = -0.25; z < 1.25; z += 0.1) {
                    boolean zOK = (z >= 0.0) && (z <= 1.0);
                    Region.Location expected =
                        (xOK && yOK && zOK) ? Region.Location.INSIDE : Region.Location.OUTSIDE;
                    Assert.assertEquals(expected, tree.checkPoint(new Cartesian3D(x, y, z)));
                }
            }
        }
        checkPoints(Region.Location.BOUNDARY, tree, new Cartesian3D[] {
            new Cartesian3D(0.0, 0.5, 0.5),
            new Cartesian3D(1.0, 0.5, 0.5),
            new Cartesian3D(0.5, 0.0, 0.5),
            new Cartesian3D(0.5, 1.0, 0.5),
            new Cartesian3D(0.5, 0.5, 0.0),
            new Cartesian3D(0.5, 0.5, 1.0)
        });
        checkPoints(Region.Location.OUTSIDE, tree, new Cartesian3D[] {
            new Cartesian3D(0.0, 1.2, 1.2),
            new Cartesian3D(1.0, 1.2, 1.2),
            new Cartesian3D(1.2, 0.0, 1.2),
            new Cartesian3D(1.2, 1.0, 1.2),
            new Cartesian3D(1.2, 1.2, 0.0),
            new Cartesian3D(1.2, 1.2, 1.0)
        });
    }

    @Test
    public void testInvertedBox() {
        // arrange
        PolyhedronsSet tree = new PolyhedronsSet(0, 1, 0, 1, 0, 1, 1.0e-10);

        // act
        tree = (PolyhedronsSet) new RegionFactory<Euclidean3D>().getComplement(tree);

        // assert
        GeometryTestUtils.assertPositiveInfinity(tree.getSize());
        Assert.assertEquals(6.0, tree.getBoundarySize(), 1.0e-10);

        Cartesian3D barycenter = (Cartesian3D) tree.getBarycenter();
        Assert.assertTrue(Double.isNaN(barycenter.getX()));
        Assert.assertTrue(Double.isNaN(barycenter.getY()));
        Assert.assertTrue(Double.isNaN(barycenter.getZ()));

        for (double x = -0.25; x < 1.25; x += 0.1) {
            boolean xOK = (x < 0.0) || (x > 1.0);
            for (double y = -0.25; y < 1.25; y += 0.1) {
                boolean yOK = (y < 0.0) || (y > 1.0);
                for (double z = -0.25; z < 1.25; z += 0.1) {
                    boolean zOK = (z < 0.0) || (z > 1.0);
                    Region.Location expected =
                        (xOK || yOK || zOK) ? Region.Location.INSIDE : Region.Location.OUTSIDE;
                    Assert.assertEquals(expected, tree.checkPoint(new Cartesian3D(x, y, z)));
                }
            }
        }
        checkPoints(Region.Location.BOUNDARY, tree, new Cartesian3D[] {
            new Cartesian3D(0.0, 0.5, 0.5),
            new Cartesian3D(1.0, 0.5, 0.5),
            new Cartesian3D(0.5, 0.0, 0.5),
            new Cartesian3D(0.5, 1.0, 0.5),
            new Cartesian3D(0.5, 0.5, 0.0),
            new Cartesian3D(0.5, 0.5, 1.0)
        });
        checkPoints(Region.Location.INSIDE, tree, new Cartesian3D[] {
            new Cartesian3D(0.0, 1.2, 1.2),
            new Cartesian3D(1.0, 1.2, 1.2),
            new Cartesian3D(1.2, 0.0, 1.2),
            new Cartesian3D(1.2, 1.0, 1.2),
            new Cartesian3D(1.2, 1.2, 0.0),
            new Cartesian3D(1.2, 1.2, 1.0)
        });
    }

    @Test
    public void testTetrahedron() throws MathArithmeticException {
        // arrange
        Cartesian3D vertex1 = new Cartesian3D(1, 2, 3);
        Cartesian3D vertex2 = new Cartesian3D(2, 2, 4);
        Cartesian3D vertex3 = new Cartesian3D(2, 3, 3);
        Cartesian3D vertex4 = new Cartesian3D(1, 3, 4);

        // act
        PolyhedronsSet tree =
            (PolyhedronsSet) new RegionFactory<Euclidean3D>().buildConvex(
                new Plane(vertex3, vertex2, vertex1, TEST_TOLERANCE),
                new Plane(vertex2, vertex3, vertex4, TEST_TOLERANCE),
                new Plane(vertex4, vertex3, vertex1, TEST_TOLERANCE),
                new Plane(vertex1, vertex2, vertex4, TEST_TOLERANCE));

        // assert
        Assert.assertEquals(1.0 / 3.0, tree.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(2.0 * FastMath.sqrt(3.0), tree.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(1.5, 2.5, 3.5), (Cartesian3D) tree.getBarycenter(), TEST_TOLERANCE);

        double third = 1.0 / 3.0;
        checkPoints(Region.Location.BOUNDARY, tree, new Cartesian3D[] {
            vertex1, vertex2, vertex3, vertex4,
            new Cartesian3D(third, vertex1, third, vertex2, third, vertex3),
            new Cartesian3D(third, vertex2, third, vertex3, third, vertex4),
            new Cartesian3D(third, vertex3, third, vertex4, third, vertex1),
            new Cartesian3D(third, vertex4, third, vertex1, third, vertex2)
        });
        checkPoints(Region.Location.OUTSIDE, tree, new Cartesian3D[] {
            new Cartesian3D(1, 2, 4),
            new Cartesian3D(2, 2, 3),
            new Cartesian3D(2, 3, 4),
            new Cartesian3D(1, 3, 3)
        });
    }

    @Test
    public void testSphere() {
        // arrange
        // (use a high tolerance value here since the sphere is only an approximation)
        double approximationTolerance = 0.2;
        double radius = 1.0;

        // act
        PolyhedronsSet polySet = createSphere(new Cartesian3D(1, 2, 3), radius, 8, 16);

        // assert
        Assert.assertEquals(sphereVolume(radius), polySet.getSize(), approximationTolerance);
        Assert.assertEquals(sphereSurface(radius), polySet.getBoundarySize(), approximationTolerance);
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(1, 2, 3), (Cartesian3D) polySet.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(polySet.isEmpty());
        Assert.assertFalse(polySet.isFull());

        checkPoints(Region.Location.OUTSIDE, polySet,
                new Cartesian3D(-0.1, 2, 3),
                new Cartesian3D(2.1, 2, 3),
                new Cartesian3D(1, 0.9, 3),
                new Cartesian3D(1, 3.1, 3),
                new Cartesian3D(1, 2, 1.9),
                new Cartesian3D(1, 2, 4.1),
                new Cartesian3D(1.6, 2.6, 3.6));

        checkPoints(Region.Location.INSIDE, polySet,
                new Cartesian3D(1, 2, 3),
                new Cartesian3D(0.1, 2, 3),
                new Cartesian3D(1.9, 2, 3),
                new Cartesian3D(1, 2.1, 3),
                new Cartesian3D(1, 2.9, 3),
                new Cartesian3D(1, 2, 2.1),
                new Cartesian3D(1, 2, 3.9),
                new Cartesian3D(1.5, 2.5, 3.5));
    }

    @Test
    public void testIsometry() throws MathArithmeticException, MathIllegalArgumentException {
        // arrange
        Cartesian3D vertex1 = new Cartesian3D(1.1, 2.2, 3.3);
        Cartesian3D vertex2 = new Cartesian3D(2.0, 2.4, 4.2);
        Cartesian3D vertex3 = new Cartesian3D(2.8, 3.3, 3.7);
        Cartesian3D vertex4 = new Cartesian3D(1.0, 3.6, 4.5);

        // act
        PolyhedronsSet tree =
            (PolyhedronsSet) new RegionFactory<Euclidean3D>().buildConvex(
                new Plane(vertex3, vertex2, vertex1, TEST_TOLERANCE),
                new Plane(vertex2, vertex3, vertex4, TEST_TOLERANCE),
                new Plane(vertex4, vertex3, vertex1, TEST_TOLERANCE),
                new Plane(vertex1, vertex2, vertex4, TEST_TOLERANCE));

        // assert
        Cartesian3D barycenter = (Cartesian3D) tree.getBarycenter();
        Cartesian3D s = new Cartesian3D(10.2, 4.3, -6.7);
        Cartesian3D c = new Cartesian3D(-0.2, 2.1, -3.2);
        Rotation r = new Rotation(new Cartesian3D(6.2, -4.4, 2.1), 0.12, RotationConvention.VECTOR_OPERATOR);

        tree = tree.rotate(c, r).translate(s);

        Cartesian3D newB =
            new Cartesian3D(1.0, s,
                         1.0, c,
                         1.0, r.applyTo(barycenter.subtract(c)));
        Assert.assertEquals(0.0,
                            newB.subtract((Cartesian3D) tree.getBarycenter()).getNorm(),
                            TEST_TOLERANCE);

        final Cartesian3D[] expectedV = new Cartesian3D[] {
            new Cartesian3D(1.0, s,
                         1.0, c,
                         1.0, r.applyTo(vertex1.subtract(c))),
                         new Cartesian3D(1.0, s,
                                      1.0, c,
                                      1.0, r.applyTo(vertex2.subtract(c))),
                                      new Cartesian3D(1.0, s,
                                                   1.0, c,
                                                   1.0, r.applyTo(vertex3.subtract(c))),
                                                   new Cartesian3D(1.0, s,
                                                                1.0, c,
                                                                1.0, r.applyTo(vertex4.subtract(c)))
        };
        tree.getTree(true).visit(new BSPTreeVisitor<Euclidean3D>() {

            @Override
            public Order visitOrder(BSPTree<Euclidean3D> node) {
                return Order.MINUS_SUB_PLUS;
            }

            @Override
            public void visitInternalNode(BSPTree<Euclidean3D> node) {
                @SuppressWarnings("unchecked")
                BoundaryAttribute<Euclidean3D> attribute =
                    (BoundaryAttribute<Euclidean3D>) node.getAttribute();
                if (attribute.getPlusOutside() != null) {
                    checkFacet((SubPlane) attribute.getPlusOutside());
                }
                if (attribute.getPlusInside() != null) {
                    checkFacet((SubPlane) attribute.getPlusInside());
                }
            }

            @Override
            public void visitLeafNode(BSPTree<Euclidean3D> node) {
            }

            private void checkFacet(SubPlane facet) {
                Plane plane = (Plane) facet.getHyperplane();
                Cartesian2D[][] vertices =
                    ((PolygonsSet) facet.getRemainingRegion()).getVertices();
                Assert.assertEquals(1, vertices.length);
                for (int i = 0; i < vertices[0].length; ++i) {
                    Cartesian3D v = plane.toSpace(vertices[0][i]);
                    double d = Double.POSITIVE_INFINITY;
                    for (int k = 0; k < expectedV.length; ++k) {
                        d = FastMath.min(d, v.subtract(expectedV[k]).getNorm());
                    }
                    Assert.assertEquals(0, d, TEST_TOLERANCE);
                }
            }

        });

    }

    @Test
    public void testBuildBox() {
        // arrange
        double x = 1.0;
        double y = 2.0;
        double z = 3.0;
        double w = 0.1;
        double l = 1.0;

        // act
        PolyhedronsSet tree =
            new PolyhedronsSet(x - l, x + l, y - w, y + w, z - w, z + w, TEST_TOLERANCE);

        // assert
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(x, y, z), (Cartesian3D) tree.getBarycenter(), TEST_TOLERANCE);
        Assert.assertEquals(8 * l * w * w, tree.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(8 * w * (2 * l + w), tree.getBoundarySize(), TEST_TOLERANCE);
    }

    @Test
    public void testCross() {
        // arrange
        double x = 1.0;
        double y = 2.0;
        double z = 3.0;
        double w = 0.1;
        double l = 1.0;
        PolyhedronsSet xBeam =
            new PolyhedronsSet(x - l, x + l, y - w, y + w, z - w, z + w, TEST_TOLERANCE);
        PolyhedronsSet yBeam =
            new PolyhedronsSet(x - w, x + w, y - l, y + l, z - w, z + w, TEST_TOLERANCE);
        PolyhedronsSet zBeam =
            new PolyhedronsSet(x - w, x + w, y - w, y + w, z - l, z + l, TEST_TOLERANCE);
        RegionFactory<Euclidean3D> factory = new RegionFactory<>();

        // act
        PolyhedronsSet tree = (PolyhedronsSet) factory.union(xBeam, factory.union(yBeam, zBeam));

        // assert
        GeometryTestUtils.assertVectorEquals(new Cartesian3D(x, y, z), (Cartesian3D) tree.getBarycenter(), TEST_TOLERANCE);
        Assert.assertEquals(8 * w * w * (3 * l - 2 * w), tree.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(24 * w * (2 * l - w), tree.getBoundarySize(), TEST_TOLERANCE);
    }

    // Issue MATH-780
    // See https://issues.apache.org/jira/browse/MATH-780
    @Test
    public void testCreateFromBoundaries_handlesSmallBoundariesCreatedDuringConstruction() throws MathArithmeticException {
        // arrange
        float[] coords = {
            1.000000f, -1.000000f, -1.000000f,
            1.000000f, -1.000000f, 1.000000f,
            -1.000000f, -1.000000f, 1.000000f,
            -1.000000f, -1.000000f, -1.000000f,
            1.000000f, 1.000000f, -1f,
            0.999999f, 1.000000f, 1.000000f,   // 1.000000f, 1.000000f, 1.000000f,
            -1.000000f, 1.000000f, 1.000000f,
            -1.000000f, 1.000000f, -1.000000f};
        int[] indices = {
            0, 1, 2, 0, 2, 3,
            4, 7, 6, 4, 6, 5,
            0, 4, 5, 0, 5, 1,
            1, 5, 6, 1, 6, 2,
            2, 6, 7, 2, 7, 3,
            4, 0, 3, 4, 3, 7};
        ArrayList<SubHyperplane<Euclidean3D>> subHyperplaneList = new ArrayList<>();
        for (int idx = 0; idx < indices.length; idx += 3) {
            int idxA = indices[idx] * 3;
            int idxB = indices[idx + 1] * 3;
            int idxC = indices[idx + 2] * 3;
            Cartesian3D v_1 = new Cartesian3D(coords[idxA], coords[idxA + 1], coords[idxA + 2]);
            Cartesian3D v_2 = new Cartesian3D(coords[idxB], coords[idxB + 1], coords[idxB + 2]);
            Cartesian3D v_3 = new Cartesian3D(coords[idxC], coords[idxC + 1], coords[idxC + 2]);
            Cartesian3D[] vertices = {v_1, v_2, v_3};
            Plane polyPlane = new Plane(v_1, v_2, v_3, TEST_TOLERANCE);
            ArrayList<SubHyperplane<Euclidean2D>> lines = new ArrayList<>();

            Cartesian2D[] projPts = new Cartesian2D[vertices.length];
            for (int ptIdx = 0; ptIdx < projPts.length; ptIdx++) {
                projPts[ptIdx] = polyPlane.toSubSpace(vertices[ptIdx]);
            }

            SubLine lineInPlane = null;
            for (int ptIdx = 0; ptIdx < projPts.length; ptIdx++) {
                lineInPlane = new SubLine(projPts[ptIdx], projPts[(ptIdx + 1) % projPts.length], TEST_TOLERANCE);
                lines.add(lineInPlane);
            }
            Region<Euclidean2D> polyRegion = new PolygonsSet(lines, TEST_TOLERANCE);
            SubPlane polygon = new SubPlane(polyPlane, polyRegion);
            subHyperplaneList.add(polygon);
        }

        // act
        PolyhedronsSet polyhedronsSet = new PolyhedronsSet(subHyperplaneList, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(8.0, polyhedronsSet.getSize(), 3.0e-6);
        Assert.assertEquals(24.0, polyhedronsSet.getBoundarySize(), 5.0e-6);
    }

    @Test
    public void testTooThinBox() {
        // act
        PolyhedronsSet polyhedronsSet = new PolyhedronsSet(0.0, 0.0, 0.0, 1.0, 0.0, 1.0, TEST_TOLERANCE);

        // assert
        Assert.assertEquals(0.0, polyhedronsSet.getSize(), TEST_TOLERANCE);
    }

    @Test
    public void testWrongUsage() {
        // the following is a wrong usage of the constructor.
        // as explained in the javadoc, the failure is NOT detected at construction
        // time but occurs later on
        PolyhedronsSet ps = new PolyhedronsSet(new BSPTree<Euclidean3D>(), TEST_TOLERANCE);
        Assert.assertNotNull(ps);
        try {
            ps.checkPoint(Cartesian3D.ZERO);
            Assert.fail("an exception should have been thrown");
        } catch (NullPointerException npe) {
            // this is expected
        }
    }

    @Test
    public void testDumpParse() throws IOException, ParseException {
        // arrange
        double tol=1e-8;

        Cartesian3D[] verts=new Cartesian3D[8];
        double xmin=-1,xmax=1;
        double ymin=-1,ymax=1;
        double zmin=-1,zmax=1;
        verts[0]=new Cartesian3D(xmin,ymin,zmin);
        verts[1]=new Cartesian3D(xmax,ymin,zmin);
        verts[2]=new Cartesian3D(xmax,ymax,zmin);
        verts[3]=new Cartesian3D(xmin,ymax,zmin);
        verts[4]=new Cartesian3D(xmin,ymin,zmax);
        verts[5]=new Cartesian3D(xmax,ymin,zmax);
        verts[6]=new Cartesian3D(xmax,ymax,zmax);
        verts[7]=new Cartesian3D(xmin,ymax,zmax);
        //
        int[][] faces=new int[12][];
        faces[0]=new int[]{3,1,0};  // bottom (-z)
        faces[1]=new int[]{1,3,2};  // bottom (-z)
        faces[2]=new int[]{5,7,4};  // top (+z)
        faces[3]=new int[]{7,5,6};  // top (+z)
        faces[4]=new int[]{2,5,1};  // right (+x)
        faces[5]=new int[]{5,2,6};  // right (+x)
        faces[6]=new int[]{4,3,0};  // left (-x)
        faces[7]=new int[]{3,4,7};  // left (-x)
        faces[8]=new int[]{4,1,5};  // front (-y)
        faces[9]=new int[]{1,4,0};  // front (-y)
        faces[10]=new int[]{3,6,2}; // back (+y)
        faces[11]=new int[]{6,3,7}; // back (+y)

        PolyhedronsSet polyset = new PolyhedronsSet(Arrays.asList(verts), Arrays.asList(faces), tol);

        // act
        String dump = RegionDumper.dump(polyset);
        PolyhedronsSet parsed = RegionParser.parsePolyhedronsSet(dump);

        // assert
        Assert.assertEquals(8.0, polyset.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(24.0, polyset.getBoundarySize(), TEST_TOLERANCE);

        Assert.assertEquals(8.0, parsed.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(24.0, parsed.getBoundarySize(), TEST_TOLERANCE);
        Assert.assertTrue(new RegionFactory<Euclidean3D>().difference(polyset, parsed).isEmpty());
    }

    @Test
    public void testCreateFromBRep_connectedFacets() throws IOException, ParseException {
        InputStream stream = getClass().getResourceAsStream("pentomino-N.ply");
        PLYParser   parser = new PLYParser(stream);
        stream.close();
        PolyhedronsSet polyhedron = new PolyhedronsSet(parser.getVertices(), parser.getFaces(), TEST_TOLERANCE);
        Assert.assertEquals( 5.0, polyhedron.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(22.0, polyhedron.getBoundarySize(), TEST_TOLERANCE);
    }

    @Test
    public void testCreateFromBRep_verticesTooClose() throws IOException, ParseException {
        checkError("pentomino-N-too-close.ply", LocalizedFormats.CLOSE_VERTICES);
    }

    @Test
    public void testCreateFromBRep_hole() throws IOException, ParseException {
        checkError("pentomino-N-hole.ply", LocalizedFormats.EDGE_CONNECTED_TO_ONE_FACET);
    }

    @Test
    public void testCreateFromBRep_nonPlanar() throws IOException, ParseException {
        checkError("pentomino-N-out-of-plane.ply", LocalizedFormats.OUT_OF_PLANE);
    }

    @Test
    public void testCreateFromBRep_badOrientation() throws IOException, ParseException {
        checkError("pentomino-N-bad-orientation.ply", LocalizedFormats.FACET_ORIENTATION_MISMATCH);
    }

    @Test
    public void testCreateFromBRep_wrongNumberOfPoints() throws IOException, ParseException {
        checkError(Arrays.asList(Cartesian3D.ZERO, Cartesian3D.PLUS_I, Cartesian3D.PLUS_J, Cartesian3D.PLUS_K),
                   Arrays.asList(new int[] { 0, 1, 2 }, new int[] {2, 3}),
                   LocalizedFormats.WRONG_NUMBER_OF_POINTS);
    }

    private void checkError(final String resourceName, final LocalizedFormats expected) {
        try (InputStream stream = getClass().getResourceAsStream(resourceName)) {
            PLYParser   parser = new PLYParser(stream);
            checkError(parser.getVertices(), parser.getFaces(), expected);
        } catch (IOException ioe) {
            Assert.fail(ioe.getLocalizedMessage());
        } catch (ParseException pe) {
            Assert.fail(pe.getLocalizedMessage());
        }
    }

    private void checkError(final List<Cartesian3D> vertices, final List<int[]> facets,
                            final LocalizedFormats expected) {
        try {
            new PolyhedronsSet(vertices, facets, TEST_TOLERANCE);
            Assert.fail("an exception should have been thrown");
        } catch (MathIllegalArgumentException miae) {
            try {
                Field msgPatterns = ExceptionContext.class.getDeclaredField("msgPatterns");
                msgPatterns.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<Localizable> list = (List<Localizable>) msgPatterns.get(miae.getContext());
                Assert.assertEquals(expected, list.get(0));
            } catch (NoSuchFieldException nsfe) {
                Assert.fail(nsfe.getLocalizedMessage());
            } catch (IllegalAccessException iae) {
                Assert.fail(iae.getLocalizedMessage());
            }
        }
    }

    @Test
    public void testFirstIntersection() {
        // arrange
        List<SubHyperplane<Euclidean3D>> boundaries = createBoxBoundaries(Cartesian3D.ZERO, 2.0, TEST_TOLERANCE);
        PolyhedronsSet polySet = new PolyhedronsSet(boundaries, TEST_TOLERANCE);

        Line xPlus = new Line(Cartesian3D.ZERO, Cartesian3D.PLUS_I, TEST_TOLERANCE);
        Line xMinus = new Line(Cartesian3D.ZERO, Cartesian3D.MINUS_I, TEST_TOLERANCE);

        Line yPlus = new Line(Cartesian3D.ZERO, Cartesian3D.PLUS_J, TEST_TOLERANCE);
        Line yMinus = new Line(Cartesian3D.ZERO, Cartesian3D.MINUS_J, TEST_TOLERANCE);

        Line zPlus = new Line(Cartesian3D.ZERO, Cartesian3D.PLUS_K, TEST_TOLERANCE);
        Line zMinus = new Line(Cartesian3D.ZERO, Cartesian3D.MINUS_K, TEST_TOLERANCE);

        // act/assert
        assertSubPlaneNormal(new Cartesian3D(-1, 0, 0), polySet.firstIntersection(new Cartesian3D(-1.1, 0, 0), xPlus));
        assertSubPlaneNormal(new Cartesian3D(-1, 0, 0), polySet.firstIntersection(new Cartesian3D(-1, 0, 0), xPlus));
        assertSubPlaneNormal(new Cartesian3D(1, 0, 0), polySet.firstIntersection(new Cartesian3D(-0.9, 0, 0), xPlus));
        Assert.assertEquals(null, polySet.firstIntersection(new Cartesian3D(1.1, 0, 0), xPlus));

        assertSubPlaneNormal(new Cartesian3D(1, 0, 0), polySet.firstIntersection(new Cartesian3D(1.1, 0, 0), xMinus));
        assertSubPlaneNormal(new Cartesian3D(1, 0, 0), polySet.firstIntersection(new Cartesian3D(1, 0, 0), xMinus));
        assertSubPlaneNormal(new Cartesian3D(-1, 0, 0), polySet.firstIntersection(new Cartesian3D(0.9, 0, 0), xMinus));
        Assert.assertEquals(null, polySet.firstIntersection(new Cartesian3D(-1.1, 0, 0), xMinus));

        assertSubPlaneNormal(new Cartesian3D(0, -1, 0), polySet.firstIntersection(new Cartesian3D(0, -1.1, 0), yPlus));
        assertSubPlaneNormal(new Cartesian3D(0, -1, 0), polySet.firstIntersection(new Cartesian3D(0, -1, 0), yPlus));
        assertSubPlaneNormal(new Cartesian3D(0, 1, 0), polySet.firstIntersection(new Cartesian3D(0, -0.9, 0), yPlus));
        Assert.assertEquals(null, polySet.firstIntersection(new Cartesian3D(0, 1.1, 0), yPlus));

        assertSubPlaneNormal(new Cartesian3D(0, 1, 0), polySet.firstIntersection(new Cartesian3D(0, 1.1, 0), yMinus));
        assertSubPlaneNormal(new Cartesian3D(0, 1, 0), polySet.firstIntersection(new Cartesian3D(0, 1, 0), yMinus));
        assertSubPlaneNormal(new Cartesian3D(0, -1, 0), polySet.firstIntersection(new Cartesian3D(0, 0.9, 0), yMinus));
        Assert.assertEquals(null, polySet.firstIntersection(new Cartesian3D(0, -1.1, 0), yMinus));

        assertSubPlaneNormal(new Cartesian3D(0, 0, -1), polySet.firstIntersection(new Cartesian3D(0, 0, -1.1), zPlus));
        assertSubPlaneNormal(new Cartesian3D(0, 0, -1), polySet.firstIntersection(new Cartesian3D(0, 0, -1), zPlus));
        assertSubPlaneNormal(new Cartesian3D(0, 0, 1), polySet.firstIntersection(new Cartesian3D(0, 0, -0.9), zPlus));
        Assert.assertEquals(null, polySet.firstIntersection(new Cartesian3D(0, 0, 1.1), zPlus));

        assertSubPlaneNormal(new Cartesian3D(0, 0, 1), polySet.firstIntersection(new Cartesian3D(0, 0, 1.1), zMinus));
        assertSubPlaneNormal(new Cartesian3D(0, 0, 1), polySet.firstIntersection(new Cartesian3D(0, 0, 1), zMinus));
        assertSubPlaneNormal(new Cartesian3D(0, 0, -1), polySet.firstIntersection(new Cartesian3D(0, 0, 0.9), zMinus));
        Assert.assertEquals(null, polySet.firstIntersection(new Cartesian3D(0, 0, -1.1), zMinus));
    }

    // Issue 1211
    // See https://issues.apache.org/jira/browse/MATH-1211
    @Test
    public void testFirstIntersection_onlyReturnsPointsInDirectionOfRay() throws IOException, ParseException {
        // arrange
        PolyhedronsSet polyset = RegionParser.parsePolyhedronsSet(loadTestData("issue-1211.bsp"));
        UniformRandomProvider random = RandomSource.create(RandomSource.WELL_1024_A, 0xb97c9d1ade21e40al);

        // act/assert
        int nrays = 1000;
        for (int i = 0; i < nrays; i++) {
            Cartesian3D origin    = Cartesian3D.ZERO;
            Cartesian3D direction = new Cartesian3D(2 * random.nextDouble() - 1,
                                              2 * random.nextDouble() - 1,
                                              2 * random.nextDouble() - 1).normalize();
            Line line = new Line(origin, origin.add(direction), polyset.getTolerance());
            SubHyperplane<Euclidean3D> plane = polyset.firstIntersection(origin, line);
            if (plane != null) {
                Cartesian3D intersectionPoint = ((Plane)plane.getHyperplane()).intersection(line);
                double dotProduct = direction.dotProduct(intersectionPoint.subtract(origin));
                Assert.assertTrue(dotProduct > 0);
            }
        }
    }

    @Test
    public void testBoolean_union() throws IOException {
        // arrange
        double tolerance = 0.05;
        double size = 1.0;
        double radius = size * 0.5;
        PolyhedronsSet box = new PolyhedronsSet(0, size, 0, size, 0, size, TEST_TOLERANCE);
        PolyhedronsSet sphere = createSphere(new Cartesian3D(size * 0.5, size * 0.5, size), radius, 8, 16);

        // act
        PolyhedronsSet result = (PolyhedronsSet) new RegionFactory<Euclidean3D>().union(box, sphere);

        // OBJWriter.write("union.obj", result);

        // assert
        Assert.assertEquals(cubeVolume(size) + (sphereVolume(radius) * 0.5),
                result.getSize(), tolerance);
        Assert.assertEquals(cubeSurface(size) - circleSurface(radius) + (0.5 * sphereSurface(radius)),
                result.getBoundarySize(), tolerance);
        Assert.assertFalse(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-0.1, 0.5, 0.5),
                new Cartesian3D(1.1, 0.5, 0.5),
                new Cartesian3D(0.5, -0.1, 0.5),
                new Cartesian3D(0.5, 1.1, 0.5),
                new Cartesian3D(0.5, 0.5, -0.1),
                new Cartesian3D(0.5, 0.5, 1.6));

        checkPoints(Region.Location.INSIDE, result,
                new Cartesian3D(0.1, 0.5, 0.5),
                new Cartesian3D(0.9, 0.5, 0.5),
                new Cartesian3D(0.5, 0.1, 0.5),
                new Cartesian3D(0.5, 0.9, 0.5),
                new Cartesian3D(0.5, 0.5, 0.1),
                new Cartesian3D(0.5, 0.5, 1.4));
    }

    @Test
    public void testUnion_self() {
        // arrange
        double tolerance = 0.2;
        double radius = 1.0;

        PolyhedronsSet sphere = createSphere(Cartesian3D.ZERO, radius, 8, 16);

        // act
        PolyhedronsSet result = (PolyhedronsSet) new RegionFactory<Euclidean3D>().union(sphere, sphere.copySelf());

        // assert
        Assert.assertEquals(sphereVolume(radius), result.getSize(), tolerance);
        Assert.assertEquals(sphereSurface(radius), result.getBoundarySize(), tolerance);
        GeometryTestUtils.assertVectorEquals(Cartesian3D.ZERO, (Cartesian3D) result.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-1.1, 0, 0),
                new Cartesian3D(1.1, 0, 0),
                new Cartesian3D(0, -1.1, 0),
                new Cartesian3D(0, 1.1, 0),
                new Cartesian3D(0, 0, -1.1),
                new Cartesian3D(0, 0, 1.1));

        checkPoints(Region.Location.INSIDE, result,
                new Cartesian3D(-0.9, 0, 0),
                new Cartesian3D(0.9, 0, 0),
                new Cartesian3D(0, -0.9, 0),
                new Cartesian3D(0, 0.9, 0),
                new Cartesian3D(0, 0, -0.9),
                new Cartesian3D(0, 0, 0.9),
                Cartesian3D.ZERO);
    }

    @Test
    public void testBoolean_intersection() throws IOException {
        // arrange
        double tolerance = 0.05;
        double size = 1.0;
        double radius = size * 0.5;
        PolyhedronsSet box = new PolyhedronsSet(0, size, 0, size, 0, size, TEST_TOLERANCE);
        PolyhedronsSet sphere = createSphere(new Cartesian3D(size * 0.5, size * 0.5, size), radius, 8, 16);

        // act
        PolyhedronsSet result = (PolyhedronsSet) new RegionFactory<Euclidean3D>().intersection(box, sphere);

        // OBJWriter.write("intersection.obj", result);

        // assert
        Assert.assertEquals((sphereVolume(radius) * 0.5), result.getSize(), tolerance);
        Assert.assertEquals(circleSurface(radius) + (0.5 * sphereSurface(radius)),
                result.getBoundarySize(), tolerance);
        Assert.assertFalse(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-0.1, 0.5, 1.0),
                new Cartesian3D(1.1, 0.5, 1.0),
                new Cartesian3D(0.5, -0.1, 1.0),
                new Cartesian3D(0.5, 1.1, 1.0),
                new Cartesian3D(0.5, 0.5, 0.4),
                new Cartesian3D(0.5, 0.5, 1.1));

        checkPoints(Region.Location.INSIDE, result,
                new Cartesian3D(0.1, 0.5, 0.9),
                new Cartesian3D(0.9, 0.5, 0.9),
                new Cartesian3D(0.5, 0.1, 0.9),
                new Cartesian3D(0.5, 0.9, 0.9),
                new Cartesian3D(0.5, 0.5, 0.6),
                new Cartesian3D(0.5, 0.5, 0.9));
    }

    @Test
    public void testIntersection_self() {
        // arrange
        double tolerance = 0.2;
        double radius = 1.0;

        PolyhedronsSet sphere = createSphere(Cartesian3D.ZERO, radius, 8, 16);

        // act
        PolyhedronsSet result = (PolyhedronsSet) new RegionFactory<Euclidean3D>().intersection(sphere, sphere.copySelf());

        // assert
        Assert.assertEquals(sphereVolume(radius), result.getSize(), tolerance);
        Assert.assertEquals(sphereSurface(radius), result.getBoundarySize(), tolerance);
        GeometryTestUtils.assertVectorEquals(Cartesian3D.ZERO, (Cartesian3D) result.getBarycenter(), TEST_TOLERANCE);
        Assert.assertFalse(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-1.1, 0, 0),
                new Cartesian3D(1.1, 0, 0),
                new Cartesian3D(0, -1.1, 0),
                new Cartesian3D(0, 1.1, 0),
                new Cartesian3D(0, 0, -1.1),
                new Cartesian3D(0, 0, 1.1));

        checkPoints(Region.Location.INSIDE, result,
                new Cartesian3D(-0.9, 0, 0),
                new Cartesian3D(0.9, 0, 0),
                new Cartesian3D(0, -0.9, 0),
                new Cartesian3D(0, 0.9, 0),
                new Cartesian3D(0, 0, -0.9),
                new Cartesian3D(0, 0, 0.9),
                Cartesian3D.ZERO);
    }

    @Test
    public void testBoolean_xor_twoCubes() throws IOException {
        // arrange
        double size = 1.0;
        PolyhedronsSet box1 = new PolyhedronsSet(
                0, size,
                0, size,
                0, size, TEST_TOLERANCE);
        PolyhedronsSet box2 = new PolyhedronsSet(
                0.5, size + 0.5,
                0.5, size + 0.5,
                0.5, size + 0.5, TEST_TOLERANCE);

        // act
        PolyhedronsSet result = (PolyhedronsSet) new RegionFactory<Euclidean3D>().xor(box1, box2);

        // OBJWriter.write("xor_twoCubes.obj", result);

        Assert.assertEquals((2 * cubeVolume(size)) - (2 * cubeVolume(size * 0.5)), result.getSize(), TEST_TOLERANCE);

        // assert
        Assert.assertEquals(2 * cubeSurface(size), result.getBoundarySize(), TEST_TOLERANCE);
        Assert.assertFalse(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-0.1, -0.1, -0.1),
                new Cartesian3D(0.75, 0.75, 0.75),
                new Cartesian3D(1.6, 1.6, 1.6));

        checkPoints(Region.Location.BOUNDARY, result,
                new Cartesian3D(0, 0, 0),
                new Cartesian3D(0.5, 0.5, 0.5),
                new Cartesian3D(1, 1, 1),
                new Cartesian3D(1.5, 1.5, 1.5));

        checkPoints(Region.Location.INSIDE, result,
                new Cartesian3D(0.1, 0.1, 0.1),
                new Cartesian3D(0.4, 0.4, 0.4),
                new Cartesian3D(1.1, 1.1, 1.1),
                new Cartesian3D(1.4, 1.4, 1.4));
    }

    @Test
    public void testBoolean_xor_cubeAndSphere() throws IOException {
        // arrange
        double tolerance = 0.05;
        double size = 1.0;
        double radius = size * 0.5;
        PolyhedronsSet box = new PolyhedronsSet(0, size, 0, size, 0, size, TEST_TOLERANCE);
        PolyhedronsSet sphere = createSphere(new Cartesian3D(size * 0.5, size * 0.5, size), radius, 8, 16);

        // act
        PolyhedronsSet result = (PolyhedronsSet) new RegionFactory<Euclidean3D>().xor(box, sphere);

        // OBJWriter.write("xor_cubeAndSphere.obj", result);

        Assert.assertEquals(cubeVolume(size), result.getSize(), tolerance);

        // assert
        Assert.assertEquals(cubeSurface(size) + (sphereSurface(radius)),
                result.getBoundarySize(), tolerance);
        Assert.assertFalse(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-0.1, 0.5, 0.5),
                new Cartesian3D(1.1, 0.5, 0.5),
                new Cartesian3D(0.5, -0.1, 0.5),
                new Cartesian3D(0.5, 1.1, 0.5),
                new Cartesian3D(0.5, 0.5, -0.1),
                new Cartesian3D(0.5, 0.5, 1.6),
                new Cartesian3D(0.5, 0.5, 0.9));

        checkPoints(Region.Location.INSIDE, result,
                new Cartesian3D(0.1, 0.5, 0.5),
                new Cartesian3D(0.9, 0.5, 0.5),
                new Cartesian3D(0.5, 0.1, 0.5),
                new Cartesian3D(0.5, 0.9, 0.5),
                new Cartesian3D(0.5, 0.5, 0.1),
                new Cartesian3D(0.5, 0.5, 1.4));
    }

    @Test
    public void testXor_self() {
        // arrange
        double radius = 1.0;

        PolyhedronsSet sphere = createSphere(Cartesian3D.ZERO, radius, 8, 16);

        // act
        PolyhedronsSet result = (PolyhedronsSet) new RegionFactory<Euclidean3D>().xor(sphere, sphere.copySelf());

        // assert
        Assert.assertEquals(0.0, result.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(0.0, result.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(Cartesian3D.NaN, (Cartesian3D) result.getBarycenter(), TEST_TOLERANCE);
        Assert.assertTrue(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-1.1, 0, 0),
                new Cartesian3D(1.1, 0, 0),
                new Cartesian3D(0, -1.1, 0),
                new Cartesian3D(0, 1.1, 0),
                new Cartesian3D(0, 0, -1.1),
                new Cartesian3D(0, 0, 1.1),
                new Cartesian3D(-0.9, 0, 0),
                new Cartesian3D(0.9, 0, 0),
                new Cartesian3D(0, -0.9, 0),
                new Cartesian3D(0, 0.9, 0),
                new Cartesian3D(0, 0, -0.9),
                new Cartesian3D(0, 0, 0.9),
                Cartesian3D.ZERO);
    }

    @Test
    public void testBoolean_difference() throws IOException {
        // arrange
        double tolerance = 0.05;
        double size = 1.0;
        double radius = size * 0.5;
        PolyhedronsSet box = new PolyhedronsSet(0, size, 0, size, 0, size, TEST_TOLERANCE);
        PolyhedronsSet sphere = createSphere(new Cartesian3D(size * 0.5, size * 0.5, size), radius, 8, 16);

        // act
        PolyhedronsSet result = (PolyhedronsSet) new RegionFactory<Euclidean3D>().difference(box, sphere);

        // OBJWriter.write("difference.obj", result);

        // assert
        Assert.assertEquals(cubeVolume(size) - (sphereVolume(radius) * 0.5), result.getSize(), tolerance);
        Assert.assertEquals(cubeSurface(size) - circleSurface(radius) + (0.5 * sphereSurface(radius)),
                result.getBoundarySize(), tolerance);
        Assert.assertFalse(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-0.1, 0.5, 1.0),
                new Cartesian3D(1.1, 0.5, 1.0),
                new Cartesian3D(0.5, -0.1, 1.0),
                new Cartesian3D(0.5, 1.1, 1.0),
                new Cartesian3D(0.5, 0.5, -0.1),
                new Cartesian3D(0.5, 0.5, 0.6));

        checkPoints(Region.Location.INSIDE, result,
                new Cartesian3D(0.1, 0.5, 0.4),
                new Cartesian3D(0.9, 0.5, 0.4),
                new Cartesian3D(0.5, 0.1, 0.4),
                new Cartesian3D(0.5, 0.9, 0.4),
                new Cartesian3D(0.5, 0.5, 0.1),
                new Cartesian3D(0.5, 0.5, 0.4));
    }

    @Test
    public void testDifference_self() {
        // arrange
        double radius = 1.0;

        PolyhedronsSet sphere = createSphere(Cartesian3D.ZERO, radius, 8, 16);

        // act
        PolyhedronsSet result = (PolyhedronsSet) new RegionFactory<Euclidean3D>().difference(sphere, sphere.copySelf());

        // assert
        Assert.assertEquals(0.0, result.getSize(), TEST_TOLERANCE);
        Assert.assertEquals(0.0, result.getBoundarySize(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(Cartesian3D.NaN, (Cartesian3D) result.getBarycenter(), TEST_TOLERANCE);
        Assert.assertTrue(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-1.1, 0, 0),
                new Cartesian3D(1.1, 0, 0),
                new Cartesian3D(0, -1.1, 0),
                new Cartesian3D(0, 1.1, 0),
                new Cartesian3D(0, 0, -1.1),
                new Cartesian3D(0, 0, 1.1),
                new Cartesian3D(-0.9, 0, 0),
                new Cartesian3D(0.9, 0, 0),
                new Cartesian3D(0, -0.9, 0),
                new Cartesian3D(0, 0.9, 0),
                new Cartesian3D(0, 0, -0.9),
                new Cartesian3D(0, 0, 0.9),
                Cartesian3D.ZERO);
    }

    @Test
    public void testBoolean_multiple() throws IOException {
        // arrange
        double tolerance = 0.05;
        double size = 1.0;
        double radius = size * 0.5;
        PolyhedronsSet box = new PolyhedronsSet(0, size, 0, size, 0, size, TEST_TOLERANCE);
        PolyhedronsSet sphereToAdd = createSphere(new Cartesian3D(size * 0.5, size * 0.5, size), radius, 8, 16);
        PolyhedronsSet sphereToRemove1 = createSphere(new Cartesian3D(size * 0.5, 0, size * 0.5), radius, 8, 16);
        PolyhedronsSet sphereToRemove2 = createSphere(new Cartesian3D(size * 0.5, 1, size * 0.5), radius, 8, 16);

        RegionFactory<Euclidean3D> factory = new RegionFactory<Euclidean3D>();

        // act
        PolyhedronsSet result = (PolyhedronsSet) factory.union(box, sphereToAdd);
        result = (PolyhedronsSet) factory.difference(result, sphereToRemove1);
        result = (PolyhedronsSet) factory.difference(result, sphereToRemove2);

        // OBJWriter.write("multiple.obj", result);

        // assert
        Assert.assertEquals(cubeVolume(size) - (sphereVolume(radius) * 0.5),
                result.getSize(), tolerance);
        Assert.assertEquals(cubeSurface(size) - (3.0 * circleSurface(radius)) + (1.5 * sphereSurface(radius)),
                result.getBoundarySize(), tolerance);
        Assert.assertFalse(result.isEmpty());
        Assert.assertFalse(result.isFull());

        checkPoints(Region.Location.OUTSIDE, result,
                new Cartesian3D(-0.1, 0.5, 0.5),
                new Cartesian3D(1.1, 0.5, 0.5),
                new Cartesian3D(0.5, 0.4, 0.5),
                new Cartesian3D(0.5, 0.6, 0.5),
                new Cartesian3D(0.5, 0.5, -0.1),
                new Cartesian3D(0.5, 0.5, 1.6));

        checkPoints(Region.Location.INSIDE, result,
                new Cartesian3D(0.1, 0.5, 0.1),
                new Cartesian3D(0.9, 0.5, 0.1),
                new Cartesian3D(0.5, 0.4, 0.1),
                new Cartesian3D(0.5, 0.6, 0.1),
                new Cartesian3D(0.5, 0.5, 0.1),
                new Cartesian3D(0.5, 0.5, 1.4));
    }

    @Test
    public void testProjectToBoundary() {
        // arrange
        PolyhedronsSet polySet = new PolyhedronsSet(0, 1, 0, 1, 0, 1, TEST_TOLERANCE);

        // act/assert
        checkProjectToBoundary(polySet, new Cartesian3D(0.4, 0.5, 0.5),
                new Cartesian3D(0, 0.5, 0.5), -0.4);
        checkProjectToBoundary(polySet, new Cartesian3D(1.5, 0.5, 0.5),
                new Cartesian3D(1, 0.5, 0.5), 0.5);
        checkProjectToBoundary(polySet, new Cartesian3D(2, 2, 2),
                new Cartesian3D(1, 1, 1), FastMath.sqrt(3));
    }

    @Test
    public void testProjectToBoundary_invertedRegion() {
        // arrange
        PolyhedronsSet polySet = new PolyhedronsSet(0, 1, 0, 1, 0, 1, TEST_TOLERANCE);
        polySet = (PolyhedronsSet) new RegionFactory<Euclidean3D>().getComplement(polySet);

        // act/assert
        checkProjectToBoundary(polySet, new Cartesian3D(0.4, 0.5, 0.5),
                new Cartesian3D(0, 0.5, 0.5), 0.4);
        checkProjectToBoundary(polySet, new Cartesian3D(1.5, 0.5, 0.5),
                new Cartesian3D(1, 0.5, 0.5), -0.5);
        checkProjectToBoundary(polySet, new Cartesian3D(2, 2, 2),
                new Cartesian3D(1, 1, 1), -FastMath.sqrt(3));
    }

    private void checkProjectToBoundary(PolyhedronsSet poly, Cartesian3D toProject,
            Cartesian3D expectedPoint, double expectedOffset) {
        BoundaryProjection<Euclidean3D> proj = poly.projectToBoundary(toProject);

        GeometryTestUtils.assertVectorEquals(toProject, (Cartesian3D) proj.getOriginal(), TEST_TOLERANCE);
        GeometryTestUtils.assertVectorEquals(expectedPoint, (Cartesian3D) proj.getProjected(), TEST_TOLERANCE);
        Assert.assertEquals(expectedOffset, proj.getOffset(), TEST_TOLERANCE);
    }

    private String loadTestData(final String resourceName)
            throws IOException {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(resourceName), "UTF-8")) {
            StringBuilder builder = new StringBuilder();
            for (int c = reader.read(); c >= 0; c = reader.read()) {
                builder.append((char) c);
            }
            return builder.toString();
        }
    }

    private void checkPoints(Region.Location expected, PolyhedronsSet poly, Cartesian3D ... points) {
        for (int i = 0; i < points.length; ++i) {
            Assert.assertEquals("Incorrect location for " + points[i], expected, poly.checkPoint(points[i]));
        }
    }

    private List<SubHyperplane<Euclidean3D>> createBoxBoundaries(Cartesian3D center, double size, double tolerance) {
        List<SubHyperplane<Euclidean3D>> boundaries = new ArrayList<>();

        double offset = size * 0.5;

        Plane xMinus = new Plane(center.add(new Cartesian3D(-offset, 0, 0)), Cartesian3D.MINUS_I, tolerance);
        Plane xPlus = new Plane(center.add(new Cartesian3D(offset, 0, 0)), Cartesian3D.PLUS_I, tolerance);
        Plane yPlus = new Plane(center.add(new Cartesian3D(0, offset, 0)), Cartesian3D.PLUS_J, tolerance);
        Plane yMinus = new Plane(center.add(new Cartesian3D(0, -offset, 0)), Cartesian3D.MINUS_J, tolerance);
        Plane zPlus = new Plane(center.add(new Cartesian3D(0, 0, offset)), Cartesian3D.PLUS_K, tolerance);
        Plane zMinus = new Plane(center.add(new Cartesian3D(0, 0, -offset)), Cartesian3D.MINUS_K, tolerance);

        // +x
        boundaries.add(createSubPlane(xPlus,
                        center.add(new Cartesian3D(offset, offset, offset)),
                        center.add(new Cartesian3D(offset, -offset, offset)),
                        center.add(new Cartesian3D(offset, -offset, -offset)),
                        center.add(new Cartesian3D(offset, offset, -offset))));

        // -x
        boundaries.add(createSubPlane(xMinus,
                        center.add(new Cartesian3D(-offset, -offset, offset)),
                        center.add(new Cartesian3D(-offset, offset, offset)),
                        center.add(new Cartesian3D(-offset, offset, -offset)),
                        center.add(new Cartesian3D(-offset, -offset, -offset))));

        // +y
        boundaries.add(createSubPlane(yPlus,
                        center.add(new Cartesian3D(-offset, offset, offset)),
                        center.add(new Cartesian3D(offset, offset, offset)),
                        center.add(new Cartesian3D(offset, offset, -offset)),
                        center.add(new Cartesian3D(-offset, offset, -offset))));

        // -y
        boundaries.add(createSubPlane(yMinus,
                        center.add(new Cartesian3D(-offset, -offset, offset)),
                        center.add(new Cartesian3D(-offset, -offset, -offset)),
                        center.add(new Cartesian3D(offset, -offset, -offset)),
                        center.add(new Cartesian3D(offset, -offset, offset))));

        // +z
        boundaries.add(createSubPlane(zPlus,
                        center.add(new Cartesian3D(-offset, -offset, offset)),
                        center.add(new Cartesian3D(offset, -offset, offset)),
                        center.add(new Cartesian3D(offset, offset, offset)),
                        center.add(new Cartesian3D(-offset, offset, offset))));

        // -z
        boundaries.add(createSubPlane(zMinus,
                        center.add(new Cartesian3D(-offset, -offset, -offset)),
                        center.add(new Cartesian3D(-offset, offset, -offset)),
                        center.add(new Cartesian3D(offset, offset, -offset)),
                        center.add(new Cartesian3D(offset, -offset, -offset))));

        return boundaries;
    }

    private SubPlane createSubPlane(Plane plane, Cartesian3D...points) {
        Cartesian2D[] points2d = new Cartesian2D[points.length];
        for (int i=0; i<points.length; ++i) {
            points2d[i] = plane.toSubSpace(points[i]);
        }

        PolygonsSet polygon = new PolygonsSet(plane.getTolerance(), points2d);

        return new SubPlane(plane, polygon);
    }

    private PolyhedronsSet createSphere(Cartesian3D center, double radius, int stacks, int slices) {
        List<Plane> planes = new ArrayList<>();

        // add top and bottom planes (+/- z)
        Cartesian3D topZ = new Cartesian3D(center.getX(), center.getY(), center.getZ() + radius);
        Cartesian3D bottomZ = new Cartesian3D(center.getX(), center.getY(), center.getZ() - radius);

        planes.add(new Plane(topZ, Cartesian3D.PLUS_K, TEST_TOLERANCE));
        planes.add(new Plane(bottomZ, Cartesian3D.MINUS_K, TEST_TOLERANCE));

        // add the side planes
        double vDelta = FastMath.PI / stacks;
        double hDelta = FastMath.PI * 2 / slices;

        double adjustedRadius = (radius + (radius * FastMath.cos(vDelta * 0.5))) / 2.0;

        double vAngle;
        double hAngle;
        double stackRadius;
        double stackHeight;
        double x, y;
        Cartesian3D norm, pt;

        vAngle = -0.5 * vDelta;
        for (int v=0; v<stacks; ++v) {
            vAngle += vDelta;

            stackRadius = FastMath.sin(vAngle) * adjustedRadius;
            stackHeight = FastMath.cos(vAngle) * adjustedRadius;

            hAngle = -0.5 * hDelta;
            for (int h=0; h<slices; ++h) {
                hAngle += hDelta;

                x = FastMath.cos(hAngle) * stackRadius;
                y = FastMath.sin(hAngle) * stackRadius;

                norm = new Cartesian3D(x, y, stackHeight).normalize();
                pt = norm.scalarMultiply(adjustedRadius).add(center);

                planes.add(new Plane(pt, norm, TEST_TOLERANCE));
            }
        }

        return (PolyhedronsSet) new RegionFactory<Euclidean3D>().buildConvex(planes.toArray(new Plane[0]));
    }

    private void assertSubPlaneNormal(Cartesian3D expectedNormal, SubHyperplane<Euclidean3D> sub) {
        Cartesian3D norm = ((Plane) sub.getHyperplane()).getNormal();
        GeometryTestUtils.assertVectorEquals(expectedNormal, norm, TEST_TOLERANCE);
    }

    private double cubeVolume(double size) {
        return size * size * size;
    }

    private double cubeSurface(double size) {
        return 6.0 * size * size;
    }

    private double sphereVolume(double radius) {
        return 4.0 * FastMath.PI * radius * radius * radius / 3.0;
    }

    private double sphereSurface(double radius) {
        return 4.0 * FastMath.PI * radius * radius;
    }

    private double circleSurface(double radius) {
        return FastMath.PI * radius * radius;
    }
}
