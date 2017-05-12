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

import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.util.ExceptionContext;
import org.apache.commons.math4.exception.util.Localizable;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.geometry.Vector;
import org.apache.commons.math4.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math4.geometry.euclidean.threed.Plane;
import org.apache.commons.math4.geometry.euclidean.threed.PolyhedronsSet;
import org.apache.commons.math4.geometry.euclidean.threed.Rotation;
import org.apache.commons.math4.geometry.euclidean.threed.SubPlane;
import org.apache.commons.math4.geometry.euclidean.threed.Cartesian3D;
import org.apache.commons.math4.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math4.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math4.geometry.euclidean.twod.SubLine;
import org.apache.commons.math4.geometry.euclidean.twod.Cartesian2D;
import org.apache.commons.math4.geometry.partitioning.BSPTree;
import org.apache.commons.math4.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math4.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math4.geometry.partitioning.Region;
import org.apache.commons.math4.geometry.partitioning.RegionDumper;
import org.apache.commons.math4.geometry.partitioning.RegionFactory;
import org.apache.commons.math4.geometry.partitioning.RegionParser;
import org.apache.commons.math4.geometry.partitioning.SubHyperplane;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

public class PolyhedronsSetTest {

    @Test
    public void testBox() {
        PolyhedronsSet tree = new PolyhedronsSet(0, 1, 0, 1, 0, 1, 1.0e-10);
        Assert.assertEquals(1.0, tree.getSize(), 1.0e-10);
        Assert.assertEquals(6.0, tree.getBoundarySize(), 1.0e-10);
        Cartesian3D barycenter = (Cartesian3D) tree.getBarycenter();
        Assert.assertEquals(0.5, barycenter.getX(), 1.0e-10);
        Assert.assertEquals(0.5, barycenter.getY(), 1.0e-10);
        Assert.assertEquals(0.5, barycenter.getZ(), 1.0e-10);
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
    public void testTetrahedron() throws MathArithmeticException {
        Cartesian3D vertex1 = new Cartesian3D(1, 2, 3);
        Cartesian3D vertex2 = new Cartesian3D(2, 2, 4);
        Cartesian3D vertex3 = new Cartesian3D(2, 3, 3);
        Cartesian3D vertex4 = new Cartesian3D(1, 3, 4);
        PolyhedronsSet tree =
            (PolyhedronsSet) new RegionFactory<Euclidean3D>().buildConvex(
                new Plane(vertex3, vertex2, vertex1, 1.0e-10),
                new Plane(vertex2, vertex3, vertex4, 1.0e-10),
                new Plane(vertex4, vertex3, vertex1, 1.0e-10),
                new Plane(vertex1, vertex2, vertex4, 1.0e-10));
        Assert.assertEquals(1.0 / 3.0, tree.getSize(), 1.0e-10);
        Assert.assertEquals(2.0 * FastMath.sqrt(3.0), tree.getBoundarySize(), 1.0e-10);
        Cartesian3D barycenter = (Cartesian3D) tree.getBarycenter();
        Assert.assertEquals(1.5, barycenter.getX(), 1.0e-10);
        Assert.assertEquals(2.5, barycenter.getY(), 1.0e-10);
        Assert.assertEquals(3.5, barycenter.getZ(), 1.0e-10);
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
    public void testIsometry() throws MathArithmeticException, MathIllegalArgumentException {
        Cartesian3D vertex1 = new Cartesian3D(1.1, 2.2, 3.3);
        Cartesian3D vertex2 = new Cartesian3D(2.0, 2.4, 4.2);
        Cartesian3D vertex3 = new Cartesian3D(2.8, 3.3, 3.7);
        Cartesian3D vertex4 = new Cartesian3D(1.0, 3.6, 4.5);
        PolyhedronsSet tree =
            (PolyhedronsSet) new RegionFactory<Euclidean3D>().buildConvex(
                new Plane(vertex3, vertex2, vertex1, 1.0e-10),
                new Plane(vertex2, vertex3, vertex4, 1.0e-10),
                new Plane(vertex4, vertex3, vertex1, 1.0e-10),
                new Plane(vertex1, vertex2, vertex4, 1.0e-10));
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
                            newB.subtract((Vector<Euclidean3D>) tree.getBarycenter()).getNorm(),
                            1.0e-10);

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
                    Assert.assertEquals(0, d, 1.0e-10);
                }
            }

        });

    }

    @Test
    public void testBuildBox() {
        double x = 1.0;
        double y = 2.0;
        double z = 3.0;
        double w = 0.1;
        double l = 1.0;
        PolyhedronsSet tree =
            new PolyhedronsSet(x - l, x + l, y - w, y + w, z - w, z + w, 1.0e-10);
        Cartesian3D barycenter = (Cartesian3D) tree.getBarycenter();
        Assert.assertEquals(x, barycenter.getX(), 1.0e-10);
        Assert.assertEquals(y, barycenter.getY(), 1.0e-10);
        Assert.assertEquals(z, barycenter.getZ(), 1.0e-10);
        Assert.assertEquals(8 * l * w * w, tree.getSize(), 1.0e-10);
        Assert.assertEquals(8 * w * (2 * l + w), tree.getBoundarySize(), 1.0e-10);
    }

    @Test
    public void testCross() {

        double x = 1.0;
        double y = 2.0;
        double z = 3.0;
        double w = 0.1;
        double l = 1.0;
        PolyhedronsSet xBeam =
            new PolyhedronsSet(x - l, x + l, y - w, y + w, z - w, z + w, 1.0e-10);
        PolyhedronsSet yBeam =
            new PolyhedronsSet(x - w, x + w, y - l, y + l, z - w, z + w, 1.0e-10);
        PolyhedronsSet zBeam =
            new PolyhedronsSet(x - w, x + w, y - w, y + w, z - l, z + l, 1.0e-10);
        RegionFactory<Euclidean3D> factory = new RegionFactory<>();
        PolyhedronsSet tree = (PolyhedronsSet) factory.union(xBeam, factory.union(yBeam, zBeam));
        Cartesian3D barycenter = (Cartesian3D) tree.getBarycenter();

        Assert.assertEquals(x, barycenter.getX(), 1.0e-10);
        Assert.assertEquals(y, barycenter.getY(), 1.0e-10);
        Assert.assertEquals(z, barycenter.getZ(), 1.0e-10);
        Assert.assertEquals(8 * w * w * (3 * l - 2 * w), tree.getSize(), 1.0e-10);
        Assert.assertEquals(24 * w * (2 * l - w), tree.getBoundarySize(), 1.0e-10);

    }

    @Test
    public void testIssue780() throws MathArithmeticException {
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
            Plane polyPlane = new Plane(v_1, v_2, v_3, 1.0e-10);
            ArrayList<SubHyperplane<Euclidean2D>> lines = new ArrayList<>();

            Cartesian2D[] projPts = new Cartesian2D[vertices.length];
            for (int ptIdx = 0; ptIdx < projPts.length; ptIdx++) {
                projPts[ptIdx] = polyPlane.toSubSpace(vertices[ptIdx]);
            }

            SubLine lineInPlane = null;
            for (int ptIdx = 0; ptIdx < projPts.length; ptIdx++) {
                lineInPlane = new SubLine(projPts[ptIdx], projPts[(ptIdx + 1) % projPts.length], 1.0e-10);
                lines.add(lineInPlane);
            }
            Region<Euclidean2D> polyRegion = new PolygonsSet(lines, 1.0e-10);
            SubPlane polygon = new SubPlane(polyPlane, polyRegion);
            subHyperplaneList.add(polygon);
        }
        PolyhedronsSet polyhedronsSet = new PolyhedronsSet(subHyperplaneList, 1.0e-10);
        Assert.assertEquals( 8.0, polyhedronsSet.getSize(), 3.0e-6);
        Assert.assertEquals(24.0, polyhedronsSet.getBoundarySize(), 5.0e-6);
    }

    @Test
    public void testTooThinBox() {
        Assert.assertEquals(0.0,
                            new PolyhedronsSet(0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0e-10).getSize(),
                            1.0e-10);
    }

    @Test
    public void testWrongUsage() {
        // the following is a wrong usage of the constructor.
        // as explained in the javadoc, the failure is NOT detected at construction
        // time but occurs later on
        PolyhedronsSet ps = new PolyhedronsSet(new BSPTree<Euclidean3D>(), 1.0e-10);
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
            Assert.assertEquals(8.0, polyset.getSize(), 1.0e-10);
            Assert.assertEquals(24.0, polyset.getBoundarySize(), 1.0e-10);
            String dump = RegionDumper.dump(polyset);
            PolyhedronsSet parsed = RegionParser.parsePolyhedronsSet(dump);
            Assert.assertEquals(8.0, parsed.getSize(), 1.0e-10);
            Assert.assertEquals(24.0, parsed.getBoundarySize(), 1.0e-10);
            Assert.assertTrue(new RegionFactory<Euclidean3D>().difference(polyset, parsed).isEmpty());
    }

    @Test
    public void testConnectedFacets() throws IOException, ParseException {
        InputStream stream = getClass().getResourceAsStream("pentomino-N.ply");
        PLYParser   parser = new PLYParser(stream);
        stream.close();
        PolyhedronsSet polyhedron = new PolyhedronsSet(parser.getVertices(), parser.getFaces(), 1.0e-10);
        Assert.assertEquals( 5.0, polyhedron.getSize(), 1.0e-10);
        Assert.assertEquals(22.0, polyhedron.getBoundarySize(), 1.0e-10);
    }

    @Test
    public void testTooClose() throws IOException, ParseException {
        checkError("pentomino-N-too-close.ply", LocalizedFormats.CLOSE_VERTICES);
    }

    @Test
    public void testHole() throws IOException, ParseException {
        checkError("pentomino-N-hole.ply", LocalizedFormats.EDGE_CONNECTED_TO_ONE_FACET);
    }

    @Test
    public void testNonPlanar() throws IOException, ParseException {
        checkError("pentomino-N-out-of-plane.ply", LocalizedFormats.OUT_OF_PLANE);
    }

    @Test
    public void testOrientation() throws IOException, ParseException {
        checkError("pentomino-N-bad-orientation.ply", LocalizedFormats.FACET_ORIENTATION_MISMATCH);
    }

    @Test
    public void testFacet2Vertices() throws IOException, ParseException {
        checkError(Arrays.asList(Cartesian3D.ZERO, Cartesian3D.PLUS_I, Cartesian3D.PLUS_J, Cartesian3D.PLUS_K),
                   Arrays.asList(new int[] { 0, 1, 2 }, new int[] {2, 3}),
                   LocalizedFormats.WRONG_NUMBER_OF_POINTS);
    }

    private void checkError(final String resourceName, final LocalizedFormats expected) {
        try {
            InputStream stream = getClass().getResourceAsStream(resourceName);
            PLYParser   parser = new PLYParser(stream);
            stream.close();
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
            new PolyhedronsSet(vertices, facets, 1.0e-10);
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
    public void testIssue1211() throws IOException, ParseException {

        PolyhedronsSet polyset = RegionParser.parsePolyhedronsSet(loadTestData("issue-1211.bsp"));
        UniformRandomProvider random = RandomSource.create(RandomSource.WELL_1024_A, 0xb97c9d1ade21e40al);
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

    private String loadTestData(final String resourceName)
            throws IOException {
            InputStream stream = getClass().getResourceAsStream(resourceName);
            Reader reader = new InputStreamReader(stream, "UTF-8");
            StringBuilder builder = new StringBuilder();
            for (int c = reader.read(); c >= 0; c = reader.read()) {
                builder.append((char) c);
            }
            return builder.toString();
        }

    private void checkPoints(Region.Location expected, PolyhedronsSet tree, Cartesian3D[] points) {
        for (int i = 0; i < points.length; ++i) {
            Assert.assertEquals(expected, tree.checkPoint(points[i]));
        }
    }

}
