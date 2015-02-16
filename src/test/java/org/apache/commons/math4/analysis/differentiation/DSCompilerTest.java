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

package org.apache.commons.math4.analysis.differentiation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math4.analysis.differentiation.DSCompiler;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.util.CombinatoricsUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for class {@link DSCompiler}.
 */
public class DSCompilerTest {

    @Test
    public void testSize() {
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 6; ++j) {
                long expected = CombinatoricsUtils.binomialCoefficient(i + j, i);
                Assert.assertEquals(expected, DSCompiler.getCompiler(i, j).getSize());
                Assert.assertEquals(expected, DSCompiler.getCompiler(j, i).getSize());
            }
        }
    }

    @Test
    public void testIndices() {

        DSCompiler c = DSCompiler.getCompiler(0, 0);
        checkIndices(c.getPartialDerivativeOrders(0), new int[0]);

        c = DSCompiler.getCompiler(0, 1);
        checkIndices(c.getPartialDerivativeOrders(0), new int[0]);

        c = DSCompiler.getCompiler(1, 0);
        checkIndices(c.getPartialDerivativeOrders(0), 0);

        c = DSCompiler.getCompiler(1, 1);
        checkIndices(c.getPartialDerivativeOrders(0), 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1);

        c = DSCompiler.getCompiler(1, 2);
        checkIndices(c.getPartialDerivativeOrders(0), 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1);
        checkIndices(c.getPartialDerivativeOrders(2), 2);

        c = DSCompiler.getCompiler(2, 1);
        checkIndices(c.getPartialDerivativeOrders(0), 0, 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1, 0);
        checkIndices(c.getPartialDerivativeOrders(2), 0, 1);

        c = DSCompiler.getCompiler(1, 3);
        checkIndices(c.getPartialDerivativeOrders(0), 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1);
        checkIndices(c.getPartialDerivativeOrders(2), 2);
        checkIndices(c.getPartialDerivativeOrders(3), 3);

        c = DSCompiler.getCompiler(2, 2);
        checkIndices(c.getPartialDerivativeOrders(0), 0, 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1, 0);
        checkIndices(c.getPartialDerivativeOrders(2), 2, 0);
        checkIndices(c.getPartialDerivativeOrders(3), 0, 1);
        checkIndices(c.getPartialDerivativeOrders(4), 1, 1);
        checkIndices(c.getPartialDerivativeOrders(5), 0, 2);

        c = DSCompiler.getCompiler(3, 1);
        checkIndices(c.getPartialDerivativeOrders(0), 0, 0, 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1, 0, 0);
        checkIndices(c.getPartialDerivativeOrders(2), 0, 1, 0);
        checkIndices(c.getPartialDerivativeOrders(3), 0, 0, 1);

        c = DSCompiler.getCompiler(1, 4);
        checkIndices(c.getPartialDerivativeOrders(0), 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1);
        checkIndices(c.getPartialDerivativeOrders(2), 2);
        checkIndices(c.getPartialDerivativeOrders(3), 3);
        checkIndices(c.getPartialDerivativeOrders(4), 4);

        c = DSCompiler.getCompiler(2, 3);
        checkIndices(c.getPartialDerivativeOrders(0), 0, 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1, 0);
        checkIndices(c.getPartialDerivativeOrders(2), 2, 0);
        checkIndices(c.getPartialDerivativeOrders(3), 3, 0);
        checkIndices(c.getPartialDerivativeOrders(4), 0, 1);
        checkIndices(c.getPartialDerivativeOrders(5), 1, 1);
        checkIndices(c.getPartialDerivativeOrders(6), 2, 1);
        checkIndices(c.getPartialDerivativeOrders(7), 0, 2);
        checkIndices(c.getPartialDerivativeOrders(8), 1, 2);
        checkIndices(c.getPartialDerivativeOrders(9), 0, 3);

        c = DSCompiler.getCompiler(3, 2);
        checkIndices(c.getPartialDerivativeOrders(0), 0, 0, 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1, 0, 0);
        checkIndices(c.getPartialDerivativeOrders(2), 2, 0, 0);
        checkIndices(c.getPartialDerivativeOrders(3), 0, 1, 0);
        checkIndices(c.getPartialDerivativeOrders(4), 1, 1, 0);
        checkIndices(c.getPartialDerivativeOrders(5), 0, 2, 0);
        checkIndices(c.getPartialDerivativeOrders(6), 0, 0, 1);
        checkIndices(c.getPartialDerivativeOrders(7), 1, 0, 1);
        checkIndices(c.getPartialDerivativeOrders(8), 0, 1, 1);
        checkIndices(c.getPartialDerivativeOrders(9), 0, 0, 2);

        c = DSCompiler.getCompiler(4, 1);
        checkIndices(c.getPartialDerivativeOrders(0), 0, 0, 0, 0);
        checkIndices(c.getPartialDerivativeOrders(1), 1, 0, 0, 0);
        checkIndices(c.getPartialDerivativeOrders(2), 0, 1, 0, 0);
        checkIndices(c.getPartialDerivativeOrders(3), 0, 0, 1, 0);
        checkIndices(c.getPartialDerivativeOrders(4), 0, 0, 0, 1);

    }

    @Test(expected=DimensionMismatchException.class)
    public void testIncompatibleParams() {
        DSCompiler.getCompiler(3, 2).checkCompatibility(DSCompiler.getCompiler(4, 2));
    }

    @Test(expected=DimensionMismatchException.class)
    public void testIncompatibleOrder() {
        DSCompiler.getCompiler(3, 3).checkCompatibility(DSCompiler.getCompiler(3, 2));
    }

    @Test
    public void testSymmetry() {
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 6; ++j) {
                DSCompiler c = DSCompiler.getCompiler(i, j);
                for (int k = 0; k < c.getSize(); ++k) {
                    Assert.assertEquals(k, c.getPartialDerivativeIndex(c.getPartialDerivativeOrders(k)));
                }
            }
        }
    }

    @Test public void testMultiplicationRules()
        throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        Map<String,String> referenceRules = new HashMap<String, String>();
        referenceRules.put("(f*g)",          "f * g");
        referenceRules.put("d(f*g)/dx",      "f * dg/dx + df/dx * g");
        referenceRules.put("d(f*g)/dy",      referenceRules.get("d(f*g)/dx").replaceAll("x", "y"));
        referenceRules.put("d(f*g)/dz",      referenceRules.get("d(f*g)/dx").replaceAll("x", "z"));
        referenceRules.put("d(f*g)/dt",      referenceRules.get("d(f*g)/dx").replaceAll("x", "t"));
        referenceRules.put("d2(f*g)/dx2",    "f * d2g/dx2 + 2 * df/dx * dg/dx + d2f/dx2 * g");
        referenceRules.put("d2(f*g)/dy2",    referenceRules.get("d2(f*g)/dx2").replaceAll("x", "y"));
        referenceRules.put("d2(f*g)/dz2",    referenceRules.get("d2(f*g)/dx2").replaceAll("x", "z"));
        referenceRules.put("d2(f*g)/dt2",    referenceRules.get("d2(f*g)/dx2").replaceAll("x", "t"));
        referenceRules.put("d2(f*g)/dxdy",   "f * d2g/dxdy + df/dy * dg/dx + df/dx * dg/dy + d2f/dxdy * g");
        referenceRules.put("d2(f*g)/dxdz",   referenceRules.get("d2(f*g)/dxdy").replaceAll("y", "z"));
        referenceRules.put("d2(f*g)/dxdt",   referenceRules.get("d2(f*g)/dxdy").replaceAll("y", "t"));
        referenceRules.put("d2(f*g)/dydz",   referenceRules.get("d2(f*g)/dxdz").replaceAll("x", "y"));
        referenceRules.put("d2(f*g)/dydt",   referenceRules.get("d2(f*g)/dxdt").replaceAll("x", "y"));
        referenceRules.put("d2(f*g)/dzdt",   referenceRules.get("d2(f*g)/dxdt").replaceAll("x", "z"));
        referenceRules.put("d3(f*g)/dx3",    "f * d3g/dx3 +" +
                                             " 3 * df/dx * d2g/dx2 +" +
                                             " 3 * d2f/dx2 * dg/dx +" +
                                             " d3f/dx3 * g");
        referenceRules.put("d3(f*g)/dy3",   referenceRules.get("d3(f*g)/dx3").replaceAll("x", "y"));
        referenceRules.put("d3(f*g)/dz3",   referenceRules.get("d3(f*g)/dx3").replaceAll("x", "z"));
        referenceRules.put("d3(f*g)/dt3",   referenceRules.get("d3(f*g)/dx3").replaceAll("x", "t"));
        referenceRules.put("d3(f*g)/dx2dy",  "f * d3g/dx2dy +" +
                                             " df/dy * d2g/dx2 +" +
                                             " 2 * df/dx * d2g/dxdy +" +
                                             " 2 * d2f/dxdy * dg/dx +" +
                                             " d2f/dx2 * dg/dy +" +
                                             " d3f/dx2dy * g");
        referenceRules.put("d3(f*g)/dxdy2",  "f * d3g/dxdy2 +" +
                                             " 2 * df/dy * d2g/dxdy +" +
                                             " d2f/dy2 * dg/dx +" +
                                             " df/dx * d2g/dy2 +" +
                                             " 2 * d2f/dxdy * dg/dy +" +
                                             " d3f/dxdy2 * g");
        referenceRules.put("d3(f*g)/dx2dz",   referenceRules.get("d3(f*g)/dx2dy").replaceAll("y", "z"));
        referenceRules.put("d3(f*g)/dy2dz",   referenceRules.get("d3(f*g)/dx2dz").replaceAll("x", "y"));
        referenceRules.put("d3(f*g)/dxdz2",   referenceRules.get("d3(f*g)/dxdy2").replaceAll("y", "z"));
        referenceRules.put("d3(f*g)/dydz2",   referenceRules.get("d3(f*g)/dxdz2").replaceAll("x", "y"));
        referenceRules.put("d3(f*g)/dx2dt",   referenceRules.get("d3(f*g)/dx2dz").replaceAll("z", "t"));
        referenceRules.put("d3(f*g)/dy2dt",   referenceRules.get("d3(f*g)/dx2dt").replaceAll("x", "y"));
        referenceRules.put("d3(f*g)/dz2dt",   referenceRules.get("d3(f*g)/dx2dt").replaceAll("x", "z"));
        referenceRules.put("d3(f*g)/dxdt2",   referenceRules.get("d3(f*g)/dxdy2").replaceAll("y", "t"));
        referenceRules.put("d3(f*g)/dydt2",   referenceRules.get("d3(f*g)/dxdt2").replaceAll("x", "y"));
        referenceRules.put("d3(f*g)/dzdt2",   referenceRules.get("d3(f*g)/dxdt2").replaceAll("x", "z"));
        referenceRules.put("d3(f*g)/dxdydz", "f * d3g/dxdydz +" +
                                             " df/dz * d2g/dxdy +" +
                                             " df/dy * d2g/dxdz +" +
                                             " d2f/dydz * dg/dx +" +
                                             " df/dx * d2g/dydz +" +
                                             " d2f/dxdz * dg/dy +" +
                                             " d2f/dxdy * dg/dz +" +
                                             " d3f/dxdydz * g");
        referenceRules.put("d3(f*g)/dxdydt", referenceRules.get("d3(f*g)/dxdydz").replaceAll("z", "t"));
        referenceRules.put("d3(f*g)/dxdzdt", referenceRules.get("d3(f*g)/dxdydt").replaceAll("y", "z"));
        referenceRules.put("d3(f*g)/dydzdt", referenceRules.get("d3(f*g)/dxdzdt").replaceAll("x", "y"));

        Field multFieldArrayField = DSCompiler.class.getDeclaredField("multIndirection");
        multFieldArrayField.setAccessible(true);
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 4; ++j) {
                DSCompiler compiler = DSCompiler.getCompiler(i, j);
                int[][][] multIndirection = (int[][][]) multFieldArrayField.get(compiler);
                for (int k = 0; k < multIndirection.length; ++k) {
                    String product = ordersToString(compiler.getPartialDerivativeOrders(k),
                                                    "(f*g)", "x", "y", "z", "t");
                    StringBuilder rule = new StringBuilder();
                    for (int[] term : multIndirection[k]) {
                        if (rule.length() > 0) {
                            rule.append(" + ");
                        }
                        if (term[0] > 1) {
                            rule.append(term[0]).append(" * ");
                        }
                        rule.append(ordersToString(compiler.getPartialDerivativeOrders(term[1]),
                                                   "f", "x", "y", "z", "t"));
                        rule.append(" * ");
                        rule.append(ordersToString(compiler.getPartialDerivativeOrders(term[2]),
                                                   "g", "x", "y", "z", "t"));
                    }
                    Assert.assertEquals(product, referenceRules.get(product), rule.toString());
                }
            }
        }
    }

    @Test public void testCompositionRules()
        throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        // the following reference rules have all been computed independently from the library,
        // using only pencil and paper and some search and replace to handle symmetries
        Map<String,String> referenceRules = new HashMap<String, String>();
        referenceRules.put("(f(g))",              "(f(g))");
        referenceRules.put("d(f(g))/dx",          "d(f(g))/dg * dg/dx");
        referenceRules.put("d(f(g))/dy",          referenceRules.get("d(f(g))/dx").replaceAll("x", "y"));
        referenceRules.put("d(f(g))/dz",          referenceRules.get("d(f(g))/dx").replaceAll("x", "z"));
        referenceRules.put("d(f(g))/dt",          referenceRules.get("d(f(g))/dx").replaceAll("x", "t"));
        referenceRules.put("d2(f(g))/dx2",        "d2(f(g))/dg2 * dg/dx * dg/dx + d(f(g))/dg * d2g/dx2");
        referenceRules.put("d2(f(g))/dy2",        referenceRules.get("d2(f(g))/dx2").replaceAll("x", "y"));
        referenceRules.put("d2(f(g))/dz2",        referenceRules.get("d2(f(g))/dx2").replaceAll("x", "z"));
        referenceRules.put("d2(f(g))/dt2",        referenceRules.get("d2(f(g))/dx2").replaceAll("x", "t"));
        referenceRules.put("d2(f(g))/dxdy",       "d2(f(g))/dg2 * dg/dx * dg/dy + d(f(g))/dg * d2g/dxdy");
        referenceRules.put("d2(f(g))/dxdz",       referenceRules.get("d2(f(g))/dxdy").replaceAll("y", "z"));
        referenceRules.put("d2(f(g))/dxdt",       referenceRules.get("d2(f(g))/dxdy").replaceAll("y", "t"));
        referenceRules.put("d2(f(g))/dydz",       referenceRules.get("d2(f(g))/dxdz").replaceAll("x", "y"));
        referenceRules.put("d2(f(g))/dydt",       referenceRules.get("d2(f(g))/dxdt").replaceAll("x", "y"));
        referenceRules.put("d2(f(g))/dzdt",       referenceRules.get("d2(f(g))/dxdt").replaceAll("x", "z"));
        referenceRules.put("d3(f(g))/dx3",        "d3(f(g))/dg3 * dg/dx * dg/dx * dg/dx +" +
                                                  " 3 * d2(f(g))/dg2 * dg/dx * d2g/dx2 +" +
                                                  " d(f(g))/dg * d3g/dx3");
        referenceRules.put("d3(f(g))/dy3",        referenceRules.get("d3(f(g))/dx3").replaceAll("x", "y"));
        referenceRules.put("d3(f(g))/dz3",        referenceRules.get("d3(f(g))/dx3").replaceAll("x", "z"));
        referenceRules.put("d3(f(g))/dt3",        referenceRules.get("d3(f(g))/dx3").replaceAll("x", "t"));
        referenceRules.put("d3(f(g))/dxdy2",      "d3(f(g))/dg3 * dg/dx * dg/dy * dg/dy +" +
                                                  " 2 * d2(f(g))/dg2 * dg/dy * d2g/dxdy +" +
                                                  " d2(f(g))/dg2 * dg/dx * d2g/dy2 +" +
                                                  " d(f(g))/dg * d3g/dxdy2");
        referenceRules.put("d3(f(g))/dxdz2",      referenceRules.get("d3(f(g))/dxdy2").replaceAll("y", "z"));
        referenceRules.put("d3(f(g))/dxdt2",      referenceRules.get("d3(f(g))/dxdy2").replaceAll("y", "t"));
        referenceRules.put("d3(f(g))/dydz2",      referenceRules.get("d3(f(g))/dxdz2").replaceAll("x", "y"));
        referenceRules.put("d3(f(g))/dydt2",      referenceRules.get("d3(f(g))/dxdt2").replaceAll("x", "y"));
        referenceRules.put("d3(f(g))/dzdt2",      referenceRules.get("d3(f(g))/dxdt2").replaceAll("x", "z"));
        referenceRules.put("d3(f(g))/dx2dy",      "d3(f(g))/dg3 * dg/dx * dg/dx * dg/dy +" +
                                                  " 2 * d2(f(g))/dg2 * dg/dx * d2g/dxdy +" +
                                                  " d2(f(g))/dg2 * d2g/dx2 * dg/dy +" +
                                                  " d(f(g))/dg * d3g/dx2dy");
        referenceRules.put("d3(f(g))/dx2dz",      referenceRules.get("d3(f(g))/dx2dy").replaceAll("y", "z"));
        referenceRules.put("d3(f(g))/dx2dt",      referenceRules.get("d3(f(g))/dx2dy").replaceAll("y", "t"));
        referenceRules.put("d3(f(g))/dy2dz",      referenceRules.get("d3(f(g))/dx2dz").replaceAll("x", "y"));
        referenceRules.put("d3(f(g))/dy2dt",      referenceRules.get("d3(f(g))/dx2dt").replaceAll("x", "y"));
        referenceRules.put("d3(f(g))/dz2dt",      referenceRules.get("d3(f(g))/dx2dt").replaceAll("x", "z"));
        referenceRules.put("d3(f(g))/dxdydz",     "d3(f(g))/dg3 * dg/dx * dg/dy * dg/dz +" +
                                                  " d2(f(g))/dg2 * dg/dy * d2g/dxdz +" +
                                                  " d2(f(g))/dg2 * dg/dx * d2g/dydz +" +
                                                  " d2(f(g))/dg2 * d2g/dxdy * dg/dz +" +
                                                  " d(f(g))/dg * d3g/dxdydz");
        referenceRules.put("d3(f(g))/dxdydt",     referenceRules.get("d3(f(g))/dxdydz").replaceAll("z", "t"));
        referenceRules.put("d3(f(g))/dxdzdt",     referenceRules.get("d3(f(g))/dxdydt").replaceAll("y", "z"));
        referenceRules.put("d3(f(g))/dydzdt",     referenceRules.get("d3(f(g))/dxdzdt").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dx4",        "d4(f(g))/dg4 * dg/dx * dg/dx * dg/dx * dg/dx +" +
                                                  " 6 * d3(f(g))/dg3 * dg/dx * dg/dx * d2g/dx2 +" +
                                                  " 3 * d2(f(g))/dg2 * d2g/dx2 * d2g/dx2 +" +
                                                  " 4 * d2(f(g))/dg2 * dg/dx * d3g/dx3 +" +
                                                  " d(f(g))/dg * d4g/dx4");
        referenceRules.put("d4(f(g))/dy4",        referenceRules.get("d4(f(g))/dx4").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dz4",        referenceRules.get("d4(f(g))/dx4").replaceAll("x", "z"));
        referenceRules.put("d4(f(g))/dt4",        referenceRules.get("d4(f(g))/dx4").replaceAll("x", "t"));
        referenceRules.put("d4(f(g))/dx3dy",      "d4(f(g))/dg4 * dg/dx * dg/dx * dg/dx * dg/dy +" +
                                                  " 3 * d3(f(g))/dg3 * dg/dx * dg/dx * d2g/dxdy +" +
                                                  " 3 * d3(f(g))/dg3 * dg/dx * d2g/dx2 * dg/dy +" +
                                                  " 3 * d2(f(g))/dg2 * d2g/dx2 * d2g/dxdy +" +
                                                  " 3 * d2(f(g))/dg2 * dg/dx * d3g/dx2dy +" +
                                                  " d2(f(g))/dg2 * d3g/dx3 * dg/dy +" +
                                                  " d(f(g))/dg * d4g/dx3dy");
        referenceRules.put("d4(f(g))/dx3dz",      referenceRules.get("d4(f(g))/dx3dy").replaceAll("y", "z"));
        referenceRules.put("d4(f(g))/dx3dt",      referenceRules.get("d4(f(g))/dx3dy").replaceAll("y", "t"));
        referenceRules.put("d4(f(g))/dxdy3",      "d4(f(g))/dg4 * dg/dx * dg/dy * dg/dy * dg/dy +" +
                                                  " 3 * d3(f(g))/dg3 * dg/dy * dg/dy * d2g/dxdy +" +
                                                  " 3 * d3(f(g))/dg3 * dg/dx * dg/dy * d2g/dy2 +" +
                                                  " 3 * d2(f(g))/dg2 * d2g/dxdy * d2g/dy2 +" +
                                                  " 3 * d2(f(g))/dg2 * dg/dy * d3g/dxdy2 +" +
                                                  " d2(f(g))/dg2 * dg/dx * d3g/dy3 +" +
                                                  " d(f(g))/dg * d4g/dxdy3");
        referenceRules.put("d4(f(g))/dxdz3",      referenceRules.get("d4(f(g))/dxdy3").replaceAll("y", "z"));
        referenceRules.put("d4(f(g))/dxdt3",      referenceRules.get("d4(f(g))/dxdy3").replaceAll("y", "t"));
        referenceRules.put("d4(f(g))/dy3dz",      referenceRules.get("d4(f(g))/dx3dz").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dy3dt",      referenceRules.get("d4(f(g))/dx3dt").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dydz3",      referenceRules.get("d4(f(g))/dxdz3").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dydt3",      referenceRules.get("d4(f(g))/dxdt3").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dz3dt",      referenceRules.get("d4(f(g))/dx3dt").replaceAll("x", "z"));
        referenceRules.put("d4(f(g))/dzdt3",      referenceRules.get("d4(f(g))/dxdt3").replaceAll("x", "z"));
        referenceRules.put("d4(f(g))/dx2dy2",     "d4(f(g))/dg4 * dg/dx * dg/dx * dg/dy * dg/dy +" +
                                                  " 4 * d3(f(g))/dg3 * dg/dx * dg/dy * d2g/dxdy +" +
                                                  " d3(f(g))/dg3 * dg/dx * dg/dx * d2g/dy2 +" +
                                                  " 2 * d2(f(g))/dg2 * d2g/dxdy * d2g/dxdy +" +
                                                  " 2 * d2(f(g))/dg2 * dg/dx * d3g/dxdy2 +" +
                                                  " d3(f(g))/dg3 * d2g/dx2 * dg/dy * dg/dy +" +
                                                  " 2 * d2(f(g))/dg2 * dg/dy * d3g/dx2dy +" +
                                                  " d2(f(g))/dg2 * d2g/dx2 * d2g/dy2 +" +
                                                  " d(f(g))/dg * d4g/dx2dy2");
        referenceRules.put("d4(f(g))/dx2dz2",     referenceRules.get("d4(f(g))/dx2dy2").replaceAll("y", "z"));
        referenceRules.put("d4(f(g))/dx2dt2",     referenceRules.get("d4(f(g))/dx2dy2").replaceAll("y", "t"));
        referenceRules.put("d4(f(g))/dy2dz2",     referenceRules.get("d4(f(g))/dx2dz2").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dy2dt2",     referenceRules.get("d4(f(g))/dx2dt2").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dz2dt2",     referenceRules.get("d4(f(g))/dx2dt2").replaceAll("x", "z"));

        referenceRules.put("d4(f(g))/dx2dydz",    "d4(f(g))/dg4 * dg/dx * dg/dx * dg/dy * dg/dz +" +
                                                  " 2 * d3(f(g))/dg3 * dg/dx * dg/dy * d2g/dxdz +" +
                                                  " d3(f(g))/dg3 * dg/dx * dg/dx * d2g/dydz +" +
                                                  " 2 * d3(f(g))/dg3 * dg/dx * d2g/dxdy * dg/dz +" +
                                                  " 2 * d2(f(g))/dg2 * d2g/dxdy * d2g/dxdz +" +
                                                  " 2 * d2(f(g))/dg2 * dg/dx * d3g/dxdydz +" +
                                                  " d3(f(g))/dg3 * d2g/dx2 * dg/dy * dg/dz +" +
                                                  " d2(f(g))/dg2 * dg/dy * d3g/dx2dz +" +
                                                  " d2(f(g))/dg2 * d2g/dx2 * d2g/dydz +" +
                                                  " d2(f(g))/dg2 * d3g/dx2dy * dg/dz +" +
                                                  " d(f(g))/dg * d4g/dx2dydz");
        referenceRules.put("d4(f(g))/dx2dydt",    referenceRules.get("d4(f(g))/dx2dydz").replaceAll("z", "t"));
        referenceRules.put("d4(f(g))/dx2dzdt",    referenceRules.get("d4(f(g))/dx2dydt").replaceAll("y", "z"));
        referenceRules.put("d4(f(g))/dxdy2dz",    "d4(f(g))/dg4 * dg/dx * dg/dy * dg/dy * dg/dz +" +
                                                  " d3(f(g))/dg3 * dg/dy * dg/dy * d2g/dxdz +" +
                                                  " 2 * d3(f(g))/dg3 * dg/dx * dg/dy * d2g/dydz +" +
                                                  " 2 * d3(f(g))/dg3 * dg/dy * d2g/dxdy * dg/dz +" +
                                                  " 2 * d2(f(g))/dg2 * d2g/dxdy * d2g/dydz +" +
                                                  " 2 * d2(f(g))/dg2 * dg/dy * d3g/dxdydz +" +
                                                  " d3(f(g))/dg3 * dg/dx * d2g/dy2 * dg/dz +" +
                                                  " d2(f(g))/dg2 * d2g/dy2 * d2g/dxdz +" +
                                                  " d2(f(g))/dg2 * dg/dx * d3g/dy2dz +" +
                                                  " d2(f(g))/dg2 * d3g/dxdy2 * dg/dz +" +
                                                  " d(f(g))/dg * d4g/dxdy2dz");
        referenceRules.put("d4(f(g))/dxdy2dt",    referenceRules.get("d4(f(g))/dxdy2dz").replaceAll("z", "t"));
        referenceRules.put("d4(f(g))/dy2dzdt",    referenceRules.get("d4(f(g))/dx2dzdt").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dxdydz2",    "d4(f(g))/dg4 * dg/dx * dg/dy * dg/dz * dg/dz +" +
                                                  " 2 * d3(f(g))/dg3 * dg/dy * dg/dz * d2g/dxdz +" +
                                                  " 2 * d3(f(g))/dg3 * dg/dx * dg/dz * d2g/dydz +" +
                                                  " d3(f(g))/dg3 * dg/dx * dg/dy * d2g/dz2 +" +
                                                  " 2 * d2(f(g))/dg2 * d2g/dxdz * d2g/dydz +" +
                                                  " d2(f(g))/dg2 * dg/dy * d3g/dxdz2 +" +
                                                  " d2(f(g))/dg2 * dg/dx * d3g/dydz2 +" +
                                                  " d3(f(g))/dg3 * d2g/dxdy * dg/dz * dg/dz +" +
                                                  " 2 * d2(f(g))/dg2 * dg/dz * d3g/dxdydz +" +
                                                  " d2(f(g))/dg2 * d2g/dxdy * d2g/dz2 +" +
                                                  " d(f(g))/dg * d4g/dxdydz2");
        referenceRules.put("d4(f(g))/dxdz2dt",    referenceRules.get("d4(f(g))/dxdy2dt").replaceAll("y", "z"));
        referenceRules.put("d4(f(g))/dydz2dt",    referenceRules.get("d4(f(g))/dxdz2dt").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dxdydt2",    referenceRules.get("d4(f(g))/dxdydz2").replaceAll("z", "t"));
        referenceRules.put("d4(f(g))/dxdzdt2",    referenceRules.get("d4(f(g))/dxdydt2").replaceAll("y", "z"));
        referenceRules.put("d4(f(g))/dydzdt2",    referenceRules.get("d4(f(g))/dxdzdt2").replaceAll("x", "y"));
        referenceRules.put("d4(f(g))/dxdydzdt",   "d4(f(g))/dg4 * dg/dx * dg/dy * dg/dz * dg/dt +" +
                                                  " d3(f(g))/dg3 * dg/dy * dg/dz * d2g/dxdt +" +
                                                  " d3(f(g))/dg3 * dg/dx * dg/dz * d2g/dydt +" +
                                                  " d3(f(g))/dg3 * dg/dx * dg/dy * d2g/dzdt +" +
                                                  " d3(f(g))/dg3 * dg/dy * d2g/dxdz * dg/dt +" +
                                                  " d2(f(g))/dg2 * d2g/dxdz * d2g/dydt +" +
                                                  " d2(f(g))/dg2 * dg/dy * d3g/dxdzdt +" +
                                                  " d3(f(g))/dg3 * dg/dx * d2g/dydz * dg/dt +" +
                                                  " d2(f(g))/dg2 * d2g/dydz * d2g/dxdt +" +
                                                  " d2(f(g))/dg2 * dg/dx * d3g/dydzdt +" +
                                                  " d3(f(g))/dg3 * d2g/dxdy * dg/dz * dg/dt +" +
                                                  " d2(f(g))/dg2 * dg/dz * d3g/dxdydt +" +
                                                  " d2(f(g))/dg2 * d2g/dxdy * d2g/dzdt +" +
                                                  " d2(f(g))/dg2 * d3g/dxdydz * dg/dt +" +
                                                  " d(f(g))/dg * d4g/dxdydzdt");

        Field compFieldArrayField = DSCompiler.class.getDeclaredField("compIndirection");
        compFieldArrayField.setAccessible(true);
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                DSCompiler compiler = DSCompiler.getCompiler(i, j);
                int[][][] compIndirection = (int[][][]) compFieldArrayField.get(compiler);
                for (int k = 0; k < compIndirection.length; ++k) {
                    String product = ordersToString(compiler.getPartialDerivativeOrders(k),
                                                    "(f(g))", "x", "y", "z", "t");
                    StringBuilder rule = new StringBuilder();
                    for (int[] term : compIndirection[k]) {
                        if (rule.length() > 0) {
                            rule.append(" + ");
                        }
                        if (term[0] > 1) {
                            rule.append(term[0]).append(" * ");
                        }
                        rule.append(orderToString(term[1], "(f(g))", "g"));
                        for (int l = 2; l < term.length; ++l) {
                            rule.append(" * ");
                            rule.append(ordersToString(compiler.getPartialDerivativeOrders(term[l]),
                                                       "g", "x", "y", "z", "t"));
                        }
                    }
                    Assert.assertEquals(product, referenceRules.get(product), rule.toString());
                }
            }
        }
    }

    private void checkIndices(int[] indices, int ... expected) {
        Assert.assertEquals(expected.length, indices.length);
        for (int i = 0; i < expected.length; ++i) {
            Assert.assertEquals(expected[i], indices[i]);
        }
    }

    private String orderToString(int order, String functionName, String parameterName) {
        if (order == 0) {
            return functionName;
        } else if (order == 1) {
            return "d" + functionName + "/d" + parameterName;
        } else {
            return "d" + order + functionName + "/d" + parameterName + order;
        }
    }

    private String ordersToString(int[] orders, String functionName, String ... parametersNames) {

        int sumOrders = 0;
        for (int order : orders) {
            sumOrders += order;
        }

        if (sumOrders == 0) {
            return functionName;
        }

        StringBuilder builder = new StringBuilder();
        builder.append('d');
        if (sumOrders > 1) {
            builder.append(sumOrders);
        }
        builder.append(functionName).append('/');
        for (int i = 0; i < orders.length; ++i) {
            if (orders[i] > 0) {
                builder.append('d').append(parametersNames[i]);
                if (orders[i] > 1) {
                    builder.append(orders[i]);
                }
            }
        }
        return builder.toString();

    }

}
