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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar;

import java.util.function.Function;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.core.jdkmath.AccurateMath;

/**
 * Multivariate scalar functions for testing an optimizer.
 */
public enum TestFunction {
    SPHERE(dim -> {
            return x -> {
                double f = 0;
                for (int i = 0; i < dim; i++) {
                    f += x[i] * x[i];
                }
                return f;
            };
        }),
    CIGAR(dim -> {
            return x -> {
                double f = x[0] * x[0];
                for (int i = 1; i < dim; i++) {
                    f += 1e3 * x[i] * x[i];
                }
                return f;
            };
        }),
    TABLET(dim -> {
            return x -> {
                double f = 1e3 * x[0] * x[0];
                for (int i = 1; i < dim; i++) {
                    f += x[i] * x[i];
                }
                return f;
            };
        }),
    CIG_TAB(dim -> {
            final double factor = 1e4;
            final int last = dim - 1;
            return x -> {
                double f = x[0] * x[0] / factor + factor * x[last] * x[last];
                for (int i = 1; i < last; i++) {
                    f += x[i] * x[i];
                }
                return f;
            };
        }),
    TWO_AXES(dim -> {
            return x -> {
                double f = 0;
                for (int i = 0; i < dim; i++) {
                    f += (i < dim / 2 ? 1e6 : 1) * x[i] * x[i];
                }
                return f;
            };
        }),
    ELLI(dim -> {
            final double last = dim - 1;
            return x -> {
                double f = 0;
                for (int i = 0; i < dim; i++) {
                    f += Math.pow(1e3, i / last) * x[i] * x[i];
                }
                return f;
            };
        }),
    MINUS_ELLI(dim -> {
            final MultivariateFunction elli = ELLI.withDimension(dim);
            return x -> {
                return 1 - elli.value(x);
            };
        }),
    DIFF_POW(dim -> {
            final double A = 10d / (dim - 1);
            return x -> {
                double f = 0;
                for (int i = 0; i < dim; i++) {
                    f += AccurateMath.pow(Math.abs(x[i]), A * i + 2);
                }
                return f;
            };
        }),
    SS_DIFF_POW(dim -> {
            final MultivariateFunction diffPow = DIFF_POW.withDimension(dim);
            return x -> {
                double f = Math.pow(diffPow.value(x), 0.25);
                return f;
            };
        }),
    ACKLEY(dim -> {
            final double A = 20;
            final double B = 0.2;
            final double C = 2 * Math.PI;
            return x -> {
                double acc1 = 0;
                double acc2 = 0;
                for (int i = 0; i < dim; i++) {
                    final double v = x[i];
                    acc1 += v * v;
                    acc2 += Math.cos(C * v);
                }
                acc1 = -B * Math.sqrt(acc1 / dim);
                acc2 /= dim;

                return -A * Math.exp(acc1) - Math.exp(acc2) + A + Math.E;
            };
        }),
    // https://www.sfu.ca/~ssurjano/rastr.html
    RASTRIGIN(dim -> {
            final double A = 10;
            return x -> {
                double sum = 0;
                for (int i = 0; i < dim; i++) {
                    final double xi = x[i];
                    sum += xi * xi - A * Math.cos(2 * Math.PI * xi);
                }
                return A * dim + sum;
            };
        }),
    // https://www.sfu.ca/~ssurjano/powell.html
    POWELL(dim -> {
            final int last = dim / 4;
            return x -> {
                double f = 0;
                for (int i = 0; i < last; i++) {
                    final int fourI = 4 * i;
                    final double x4i = x[fourI];
                    final double x4iP1 = x[fourI + 1];
                    final double x4iP2 = x[fourI + 2];
                    final double x4iP3 = x[fourI + 3];
                    final double a = x4i + 10 * x4iP1;
                    final double b = x4iP2 - x4iP3;
                    final double c = x4iP1 - 2 * x4iP2;
                    final double d = x4i - x4iP3;
                    f += a * a + 5 * b * b + c * c * c * c + 10 * d * d * d * d;
                }
                return f;
            };
        }),
    ROSENBROCK(dim -> {
            final int last = dim - 1;
            return x -> {
                double f = 0;
                for (int i = 0; i < last; i++) {
                    final double xi = x[i];
                    final double xiP1 = x[i + 1];
                    final double a = xiP1 - xi * xi;
                    final double b = xi - 1;
                    f += 1e2 * a * a + b * b;
                }
                return f;
            };
        }),
    PARABOLA(dim -> {
            return x -> {
                double f = 0;
                for (int i = 0; i < dim; i++) {
                    final double xi = x[i];
                    f += xi * xi;
                }
                return f;
            };
        });

    /** Template for variable dimension. */
    private final Function<Integer, MultivariateFunction> generator;

    /**
     * @param gen Template for variable dimension.
     */
    TestFunction(Function<Integer, MultivariateFunction> gen) {
        generator = gen;
    }

    /**
     * @param dim Dimension.
     * @return the function for the given dimension.
     */
    public MultivariateFunction withDimension(final int dim) {
        return new MultivariateFunction() {
            /** Delegate. */
            private final MultivariateFunction f = generator.apply(dim);

            @Override
            public double value(double[] x) {
                if (x.length != dim) {
                    throw new IllegalArgumentException("Dimension mismatch: " + x.length +
                                                       "(expected: " + dim + ")");
                }
                return f.value(x);
            };

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder();
                sb.append("[")
                    .append(TestFunction.this.toString())
                    .append(" dim=")
                    .append(dim)
                    .append("]");
                return sb.toString();
            }
        };
    }
}
