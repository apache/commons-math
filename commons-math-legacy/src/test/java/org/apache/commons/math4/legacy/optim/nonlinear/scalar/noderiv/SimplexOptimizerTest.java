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
import org.opentest4j.AssertionFailedError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.TestFunction;

/**
 * Tests for {@link SimplexOptimizer simplex-based algorithms}.
 */
public class SimplexOptimizerTest {
    private static final String NELDER_MEAD_INPUT_FILE = "std_test_func.simplex.nelder_mead.csv";
    private static final String MULTIDIRECTIONAL_INPUT_FILE = "std_test_func.simplex.multidirectional.csv";

    @ParameterizedTest
    @CsvFileSource(resources = NELDER_MEAD_INPUT_FILE)
    void testFunctionWithNelderMead(@AggregateWith(TaskAggregator.class) Task task) {
        task.run(new NelderMeadTransform());
    }

    @ParameterizedTest
    @CsvFileSource(resources = MULTIDIRECTIONAL_INPUT_FILE)
    void testFunctionWithMultiDirectional(@AggregateWith(TaskAggregator.class) Task task) {
        task.run(new MultiDirectionalTransform());
    }

    /**
     * Optimization task.
     */
    public static class Task {
        /** Function evaluations hard count (debugging). */
        private static final int FUNC_EVAL_DEBUG = 20000;
        /** Default convergence criterion. */
        private static final double CONVERGENCE_CHECK = 1e-9;
        /** Default simplex size. */
        private static final double SIDE_LENGTH = 1;
        /** Function. */
        private final MultivariateFunction f;
        /** Initial value. */
        private final double[] start;
        /** Optimum. */
        private final double[] optimum;
        /** Tolerance. */
        private final double pointTolerance;
        /** Allowed function evaluations. */
        private final int functionEvaluations;
        /** Repeats on failure. */
        private final int repeatsOnFailure;
        /** Range of random noise. */
        private double jitter;

        /**
         * @param f Test function.
         * @param start Start point.
         * @param optimum Optimum.
         * @param pointTolerance Allowed distance between result and
         * {@code optimum}.
         * @param functionEvaluations Allowed number of function evaluations.
         * @param repeatsOnFailure Maximum number of times to rerun when an
         * {@link AssertionFailedError} is thrown.
         * @param jitter Size of random jitter.
         */
        Task(MultivariateFunction f,
             double[] start,
             double[] optimum,
             double pointTolerance,
             int functionEvaluations,
             int repeatsOnFailure,
             double jitter) {
            this.f = f;
            this.start = start;
            this.optimum = optimum;
            this.pointTolerance = pointTolerance;
            this.functionEvaluations = functionEvaluations;
            this.repeatsOnFailure = repeatsOnFailure;
            this.jitter = jitter;
        }

        @Override
        public String toString() {
            return f.toString();
        }

        /**
         * @param factory Simplex transform factory.
         */
        public void run(Simplex.TransformFactory factory) {
            // Let run with a maximum number of evaluations larger than expected
            // (as specified by "functionEvaluations") in order to have the unit
            // test failure message (see assertion below) report the actual number
            // required by the current code.
            final int maxEval = Math.max(functionEvaluations, FUNC_EVAL_DEBUG);

            int currentRetry = -1;
            AssertionFailedError lastFailure = null;
            while (currentRetry++ <= repeatsOnFailure) {
                try {
                    final String name = f.toString();

                    final SimplexOptimizer optim = new SimplexOptimizer(-1, CONVERGENCE_CHECK);
                    final Simplex initialSimplex =
                        Simplex.alongAxes(OptimTestUtils.point(start.length,
                                                               SIDE_LENGTH,
                                                               jitter));
                    final double[] startPoint = OptimTestUtils.point(start, jitter);
                    final PointValuePair result =
                        optim.optimize(new MaxEval(maxEval),
                                       new ObjectiveFunction(f),
                                       GoalType.MINIMIZE,
                                       new InitialGuess(startPoint),
                                       initialSimplex,
                                       factory);

                    final double[] endPoint = result.getPoint();
                    final double funcValue = result.getValue();
                    final double dist = MathArrays.distance(optimum, endPoint);
                    Assertions.assertEquals(0d, dist, pointTolerance,
                                            name + ": distance to optimum" +
                                            " f(" + Arrays.toString(endPoint) + ")=" +
                                            funcValue);

                    final int nEval = optim.getEvaluations();
                    Assertions.assertTrue(nEval < functionEvaluations,
                                          name + ": nEval=" + nEval);

                    break; // Assertions passed: Retry not neccessary.
                } catch (AssertionFailedError e) {
                    if (currentRetry >= repeatsOnFailure) {
                        // Allowed repeats have been exhausted: Bail out.
                        throw e;
                    }
                }
            }
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
            final double[] start = toArrayOfDoubles(a.getString(index++), dim);
            final double[] optimum = toArrayOfDoubles(a.getString(index++), dim);
            final double pointTol = a.getDouble(index++);
            final int funcEval = a.getInteger(index++);
            final int repeat = a.getInteger(index++);
            final double jitter = a.getDouble(index++);

            return new Task(funcGen.withDimension(dim),
                            start,
                            optimum,
                            pointTol,
                            funcEval,
                            repeat,
                            jitter);
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
