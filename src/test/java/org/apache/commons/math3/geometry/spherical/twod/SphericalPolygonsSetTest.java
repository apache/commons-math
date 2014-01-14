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
package org.apache.commons.math3.geometry.spherical.twod;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.Region.Location;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.random.Well1024a;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;

public class SphericalPolygonsSetTest {

    @Test
    public void testFullSphere() {
        SphericalPolygonsSet full = new SphericalPolygonsSet(1.0e-10);
        UnitSphereRandomVectorGenerator random =
                new UnitSphereRandomVectorGenerator(3, new Well1024a(0x852fd2a0ed8d2f6dl));
        for (int i = 0; i < 1000; ++i) {
            Vector3D v = new Vector3D(random.nextVector());
            Assert.assertEquals(Location.INSIDE, full.checkPoint(new S2Point(v)));
        }
        Assert.assertEquals(4 * FastMath.PI, new SphericalPolygonsSet(0.01, new S2Point[0]).getSize(), 1.0e-10);
        Assert.assertEquals(0, new SphericalPolygonsSet(0.01, new S2Point[0]).getBoundarySize(), 1.0e-10);
    }

    @Test
    public void testSouthHemisphere() {
        double tol = 0.01;
        double sinTol = FastMath.sin(tol);
        SphericalPolygonsSet south = new SphericalPolygonsSet(Vector3D.MINUS_K, tol);
        UnitSphereRandomVectorGenerator random =
                new UnitSphereRandomVectorGenerator(3, new Well1024a(0x6b9d4a6ad90d7b0bl));
        for (int i = 0; i < 1000; ++i) {
            Vector3D v = new Vector3D(random.nextVector());
            if (v.getZ() < -sinTol) {
                Assert.assertEquals(Location.INSIDE, south.checkPoint(new S2Point(v)));
            } else if (v.getZ() > sinTol) {
                Assert.assertEquals(Location.OUTSIDE, south.checkPoint(new S2Point(v)));
            } else {
                Assert.assertEquals(Location.BOUNDARY, south.checkPoint(new S2Point(v)));
            }
        }
    }

    @Test
    public void testPositiveOctantByIntersection() {
        double tol = 0.01;
        double sinTol = FastMath.sin(tol);
        RegionFactory<Sphere2D> factory = new RegionFactory<Sphere2D>();
        SphericalPolygonsSet plusX = new SphericalPolygonsSet(Vector3D.PLUS_I, tol);
        SphericalPolygonsSet plusY = new SphericalPolygonsSet(Vector3D.PLUS_J, tol);
        SphericalPolygonsSet plusZ = new SphericalPolygonsSet(Vector3D.PLUS_K, tol);
        SphericalPolygonsSet octant =
                (SphericalPolygonsSet) factory.intersection(factory.intersection(plusX, plusY), plusZ);
        UnitSphereRandomVectorGenerator random =
                new UnitSphereRandomVectorGenerator(3, new Well1024a(0x9c9802fde3cbcf25l));
        for (int i = 0; i < 1000; ++i) {
            Vector3D v = new Vector3D(random.nextVector());
            if ((v.getX() > sinTol) && (v.getY() > sinTol) && (v.getZ() > sinTol)) {
                Assert.assertEquals(Location.INSIDE, octant.checkPoint(new S2Point(v)));
            } else if ((v.getX() < -sinTol) || (v.getY() < -sinTol) || (v.getZ() < -sinTol)) {
                Assert.assertEquals(Location.OUTSIDE, octant.checkPoint(new S2Point(v)));
            } else {
                Assert.assertEquals(Location.BOUNDARY, octant.checkPoint(new S2Point(v)));
            }
        }

        List<SphericalPolygonsSet.Vertex> loops = octant.getBoundaryLoops();
        Assert.assertEquals(1, loops.size());
        boolean xPFound = false;
        boolean yPFound = false;
        boolean zPFound = false;
        boolean xVFound = false;
        boolean yVFound = false;
        boolean zVFound = false;
        SphericalPolygonsSet.Vertex first = loops.get(0);
        int count = 0;
        for (SphericalPolygonsSet.Vertex v = first; count == 0 || v != first; v = v.getOutgoing().getEnd()) {
            ++count;
            SphericalPolygonsSet.Edge e = v.getIncoming();
            Assert.assertTrue(v == e.getStart().getOutgoing().getEnd());
            xPFound = xPFound || e.getCircle().getPole().distance(Vector3D.PLUS_I) < 1.0e-10;
            yPFound = yPFound || e.getCircle().getPole().distance(Vector3D.PLUS_J) < 1.0e-10;
            zPFound = zPFound || e.getCircle().getPole().distance(Vector3D.PLUS_K) < 1.0e-10;
            Assert.assertEquals(0.5 * FastMath.PI, e.getLength(), 1.0e-10);
            xVFound = xVFound || v.getLocation().getVector().distance(Vector3D.PLUS_I) < 1.0e-10;
            yVFound = yVFound || v.getLocation().getVector().distance(Vector3D.PLUS_J) < 1.0e-10;
            zVFound = zVFound || v.getLocation().getVector().distance(Vector3D.PLUS_K) < 1.0e-10;
        }
        Assert.assertTrue(xPFound);
        Assert.assertTrue(yPFound);
        Assert.assertTrue(zPFound);
        Assert.assertTrue(xVFound);
        Assert.assertTrue(yVFound);
        Assert.assertTrue(zVFound);
        Assert.assertEquals(3, count);

        Assert.assertEquals(0.0,
                            new Vector3D(1, 1, 1).normalize().distance(((S2Point) octant.getBarycenter()).getVector()),
                            1.0e-10);
        Assert.assertEquals(0.5 * FastMath.PI, octant.getSize(), 1.0e-10);

    }

    @Test
    public void testPositiveOctantByVertices() {
        double tol = 0.01;
        double sinTol = FastMath.sin(tol);
        SphericalPolygonsSet octant = new SphericalPolygonsSet(tol, S2Point.PLUS_I, S2Point.PLUS_J, S2Point.PLUS_K);
        UnitSphereRandomVectorGenerator random =
                new UnitSphereRandomVectorGenerator(3, new Well1024a(0xb8fc5acc91044308l));
        for (int i = 0; i < 1000; ++i) {
            Vector3D v = new Vector3D(random.nextVector());
            if ((v.getX() > sinTol) && (v.getY() > sinTol) && (v.getZ() > sinTol)) {
                Assert.assertEquals(Location.INSIDE, octant.checkPoint(new S2Point(v)));
            } else if ((v.getX() < -sinTol) || (v.getY() < -sinTol) || (v.getZ() < -sinTol)) {
                Assert.assertEquals(Location.OUTSIDE, octant.checkPoint(new S2Point(v)));
            } else {
                Assert.assertEquals(Location.BOUNDARY, octant.checkPoint(new S2Point(v)));
            }
        }
    }

    @Test
    public void testNonConvex() {
        double tol = 0.01;
        double sinTol = FastMath.sin(tol);
        RegionFactory<Sphere2D> factory = new RegionFactory<Sphere2D>();
        SphericalPolygonsSet plusX = new SphericalPolygonsSet(Vector3D.PLUS_I, tol);
        SphericalPolygonsSet plusY = new SphericalPolygonsSet(Vector3D.PLUS_J, tol);
        SphericalPolygonsSet plusZ = new SphericalPolygonsSet(Vector3D.PLUS_K, tol);
        SphericalPolygonsSet threeOctants =
                (SphericalPolygonsSet) factory.difference(plusZ, factory.intersection(plusX, plusY));

        UnitSphereRandomVectorGenerator random =
                new UnitSphereRandomVectorGenerator(3, new Well1024a(0x9c9802fde3cbcf25l));
        for (int i = 0; i < 1000; ++i) {
            Vector3D v = new Vector3D(random.nextVector());
            if (((v.getX() < -sinTol) || (v.getY() < -sinTol)) && (v.getZ() > sinTol)) {
                Assert.assertEquals(Location.INSIDE, threeOctants.checkPoint(new S2Point(v)));
            } else if (((v.getX() > sinTol) && (v.getY() > sinTol)) || (v.getZ() < -sinTol)) {
                Assert.assertEquals(Location.OUTSIDE, threeOctants.checkPoint(new S2Point(v)));
            } else {
                Assert.assertEquals(Location.BOUNDARY, threeOctants.checkPoint(new S2Point(v)));
            }
        }

        List<SphericalPolygonsSet.Vertex> loops = threeOctants.getBoundaryLoops();
        Assert.assertEquals(1, loops.size());
        boolean xPFound = false;
        boolean yPFound = false;
        boolean zPFound = false;
        boolean xVFound = false;
        boolean yVFound = false;
        boolean zVFound = false;
        SphericalPolygonsSet.Vertex first = loops.get(0);
        int count = 0;
        for (SphericalPolygonsSet.Vertex v = first; count == 0 || v != first; v = v.getOutgoing().getEnd()) {
            ++count;
            SphericalPolygonsSet.Edge e = v.getIncoming();
            Assert.assertTrue(v == e.getStart().getOutgoing().getEnd());
            xPFound = xPFound || e.getCircle().getPole().distance(Vector3D.MINUS_I) < 1.0e-10;
            yPFound = yPFound || e.getCircle().getPole().distance(Vector3D.MINUS_J) < 1.0e-10;
            zPFound = zPFound || e.getCircle().getPole().distance(Vector3D.PLUS_K)  < 1.0e-10;
            if (Vector3D.PLUS_K.distance(e.getCircle().getPole()) < 1.0e-10) {
                Assert.assertEquals(1.5 * FastMath.PI, e.getLength(), 1.0e-10);
            } else {
                Assert.assertEquals(0.5 * FastMath.PI, e.getLength(), 1.0e-10);
            }
            xVFound = xVFound || v.getLocation().getVector().distance(Vector3D.PLUS_I) < 1.0e-10;
            yVFound = yVFound || v.getLocation().getVector().distance(Vector3D.PLUS_J) < 1.0e-10;
            zVFound = zVFound || v.getLocation().getVector().distance(Vector3D.PLUS_K) < 1.0e-10;
        }
        Assert.assertTrue(xPFound);
        Assert.assertTrue(yPFound);
        Assert.assertTrue(zPFound);
        Assert.assertTrue(xVFound);
        Assert.assertTrue(yVFound);
        Assert.assertTrue(zVFound);
        Assert.assertEquals(3, count);

        Assert.assertEquals(1.5 * FastMath.PI, threeOctants.getSize(), 1.0e-10);

    }

    @Test
    public void testModeratlyComplexShape() {
        double tol = 0.01;
        List<SubHyperplane<Sphere2D>> boundary = new ArrayList<SubHyperplane<Sphere2D>>();
        boundary.add(create(Vector3D.MINUS_J, Vector3D.PLUS_I,  Vector3D.PLUS_K,  tol, 0.0, 0.5 * FastMath.PI));
        boundary.add(create(Vector3D.MINUS_I, Vector3D.PLUS_K,  Vector3D.PLUS_J,  tol, 0.0, 0.5 * FastMath.PI));
        boundary.add(create(Vector3D.PLUS_K,  Vector3D.PLUS_J,  Vector3D.MINUS_I, tol, 0.0, 0.5 * FastMath.PI));
        boundary.add(create(Vector3D.MINUS_J, Vector3D.MINUS_I, Vector3D.MINUS_K, tol, 0.0, 0.5 * FastMath.PI));
        boundary.add(create(Vector3D.MINUS_I, Vector3D.MINUS_K, Vector3D.MINUS_J, tol, 0.0, 0.5 * FastMath.PI));
        boundary.add(create(Vector3D.PLUS_K,  Vector3D.MINUS_J, Vector3D.PLUS_I,  tol, 0.0, 0.5 * FastMath.PI));
        SphericalPolygonsSet polygon = new SphericalPolygonsSet(boundary, tol);

        Assert.assertEquals(Location.OUTSIDE, polygon.checkPoint(new S2Point(new Vector3D( 1,  1,  1).normalize())));
        Assert.assertEquals(Location.INSIDE,  polygon.checkPoint(new S2Point(new Vector3D(-1,  1,  1).normalize())));
        Assert.assertEquals(Location.INSIDE,  polygon.checkPoint(new S2Point(new Vector3D(-1, -1,  1).normalize())));
        Assert.assertEquals(Location.INSIDE,  polygon.checkPoint(new S2Point(new Vector3D( 1, -1,  1).normalize())));
        Assert.assertEquals(Location.OUTSIDE, polygon.checkPoint(new S2Point(new Vector3D( 1,  1, -1).normalize())));
        Assert.assertEquals(Location.OUTSIDE, polygon.checkPoint(new S2Point(new Vector3D(-1,  1, -1).normalize())));
        Assert.assertEquals(Location.INSIDE,  polygon.checkPoint(new S2Point(new Vector3D(-1, -1, -1).normalize())));
        Assert.assertEquals(Location.OUTSIDE, polygon.checkPoint(new S2Point(new Vector3D( 1, -1, -1).normalize())));

        Assert.assertEquals(MathUtils.TWO_PI, polygon.getSize(), 1.0e-10);
        Assert.assertEquals(3 * FastMath.PI, polygon.getBoundarySize(), 1.0e-10);

        List<SphericalPolygonsSet.Vertex> loops = polygon.getBoundaryLoops();
        Assert.assertEquals(1, loops.size());
        boolean pXFound = false;
        boolean mXFound = false;
        boolean pYFound = false;
        boolean mYFound = false;
        boolean pZFound = false;
        boolean mZFound = false;
        SphericalPolygonsSet.Vertex first = loops.get(0);
        int count = 0;
        for (SphericalPolygonsSet.Vertex v = first; count == 0 || v != first; v = v.getOutgoing().getEnd()) {
            ++count;
            SphericalPolygonsSet.Edge e = v.getIncoming();
            Assert.assertTrue(v == e.getStart().getOutgoing().getEnd());
            pXFound = pXFound || v.getLocation().getVector().distance(Vector3D.PLUS_I)  < 1.0e-10;
            mXFound = mXFound || v.getLocation().getVector().distance(Vector3D.MINUS_I) < 1.0e-10;
            pYFound = pYFound || v.getLocation().getVector().distance(Vector3D.PLUS_J)  < 1.0e-10;
            mYFound = mYFound || v.getLocation().getVector().distance(Vector3D.MINUS_J) < 1.0e-10;
            pZFound = pZFound || v.getLocation().getVector().distance(Vector3D.PLUS_K)  < 1.0e-10;
            mZFound = mZFound || v.getLocation().getVector().distance(Vector3D.MINUS_K) < 1.0e-10;
            Assert.assertEquals(0.5 * FastMath.PI, e.getLength(), 1.0e-10);
        }
        Assert.assertTrue(pXFound);
        Assert.assertTrue(mXFound);
        Assert.assertTrue(pYFound);
        Assert.assertTrue(mYFound);
        Assert.assertTrue(pZFound);
        Assert.assertTrue(mZFound);
        Assert.assertEquals(6, count);

    }

    @Test
    public void testSeveralParts() {
        double tol = 0.01;
        double sinTol = FastMath.sin(tol);
        List<SubHyperplane<Sphere2D>> boundary = new ArrayList<SubHyperplane<Sphere2D>>();

        // first part: +X, +Y, +Z octant
        boundary.add(create(Vector3D.PLUS_J,  Vector3D.PLUS_K,  Vector3D.PLUS_I,  tol, 0.0, 0.5 * FastMath.PI));
        boundary.add(create(Vector3D.PLUS_K,  Vector3D.PLUS_I,  Vector3D.PLUS_J,  tol, 0.0, 0.5 * FastMath.PI));
        boundary.add(create(Vector3D.PLUS_I,  Vector3D.PLUS_J,  Vector3D.PLUS_K,  tol, 0.0, 0.5 * FastMath.PI));

        // first part: -X, -Y, -Z octant
        boundary.add(create(Vector3D.MINUS_J, Vector3D.MINUS_I, Vector3D.MINUS_K, tol, 0.0, 0.5 * FastMath.PI));
        boundary.add(create(Vector3D.MINUS_I, Vector3D.MINUS_K, Vector3D.MINUS_J, tol, 0.0, 0.5 * FastMath.PI));
        boundary.add(create(Vector3D.MINUS_K, Vector3D.MINUS_J, Vector3D.MINUS_I,  tol, 0.0, 0.5 * FastMath.PI));

        SphericalPolygonsSet polygon = new SphericalPolygonsSet(boundary, tol);

        UnitSphereRandomVectorGenerator random =
                new UnitSphereRandomVectorGenerator(3, new Well1024a(0xcc5ce49949e0d3ecl));
        for (int i = 0; i < 1000; ++i) {
            Vector3D v = new Vector3D(random.nextVector());
            if ((v.getX() < -sinTol) && (v.getY() < -sinTol) && (v.getZ() < -sinTol)) {
                Assert.assertEquals(Location.INSIDE, polygon.checkPoint(new S2Point(v)));
            } else if ((v.getX() < sinTol) && (v.getY() < sinTol) && (v.getZ() < sinTol)) {
                Assert.assertEquals(Location.BOUNDARY, polygon.checkPoint(new S2Point(v)));
            } else if ((v.getX() > sinTol) && (v.getY() > sinTol) && (v.getZ() > sinTol)) {
                Assert.assertEquals(Location.INSIDE, polygon.checkPoint(new S2Point(v)));
            } else if ((v.getX() > -sinTol) && (v.getY() > -sinTol) && (v.getZ() > -sinTol)) {
                Assert.assertEquals(Location.BOUNDARY, polygon.checkPoint(new S2Point(v)));
            } else {
                Assert.assertEquals(Location.OUTSIDE, polygon.checkPoint(new S2Point(v)));
            }
        }

        Assert.assertEquals(FastMath.PI, polygon.getSize(), 1.0e-10);
        Assert.assertEquals(3 * FastMath.PI, polygon.getBoundarySize(), 1.0e-10);

        // there should be two separate boundary loops
        Assert.assertEquals(2, polygon.getBoundaryLoops().size());

    }

    @Test
    public void testPartWithHole() {
        double tol = 0.01;
        double alpha = 0.7;
        S2Point center = new S2Point(new Vector3D(1, 1, 1));
        SphericalPolygonsSet octant = new SphericalPolygonsSet(center.getVector(), Vector3D.PLUS_K, alpha, 6, tol);
        SphericalPolygonsSet hole   = new SphericalPolygonsSet(tol,
                                                               new S2Point(FastMath.PI / 6, FastMath.PI / 3),
                                                               new S2Point(FastMath.PI / 3, FastMath.PI / 3),
                                                               new S2Point(FastMath.PI / 4, FastMath.PI / 6));
        SphericalPolygonsSet octantWithHole =
                (SphericalPolygonsSet) new RegionFactory<Sphere2D>().difference(octant, hole);

        for (double phi = center.getPhi() - alpha + 0.1; phi < center.getPhi() + alpha - 0.1; phi += 0.07) {
            Location l = octantWithHole.checkPoint(new S2Point(FastMath.PI / 4, phi));
            if (phi < FastMath.PI / 6 || phi > FastMath.PI / 3) {
                Assert.assertEquals(Location.INSIDE,  l);
            } else {
                Assert.assertEquals(Location.OUTSIDE, l);
            }
        }

        // there should be two separate boundary loops
        Assert.assertEquals(2, octantWithHole.getBoundaryLoops().size());

        Assert.assertEquals(octant.getBoundarySize() + hole.getBoundarySize(), octantWithHole.getBoundarySize(), 1.0e-10);
        Assert.assertEquals(octant.getSize() - hole.getSize(), octantWithHole.getSize(), 1.0e-10);

    }

    @Test
    public void testConcentricSubParts() {
        double tol = 0.001;
        Vector3D center = new Vector3D(1, 1, 1);
        SphericalPolygonsSet hexaOut   = new SphericalPolygonsSet(center, Vector3D.PLUS_K, 0.9,  6, tol);
        SphericalPolygonsSet hexaIn    = new SphericalPolygonsSet(center, Vector3D.PLUS_K, 0.8,  6, tol);
        SphericalPolygonsSet pentaOut  = new SphericalPolygonsSet(center, Vector3D.PLUS_K, 0.7,  5, tol);
        SphericalPolygonsSet pentaIn   = new SphericalPolygonsSet(center, Vector3D.PLUS_K, 0.6,  5, tol);
        SphericalPolygonsSet quadriOut = new SphericalPolygonsSet(center, Vector3D.PLUS_K, 0.5,  4, tol);
        SphericalPolygonsSet quadriIn  = new SphericalPolygonsSet(center, Vector3D.PLUS_K, 0.4,  4, tol);
        SphericalPolygonsSet triOut    = new SphericalPolygonsSet(center, Vector3D.PLUS_K, 0.25, 3, tol);
        SphericalPolygonsSet triIn     = new SphericalPolygonsSet(center, Vector3D.PLUS_K, 0.15, 3, tol);

        RegionFactory<Sphere2D> factory = new RegionFactory<Sphere2D>();
        SphericalPolygonsSet hexa   = (SphericalPolygonsSet) factory.difference(hexaOut,   hexaIn);
        SphericalPolygonsSet penta  = (SphericalPolygonsSet) factory.difference(pentaOut,  pentaIn);
        SphericalPolygonsSet quadri = (SphericalPolygonsSet) factory.difference(quadriOut, quadriIn);
        SphericalPolygonsSet tri    = (SphericalPolygonsSet) factory.difference(triOut,    triIn);
        SphericalPolygonsSet concentric =
                (SphericalPolygonsSet) factory.union(factory.union(hexa, penta), factory.union(quadri, tri));

        // there should be two separate boundary loops
        Assert.assertEquals(8, concentric.getBoundaryLoops().size());

        Assert.assertEquals(hexaOut.getBoundarySize()   + hexaIn.getBoundarySize()   +
                            pentaOut.getBoundarySize()  + pentaIn.getBoundarySize()  +
                            quadriOut.getBoundarySize() + quadriIn.getBoundarySize() +
                            triOut.getBoundarySize()    + triIn.getBoundarySize(),
                            concentric.getBoundarySize(), 1.0e-10);
        Assert.assertEquals(hexaOut.getSize()   - hexaIn.getSize()   +
                            pentaOut.getSize()  - pentaIn.getSize()  +
                            quadriOut.getSize() - quadriIn.getSize() +
                            triOut.getSize()    - triIn.getSize(),
                            concentric.getSize(), 1.0e-10);

    }

    private SubCircle create(Vector3D pole, Vector3D x, Vector3D y,
                             double tolerance, double ... limits) {
        RegionFactory<Sphere1D> factory = new RegionFactory<Sphere1D>();
        Circle circle = new Circle(pole, tolerance);
        Circle phased =
                (Circle) Circle.getTransform(new Rotation(circle.getXAxis(), circle.getYAxis(), x, y)).apply(circle);
        ArcsSet set = (ArcsSet) factory.getComplement(new ArcsSet(tolerance));
        for (int i = 0; i < limits.length; i += 2) {
            set = (ArcsSet) factory.union(set, new ArcsSet(limits[i], limits[i + 1], tolerance));
        }
        return new SubCircle(phased, set);
    }

//    private static void displayCells(String name, SphericalPolygonsSet polygons, double maxStep, double grid) {
//        TesselationDisplay td = new TesselationDisplay(name, maxStep, grid, polygons.getTolerance());
//        polygons.getTree(false).visit(td);
//        td.close();
//    }
//
//    private static class TesselationDisplay implements BSPTreeVisitor<Sphere2D> {
//        private double maxStep;
//        private double grid;
//        private double tolerance;
//        private PrintStream out;
//        public TesselationDisplay(String name, double maxStep, double grid, double tolerance) {
//            try {
//            this.out = new PrintStream(new File(new File(System.getProperty("user.home")), name));
//            this.maxStep   = maxStep;
//            this.grid      = grid;
//            this.tolerance = tolerance;
//            } catch (IOException ioe) {
//                Assert.fail(ioe.getMessage());
//            }
//        }
//        public Order visitOrder(BSPTree<Sphere2D> node) {
//            return Order.MINUS_PLUS_SUB;
//        }
//        public void visitInternalNode(BSPTree<Sphere2D> node) {
//        }
//        public void visitLeafNode(BSPTree<Sphere2D> node) {
//            if ((Boolean) node.getAttribute()) {
//                final SphericalPolygonsSet cell =
//                        new SphericalPolygonsSet(node.pruneAroundConvexCell(true, false, null), tolerance);
//                display(out, cell, maxStep, grid);
//            }
//        }
//        public void close() {
//            out.close();
//        }
//    }
//
//    private static void display(String name, SphericalPolygonsSet polygons, double maxStep, double grid) {
//        PrintStream out = null;
//        try {
//            out = new PrintStream(new File(new File(System.getProperty("user.home")), name));
//            display(out, polygons, maxStep, grid);
//        } catch (IOException ioe) {
//            Assert.fail(ioe.getMessage());
//        } finally {
//            if (out != null) {
//                out.close();
//            }
//        }
//    }
//
//    private static void display(PrintStream out, SphericalPolygonsSet polygons, double maxStep, double grid) {
//            for (final SphericalPolygonsSet.Vertex start : polygons.getBoundaryLoops()) {
//                int n = 0;
//                for (SphericalPolygonsSet.Edge edge = start.getOutgoing();
//                     n == 0 || edge.getStart() != start;
//                     edge = edge.getEnd().getOutgoing()) {
//                    int m = (int) FastMath.ceil(edge.getLength() / maxStep);
//                    double step = edge.getLength() / m;
//                    for (int i = 0; i <= m; ++i) {
//                       final Vector3D p = edge.getPointAt(i * step);
//                       out.format(Locale.US, "%9.6f %9.6f %9.6f%n", p.getX(), p.getY(), p.getZ());
//                    }
//                    n++;
//                }
//                out.println("&");
//            }
//            if (grid > 0) {
//                for (double phi = grid; phi < FastMath.PI - grid; phi += grid) {
//                    Location previous = Location.OUTSIDE;
//                    for (double theta = 0; theta < MathUtils.TWO_PI; theta += grid) {
//                        S2Point point = new S2Point(theta, phi);
//                        Location current = polygons.checkPoint(point);
//                        if (current == Location.OUTSIDE) {
//                            if (previous != Location.OUTSIDE) {
//                                out.println("&");
//                            }
//                        } else {
//                            out.format(Locale.US, "%9.6f %9.6f %9.6f%n",
//                                       point.getVector().getX(), point.getVector().getY(), point.getVector().getZ());
//                        }
//                        previous = current;
//                    }
//                    out.println("&");
//                }
//            }
//    }

}
