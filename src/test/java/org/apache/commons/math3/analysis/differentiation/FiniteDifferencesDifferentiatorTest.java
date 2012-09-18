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

package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.analysis.QuinticFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateMatrixFunction;
import org.apache.commons.math3.analysis.UnivariateVectorFunction;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;
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
    public void testSerialization() {
        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(3, 1.0e-3);
        FiniteDifferencesDifferentiator recovered =
                (FiniteDifferencesDifferentiator) TestUtils.serializeAndRecover(differentiator);
        Assert.assertEquals(differentiator.getNbPoints(), recovered.getNbPoints());
        Assert.assertEquals(differentiator.getStepSize(), recovered.getStepSize(), 1.0e-15);
    }

    @Test
    public void testConstant() {
        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(5, 0.01);
        UnivariateDifferentiableFunction f =
                differentiator.differentiate(new UnivariateFunction() {
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
                    public double value(double x) {
                        return 2 - 3 * x;
                    }
                });
        for (double x = -10; x < 10; x += 0.1) {
            DerivativeStructure y = f.value(new DerivativeStructure(1, 2, 0, x));
            Assert.assertEquals(2 - 3 * x, y.getValue(), 1.0e-20);
            Assert.assertEquals(-3.0, y.getPartialDerivative(1), 4.0e-13);
            Assert.assertEquals( 0.0, y.getPartialDerivative(2), 5.0e-11);
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
            2.776e-17, 1.742e-15, 2.385e-13, 1.329e-11, 2.668e-9, 8.873e-8
        };
       double[] maxError = new double[expectedError.length];
        for (double x = -10; x < 10; x += 0.1) {
            DerivativeStructure dsX  = new DerivativeStructure(1, maxError.length - 1, 0, x);
            DerivativeStructure yRef = gaussian.value(dsX);
            DerivativeStructure y    = f.value(dsX);
            for (int order = 0; order <= yRef.getOrder(); ++order) {
                maxError[order] = FastMath.max(maxError[order],
                                        FastMath.abs(yRef.getPartialDerivative(order) -
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
                maxErrorGood[order] = FastMath.max(maxErrorGood[order],
                                                   FastMath.abs(yRef.getPartialDerivative(order) -
                                                                yGood.getPartialDerivative(order)));
                maxErrorBad[order]  = FastMath.max(maxErrorBad[order],
                                                   FastMath.abs(yRef.getPartialDerivative(order) -
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
            1.792e-22, 6.926e-5, 56.25, 1.783e8, 2.468e14, 3.056e20, 5.857e26            
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
                    public double[][] value(double x) {
                        // this exception should not be thrown because wrong order
                        // should be detected before function call
                        throw new MathInternalError();
                    }
                });
        f.value(new DerivativeStructure(1, 3, 0, 1.0));
    }

    @Test
    public void testVectorFunction() {

        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(7, 0.01);
        UnivariateDifferentiableVectorFunction f =
                differentiator.differentiate(new UnivariateVectorFunction() {
            
            public double[] value(double x) {
                return new double[] { FastMath.cos(x), FastMath.sin(x) };
            }
            
        });

        for (double x = -10; x < 10; x += 0.1) {
            DerivativeStructure[] y = f.value(new DerivativeStructure(1, 2, 0, x));
            double cos = FastMath.cos(x);
            double sin = FastMath.sin(x);
            Assert.assertEquals(cos, y[0].getValue(), 2.0e-16);
            Assert.assertEquals(sin, y[1].getValue(), 2.0e-16);
            Assert.assertEquals(-sin, y[0].getPartialDerivative(1), 5.0e-14);
            Assert.assertEquals( cos, y[1].getPartialDerivative(1), 5.0e-14);
            Assert.assertEquals(-cos, y[0].getPartialDerivative(2), 6.0e-12);
            Assert.assertEquals(-sin, y[1].getPartialDerivative(2), 6.0e-12);
        }

    }

    @Test
    public void testMatrixFunction() {

        FiniteDifferencesDifferentiator differentiator =
                new FiniteDifferencesDifferentiator(7, 0.01);
        UnivariateDifferentiableMatrixFunction f =
                differentiator.differentiate(new UnivariateMatrixFunction() {
            
            public double[][] value(double x) {
                return new double[][] {
                    { FastMath.cos(x),  FastMath.sin(x)  },
                    { FastMath.cosh(x), FastMath.sinh(x) }
                };
            }
            
        });

        for (double x = -1; x < 1; x += 0.02) {
            DerivativeStructure[][] y = f.value(new DerivativeStructure(1, 2, 0, x));
            double cos = FastMath.cos(x);
            double sin = FastMath.sin(x);
            double cosh = FastMath.cosh(x);
            double sinh = FastMath.sinh(x);
            Assert.assertEquals(cos,   y[0][0].getValue(), 7.0e-18);
            Assert.assertEquals(sin,   y[0][1].getValue(), 7.0e-18);
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
            1.110e-16, 2.66e-12, 4.803e-9, 5.486e-5
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
                           maxError[xOrder +yOrder] = FastMath.max(maxError[xOrder + yOrder],
                                                                    FastMath.abs(sRef.getPartialDerivative(xOrder, yOrder) -
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
