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
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.sampling.distribution.ContinuousUniformSampler;
import org.apache.commons.rng.sampling.UnitSphereSampler;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.math4.legacy.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.legacy.exception.TooManyEvaluationsException;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.SimpleBounds;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.SimulatedAnnealing;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.TestFunction;

/**
 * Tests for {@link SimplexOptimizer simplex-based algorithms}.
 */
public class SimplexOptimizerTest {
    private static final String NELDER_MEAD_INPUT_FILE = "std_test_func.simplex.nelder_mead.csv";
    private static final String MULTIDIRECTIONAL_INPUT_FILE = "std_test_func.simplex.multidirectional.csv";
    private static final String HEDAR_FUKUSHIMA_INPUT_FILE = "std_test_func.simplex.hedar_fukushima.csv";

    @Test
    public void testMaxEvaluations() {
        Assertions.assertThrows(TooManyEvaluationsException.class, () -> {
                final int dim = 4;
                final SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-3);
                optimizer.optimize(new MaxEval(20),
                                   new ObjectiveFunction(TestFunction.PARABOLA.withDimension(dim)),
                                   GoalType.MINIMIZE,
                                   new InitialGuess(new double[] { 3, -1, -3, 1 }),
                                   Simplex.equalSidesAlongAxes(dim, 1d),
                                   new NelderMeadTransform());
            });
    }

    @Test
    public void testBoundsUnsupported() {
        Assertions.assertThrows(MathUnsupportedOperationException.class, () -> {
                final int dim = 2;
                final SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
                optimizer.optimize(new MaxEval(100),
                                   new ObjectiveFunction(TestFunction.PARABOLA.withDimension(dim)),
                                   GoalType.MINIMIZE,
                                   new InitialGuess(new double[] { -3, 0 }),
                                   Simplex.alongAxes(new double[] { 0.2, 0.2 }),
                                   new NelderMeadTransform(),
                                   new SimpleBounds(new double[] { -5, -1 },
                                                    new double[] { 5, 1 }));
            });
    }

    @ParameterizedTest
    @CsvFileSource(resources = NELDER_MEAD_INPUT_FILE)
    void testFunctionWithNelderMead(@AggregateWith(TaskAggregator.class) Task task) {
        // task.checkAlongLine(1000);
        task.run(new NelderMeadTransform());
    }

    @ParameterizedTest
    @CsvFileSource(resources = MULTIDIRECTIONAL_INPUT_FILE)
    void testFunctionWithMultiDirectional(@AggregateWith(TaskAggregator.class) Task task) {
        task.run(new MultiDirectionalTransform());
    }

    @ParameterizedTest
    @CsvFileSource(resources = HEDAR_FUKUSHIMA_INPUT_FILE)
    void testFunctionWithHedarFukushima(@AggregateWith(TaskAggregator.class) Task task) {
        task.run(new HedarFukushimaTransform());
    }

    /**
     * Optimization task.
     */
    public static class Task {
        /** Function evaluations hard count (debugging). */
        private static final int FUNC_EVAL_DEBUG = 500000;
        /** Default convergence criterion. */
        private static final double CONVERGENCE_CHECK = 1e-9;
        /** Default cooling factor. */
        private static final double SA_COOL_FACTOR = 0.7;
        /** Default acceptance probability at beginning of SA. */
        private static final double SA_START_PROB = 0.9;
        /** Default acceptance probability at end of SA. */
        private static final double SA_END_PROB = 1e-20;
        /** Function. */
        private final MultivariateFunction function;
        /** Initial value. */
        private final double[] start;
        /** Optimum. */
        private final double[] optimum;
        /** Tolerance. */
        private final double pointTolerance;
        /** Allowed function evaluations. */
        private final int functionEvaluations;
        /** Side length of initial simplex. */
        private final double simplexSideLength;
        /** Whether to perform simulated annealing. */
        private final boolean withSA;
        /** File prefix (for saving debugging info). */
        private final String tracePrefix;
        /** Indices of simplex points to be saved for debugging. */
        private final int[] traceIndices;

        /**
         * @param function Test function.
         * @param start Start point.
         * @param optimum Optimum.
         * @param pointTolerance Allowed distance between result and
         * {@code optimum}.
         * @param functionEvaluations Allowed number of function evaluations.
         * @param simplexSideLength Side length of initial simplex.
         * @param withSA Whether to perform simulated annealing.
         * @param tracePrefix Prefix of the file where to save simplex
         * transformations during the optimization.
         * Can be {@code null} (no debugging).
         * @param traceIndices Indices of simplex points to be saved.
         * Can be {@code null} (all points are saved).
         */
        Task(MultivariateFunction function,
             double[] start,
             double[] optimum,
             double pointTolerance,
             int functionEvaluations,
             double simplexSideLength,
             boolean withSA,
             String tracePrefix,
             int[] traceIndices) {
            this.function = function;
            this.start = start;
            this.optimum = optimum;
            this.pointTolerance = pointTolerance;
            this.functionEvaluations = functionEvaluations;
            this.simplexSideLength = simplexSideLength;
            this.withSA = withSA;
            this.tracePrefix = tracePrefix;
            this.traceIndices = traceIndices;
        }

        @Override
        public String toString() {
            return function.toString();
        }

        /**
         * @param factory Simplex transform factory.
         */
        /* package-private */ void run(Simplex.TransformFactory factory) {
            // Let run with a maximum number of evaluations larger than expected
            // (as specified by "functionEvaluations") in order to have the unit
            // test failure message (see assertion below) report the actual number
            // required by the current code.
            final int maxEval = Math.max(functionEvaluations, FUNC_EVAL_DEBUG);

            final String name = function.toString();
            final int dim = start.length;

            final SimulatedAnnealing sa;
            if (withSA) {
                final SimulatedAnnealing.CoolingSchedule coolSched =
                    SimulatedAnnealing.CoolingSchedule.decreasingExponential(SA_COOL_FACTOR);

                sa = new SimulatedAnnealing(dim,
                                            SA_START_PROB,
                                            SA_END_PROB,
                                            coolSched,
                                            RandomSource.KISS.create());
            } else {
                sa = null;
            }

            final SimplexOptimizer optim = new SimplexOptimizer(-1, CONVERGENCE_CHECK);
            if (tracePrefix != null) {
                optim.addObserver(createCallback(factory));
            }

            final Simplex initialSimplex = Simplex.equalSidesAlongAxes(dim, simplexSideLength);
            final PointValuePair result =
                optim.optimize(new MaxEval(maxEval),
                               new ObjectiveFunction(function),
                               GoalType.MINIMIZE,
                               new InitialGuess(start),
                               initialSimplex,
                               factory,
                               sa);

            final double[] endPoint = result.getPoint();
            final double funcValue = result.getValue();
            final double dist = MathArrays.distance(optimum, endPoint);
            Assertions.assertEquals(0d, dist, pointTolerance,
                                    () -> name + ": distance to optimum" +
                                    " f(" + Arrays.toString(endPoint) + ")=" +
                                    funcValue);

            final int nEval = optim.getEvaluations();
            Assertions.assertTrue(nEval < functionEvaluations,
                                  () -> name + ": nEval=" + nEval);
        }

        /**
         * @param factory Simplex transform factory.
         * @return a function to save the simplex's states to file.
         */
        private SimplexOptimizer.Observer createCallback(Simplex.TransformFactory factory) {
            if (tracePrefix == null) {
                throw new IllegalArgumentException("Missing file prefix");
            }

            final String sep = "__";
            final String name = tracePrefix + sanitizeBasename(function + sep +
                                                               Arrays.toString(start) + sep +
                                                               factory + sep);

            // Create file; write first data block (optimum) and columns header.
            try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(name)))) {
                out.println("# Function: " + function);
                out.println("# Transform: " + factory);
                out.println("#");

                out.println("# Optimum");
                for (double c : optimum) {
                    out.print(c + " ");
                }
                out.println();
                out.println();

                out.println("#");
                out.print("# <1: evaluations> <2: f(x)> <3: |f(x) - f(optimum)|>");
                for (int i = 0; i < start.length; i++) {
                    out.print(" <" + (i + 4) + ": x[" + i + "]>");
                }
                out.println();
            } catch (IOException e) {
                Assertions.fail(e.getMessage());
            }

            final double fAtOptimum = function.value(optimum);

            // Return callback function.
            return (simplex, isInit, numEval) -> {
                try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(name),
                                                                               StandardOpenOption.APPEND))) {
                    if (isInit) {
                        // Blank line indicating the start of an optimization
                        // (new data block).
                        out.println();
                        out.println("# [init]"); // Initial simplex.
                    }

                    final String fieldSep = " ";
                    // 1 line per simplex point (requested for tracing).
                    final List<PointValuePair> points = simplex.asList();
                    for (int index : traceIndices) {
                        final PointValuePair p = points.get(index);
                        out.print(numEval + fieldSep +
                                  p.getValue() + fieldSep +
                                  Math.abs(p.getValue() - fAtOptimum) + fieldSep);

                        final double[] coord = p.getPoint();
                        for (int i = 0; i < coord.length; i++) {
                            out.print(coord[i] + fieldSep);
                        }
                        out.println();
                    }
                    // Blank line between simplexes.
                    out.println();
                } catch (IOException e) {
                    Assertions.fail(e.getMessage());
                }
            };
        }

        /**
         * Asserts that the lowest function value (along a line starting at
         * {@code start} is reached at the {@code optimum}.
         *
         * @param numPoints Number of points at which to evaluate the function.
         */
        public void checkAlongLine(int numPoints) {
            if (tracePrefix != null) {
                final String name = tracePrefix + createPlotBasename(function, start, optimum);
                try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(name)))) {
                    checkAlongLine(numPoints, out);
                } catch (IOException e) {
                    Assertions.fail(e.getMessage());
                }
            } else {
                checkAlongLine(numPoints, null);
            }
        }

        /**
         * Computes the values of the function along the straight line between
         * {@link #startPoint} and {@link #optimum} and asserts that the value
         * at the latter is smaller than at any other points along the line.
         * <p>
         * If the {@code output} stream is not {@code null}, two columns are
         * printed:
         * <ol>
         *  <li>parameter in the {@code [0, 1]} interval (0 at {@link #startPoint}
         *   and 1 at {@link #optimum}),</li>
         *  <li>function value at {@code t * (optimum - startPoint)}.</li>
         * </ol>
         *
         * @param numPoints Number of points to evaluate between {@link #start}
         * and {@link #optimum}.
         * @param output Output stream.
         */
        private void checkAlongLine(int numPoints,
                                    PrintWriter output) {
            final double delta = 1d / numPoints;

            final int dim = start.length;
            final double[] dir = new double[dim];
            for (int i = 0; i < dim; i++) {
                dir[i] = optimum[i] - start[i];
            }

            double[] minPoint = null;
            double minValue = Double.POSITIVE_INFINITY;
            int count = 0;
            while (count <= numPoints) {
                final double[] p = new double[dim];
                final double t = count * delta;
                for (int i = 0; i < dim; i++) {
                    p[i] = start[i] + t * dir[i];
                }

                final double value = function.value(p);
                if (value <= minValue) {
                    minValue = value;
                    minPoint = p;
                }

                if (output != null) {
                    output.println(t + " " + value);
                }

                ++count;
            }

            final double tol = 1e-15;
            final double[] point = minPoint;
            final double value = minValue;
            Assertions.assertArrayEquals(optimum, minPoint, tol,
                                         () -> "Minimum: f(" + Arrays.toString(point) + ")=" + value);
        }

        /**
         * Generates a string suitable as a file name.
         *
         * @param f Function.
         * @param start Start point.
         * @param end End point.
         * @return a string.
         */
        private static String createPlotBasename(MultivariateFunction f,
                                                 double[] start,
                                                 double[] end) {
            final String s = f.toString() + "__" +
                Arrays.toString(start) + "__" +
                Arrays.toString(end);

            return sanitizeBasename(s) + ".dat";
        }

        /**
         * Generates a string suitable as a file name:
         * Brackets and parentheses are removed; space, slash, "=" sign and
         * comma characters are converted to underscores.
         *
         * @param str String.
         * @return a string.
         */
        private static String sanitizeBasename(String str) {
            final String repl = "_";
            return str
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .replaceAll("\\[", "")
                .replaceAll("\\]", "")
                .replaceAll("=", repl)
                .replaceAll(",\\s+", repl)
                .replaceAll(",", repl)
                .replaceAll("\\s", repl)
                .replaceAll("/", repl)
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");
        }
    }

    /**
     * Helper for preparing a {@link Task}.
     */
    public static class TaskAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor a,
                                         ParameterContext context)
            throws ArgumentsAggregationException {

            int index = 0; // Argument index.

            final TestFunction funcGen = a.get(index++, TestFunction.class);
            final int dim = a.getInteger(index++);
            final double[] optimum = toArrayOfDoubles(a.getString(index++), dim);
            final double minRadius = a.getDouble(index++);
            final double maxRadius = a.getDouble(index++);
            if (minRadius < 0 ||
                maxRadius < 0 ||
                minRadius >= maxRadius) {
                throw new ArgumentsAggregationException("radii");
            }
            final double pointTol = a.getDouble(index++);
            final int funcEval = a.getInteger(index++);
            final boolean withSA = a.getBoolean(index++);

            // Generate a start point within a spherical shell around the optimum.
            final UniformRandomProvider rng = OptimTestUtils.rng();
            final double radius = ContinuousUniformSampler.of(rng, minRadius, maxRadius).sample();
            final double[] start = UnitSphereSampler.of(rng, dim).sample();
            for (int i = 0; i < dim; i++) {
                start[i] *= radius;
                start[i] += optimum[i];
            }
            // Simplex side.
            final double sideLength = 0.5 * (maxRadius - minRadius);

            if (index == a.size()) {
                // No more arguments.
                return new Task(funcGen.withDimension(dim),
                                start,
                                optimum,
                                pointTol,
                                funcEval,
                                sideLength,
                                withSA,
                                null,
                                null);
            } else {
                // Debugging configuration.
                final String tracePrefix = a.getString(index++);
                final int[] spxIndices = tracePrefix == null ?
                    null :
                    toSimplexIndices(a.getString(index++), dim);

                return new Task(funcGen.withDimension(dim),
                                start,
                                optimum,
                                pointTol,
                                funcEval,
                                sideLength,
                                withSA,
                                tracePrefix,
                                spxIndices);
            }
        }

        /**
         * @param str Space-separated list of indices referring to
         * simplex's points (in the interval {@code [0, dim]}).
         * The string "LAST" will be converted to index {@code dim}.
         * The empty string, the string "ALL" and {@code null} will be
         * converted to all the indices in the interval {@code [0, dim]}.
         * @param dim Space dimension.
         * @return the indices (in the order specified in {@code str}).
         * @throws IllegalArgumentException if an index is out the
         * {@code [0, dim]} interval.
         */
        private static int[] toSimplexIndices(String str,
                                              int dim) {
            final List<Integer> list = new ArrayList<>();

            if (str == null ||
                str.equals("")) {
                for (int i = 0; i <= dim; i++) {
                    list.add(i);
                }
            } else {
                for (String s : str.split("\\s+")) {
                    if (s.equals("LAST")) {
                        list.add(dim);
                    } else if (str.equals("ALL")) {
                        for (int i = 0; i <= dim; i++) {
                            list.add(i);
                        }
                    } else {
                        final int index = Integer.valueOf(s);
                        if (index < 0 ||
                            index > dim) {
                            throw new IllegalArgumentException("index: " + index +
                                                               " (dim=" + dim + ")");
                        }
                        list.add(index);
                    }
                }
            }

            final int len = list.size();
            final int[] indices = new int[len];
            for (int i = 0; i < len; i++) {
                indices[i] = list.get(i);
            }

            return indices;
        }

        /**
         * @param params Comma-separated list of values.
         * @param dim Expected number of values.
         * @return an array of {@code double} values.
         * @throws ArgumentsAggregationException if the number of values
         * is not equal to {@code dim}.
         */
        private static double[] toArrayOfDoubles(String params,
                                                 int dim) {
            final String[] s = params.trim().split("\\s+");

            if (s.length != dim) {
                final String msg = "Expected " + dim + " values: " + Arrays.toString(s);
                throw new ArgumentsAggregationException(msg);
            }

            final double[] p = new double[dim];
            for (int i = 0; i < dim; i++) {
                p[i] = Double.valueOf(s[i]);
            }

            return p;
        }
    }
}
