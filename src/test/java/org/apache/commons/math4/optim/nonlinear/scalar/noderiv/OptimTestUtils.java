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
package org.apache.commons.math4.optim.nonlinear.scalar.noderiv;

import java.util.Arrays;
import java.util.Random;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.analysis.MultivariateFunction;
import org.apache.commons.math4.util.FastMath;

/**
 * Utilities for testing the optimizers.
 */
class OptimTestUtils {

    static class Sphere implements MultivariateFunction {

        @Override
        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length; ++i) {
                f += x[i] * x[i];
            }
            return f;
        }
    }

    static class Cigar implements MultivariateFunction {
        private double factor;

        Cigar() {
            this(1e3);
        }

        Cigar(double axisratio) {
            factor = axisratio * axisratio;
        }

        @Override
        public double value(double[] x) {
            double f = x[0] * x[0];
            for (int i = 1; i < x.length; ++i) {
                f += factor * x[i] * x[i];
            }
            return f;
        }
    }

    static class Tablet implements MultivariateFunction {
        private double factor;

        Tablet() {
            this(1e3);
        }

        Tablet(double axisratio) {
            factor = axisratio * axisratio;
        }

        @Override
        public double value(double[] x) {
            double f = factor * x[0] * x[0];
            for (int i = 1; i < x.length; ++i) {
                f += x[i] * x[i];
            }
            return f;
        }
    }

    static class CigTab implements MultivariateFunction {
        private double factor;

        CigTab() {
            this(1e4);
        }

        CigTab(double axisratio) {
            factor = axisratio;
        }

        @Override
        public double value(double[] x) {
            int end = x.length - 1;
            double f = x[0] * x[0] / factor + factor * x[end] * x[end];
            for (int i = 1; i < end; ++i) {
                f += x[i] * x[i];
            }
            return f;
        }
    }

    static class TwoAxes implements MultivariateFunction {

        private double factor;

        TwoAxes() {
            this(1e6);
        }

        TwoAxes(double axisratio) {
            factor = axisratio * axisratio;
        }

        @Override
        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length; ++i) {
                f += (i < x.length / 2 ? factor : 1) * x[i] * x[i];
            }
            return f;
        }
    }

    static class ElliRotated implements MultivariateFunction {
        private Basis B = new Basis();
        private double factor;

        ElliRotated() {
            this(1e3);
        }

        ElliRotated(double axisratio) {
            factor = axisratio * axisratio;
        }

        @Override
        public double value(double[] x) {
            double f = 0;
            x = B.Rotate(x);
            for (int i = 0; i < x.length; ++i) {
                f += FastMath.pow(factor, i / (x.length - 1.)) * x[i] * x[i];
            }
            return f;
        }
    }

    static class Elli implements MultivariateFunction {

        private double factor;

        Elli() {
            this(1e3);
        }

        Elli(double axisratio) {
            factor = axisratio * axisratio;
        }

        @Override
        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length; ++i) {
                f += FastMath.pow(factor, i / (x.length - 1.)) * x[i] * x[i];
            }
            return f;
        }
    }

    static class MinusElli implements MultivariateFunction {
        private final Elli elli = new Elli();
        @Override
        public double value(double[] x) {
            return 1.0 - elli.value(x);
        }
    }

    static class DiffPow implements MultivariateFunction {
        @Override
        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length; ++i) {
                f += FastMath.pow(FastMath.abs(x[i]), 2. + 10 * (double) i
                        / (x.length - 1.));
            }
            return f;
        }
    }

    static class SsDiffPow implements MultivariateFunction { 
        @Override
        public double value(double[] x) {
            double f = FastMath.pow(new DiffPow().value(x), 0.25);
            return f;
        }
    }
 
    static class Rosen implements MultivariateFunction {
        @Override
        public double value(double[] x) {
            double f = 0;
            for (int i = 0; i < x.length - 1; i++) {
                final double a = x[i] * x[i] - x[i + 1];
                final double b = x[i] - 1;
                f += 1e2 * a * a + b * b;
            }
            return f;
        }
    }
 
    static class Ackley implements MultivariateFunction {
        private double axisratio;
         
        Ackley(double axra) {
            axisratio = axra;
        }
         
        public Ackley() {
            this(1);
        }
         
        @Override
        public double value(double[] x) {
            double f = 0;
            double res2 = 0;
            double fac = 0;
            for (int i = 0; i < x.length; ++i) {
                fac = FastMath.pow(axisratio, (i - 1.) / (x.length - 1.));
                f += fac * fac * x[i] * x[i];
                res2 += FastMath.cos(2. * FastMath.PI * fac * x[i]);
            }
            f = (20. - 20. * FastMath.exp(-0.2 * FastMath.sqrt(f / x.length))
                 + FastMath.exp(1.) - FastMath.exp(res2 / x.length));
            return f;
        }
    }

    static class Rastrigin implements MultivariateFunction { 
        private double axisratio;
        private double amplitude;

        Rastrigin() {
            this(1, 10);
        }
     
        Rastrigin(double axisratio, double amplitude) {
            this.axisratio = axisratio;
            this.amplitude = amplitude;
        }
     
        @Override
        public double value(double[] x) {
            double f = 0;
            double fac;
            for (int i = 0; i < x.length; ++i) {
                fac = FastMath.pow(axisratio, (i - 1.) / (x.length - 1.));
                if (i == 0 && x[i] < 0) {
                    fac *= 1.;
                }
                f += fac * fac * x[i] * x[i] + amplitude
                    * (1. - FastMath.cos(2. * FastMath.PI * fac * x[i]));
            }
            return f;
        }
    }

    static class FourExtrema implements MultivariateFunction {
        // The following function has 4 local extrema.
        static final double xM = -3.841947088256863675365;
        static final double yM = -1.391745200270734924416;
        static final double xP =  0.2286682237349059125691;
        static final double yP = -yM;
        static final double valueXmYm = 0.2373295333134216789769; // Local maximum.
        static final double valueXmYp = -valueXmYm; // Local minimum.
        static final double valueXpYm = -0.7290400707055187115322; // Global minimum.
        static final double valueXpYp = -valueXpYm; // Global maximum.

        @Override
        public double value(double[] variables) {
            final double x = variables[0];
            final double y = variables[1];
            return (x == 0 || y == 0) ? 0 :
                FastMath.atan(x) * FastMath.atan(x + 2) * FastMath.atan(y) * FastMath.atan(y) / (x * y);
        }
    }

    static class Rosenbrock implements MultivariateFunction {
        @Override
        public double value(double[] x) {
            double a = x[1] - x[0] * x[0];
            double b = 1.0 - x[0];
            return 100 * a * a + b * b;
        }
    }

    static class Powell implements MultivariateFunction {
        @Override
        public double value(double[] x) {
            double a = x[0] + 10 * x[1];
            double b = x[2] - x[3];
            double c = x[1] - 2 * x[2];
            double d = x[0] - x[3];
            return a * a + 5 * b * b + c * c * c * c + 10 * d * d * d * d;
        }
    }

    static class Gaussian2D implements MultivariateFunction {
        private final double[] maximumPosition;
        private final double std;

        public Gaussian2D(double xOpt, double yOpt, double std) {
            maximumPosition = new double[] { xOpt, yOpt };
            this.std = std;
        }

        public double getMaximum() {
            return value(maximumPosition);
        }

        public double[] getMaximumPosition() {
            return maximumPosition.clone();
        }

        @Override
        public double value(double[] point) {
            final double x = point[0], y = point[1];
            final double twoS2 = 2.0 * std * std;
            return 1.0 / (twoS2 * FastMath.PI) * FastMath.exp(-(x * x + y * y) / twoS2);
        }
    }

    static double[] point(int n, double value) {
        double[] ds = new double[n];
        Arrays.fill(ds, value);
        return ds;
    }

    /** Creates a RNG instance. */
    static UniformRandomProvider rng() {
        return RandomSource.create(RandomSource.MWC_256);
    }

    private static class Basis {
        double[][] basis;
        final Random rand = new Random(2); // use not always the same basis

        double[] Rotate(double[] x) {
            GenBasis(x.length);
            double[] y = new double[x.length];
            for (int i = 0; i < x.length; ++i) {
                y[i] = 0;
                for (int j = 0; j < x.length; ++j) {
                    y[i] += basis[i][j] * x[j];
                }
            }
            return y;
        }

        void GenBasis(int DIM) {
            if (basis != null ? basis.length == DIM : false) {
                return;
            }

            double sp;
            int i, j, k;

            /* generate orthogonal basis */
            basis = new double[DIM][DIM];
            for (i = 0; i < DIM; ++i) {
                /* sample components gaussian */
                for (j = 0; j < DIM; ++j) {
                    basis[i][j] = rand.nextGaussian();
                }
                /* substract projection of previous vectors */
                for (j = i - 1; j >= 0; --j) {
                    for (sp = 0., k = 0; k < DIM; ++k) {
                        sp += basis[i][k] * basis[j][k]; /* scalar product */
                    }
                    for (k = 0; k < DIM; ++k) {
                        basis[i][k] -= sp * basis[j][k]; /* substract */
                    }
                }
                /* normalize */
                for (sp = 0., k = 0; k < DIM; ++k) {
                    sp += basis[i][k] * basis[i][k]; /* squared norm */
                }
                for (k = 0; k < DIM; ++k) {
                    basis[i][k] /= FastMath.sqrt(sp);
                }
            }
        }
    }
}
