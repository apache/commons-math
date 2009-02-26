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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.decomposition.NotPositiveDefiniteMatrixException;
import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.ObjectiveException;
import org.apache.commons.math.optimization.ObjectiveFunction;
import org.apache.commons.math.optimization.PointValuePair;
import org.apache.commons.math.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math.random.JDKRandomGenerator;
import org.apache.commons.math.random.RandomGenerator;
import org.apache.commons.math.random.RandomVectorGenerator;
import org.apache.commons.math.random.UncorrelatedRandomVectorGenerator;
import org.apache.commons.math.random.UniformRandomGenerator;
import org.apache.commons.math.stat.descriptive.moment.VectorialCovariance;
import org.apache.commons.math.stat.descriptive.moment.VectorialMean;

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
 * functions, unpredictable dicontinuities) or difficult (complexity,
 * computation cost). In the first cases, rather than an optimum, a
 * <em>not too bad</em> point is desired. In the latter cases, an
 * optimum is desired but cannot be reasonably found. In all cases
 * direct search methods can be useful.</p>
 *
 * <p>Simplex-based direct search methods are based on comparison of
 * the objective function values at the vertices of a simplex (which is a
 * set of n+1 points in dimension n) that is updated by the algorithms
 * steps.</p>
 *
 * <p>Optimization can be attempted either in single-start or in
 * multi-start mode. Multi-start is a traditional way to try to avoid
 * being trapped in a local optimum and miss the global optimum of a
 * function. It can also be used to verify the convergence of an
 * algorithm. The various multi-start-enabled <code>optimize</code>
 * methods return the best optimum found after all starts, and the
 * {@link #getOptimum getOptimum} method can be used to retrieve all
 * optima from all starts (including the one already provided by the
 * {@link #optimize(ObjectiveFunction, int, ConvergenceChecker, double[],
 * double[]) optimize} method).</p>
 *
 * <p>This class is the base class performing the boilerplate simplex
 * initialization and handling. The simplex update by itself is
 * performed by the derived classes according to the implemented
 * algorithms.</p>
 *
 * @see ObjectiveFunction
 * @see NelderMead
 * @see MultiDirectional
 * @version $Revision$ $Date$
 * @since 1.2
 */
public abstract class DirectSearchOptimizer implements Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -3913013760494455466L;

    /** Comparator for {@link PointValuePair} objects. */
    private static final Comparator<PointValuePair> PAIR_COMPARATOR =
        new Comparator<PointValuePair>() {
            public int compare(PointValuePair o1, PointValuePair o2) {
                if (o1 == null) {
                    return (o2 == null) ? 0 : +1;
                } else if (o2 == null) {
                    return -1;
                }
                return (o1.getValue() < o2.getValue()) ? -1 : ((o1 == o2) ? 0 : +1);
            }
        };

    /** Simplex. */
    protected PointValuePair[] simplex;

    /** Objective function. */
    private ObjectiveFunction f;

    /** Indicator for minimization. */
    private boolean minimizing;

    /** Number of evaluations already performed for the current start. */
    private int evaluations;

    /** Number of evaluations already performed for all starts. */
    private int totalEvaluations;

    /** Number of starts to go. */
    private int starts;

    /** Random generator for multi-start. */
    private RandomVectorGenerator generator;

    /** Found optima. */
    private PointValuePair[] optima;

    /** Simple constructor.
     */
    protected DirectSearchOptimizer() {
    }

    /** Optimizes an objective function.
     * <p>The initial simplex is built from two vertices that are
     * considered to represent two opposite vertices of a box parallel
     * to the canonical axes of the space. The simplex is the subset of
     * vertices encountered while going from vertexA to vertexB
     * traveling along the box edges only. This can be seen as a scaled
     * regular simplex using the projected separation between the given
     * points as the scaling factor along each coordinate axis.</p>
     * <p>The optimization is performed in single-start mode.</p>
     * @param f objective function
     * @param maxEvaluations maximal number of function calls for each
     * start (note that the number will be checked <em>after</em>
     * complete simplices have been evaluated, this means that in some
     * cases this number will be exceeded by a few units, depending on
     * the dimension of the problem)
     * @param checker object to use to check for convergence
     * @param minimizing if true, function must be minimize otherwise it must be maximized
     * @param vertexA first vertex
     * @param vertexB last vertex
     * @return the point/value pairs giving the optimal value for objective function
     * @exception ObjectiveException if the objective function throws one during
     * the search
     * @exception ConvergenceException if none of the starts did
     * converge (it is not thrown if at least one start did converge)
     */
    public PointValuePair optimize(final ObjectiveFunction f, final int maxEvaluations,
                                   final ConvergenceChecker checker, final boolean minimizing,
                                   final double[] vertexA, final double[] vertexB)
        throws ObjectiveException, ConvergenceException {

        // set up optimizer
        buildSimplex(vertexA, vertexB);
        setSingleStart();

        // compute optimum
        return optimize(f, maxEvaluations, checker, minimizing);

    }

    /** Optimizes an objective function.
     * <p>The initial simplex is built from two vertices that are
     * considered to represent two opposite vertices of a box parallel
     * to the canonical axes of the space. The simplex is the subset of
     * vertices encountered while going from vertexA to vertexB
     * traveling along the box edges only. This can be seen as a scaled
     * regular simplex using the projected separation between the given
     * points as the scaling factor along each coordinate axis.</p>
     * <p>The optimization is performed in multi-start mode.</p>
     * @param f objective function
     * @param maxEvaluations maximal number of function calls for each
     * start (note that the number will be checked <em>after</em>
     * complete simplices have been evaluated, this means that in some
     * cases this number will be exceeded by a few units, depending on
     * the dimension of the problem)
     * @param checker object to use to check for convergence
     * @param minimizing if true, function must be minimize otherwise it must be maximized
     * @param vertexA first vertex
     * @param vertexB last vertex
     * @param starts number of starts to perform (including the
     * first one), multi-start is disabled if value is less than or
     * equal to 1
     * @param seed seed for the random vector generator
     * @return the point/value pairs giving the optimal value for objective function
     * @exception ObjectiveException if the obective function throws one during
     * the search
     * @exception ConvergenceException if none of the starts did
     * converge (it is not thrown if at least one start did converge)
     */
    public PointValuePair optimize(final ObjectiveFunction f, final int maxEvaluations,
                                   final ConvergenceChecker checker, final boolean minimizing,
                                   final double[] vertexA, final double[] vertexB,
                                   final int starts, final long seed)
        throws ObjectiveException, ConvergenceException {

        // set up the simplex traveling around the box
        buildSimplex(vertexA, vertexB);

        // we consider the simplex could have been produced by a generator
        // having its mean value at the center of the box, the standard
        // deviation along each axe being the corresponding half size
        final double[] mean              = new double[vertexA.length];
        final double[] standardDeviation = new double[vertexA.length];
        for (int i = 0; i < vertexA.length; ++i) {
            mean[i]              = 0.5 * (vertexA[i] + vertexB[i]);
            standardDeviation[i] = 0.5 * Math.abs(vertexA[i] - vertexB[i]);
        }

        final RandomGenerator rg = new JDKRandomGenerator();
        rg.setSeed(seed);
        final UniformRandomGenerator urg = new UniformRandomGenerator(rg);
        final RandomVectorGenerator rvg =
            new UncorrelatedRandomVectorGenerator(mean, standardDeviation, urg);
        setMultiStart(starts, rvg);

        // compute optimum
        return optimize(f, maxEvaluations, checker, minimizing);

    }

    /** Optimizes an objective function.
     * <p>The simplex is built from all its vertices.</p>
     * <p>The optimization is performed in single-start mode.</p>
     * @param f objective function
     * @param maxEvaluations maximal number of function calls for each
     * start (note that the number will be checked <em>after</em>
     * complete simplices have been evaluated, this means that in some
     * cases this number will be exceeded by a few units, depending on
     * the dimension of the problem)
     * @param checker object to use to check for convergence
     * @param minimizing if true, function must be minimize otherwise it must be maximized
     * @param vertices array containing all vertices of the simplex
     * @return the point/value pairs giving the optimal value for objective function
     * @exception ObjectiveException if the objective function throws one during
     * the search
     * @exception ConvergenceException if none of the starts did
     * converge (it is not thrown if at least one start did converge)
     */
    public PointValuePair optimize(final ObjectiveFunction f, final int maxEvaluations,
                                   final ConvergenceChecker checker, final boolean minimizing,
                                   final double[][] vertices)
        throws ObjectiveException, ConvergenceException {

        // set up optimizer
        buildSimplex(vertices);
        setSingleStart();

        // compute optimum
        return optimize(f, maxEvaluations, checker, minimizing);

    }

    /** Optimizes an objective function.
     * <p>The simplex is built from all its vertices.</p>
     * <p>The optimization is performed in multi-start mode.</p>
     * @param f objective function
     * @param maxEvaluations maximal number of function calls for each
     * start (note that the number will be checked <em>after</em>
     * complete simplices have been evaluated, this means that in some
     * cases this number will be exceeded by a few units, depending on
     * the dimension of the problem)
     * @param checker object to use to check for convergence
     * @param minimizing if true, function must be minimize otherwise it must be maximized
     * @param vertices array containing all vertices of the simplex
     * @param starts number of starts to perform (including the
     * first one), multi-start is disabled if value is less than or
     * equal to 1
     * @param seed seed for the random vector generator
     * @return the point/value pairs giving the optimal value for objective function
     * @exception NotPositiveDefiniteMatrixException if the vertices
     * array is degenerated
     * @exception ObjectiveException if the objective function throws one during
     * the search
     * @exception ConvergenceException if none of the starts did
     * converge (it is not thrown if at least one start did converge)
     */
    public PointValuePair optimize(final ObjectiveFunction f, final int maxEvaluations,
                                   final ConvergenceChecker checker, final boolean minimizing,
                                   final double[][] vertices,
                                   final int starts, final long seed)
        throws NotPositiveDefiniteMatrixException, ObjectiveException, ConvergenceException {

        try {
            // store the points into the simplex
            buildSimplex(vertices);

            // compute the statistical properties of the simplex points
            final VectorialMean meanStat = new VectorialMean(vertices[0].length);
            final VectorialCovariance covStat = new VectorialCovariance(vertices[0].length, true);
            for (int i = 0; i < vertices.length; ++i) {
                meanStat.increment(vertices[i]);
                covStat.increment(vertices[i]);
            }
            final double[] mean = meanStat.getResult();
            final RealMatrix covariance = covStat.getResult();
            

            final RandomGenerator rg = new JDKRandomGenerator();
            rg.setSeed(seed);
            final RandomVectorGenerator rvg =
                new CorrelatedRandomVectorGenerator(mean,
                                                    covariance, 1.0e-12 * covariance.getNorm(),
                                                    new UniformRandomGenerator(rg));
            setMultiStart(starts, rvg);

            // compute optimum
            return optimize(f, maxEvaluations, checker, minimizing);

        } catch (DimensionMismatchException dme) {
            // this should not happen
            throw new MathRuntimeException(dme, "unexpected exception caught");
        }

    }

    /** Optimizes an objective function.
     * <p>The simplex is built randomly.</p>
     * <p>The optimization is performed in single-start mode.</p>
     * @param f objective function
     * @param maxEvaluations maximal number of function calls for each
     * start (note that the number will be checked <em>after</em>
     * complete simplices have been evaluated, this means that in some
     * cases this number will be exceeded by a few units, depending on
     * the dimension of the problem)
     * @param checker object to use to check for convergence
     * @param minimizing if true, function must be minimize otherwise it must be maximized
     * @param generator random vector generator
     * @return the point/value pairs giving the optimal value for objective function
     * @exception ObjectiveException if the objective function throws one during
     * the search
     * @exception ConvergenceException if none of the starts did
     * converge (it is not thrown if at least one start did converge)
     */
    public PointValuePair optimize(final ObjectiveFunction f, final int maxEvaluations,
                                   final ConvergenceChecker checker, final boolean minimizing,
                                   final RandomVectorGenerator generator)
        throws ObjectiveException, ConvergenceException {

        // set up optimizer
        buildSimplex(generator);
        setSingleStart();

        // compute optimum
        return optimize(f, maxEvaluations, checker, minimizing);

    }

    /** Optimizes an objective function.
     * <p>The simplex is built randomly.</p>
     * <p>The optimization is performed in multi-start mode.</p>
     * @param f objective function
     * @param maxEvaluations maximal number of function calls for each
     * start (note that the number will be checked <em>after</em>
     * complete simplices have been evaluated, this means that in some
     * cases this number will be exceeded by a few units, depending on
     * the dimension of the problem)
     * @param checker object to use to check for convergence
     * @param minimizing if true, function must be minimize otherwise it must be maximized
     * @param generator random vector generator
     * @param starts number of starts to perform (including the
     * first one), multi-start is disabled if value is less than or
     * equal to 1
     * @return the point/value pairs giving the optimal value for objective function
     * @exception ObjectiveException if the objective function throws one during
     * the search
     * @exception ConvergenceException if none of the starts did
     * converge (it is not thrown if at least one start did converge)
     */
    public PointValuePair optimize(final ObjectiveFunction f, final int maxEvaluations,
                                   final ConvergenceChecker checker, final boolean minimizing,
                                   final RandomVectorGenerator generator,
                                   final int starts)
        throws ObjectiveException, ConvergenceException {

        // set up optimizer
        buildSimplex(generator);
        setMultiStart(starts, generator);

        // compute optimum
        return optimize(f, maxEvaluations, checker, minimizing);

    }

    /** Build a simplex from two extreme vertices.
     * <p>The two vertices are considered to represent two opposite
     * vertices of a box parallel to the canonical axes of the
     * space. The simplex is the subset of vertices encountered while
     * going from vertexA to vertexB traveling along the box edges
     * only. This can be seen as a scaled regular simplex using the
     * projected separation between the given points as the scaling
     * factor along each coordinate axis.</p>
     * @param vertexA first vertex
     * @param vertexB last vertex
     */
    private void buildSimplex(final double[] vertexA, final double[] vertexB) {

        final int n = vertexA.length;
        simplex = new PointValuePair[n + 1];

        // set up the simplex traveling around the box
        for (int i = 0; i <= n; ++i) {
            final double[] vertex = new double[n];
            if (i > 0) {
                System.arraycopy(vertexB, 0, vertex, 0, i);
            }
            if (i < n) {
                System.arraycopy(vertexA, i, vertex, i, n - i);
            }
            simplex[i] = new PointValuePair(vertex, Double.NaN);
        }

    }

    /** Build a simplex from all its points.
     * @param vertices array containing all vertices of the simplex
     */
    private void buildSimplex(final double[][] vertices) {
        final int n = vertices.length - 1;
        simplex = new PointValuePair[n + 1];
        for (int i = 0; i <= n; ++i) {
            simplex[i] = new PointValuePair(vertices[i], Double.NaN);
        }
    }

    /** Build a simplex randomly.
     * @param generator random vector generator
     */
    private void buildSimplex(final RandomVectorGenerator generator) {

        // use first vector size to compute the number of points
        final double[] vertex = generator.nextVector();
        final int n = vertex.length;
        simplex = new PointValuePair[n + 1];
        simplex[0] = new PointValuePair(vertex, Double.NaN);

        // fill up the vertex
        for (int i = 1; i <= n; ++i) {
            simplex[i] = new PointValuePair(generator.nextVector(), Double.NaN);
        }

    }

    /** Set up single-start mode.
     */
    private void setSingleStart() {
        starts    = 1;
        generator = null;
        optima    = null;
    }

    /** Set up multi-start mode.
     * @param starts number of starts to perform (including the
     * first one), multi-start is disabled if value is less than or
     * equal to 1
     * @param generator random vector generator to use for restarts
     */
    private void setMultiStart(final int starts, final RandomVectorGenerator generator) {
        if (starts < 2) {
            this.starts    = 1;
            this.generator = null;
            optima         = null;
        } else {
            this.starts    = starts;
            this.generator = generator;
            optima         = null;
        }
    }

    /** Get all the optima found during the last call to {@link
     * #optimize(ObjectiveFunction, int, ConvergenceChecker, double[], double[])
     * minimize}.
     * <p>The optimizer stores all the optima found during a set of
     * restarts when multi-start mode is enabled. The {@link
     * #optimize(ObjectiveFunction, int, ConvergenceChecker, double[], double[])
     * optimize} method returns the best point only. This method
     * returns all the points found at the end of each starts, including
     * the best one already returned by the {@link #optimize(ObjectiveFunction,
     * int, ConvergenceChecker, double[], double[]) optimize} method.
     * The array as one element for each start as specified in the constructor
     * (it has one element only if optimizer has been set up for single-start).</p>
     * <p>The array containing the optimum is ordered with the results
     * from the runs that did converge first, sorted from lowest to
     * highest objective value if minimizing (from highest to lowest if maximizing),
     * and null elements corresponding to the runs that did not converge. This means
     * all elements will be null if the {@link #optimize(ObjectiveFunction, int,
     * ConvergenceChecker, double[], double[]) optimize} method did throw a {@link
     * ConvergenceException ConvergenceException}). This also means that if the first
     * element is non null, it is the best point found accross all starts.</p>
     * @return array containing the optima, or null if {@link
     * #optimize(ObjectiveFunction, int, ConvergenceChecker, double[], double[])
     * optimize} has not been called
     */
    public PointValuePair[] getOptima() {
        return (PointValuePair[]) optima.clone();
    }

    /** Optimizes an objective function.
     * @param f objective function
     * @param maxEvaluations maximal number of function calls for each
     * start (note that the number will be checked <em>after</em>
     * complete simplices have been evaluated, this means that in some
     * cases this number will be exceeded by a few units, depending on
     * the dimension of the problem)
     * @param checker object to use to check for convergence
     * @param minimizing if true, function must be minimize otherwise it must be maximized
     * @return the point/value pairs giving the optimal value for objective function
     * @exception ObjectiveException if the objective function throws one during
     * the search
     * @exception ConvergenceException if none of the starts did
     * converge (it is not thrown if at least one start did converge)
     */
    private PointValuePair optimize(final ObjectiveFunction f, final int maxEvaluations,
                                   final ConvergenceChecker checker, final boolean minimizing)
        throws ObjectiveException, ConvergenceException {

        this.f          = f;
        this.minimizing = minimizing;
        optima = new PointValuePair[starts];
        totalEvaluations = 0;

        // multi-start loop
        for (int i = 0; i < starts; ++i) {

            evaluations = 0;
            evaluateSimplex();

            for (boolean loop = true; loop;) {
                if (checker.converged(simplex)) {
                    // we have found an optimum
                    optima[i] = simplex[0];
                    loop = false;
                } else if (evaluations >= maxEvaluations) {
                    // this start did not converge, try a new one
                    optima[i] = null;
                    loop = false;
                } else {
                    iterateSimplex();
                }
            }

            totalEvaluations += evaluations;

            if (i < (starts - 1)) {
                // restart
                buildSimplex(generator);
            }

        }

        // sort the optima from best to poorest, followed by
        // null elements
        Arrays.sort(optima, PAIR_COMPARATOR);

        if (!minimizing) {
            // revert objective function sign to match user original definition
            for (int i = 0; i < optima.length; ++i) {
                final PointValuePair current = optima[i];
                if (current != null) {
                    optima[i] = new PointValuePair(current.getPoint(), -current.getValue());
                }
            }
        }

        // return the found point given the best objective function value
        if (optima[0] == null) {
            throw new ConvergenceException(
                    "none of the {0} start points lead to convergence",
                    starts);
        }
        return optima[0];

    }

    /** Get the total number of evaluations of the objective function.
     * <p>
     * The total number of evaluations includes all evaluations for all
     * starts if in optimization was done in multi-start mode.
     * </p>
     * @return total number of evaluations of the objective function
     */
    public int getTotalEvaluations() {
        return totalEvaluations;
    }

    /** Compute the next simplex of the algorithm.
     * @exception ObjectiveException if the function cannot be evaluated at
     * some point
     */
    protected abstract void iterateSimplex() throws ObjectiveException;

    /** Evaluate the objective function on one point.
     * <p>A side effect of this method is to count the number of
     * function evaluations</p>
     * @param x point on which the objective function should be evaluated
     * @return objective function value at the given point
     * @exception ObjectiveException if no value can be computed for the parameters
     */
    protected double evaluate(final double[] x) throws ObjectiveException {
        evaluations++;
        return minimizing ? f.objective(x) : -f.objective(x);
    }

    /** Evaluate all the non-evaluated points of the simplex.
     * @exception ObjectiveException if no value can be computed for the parameters
     */
    protected void evaluateSimplex() throws ObjectiveException {

        // evaluate the objective function at all non-evaluated simplex points
        for (int i = 0; i < simplex.length; ++i) {
            PointValuePair pair = simplex[i];
            if (Double.isNaN(pair.getValue())) {
                simplex[i] = new PointValuePair(pair.getPoint(), evaluate(pair.getPoint()));
            }
        }

        // sort the simplex from best to poorest
        Arrays.sort(simplex, PAIR_COMPARATOR);

    }

    /** Replace the worst point of the simplex by a new point.
     * @param pointValuePair point to insert
     */
    protected void replaceWorstPoint(PointValuePair pointValuePair) {
        int n = simplex.length - 1;
        for (int i = 0; i < n; ++i) {
            if (simplex[i].getValue() > pointValuePair.getValue()) {
                PointValuePair tmp = simplex[i];
                simplex[i]        = pointValuePair;
                pointValuePair     = tmp;
            }
        }
        simplex[n] = pointValuePair;
    }

}
