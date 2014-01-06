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

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.Region.Location;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.random.Well1024a;
import org.apache.commons.math3.util.FastMath;
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

}
