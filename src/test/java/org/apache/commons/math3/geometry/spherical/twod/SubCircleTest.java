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

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane.SplitSubHyperplane;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.util.MathUtils;
import org.junit.Assert;
import org.junit.Test;

public class SubCircleTest {

    @Test
    public void testFullCircle() {
        Circle circle = new Circle(Vector3D.PLUS_K, 1.0e-10);
        SubCircle set = circle.wholeHyperplane();
        Assert.assertEquals(MathUtils.TWO_PI, set.getSize(), 1.0e-10);
        Assert.assertTrue(circle == set.getHyperplane());
        Assert.assertTrue(circle != set.copySelf().getHyperplane());
    }

    @Test
    public void testSide() {

        Circle xzPlane = new Circle(Vector3D.PLUS_J, 1.0e-10);

        SubCircle sc1 = create(Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_J, 1.0e-10, 1.0, 3.0, 5.0, 6.0);
        Assert.assertEquals(Side.BOTH, sc1.split(xzPlane).getSide());

        SubCircle sc2 = create(Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_J, 1.0e-10, 1.0, 3.0);
        Assert.assertEquals(Side.MINUS, sc2.split(xzPlane).getSide());

        SubCircle sc3 = create(Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_J, 1.0e-10, 5.0, 6.0);
        Assert.assertEquals(Side.PLUS, sc3.split(xzPlane).getSide());

        SubCircle sc4 = create(Vector3D.PLUS_J, Vector3D.PLUS_K, Vector3D.PLUS_I, 1.0e-10, 5.0, 6.0);
        Assert.assertEquals(Side.HYPER, sc4.split(xzPlane).getSide());

        SubCircle sc5 = create(Vector3D.MINUS_J, Vector3D.PLUS_I, Vector3D.PLUS_K, 1.0e-10, 5.0, 6.0);
        Assert.assertEquals(Side.HYPER, sc5.split(xzPlane).getSide());

    }

    @Test
    public void testSPlit() {

        Circle xzPlane = new Circle(Vector3D.PLUS_J, 1.0e-10);

        SubCircle sc1 = create(Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_J, 1.0e-10, 1.0, 3.0, 5.0, 6.0);
        SplitSubHyperplane<Sphere2D> split1 = sc1.split(xzPlane);
        ArcsSet plus1  = (ArcsSet) ((SubCircle) split1.getPlus()).getRemainingRegion();
        ArcsSet minus1 = (ArcsSet) ((SubCircle) split1.getMinus()).getRemainingRegion();
        Assert.assertEquals(1, plus1.asList().size());
        Assert.assertEquals(5.0, plus1.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(6.0, plus1.asList().get(0).getSup(), 1.0e-10);
        Assert.assertEquals(1, minus1.asList().size());
        Assert.assertEquals(1.0, minus1.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(3.0, minus1.asList().get(0).getSup(), 1.0e-10);

        SubCircle sc2 = create(Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_J, 1.0e-10, 1.0, 3.0);
        SplitSubHyperplane<Sphere2D> split2 = sc2.split(xzPlane);
        Assert.assertNull(split2.getPlus());
        ArcsSet minus2 = (ArcsSet) ((SubCircle) split2.getMinus()).getRemainingRegion();
        Assert.assertEquals(1, minus2.asList().size());
        Assert.assertEquals(1.0, minus2.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(3.0, minus2.asList().get(0).getSup(), 1.0e-10);

        SubCircle sc3 = create(Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_J, 1.0e-10, 5.0, 6.0);
        SplitSubHyperplane<Sphere2D> split3 = sc3.split(xzPlane);
        ArcsSet plus3  = (ArcsSet) ((SubCircle) split3.getPlus()).getRemainingRegion();
        Assert.assertEquals(1, plus3.asList().size());
        Assert.assertEquals(5.0, plus3.asList().get(0).getInf(), 1.0e-10);
        Assert.assertEquals(6.0, plus3.asList().get(0).getSup(), 1.0e-10);
        Assert.assertNull(split3.getMinus());

        SubCircle sc4 = create(Vector3D.PLUS_J, Vector3D.PLUS_K, Vector3D.PLUS_I, 1.0e-10, 5.0, 6.0);
        SplitSubHyperplane<Sphere2D> split4 = sc4.split(xzPlane);
        Assert.assertEquals(Side.HYPER, sc4.split(xzPlane).getSide());
        Assert.assertNull(split4.getPlus());
        Assert.assertNull(split4.getMinus());

        SubCircle sc5 = create(Vector3D.MINUS_J, Vector3D.PLUS_I, Vector3D.PLUS_K, 1.0e-10, 5.0, 6.0);
        SplitSubHyperplane<Sphere2D> split5 = sc5.split(xzPlane);
        Assert.assertEquals(Side.HYPER, sc5.split(xzPlane).getSide());
        Assert.assertNull(split5.getPlus());
        Assert.assertNull(split5.getMinus());

    }

    @Test
    public void testSideSplitConsistency() {

        double tolerance = 1.0e-6;
        Circle hyperplane = new Circle(new Vector3D(9.738804529764676E-5, -0.6772824575010357, -0.7357230887208355),
                                       tolerance);
        SubCircle sub = new SubCircle(new Circle(new Vector3D(2.1793884139073498E-4, 0.9790647032675541, -0.20354915700704285),
                                                 tolerance),
                                      new ArcsSet(4.7121441684170700, 4.7125386635004760, tolerance));
        SplitSubHyperplane<Sphere2D> split = sub.split(hyperplane);
        Assert.assertNotNull(split.getMinus());
        Assert.assertNull(split.getPlus());
        Assert.assertEquals(Side.MINUS, sub.split(hyperplane).getSide());

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

}
