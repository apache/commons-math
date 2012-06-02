// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.commons.math3.optimization.fitting;

import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.optimization.general.GaussNewtonOptimizer;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.optimization.SimpleVectorValueChecker;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

public class CurveFitterTest {

    @Test
    public void testMath303() {

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);
        fitter.addObservedPoint(2.805d, 0.6934785852953367d);
        fitter.addObservedPoint(2.74333333333333d, 0.6306772025518496d);
        fitter.addObservedPoint(1.655d, 0.9474675497289684);
        fitter.addObservedPoint(1.725d, 0.9013594835804194d);

        ParametricUnivariateFunction sif = new SimpleInverseFunction();

        double[] initialguess1 = new double[1];
        initialguess1[0] = 1.0d;
        Assert.assertEquals(1, fitter.fit(sif, initialguess1).length);

        double[] initialguess2 = new double[2];
        initialguess2[0] = 1.0d;
        initialguess2[1] = .5d;
        Assert.assertEquals(2, fitter.fit(sif, initialguess2).length);

    }

    @Test
    public void testMath304() {

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter fitter = new CurveFitter(optimizer);
        fitter.addObservedPoint(2.805d, 0.6934785852953367d);
        fitter.addObservedPoint(2.74333333333333d, 0.6306772025518496d);
        fitter.addObservedPoint(1.655d, 0.9474675497289684);
        fitter.addObservedPoint(1.725d, 0.9013594835804194d);

        ParametricUnivariateFunction sif = new SimpleInverseFunction();

        double[] initialguess1 = new double[1];
        initialguess1[0] = 1.0d;
        Assert.assertEquals(1.6357215104109237, fitter.fit(sif, initialguess1)[0], 1.0e-14);

        double[] initialguess2 = new double[1];
        initialguess2[0] = 10.0d;
        Assert.assertEquals(1.6357215104109237, fitter.fit(sif, initialguess1)[0], 1.0e-14);

    }

    @Test
    public void testMath372() {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter curveFitter = new CurveFitter(optimizer);

        curveFitter.addObservedPoint( 15,  4443);
        curveFitter.addObservedPoint( 31,  8493);
        curveFitter.addObservedPoint( 62, 17586);
        curveFitter.addObservedPoint(125, 30582);
        curveFitter.addObservedPoint(250, 45087);
        curveFitter.addObservedPoint(500, 50683);

        ParametricUnivariateFunction f = new ParametricUnivariateFunction() {

            public double value(double x, double ... parameters) {

                double a = parameters[0];
                double b = parameters[1];
                double c = parameters[2];
                double d = parameters[3];

                return d + ((a - d) / (1 + FastMath.pow(x / c, b)));
            }

            public double[] gradient(double x, double ... parameters) {

                double a = parameters[0];
                double b = parameters[1];
                double c = parameters[2];
                double d = parameters[3];

                double[] gradients = new double[4];
                double den = 1 + FastMath.pow(x / c, b);

                // derivative with respect to a
                gradients[0] = 1 / den;

                // derivative with respect to b
                // in the reported (invalid) issue, there was a sign error here
                gradients[1] = -((a - d) * FastMath.pow(x / c, b) * FastMath.log(x / c)) / (den * den);

                // derivative with respect to c
                gradients[2] = (b * FastMath.pow(x / c, b - 1) * (x / (c * c)) * (a - d)) / (den * den);

                // derivative with respect to d
                gradients[3] = 1 - (1 / den);

                return gradients;

            }
        };

        double[] initialGuess = new double[] { 1500, 0.95, 65, 35000 };
        double[] estimatedParameters = curveFitter.fit(f, initialGuess);

        Assert.assertEquals( 2411.00, estimatedParameters[0], 500.00);
        Assert.assertEquals(    1.62, estimatedParameters[1],   0.04);
        Assert.assertEquals(  111.22, estimatedParameters[2],   0.30);
        Assert.assertEquals(55347.47, estimatedParameters[3], 300.00);
        Assert.assertTrue(optimizer.getRMS() < 600.0);

    }

    @Test
    public void testMath798() {
        final double tol = 1e-14;
        final SimpleVectorValueChecker checker = new SimpleVectorValueChecker(tol, tol);
        final double[] init = new double[] { 0, 0 };
        final int maxEval = 3;

        final double[] lm = doMath798(new LevenbergMarquardtOptimizer(checker), maxEval, init);
        final double[] gn = doMath798(new GaussNewtonOptimizer(checker), maxEval, init);

        for (int i = 0; i <= 1; i++) {
            Assert.assertEquals(lm[i], gn[i], tol);
        }
    }

    /**
     * @param optimizer Optimizer.
     * @param maxEval Maximum number of function evaluations.
     * @param init First guess.
     * @return the solution found by the given optimizer.
     */
    private double[] doMath798(DifferentiableMultivariateVectorOptimizer optimizer,
                               int maxEval,
                               double[] init) {
        final CurveFitter fitter = new CurveFitter(optimizer);

        fitter.addObservedPoint(-0.2, -7.12442E-13);
        fitter.addObservedPoint(-0.199, -4.33397E-13);
        fitter.addObservedPoint(-0.198, -2.823E-13);
        fitter.addObservedPoint(-0.197, -1.40405E-13);
        fitter.addObservedPoint(-0.196, -7.80821E-15);
        fitter.addObservedPoint(-0.195, 6.20484E-14);
        fitter.addObservedPoint(-0.194, 7.24673E-14);
        fitter.addObservedPoint(-0.193, 1.47152E-13);
        fitter.addObservedPoint(-0.192, 1.9629E-13);
        fitter.addObservedPoint(-0.191, 2.12038E-13);
        fitter.addObservedPoint(-0.19, 2.46906E-13);
        fitter.addObservedPoint(-0.189, 2.77495E-13);
        fitter.addObservedPoint(-0.188, 2.51281E-13);
        fitter.addObservedPoint(-0.187, 2.64001E-13);
        fitter.addObservedPoint(-0.186, 2.8882E-13);
        fitter.addObservedPoint(-0.185, 3.13604E-13);
        fitter.addObservedPoint(-0.184, 3.14248E-13);
        fitter.addObservedPoint(-0.183, 3.1172E-13);
        fitter.addObservedPoint(-0.182, 3.12912E-13);
        fitter.addObservedPoint(-0.181, 3.06761E-13);
        fitter.addObservedPoint(-0.18, 2.8559E-13);
        fitter.addObservedPoint(-0.179, 2.86806E-13);
        fitter.addObservedPoint(-0.178, 2.985E-13);
        fitter.addObservedPoint(-0.177, 2.67148E-13);
        fitter.addObservedPoint(-0.176, 2.94173E-13);
        fitter.addObservedPoint(-0.175, 3.27528E-13);
        fitter.addObservedPoint(-0.174, 3.33858E-13);
        fitter.addObservedPoint(-0.173, 2.97511E-13);
        fitter.addObservedPoint(-0.172, 2.8615E-13);
        fitter.addObservedPoint(-0.171, 2.84624E-13);

        final double[] coeff = fitter.fit(maxEval,
                                          new PolynomialFunction.Parametric(),
                                          init);
        return coeff;
    }

    private static class SimpleInverseFunction implements ParametricUnivariateFunction {

        public double value(double x, double ... parameters) {
            return parameters[0] / x + (parameters.length < 2 ? 0 : parameters[1]);
        }

        public double[] gradient(double x, double ... doubles) {
            double[] gradientVector = new double[doubles.length];
            gradientVector[0] = 1 / x;
            if (doubles.length >= 2) {
                gradientVector[1] = 1;
            }
            return gradientVector;
        }
    }

}
