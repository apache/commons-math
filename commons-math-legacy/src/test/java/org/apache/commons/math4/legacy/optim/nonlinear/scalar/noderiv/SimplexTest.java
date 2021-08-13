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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.optim.PointValuePair;

/**
 * Test for {@link Simplex}.
 */
public class SimplexTest {
    @Test
    public void testTriangle() {
        final double[] o = new double[] { 1, 2 };
        final double s = 3.45;
        final int dim = o.length;
        final int size = dim + 1;

        final double[][] expected = new double[][] {
            o,
            new double[] { o[0] + s, o[1] },
            new double[] { o[0] + s, o[1] + s }
        };

        final Simplex simplex = Simplex.equalSidesAlongAxes(dim, s).translate(o);
        Assert.assertEquals(dim, simplex.getDimension());
        Assert.assertEquals(size, simplex.getSize());

        for (int i = 0; i < size; i++) {
            final double[] e = expected[i];
            final double[] a = simplex.get(i).getPoint();
            Assert.assertArrayEquals("i=" + i + ": e=" + Arrays.toString(e) + " a=" + Arrays.toString(a), e, a, 0.0);
        }
    }

    @Test
    public void testJavadocExample() {
        final double[] o = new double[] { 1, 10, 2 };
        final double[] start = new double[] { 1, 1, 1 };
        final int dim = o.length;
        final int size = dim + 1;

        final double[][] expected = new double[][] {
            new double[] { 1, 1, 1 },
            new double[] { 2, 1, 1 },
            new double[] { 2, 11, 1 },
            new double[] { 2, 11, 3 },
        };

        final Simplex simplex = Simplex.alongAxes(o).translate(start);
        Assert.assertEquals(dim, simplex.getDimension());
        Assert.assertEquals(size, simplex.getSize());

        for (int i = 0; i < size; i++) {
            final double[] e = expected[i];
            final double[] a = simplex.get(i).getPoint();

            final String msg = "i=" + i +
                ": e=" + Arrays.toString(e) +
                " a=" + Arrays.toString(a);
            Assert.assertArrayEquals(msg, e, a, 0.0);
        }
    }

    @Test
    public void testCentroid() {
        final List<PointValuePair> list = new ArrayList<>();

        double[] centroid = null;

        // Interval.
        list.clear();
        list.add(new PointValuePair(new double[] { 1, 1 }, 0d));
        list.add(new PointValuePair(new double[] { 2, 2 }, 0d));
        centroid = Simplex.centroid(list);
        Assert.assertEquals(1.5, centroid[0], 0d);
        Assert.assertEquals(1.5, centroid[1], 0d);

        // Square.
        list.clear();
        list.add(new PointValuePair(new double[] { 1, 0 }, 0d));
        list.add(new PointValuePair(new double[] { 2, 0 }, 0d));
        list.add(new PointValuePair(new double[] { 2, 1 }, 0d));
        list.add(new PointValuePair(new double[] { 1, 1 }, 0d));
        centroid = Simplex.centroid(list);
        Assert.assertEquals(1.5, centroid[0], 0d);
        Assert.assertEquals(0.5, centroid[1], 0d);
    }

    @Test
    public void testAsList() {
        final int dim = 10;
        final Simplex simplex = Simplex.equalSidesAlongAxes(dim, 1d);
        final List<PointValuePair> list = simplex.asList();
        Assert.assertEquals(dim + 1, list.size());

        for (int i = 0; i < dim + 1; i++) {
            final PointValuePair p = list.get(i);
            final double[] a = simplex.get(i).getPointRef();
            final double[] b = p.getPointRef();
            Assert.assertNotSame(a, b);
            Assert.assertArrayEquals(a, b, 0.0);
            Assert.assertTrue(Double.isNaN(p.getValue()));
        }
    }

    @Test
    public void testReplaceLast1() {
        final int dim = 7;
        final int nPoints = dim + 1;
        final Simplex initSimplex = Simplex.equalSidesAlongAxes(dim, 1d);
        for (PointValuePair pv : initSimplex.asList()) {
            Assert.assertTrue(Double.isNaN(pv.getValue()));
        }

        final int nRepl = 3;
        final double value = 12.345;
        final List<PointValuePair> replace = new ArrayList<>();
        for (int i = 0; i < nRepl; i++) {
            replace.add(new PointValuePair(new double[dim], value));
        }

        final Simplex newSimplex = initSimplex.replaceLast(replace);
        Assert.assertEquals(nPoints, newSimplex.getSize());

        final int from = nPoints - nRepl;
        for (int i = 0; i < from; i++) {
            Assert.assertTrue(Double.isNaN(newSimplex.get(i).getValue()));
        }
        for (int i = from; i < nPoints; i++) {
            Assert.assertEquals(value, newSimplex.get(i).getValue(), 0d);
        }
    }

    @Test
    public void testReplaceLast2() {
        final int dim = 7;
        final int nPoints = dim + 1;
        final Simplex initSimplex = Simplex.equalSidesAlongAxes(dim, 1d);
        for (PointValuePair pv : initSimplex.asList()) {
            Assert.assertTrue(Double.isNaN(pv.getValue()));
        }

        final double value = 12.345;
        final PointValuePair replace = new PointValuePair(new double[dim], value);
        final Simplex newSimplex = initSimplex.replaceLast(replace);
        Assert.assertEquals(nPoints, newSimplex.getSize());

        final int from = nPoints - 1;
        for (int i = 0; i < from; i++) {
            Assert.assertTrue(Double.isNaN(newSimplex.get(i).getValue()));
        }
        for (int i = from; i < nPoints; i++) {
            Assert.assertEquals(value, newSimplex.get(i).getValue(), 0d);
        }
    }

    @Test
    public void testNewPoint() {
        final double[] a = new double[] { 1, 2 };
        final double[] b = new double[] { 3, -1 };
        final double s = 7;

        final MultivariateFunction f = x -> {
            double v = 0;
            for (int i = 0; i < x.length; i++) {
                v += x[i] * x[i];
            }
            return v;
        };

        final double[] eP = new double[] { 15, -19 };
        final double eV = 586;

        final PointValuePair pv = Simplex.newPoint(a, s, b, f);
        Assert.assertArrayEquals(eP, pv.getPoint(), 0.0);
        Assert.assertEquals(eV, pv.getValue(), 0d);
    }
}
