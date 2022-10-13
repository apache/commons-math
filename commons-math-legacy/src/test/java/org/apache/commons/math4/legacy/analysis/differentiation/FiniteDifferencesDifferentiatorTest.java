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

package org.apache.commons.math4.legacy.analysis.differentiation;

import org.apache.commons.math4.legacy.analysis.QuinticFunction;
import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.analysis.UnivariateMatrixFunction;
import org.apache.commons.math4.legacy.analysis.UnivariateVectorFunction;
import org.apache.commons.math4.legacy.analysis.function.Gaussian;
import org.apache.commons.math4.legacy.analysis.function.Sin;
import org.apache.commons.math4.legacy.exception.MathInternalError;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for class {@link FiniteDifferencesDifferentiator}.
 */
public class FiniteDifferencesDifferentiatorTest {

    @Test(expected=NumberIsTooSmallException.class)
    public void testWrongNumberOfPoints() {
        new FiniteDifferencesDifferentiator(1, 1.0);
    }

    @Test(expected=NotPositiveException.class)
    public void testWrongStepSize() {
        new FiniteDifferencesDifferentiator(3, 0.0);
    }

    @Test
    public void testConstant() {
        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(5, 0.01);
        UnivariateDifferentiableFunction f =
                differentiator.differentiate(new UnivariateFunction() {
                    @Override
                    public double value(double x) {
                        return 42.0;
                    }
                });
        for (double x = -10; x < 10; x += 0.1) {
            DerivativeStructure y = f.value(new DerivativeStructure(1, 2, 0, x));
            Assert.assertEquals(42.0, y.getValue(), 1.0e-15);
            Assert.assertEquals( 0.0, y.getPartialDerivative(1), 1.0e-15);
            Assert.assertEquals( 0.0, y.getPartialDerivative(2), 1.0e-15);
        }
    }

    @Test
    public void testLinear() {
        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(5, 0.01);
        UnivariateDifferentiableFunction f =
                differentiator.differentiate(new UnivariateFunction() {
                    @Override
                    public double value(double x) {
                        return 2 - 3 * x;
                    }
                });
        for (double x = -10; x < 10; x += 0.1) {
            DerivativeStructure y = f.value(new DerivativeStructure(1, 2, 0, x));
            Assert.assertEquals("" + (2 - 3 * x - y.getValue()), 2 - 3 * x, y.getValue(), 2.0e-15);
            Assert.assertEquals(-3.0, y.getPartialDerivative(1), 4.0e-13);
            Assert.assertEquals( 0.0, y.getPartialDerivative(2), 9.0e-11);
        }
    }

    @Test
    public void testGaussian() {
        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(9, 0.02);
        UnivariateDifferentiableFunction gaussian = new Gaussian(1.0, 2.0);
        UnivariateDifferentiableFunction f =
                differentiator.differentiate(gaussian);
        double[] expectedError = new double[] {
            6.939e-18, 1.284e-15, 2.477e-13, 1.168e-11, 2.840e-9, 7.971e-8
        };
       double[] maxError = new double[expectedError.length];
        for (double x = -10; x < 10; x += 0.1) {
            DerivativeStructure dsX  = new DerivativeStructure(1, maxError.length - 1, 0, x);
            DerivativeStructure yRef = gaussian.value(dsX);
            DerivativeStructure y    = f.value(dsX);
            Assert.assertEquals(f.value(dsX.getValue()), f.value(dsX).getValue(), 1.0e-15);
            for (int order = 0; order <= yRef.getOrder(); ++order) {
                maxError[order] = JdkMath.max(maxError[order],
                                        JdkMath.abs(yRef.getPartialDerivative(order) -
                                                     y.getPartialDerivative(order)));
            }
        }
        for (int i = 0; i < maxError.length; ++i) {
            Assert.assertEquals(expectedError[i], maxError[i], 0.01 * expectedError[i]);
        }
    }

    @Test
    public void testStepSizeUnstability() {
        UnivariateDifferentiableFunction quintic = new QuinticFunction();
        UnivariateDifferentiableFunction goodStep =
                new FiniteDifferencesDifferentiator(7, 0.25).differentiate(quintic);
        UnivariateDifferentiableFunction badStep =
                new FiniteDifferencesDifferentiator(7, 1.0e-6).differentiate(quintic);
        double[] maxErrorGood = new double[7];
        double[] maxErrorBad  = new double[7];
        for (double x = -10; x < 10; x += 0.1) {
            DerivativeStructure dsX  = new DerivativeStructure(1, 6, 0, x);
            DerivativeStructure yRef  = quintic.value(dsX);
            DerivativeStructure yGood = goodStep.value(dsX);
            DerivativeStructure yBad  = badStep.value(dsX);
            for (int order = 0; order <= 6; ++order) {
                maxErrorGood[order] = JdkMath.max(maxErrorGood[order],
                                                   JdkMath.abs(yRef.getPartialDerivative(order) -
                                                                yGood.getPartialDerivative(order)));
                maxErrorBad[order]  = JdkMath.max(maxErrorBad[order],
                                                   JdkMath.abs(yRef.getPartialDerivative(order) -
                                                                yBad.getPartialDerivative(order)));
            }
        }

        // the 0.25 step size is good for finite differences in the quintic on this abscissa range for 7 points
        // the errors are fair
        final double[] expectedGood = new double[] {
            7.276e-12, 7.276e-11, 9.968e-10, 3.092e-9, 5.432e-8, 8.196e-8, 1.818e-6
        };

        // the 1.0e-6 step size is far too small for finite differences in the quintic on this abscissa range for 7 points
        // the errors are huge!
        final double[] expectedBad = new double[] {
            2.910e-11, 2.087e-5, 147.7, 3.820e7, 6.354e14, 6.548e19, 1.543e27
        };

        for (int i = 0; i < maxErrorGood.length; ++i) {
            Assert.assertEquals(expectedGood[i], maxErrorGood[i], 0.01 * expectedGood[i]);
            Assert.assertEquals(expectedBad[i],  maxErrorBad[i],  0.01 * expectedBad[i]);
        }
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testWrongOrder() {
        UnivariateDifferentiableFunction f =
                new FiniteDifferencesDifferentiator(3, 0.01).differentiate(new UnivariateFunction() {
                    @Override
                    public double value(double x) {
                        // this exception should not be thrown because wrong order
                        // should be detected before function call
                        throw new MathInternalError();
                    }
                });
        f.value(new DerivativeStructure(1, 3, 0, 1.0));
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testWrongOrderVector() {
        UnivariateDifferentiableVectorFunction f =
                new FiniteDifferencesDifferentiator(3, 0.01).differentiate(new UnivariateVectorFunction() {
                    @Override
                    public double[] value(double x) {
                        // this exception should not be thrown because wrong order
                        // should be detected before function call
                        throw new MathInternalError();
                    }
                });
        f.value(new DerivativeStructure(1, 3, 0, 1.0));
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testWrongOrderMatrix() {
        UnivariateDifferentiableMatrixFunction f =
                new FiniteDifferencesDifferentiator(3, 0.01).differentiate(new UnivariateMatrixFunction() {
                    @Override
                    public double[][] value(double x) {
                        // this exception should not be thrown because wrong order
                        // should be detected before function call
                        throw new MathInternalError();
                    }
                });
        f.value(new DerivativeStructure(1, 3, 0, 1.0));
    }

    @Test(expected=NumberIsTooLargeException.class)
    public void testTooLargeStep() {
        new FiniteDifferencesDifferentiator(3, 2.5, 0.0, 1.0);
    }

    @Test
    public void testBounds() {

        final double slope = 2.5;
        UnivariateFunction f = new UnivariateFunction() {
            @Override
            public double value(double x) {
                if (x < 0) {
                    throw new NumberIsTooSmallException(x, 0, true);
                } else if (x > 1) {
                    throw new NumberIsTooLargeException(x, 1, true);
                } else {
                    return slope * x;
                }
            }
        };

        UnivariateDifferentiableFunction missingBounds =
                new FiniteDifferencesDifferentiator(3, 0.1).differentiate(f);
        UnivariateDifferentiableFunction properlyBounded =
                new FiniteDifferencesDifferentiator(3, 0.1, 0.0, 1.0).differentiate(f);
        DerivativeStructure tLow  = new DerivativeStructure(1, 1, 0, 0.05);
        DerivativeStructure tHigh = new DerivativeStructure(1, 1, 0, 0.95);

        try {
            // here, we did not set the bounds, so the differences are evaluated out of domain
            // using f(-0.05), f(0.05), f(0.15)
            missingBounds.value(tLow);
            Assert.fail("an exception should have been thrown");
        } catch (NumberIsTooSmallException nse) {
            Assert.assertEquals(-0.05, nse.getArgument().doubleValue(), 1.0e-10);
        } catch (Exception e) {
            Assert.fail("wrong exception caught: " + e.getClass().getName());
        }

        try {
            // here, we did not set the bounds, so the differences are evaluated out of domain
            // using f(0.85), f(0.95), f(1.05)
            missingBounds.value(tHigh);
            Assert.fail("an exception should have been thrown");
        } catch (NumberIsTooLargeException nle) {
            Assert.assertEquals(1.05, nle.getArgument().doubleValue(), 1.0e-10);
        } catch (Exception e) {
            Assert.fail("wrong exception caught: " + e.getClass().getName());
        }

        // here, we did set the bounds, so evaluations are done within domain
        // using f(0.0), f(0.1), f(0.2)
        Assert.assertEquals(slope, properlyBounded.value(tLow).getPartialDerivative(1), 1.0e-10);

        // here, we did set the bounds, so evaluations are done within domain
        // using f(0.8), f(0.9), f(1.0)
        Assert.assertEquals(slope, properlyBounded.value(tHigh).getPartialDerivative(1), 1.0e-10);
    }

    @Test
    public void testBoundedSqrt() {

        UnivariateFunctionDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(9, 1.0 / 32, 0.0, Double.POSITIVE_INFINITY);
        UnivariateDifferentiableFunction sqrt = differentiator.differentiate(new UnivariateFunction() {
            @Override
            public double value(double x) {
                return JdkMath.sqrt(x);
            }
        });

        // we are able to compute derivative near 0, but the accuracy is much poorer there
        DerivativeStructure t001 = new DerivativeStructure(1, 1, 0, 0.01);
        Assert.assertEquals(0.5 / JdkMath.sqrt(t001.getValue()), sqrt.value(t001).getPartialDerivative(1), 1.6);
        DerivativeStructure t01 = new DerivativeStructure(1, 1, 0, 0.1);
        Assert.assertEquals(0.5 / JdkMath.sqrt(t01.getValue()), sqrt.value(t01).getPartialDerivative(1), 7.0e-3);
        DerivativeStructure t03 = new DerivativeStructure(1, 1, 0, 0.3);
        Assert.assertEquals(0.5 / JdkMath.sqrt(t03.getValue()), sqrt.value(t03).getPartialDerivative(1), 2.1e-7);
    }

    @Test
    public void testVectorFunction() {

        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(7, 0.01);
        UnivariateDifferentiableVectorFunction f =
                differentiator.differentiate(new UnivariateVectorFunction() {

            @Override
            public double[] value(double x) {
                return new double[] { JdkMath.cos(x), JdkMath.sin(x) };
            }
        });

        for (double x = -10; x < 10; x += 0.1) {
            DerivativeStructure dsX = new DerivativeStructure(1, 2, 0, x);
            DerivativeStructure[] y = f.value(dsX);
            double cos = JdkMath.cos(x);
            double sin = JdkMath.sin(x);
            double[] f1 = f.value(dsX.getValue());
            DerivativeStructure[] f2 = f.value(dsX);
            Assert.assertEquals(f1.length, f2.length);
            for (int i = 0; i < f1.length; ++i) {
                Assert.assertEquals(f1[i], f2[i].getValue(), 1.0e-15);
            }
            Assert.assertEquals( cos, y[0].getValue(), 7.0e-16);
            Assert.assertEquals( sin, y[1].getValue(), 7.0e-16);
            Assert.assertEquals(-sin, y[0].getPartialDerivative(1), 6.0e-14);
            Assert.assertEquals( cos, y[1].getPartialDerivative(1), 6.0e-14);
            Assert.assertEquals(-cos, y[0].getPartialDerivative(2), 2.0e-11);
            Assert.assertEquals(-sin, y[1].getPartialDerivative(2), 2.0e-11);
        }
    }

    @Test
    public void testMatrixFunction() {

        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(7, 0.01);
        UnivariateDifferentiableMatrixFunction f =
                differentiator.differentiate(new UnivariateMatrixFunction() {

            @Override
            public double[][] value(double x) {
                return new double[][] {
                    { JdkMath.cos(x),  JdkMath.sin(x)  },
                    { JdkMath.cosh(x), JdkMath.sinh(x) }
                };
            }
        });

        for (double x = -1; x < 1; x += 0.02) {
            DerivativeStructure dsX = new DerivativeStructure(1, 2, 0, x);
            DerivativeStructure[][] y = f.value(dsX);
            double cos = JdkMath.cos(x);
            double sin = JdkMath.sin(x);
            double cosh = JdkMath.cosh(x);
            double sinh = JdkMath.sinh(x);
            double[][] f1 = f.value(dsX.getValue());
            DerivativeStructure[][] f2 = f.value(dsX);
            Assert.assertEquals(f1.length, f2.length);
            for (int i = 0; i < f1.length; ++i) {
                Assert.assertEquals(f1[i].length, f2[i].length);
                for (int j = 0; j < f1[i].length; ++j) {
                    Assert.assertEquals(f1[i][j], f2[i][j].getValue(), 1.0e-15);
                }
            }
            Assert.assertEquals(cos,   y[0][0].getValue(), 7.0e-18);
            Assert.assertEquals(sin,   y[0][1].getValue(), 6.0e-17);
            Assert.assertEquals(cosh,  y[1][0].getValue(), 3.0e-16);
            Assert.assertEquals(sinh,  y[1][1].getValue(), 3.0e-16);
            Assert.assertEquals(-sin,  y[0][0].getPartialDerivative(1), 2.0e-14);
            Assert.assertEquals( cos,  y[0][1].getPartialDerivative(1), 2.0e-14);
            Assert.assertEquals( sinh, y[1][0].getPartialDerivative(1), 3.0e-14);
            Assert.assertEquals( cosh, y[1][1].getPartialDerivative(1), 3.0e-14);
            Assert.assertEquals(-cos,  y[0][0].getPartialDerivative(2), 3.0e-12);
            Assert.assertEquals(-sin,  y[0][1].getPartialDerivative(2), 3.0e-12);
            Assert.assertEquals( cosh, y[1][0].getPartialDerivative(2), 6.0e-12);
            Assert.assertEquals( sinh, y[1][1].getPartialDerivative(2), 6.0e-12);
        }
    }

    @Test
    public void testSeveralFreeParameters() {
        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(5, 0.001);
        UnivariateDifferentiableFunction sine = new Sin();
        UnivariateDifferentiableFunction f =
                differentiator.differentiate(sine);
        double[] expectedError = new double[] {
            6.696e-16, 1.371e-12, 2.007e-8, 1.754e-5
        };
        double[] maxError = new double[expectedError.length];
       for (double x = -2; x < 2; x += 0.1) {
           for (double y = -2; y < 2; y += 0.1) {
               DerivativeStructure dsX  = new DerivativeStructure(2, maxError.length - 1, 0, x);
               DerivativeStructure dsY  = new DerivativeStructure(2, maxError.length - 1, 1, y);
               DerivativeStructure dsT  = dsX.multiply(3).subtract(dsY.multiply(2));
               DerivativeStructure sRef = sine.value(dsT);
               DerivativeStructure s    = f.value(dsT);
               for (int xOrder = 0; xOrder <= sRef.getOrder(); ++xOrder) {
                   for (int yOrder = 0; yOrder <= sRef.getOrder(); ++yOrder) {
                       if (xOrder + yOrder <= sRef.getOrder()) {
                           maxError[xOrder +yOrder] = JdkMath.max(maxError[xOrder + yOrder],
                                                                    JdkMath.abs(sRef.getPartialDerivative(xOrder, yOrder) -
                                                                                 s.getPartialDerivative(xOrder, yOrder)));
                       }
                   }
               }
           }
       }
       for (int i = 0; i < maxError.length; ++i) {
           Assert.assertEquals(expectedError[i], maxError[i], 0.01 * expectedError[i]);
       }
    }
}
