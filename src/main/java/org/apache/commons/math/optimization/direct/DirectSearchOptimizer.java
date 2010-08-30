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

package org.apache.commons.math.optimization.direct;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.util.Incrementor;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.exception.MaxCountExceededException;
import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.MultivariateRealOptimizer;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;
import org.apache.commons.math.optimization.general.AbstractScalarOptimizer;

/**
 * This class implements simplex-based direct search optimization
 * algorithms.
 *
 * <p>Direct search methods only use objective function values, they don't
 * need derivatives and don't either try to compute approximation of
 * the derivatives. According to a 1996 paper by Margaret H. Wright
 * (<a href="http://cm.bell-labs.com/cm/cs/doc/96/4-02.ps.gz">Direct
 * Search Methods: Once Scorned, Now Respectable</a>), they are used
 * when either the computation of the derivative is impossible (noisy
 * functions, unpredictable discontinuities) or difficult (complexity,
 * computation cost). In the first cases, rather than an optimum, a
 * <em>not too bad</em> point is desired. In the latter cases, an
 * optimum is desired but cannot be reasonably found. In all cases
 * direct search methods can be useful.</p>
 *
 * <p>Simplex-based direct search methods are based on comparison of
 * the objective function values at the vertices of a simplex (which is a
 * set of n+1 points in dimension n) that is updated by the algorithms
 * steps.<p>
 *
 * <p>The initial configuration of the simplex can be set using either
 * {@link #setStartConfiguration(double[])} or {@link
 * #setStartConfiguration(double[][])}. If neither method has been called
 * before optimization is attempted, an explicit call to the first method
 * with all steps set to +1 is triggered, thus building a default
 * configuration from a unit hypercube. Each call to {@link
 * #optimize(MultivariateRealFunction, GoalType, double[]) optimize} will reuse
 * the current start configuration and move it such that its first vertex
 * is at the provided start point of the optimization. If the {@code optimize}
 * method is called to solve a different problem and the number of parameters
 * change, the start configuration will be reset to a default one with the
 * appropriate dimensions.</p>
 *
 * <p>If {@link #setConvergenceChecker(ConvergenceChecker)} is not called,
 * a default {@link SimpleScalarValueChecker} is used.</p>
 *
 * <p>Convergence is checked by providing the <em>worst</em> points of
 * previous and current simplex to the convergence checker, not the best ones.</p>
 *
 * <p>This class is the base class performing the boilerplate simplex
 * initialization and handling. The simplex update by itself is
 * performed by the derived classes according to the implemented
 * algorithms.</p>
 *
 * implements MultivariateRealOptimizer since 2.0
 *
 * @see MultivariateRealFunction
 * @see NelderMead
 * @see MultiDirectional
 * @version $Revision$ $Date$
 * @since 1.2
 */
public abstract class DirectSearchOptimizer
    extends AbstractScalarOptimizer
    implements MultivariateRealOptimizer {
    /** Simplex. */
    protected RealPointValuePair[] simplex;
    /** Start simplex configuration. */
    private double[][] startConfiguration;

    /**
     * Default constructor.
     */
    protected DirectSearchOptimizer() {
        setConvergenceChecker(new SimpleScalarValueChecker());
    }

    /**
     * Set start configuration for simplex.
     *
     * <p>The start configuration for simplex is built from a box parallel to
     * the canonical axes of the space. The simplex is the subset of vertices
     * of a box parallel to the canonical axes. It is built as the path followed
     * while traveling from one vertex of the box to the diagonally opposite
     * vertex moving only along the box edges. The first vertex of the box will
     * be located at the start point of the optimization.</p>
     * <p>As an example, in dimension 3 a simplex has 4 vertices. Setting the
     * steps to (1, 10, 2) and the start point to (1, 1, 1) would imply the
     * start simplex would be: { (1, 1, 1), (2, 1, 1), (2, 11, 1), (2, 11, 3) }.
     * The first vertex would be set to the start point at (1, 1, 1) and the
     * last vertex would be set to the diagonally opposite vertex at (2, 11, 3).</p>
     *
     * @param steps Steps along the canonical axes representing box edges. They
     * may be negative but not zero.
     * @throws IllegalArgumentException if one step is zero.
     */
    public void setStartConfiguration(final double[] steps)
        throws IllegalArgumentException {
        // only the relative position of the n final vertices with respect
        // to the first one are stored
        final int n = steps.length;
        startConfiguration = new double[n][n];
        for (int i = 0; i < n; ++i) {
            final double[] vertexI = startConfiguration[i];
            for (int j = 0; j < i + 1; ++j) {
                if (steps[j] == 0.0) {
                    throw MathRuntimeException.createIllegalArgumentException(
                          LocalizedFormats.EQUAL_VERTICES_IN_SIMPLEX, j, j + 1);
                }
                System.arraycopy(steps, 0, vertexI, 0, j + 1);
            }
        }
    }

    /**
     * Set start configuration for simplex.
     * The real initial simplex will be set up by moving the reference
     * simplex such that its first point is located at the start point of the
     * optimization.
     *
     * @param referenceSimplex Reference simplex.
     * @throws IllegalArgumentException if the reference simplex does not
     * contain at least one point, or if there is a dimension mismatch
     * in the reference simplex or if one of its vertices is duplicated.
     */
    public void setStartConfiguration(final double[][] referenceSimplex)
        throws IllegalArgumentException {

        // only the relative position of the n final vertices with respect
        // to the first one are stored
        final int n = referenceSimplex.length - 1;
        if (n < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.SIMPLEX_NEED_ONE_POINT);
        }
        startConfiguration = new double[n][n];
        final double[] ref0 = referenceSimplex[0];

        // vertices loop
        for (int i = 0; i < n + 1; ++i) {

            final double[] refI = referenceSimplex[i];

            // safety checks
            if (refI.length != n) {
                throw new DimensionMismatchException(refI.length, n);
            }
            for (int j = 0; j < i; ++j) {
                final double[] refJ = referenceSimplex[j];
                boolean allEquals = true;
                for (int k = 0; k < n; ++k) {
                    if (refI[k] != refJ[k]) {
                        allEquals = false;
                        break;
                    }
                }
                if (allEquals) {
                    throw MathRuntimeException.createIllegalArgumentException(
                          LocalizedFormats.EQUAL_VERTICES_IN_SIMPLEX, i, j);
                }
            }

            // store vertex i position relative to vertex 0 position
            if (i > 0) {
                final double[] confI = startConfiguration[i - 1];
                for (int k = 0; k < n; ++k) {
                    confI[k] = refI[k] - ref0[k];
                }
            }
        }
    }

    /** {@inheritDoc} */
    protected RealPointValuePair doOptimize()
        throws FunctionEvaluationException {

        final double[] startPoint = getStartPoint();
        if ((startConfiguration == null) ||
            (startConfiguration.length != startPoint.length)) {
            // No initial configuration has been set up for simplex
            // build a default one from a unit hypercube.
            final double[] unit = new double[startPoint.length];
            Arrays.fill(unit, 1.0);
            setStartConfiguration(unit);
        }
        
        final boolean isMinim = (getGoalType() == GoalType.MINIMIZE);
        final Comparator<RealPointValuePair> comparator
            = new Comparator<RealPointValuePair>() {
            public int compare(final RealPointValuePair o1,
                               final RealPointValuePair o2) {
                final double v1 = o1.getValue();
                final double v2 = o2.getValue();
                return isMinim ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        };

        // Initialize search.
        buildSimplex(startPoint);
        evaluateSimplex(comparator);

        RealPointValuePair[] previous = new RealPointValuePair[simplex.length];
        int iteration = 0;
        final ConvergenceChecker<RealPointValuePair> checker = getConvergenceChecker();
        while (true) {
            if (iteration > 0) {
                boolean converged = true;
                for (int i = 0; i < simplex.length; ++i) {
                    converged &= checker.converged(iteration, previous[i], simplex[i]);
                }
                if (converged) {
                    // we have found an optimum
                    return simplex[0];
                }
            }

            // We still need to search.
            System.arraycopy(simplex, 0, previous, 0, simplex.length);
            iterateSimplex(comparator);
            ++iteration;
        }
    }

    /**
     * Compute the next simplex of the algorithm.
     *
     * @param comparator Comparator to use to sort simplex vertices from best to worst.
     * @throws FunctionEvaluationException if the function cannot be evaluated at
     * some point.
     * @throws TooManyEvaluationsException if the algorithm fails to converge.
     * @throws DimensionMismatchException if the start point dimension is wrong.
     */
    protected abstract void iterateSimplex(final Comparator<RealPointValuePair> comparator)
        throws FunctionEvaluationException;

    /**
     * Build an initial simplex.
     *
     * @param startPoint Start point for optimization.
     * @throws DimensionMismatchException if the start point does not match
     * simplex dimension.
     */
    private void buildSimplex(final double[] startPoint) {

        final int n = startPoint.length;
        if (n != startConfiguration.length) {
            throw new DimensionMismatchException(n, startConfiguration.length);
        }

        // set first vertex
        simplex = new RealPointValuePair[n + 1];
        simplex[0] = new RealPointValuePair(startPoint, Double.NaN);

        // set remaining vertices
        for (int i = 0; i < n; ++i) {
            final double[] confI = startConfiguration[i];
            final double[] vertexI = new double[n];
            for (int k = 0; k < n; ++k) {
                vertexI[k] = startPoint[k] + confI[k];
            }
            simplex[i + 1] = new RealPointValuePair(vertexI, Double.NaN);
        }
    }

    /**
     * Evaluate all the non-evaluated points of the simplex.
     *
     * @param comparator Comparator to use to sort simplex vertices from best to worst.
     * @throws FunctionEvaluationException if no value can be computed for the parameters.
     * @throws TooManyEvaluationsException if the maximal number of evaluations is exceeded.
     */
    protected void evaluateSimplex(final Comparator<RealPointValuePair> comparator)
        throws FunctionEvaluationException {

        // Evaluate the objective function at all non-evaluated simplex points.
        for (int i = 0; i < simplex.length; ++i) {
            final RealPointValuePair vertex = simplex[i];
            final double[] point = vertex.getPointRef();
            if (Double.isNaN(vertex.getValue())) {
                simplex[i] = new RealPointValuePair(point, computeObjectiveValue(point), false);
            }
        }

        // Sort the simplex from best to worst.
        Arrays.sort(simplex, comparator);
    }

    /**
     * Replace the worst point of the simplex by a new point.
     *
     * @param pointValuePair Point to insert.
     * @param comparator Comparator to use to sort simplex vertices from best to worst.
     */
    protected void replaceWorstPoint(RealPointValuePair pointValuePair,
                                     final Comparator<RealPointValuePair> comparator) {
        int n = simplex.length - 1;
        for (int i = 0; i < n; ++i) {
            if (comparator.compare(simplex[i], pointValuePair) > 0) {
                RealPointValuePair tmp = simplex[i];
                simplex[i] = pointValuePair;
                pointValuePair = tmp;
            }
        }
        simplex[n] = pointValuePair;
    }
}
