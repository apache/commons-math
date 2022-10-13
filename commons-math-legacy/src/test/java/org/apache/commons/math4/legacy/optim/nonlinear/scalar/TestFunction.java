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
import java.util.function.DoubleUnaryOperator;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;

/**
 * Generators of {@link MultivariateFunction multivariate scalar functions}.
 * The functions are intended for testing optimizer implementations.
 * <p>
 * Note: The {@link #withDimension(int) function generators} take the space
 * dimension (i.e. the length of the array argument passed to the generated
 * function) as argument; it is thus assumed that the test functions can be
 * generalized to any dimension.
 */
public enum TestFunction {
    // https://www.sfu.ca/~ssurjano/spheref.html
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
            final int halfDim = dim / 2;
            return x -> {
                double f = 0;
                for (int i = 0; i < halfDim; i++) {
                    f += 1e6 * x[i] * x[i];
                }
                for (int i = halfDim; i < dim; i++) {
                    f += x[i] * x[i];
                }
                return f;
            };
        }),
    ELLI(dim -> {
            final double M = Math.pow(1e3, 1d / (dim - 1));
            return x -> {
                double factor = 1;
                double f = 0;
                for (int i = 0; i < dim; i++) {
                    f += factor * x[i] * x[i];
                    factor *= M;
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
    // https://www.sfu.ca/~ssurjano/sumpow.html
    SUM_POW(dim -> {
            return x -> {
                double f = 0;
                for (int i = 0; i < dim; i++) {
                    f += Math.pow(Math.abs(x[i]), i + 2);
                }
                return f;
            };
        }),
    // https://www.sfu.ca/~ssurjano/ackley.html
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
            final double twopi = 2 * Math.PI;
            return x -> {
                double sum = 0;
                for (int i = 0; i < dim; i++) {
                    final double xi = x[i];
                    sum += xi * xi - A * Math.cos(twopi * xi);
                }
                return A * dim + sum;
            };
        }),
    // http://benchmarkfcns.xyz/benchmarkfcns/salomonfcn.html
    SALOMON(dim -> {
            return x -> {
                double sum = 0;
                for (int i = 0; i < dim; i++) {
                    final double xi = x[i];
                    sum += xi * xi;
                }
                final double sqrtSum = Math.sqrt(sum);
                return 1 - Math.cos(2 * Math.PI * sqrtSum) + 0.1 * sqrtSum;
            };
        }),
    // https://scholarship.rice.edu/handle/1911/16304
    ROSENBROCK(dim -> {
            if ((dim & 1) != 0) {
                throw new IllegalArgumentException("Must be a multiple of 2 (was: " + dim + ")");
            }
            final int last = dim / 2;
            return x -> {
                double f = 0;
                for (int i = 1; i <= last; i++) {
                    final int twoI = 2 * i;
                    final int i0 = twoI - 1;
                    final int i1 = twoI;
                    final double x2iM1 = x[i0 - 1];
                    final double x2i = x[i1 - 1];
                    final double t2iM1 = x2i - x2iM1 * x2iM1;
                    final double t2i = 1 - x2iM1;
                    f += 100 * t2iM1 * t2iM1 + t2i * t2i;
                }
                return f;
            };
        }),
    // http://benchmarkfcns.xyz/benchmarkfcns/happycatfcn.html
    HAPPY_CAT(dim -> {
            final double alpha = 0.125;
            return x -> {
                double sum = 0;
                double sumSq = 0;
                for (int i = 0; i < dim; i++) {
                    final double xi = x[i];
                    sum += xi;
                    sumSq += xi * xi;
                }
                return Math.pow(sumSq - dim, 2 * alpha) + (0.5 * sumSq + sum) / dim + 0.5;
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
        }),
    // https://www.sfu.ca/~ssurjano/griewank.html
    GRIEWANK(dim -> {
            final double A = 4000;
            return x -> {
                double sum = 0;
                double prod = 1;
                for (int i = 0; i < dim; i++) {
                    final double xi = x[i];
                    sum += xi * xi;
                    prod *= Math.cos(xi / Math.sqrt(i + 1));
                }
                return sum / A - prod + 1;
            };
        }),
    // https://www.sfu.ca/~ssurjano/levy.html
    LEVY(dim -> {
            final int last = dim - 1;
            final DoubleUnaryOperator w = x -> 1 + 0.25 * (x - 1);
            return x -> {
                final double a0 = Math.sin(Math.PI * w.applyAsDouble(x[0]));
                double sum = a0 * a0;
                for (int i = 0; i < last; i++) {
                    final double wi = w.applyAsDouble(x[i]);
                    final double wiM1 = wi - 1;
                    final double ai = Math.sin(Math.PI * wi + 1);
                    sum += wiM1 * wiM1 * (1 + 10 * ai * ai);
                }
                final double wl = w.applyAsDouble(x[last]);
                final double wlM1 = wl - 1;
                final double al = Math.sin(2 * Math.PI * wl);
                return sum + wlM1 * wlM1 * (1 + al * al);
            };
        }),
    // https://www.sfu.ca/~ssurjano/schwef.html
    SCHWEFEL(dim -> {
            final double A = 418.9829;
            return x -> {
                double sum = 0;
                for (int i = 0; i < dim; i++) {
                    final double xi = x[i];
                    sum += xi * Math.sin(Math.sqrt(Math.abs(xi)));
                }
                return A * dim - sum;
            };
        }),
    // https://www.sfu.ca/~ssurjano/zakharov.html
    ZAKHAROV(dim -> {
            final double A = 0.5;
            return x -> {
                double sum1 = 0;
                double sum2 = 0;
                for (int i = 0; i < dim; i++) {
                    final double xi = x[i];
                    sum1 += xi * xi;
                    sum2 += A * (i + 1) * xi;
                }
                final double sum22 = sum2 * sum2;
                return sum1 + sum22 + sum22 * sum22;
            };
        }),
    // https://www.sfu.ca/~ssurjano/permdb.html
    PERM(dim -> {
            final double BETA = 10;
            return x -> {
                double sum1 = 0;
                for (int i = 0; i < dim; i++) {
                    final double iP1 = i + 1;
                    double sum2 = 0;
                    for (int j = 0; j < dim; j++) {
                        final double jP1 = j + 1;
                        final double a = Math.pow(jP1, iP1) + BETA;
                        final double b = Math.pow(x[j] / jP1, iP1) - 1;
                        sum2 += a * b;
                    }
                    sum1 += sum2 * sum2;
                }
                return sum1;
            };
        }),
    // https://www.sfu.ca/~ssurjano/stybtang.html
    STYBLINSKI_TANG(dim -> {
            final double A = 0.5;
            final double B = 16;
            final double C = 5;
            return x -> {
                double sum = 0;
                for (int i = 0; i < dim; i++) {
                    final double xi = x[i];
                    final double xi2 = xi * xi;
                    final double xi4 = xi2 * xi2;
                    sum += xi4 - B * xi2 + C * xi;
                }
                return A * sum;
            };
        }),
    // https://scholarship.rice.edu/handle/1911/16304
    POWELL(dim -> {
            if (dim % 4 != 0) {
                throw new IllegalArgumentException("Must be a multiple of 4 (was: " + dim + ")");
            }
            final int last = dim / 4;
            return x -> {
                double sum = 0;
                for (int i = 1; i <= last; i++) {
                    final int fourI = 4 * i;
                    final int i0 = fourI - 3;
                    final int i1 = fourI - 2;
                    final int i2 = fourI - 1;
                    final int i3 = fourI;
                    final double x4iM3 = x[i0 - 1];
                    final double x4iM2 = x[i1 - 1];
                    final double x4iM1 = x[i2 - 1];
                    final double x4i = x[i3 - 1];
                    final double t4iM3 = x4iM3 + 10 * x4iM2;
                    final double t4iM2 = x4iM1 - x4i;
                    final double t4iM1 = x4iM2 - 2 * x4iM1;
                    final double sqT4iM1 = t4iM1 * t4iM1;
                    final double t4i = x4iM3 - x4i;
                    final double sqT4i = t4i * t4i;
                    sum += t4iM3 * t4iM3 + 5 * t4iM2 * t4iM2 + sqT4iM1 * sqT4iM1 + 10 * sqT4i * sqT4i;
                }
                return sum;
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
