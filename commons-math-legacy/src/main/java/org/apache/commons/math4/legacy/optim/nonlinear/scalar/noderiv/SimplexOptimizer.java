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
import java.util.Comparator;
import java.util.Collections;
import java.util.function.UnaryOperator;

import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.legacy.exception.ConvergenceException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.optim.ConvergenceChecker;
import org.apache.commons.math4.legacy.optim.OptimizationData;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.SimpleValueChecker;
import org.apache.commons.math4.legacy.optim.InitialGuess;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.SimulatedAnnealing;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.PopulationSize;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.ObjectiveFunction;

/**
 * This class implements simplex-based direct search optimization.
 *
 * <p>
 * Direct search methods only use objective function values, they do
 * not need derivatives and don't either try to compute approximation
 * of the derivatives. According to a 1996 paper by Margaret H. Wright
 * (<a href="http://cm.bell-labs.com/cm/cs/doc/96/4-02.ps.gz">Direct
 * Search Methods: Once Scorned, Now Respectable</a>), they are used
 * when either the computation of the derivative is impossible (noisy
 * functions, unpredictable discontinuities) or difficult (complexity,
 * computation cost). In the first cases, rather than an optimum, a
 * <em>not too bad</em> point is desired. In the latter cases, an
 * optimum is desired but cannot be reasonably found. In all cases
 * direct search methods can be useful.
 *
 * <p>
 * Simplex-based direct search methods are based on comparison of
 * the objective function values at the vertices of a simplex (which is a
 * set of n+1 points in dimension n) that is updated by the algorithms
 * steps.
 *
 * <p>
 * In addition to those documented in
 * {@link MultivariateOptimizer#optimize(OptimizationData[]) MultivariateOptimizer},
 * an instance of this class will register the following data:
 * <ul>
 *  <li>{@link Simplex}</li>
 *  <li>{@link Simplex.TransformFactory}</li>
 *  <li>{@link SimulatedAnnealing}</li>
 *  <li>{@link PopulationSize}</li>
 * </ul>
 *
 * <p>
 * Each call to {@code optimize} will re-use the start configuration of
 * the current simplex and move it such that its first vertex is at the
 * provided start point of the optimization.
 * If the {@code optimize} method is called to solve a different problem
 * and the number of parameters change, the simplex must be re-initialized
 * to one with the appropriate dimensions.
 *
 * <p>
 * Convergence is considered achieved when <em>all</em> the simplex points
 * have converged.
 * <p>
 * Whenever {@link SimulatedAnnealing simulated annealing (SA)} is activated,
 * the SA phase will terminate when the temperature reaches \( 10^{-2} \).
 * Indeed the chance of accepting the alternative state is vanishingly small.
 * It does not mean however that the optimization has converged.
 * In such a case, the {@link PopulationSize} argument to method
 * {@link #optimize(OptimizationData[]) optimize} will trigger an additional
 * "best list" search.
 *
 * <p>
 * This implementation does not directly support constrained optimization
 * with simple bounds.
 * The call to {@link #optimize(OptimizationData[]) optimize} will throw
 * {@link MathUnsupportedOperationException} if bounds are passed to it.
 *
 * @see NelderMeadTransform
 * @see MultiDirectionalTransform
 * @see HedarFukushimaTransform
 */
public class SimplexOptimizer extends MultivariateOptimizer {
    /** Simplex update function factory. */
    private Simplex.TransformFactory updateRule;
    /** Initial simplex. */
    private Simplex initialSimplex;
    /** Simulated annealing setup (optional). */
    private SimulatedAnnealing simulatedAnnealing = null;
    /** Number of additional optimizations (optional). */
    private int bestListSize = 0;

    /**
     * @param checker Convergence checker.
     */
    public SimplexOptimizer(ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }

    /**
     * @param rel Relative threshold.
     * @param abs Absolute threshold.
     */
    public SimplexOptimizer(double rel,
                            double abs) {
        this(new SimpleValueChecker(rel, abs));
    }

    /** {@inheritDoc} */
    @Override
    protected PointValuePair doOptimize() {
        checkParameters();

        // Indirect call to "computeObjectiveValue" in order to update the
        // evaluations counter.
        final MultivariateFunction evalFunc = this::computeObjectiveValue;

        final boolean isMinim = getGoalType() == GoalType.MINIMIZE;
        final Comparator<PointValuePair> comparator = (o1, o2) -> {
            final double v1 = o1.getValue();
            final double v2 = o2.getValue();
            return isMinim ? Double.compare(v1, v2) : Double.compare(v2, v1);
        };

        // Start points for additional search.
        final List<PointValuePair> bestList = new ArrayList<>();

        Simplex currentSimplex = initialSimplex.translate(getStartPoint()).evaluate(evalFunc, comparator);
        double temperature = Double.NaN; // Only used with simulated annealing.
        Simplex previousSimplex = null;

        if (simulatedAnnealing != null) {
            temperature =
                temperature(currentSimplex.get(0),
                            currentSimplex.get(currentSimplex.getDimension()),
                            simulatedAnnealing.getStartProbability());
        }

        while (true) {
            if (previousSimplex != null) { // Skip check at first iteration.
                if (hasConverged(previousSimplex, currentSimplex)) {
                    return currentSimplex.get(0);
                }
            }

            // We still need to search.
            previousSimplex = currentSimplex;

            if (simulatedAnnealing != null) {
                // Update current temperature.
                temperature =
                    simulatedAnnealing.getCoolingSchedule().apply(temperature,
                                                                  currentSimplex);

                final double endTemperature =
                    temperature(currentSimplex.get(0),
                                currentSimplex.get(currentSimplex.getDimension()),
                                simulatedAnnealing.getEndProbability());

                if (temperature < endTemperature) {
                    break;
                }

                final UnaryOperator<Simplex> update =
                    updateRule.create(evalFunc,
                                      comparator,
                                      simulatedAnnealing.metropolis(temperature));

                for (int i = 0; i < simulatedAnnealing.getEpochDuration(); i++) {
                    currentSimplex = update.apply(currentSimplex).evaluate(evalFunc, comparator);
                }
            } else {
                // No simulated annealing.
                final UnaryOperator<Simplex> update =
                    updateRule.create(evalFunc, comparator, null);

                currentSimplex = update.apply(currentSimplex).evaluate(evalFunc, comparator);
            }

            if (bestListSize != 0) {
                // Store best points.
                for (int i = 0; i < currentSimplex.getSize(); i++) {
                    keepIfBetter(currentSimplex.get(i), comparator, bestList, bestListSize);
                }
            }

            incrementIterationCount();
        }

        // No convergence.
        if (bestList.isEmpty()) {
            // Bail out.
            throw new ConvergenceException();
        } else {
            // Additional optimizations.
            return bestListSearch(evalFunc, comparator, bestList);
        }
    }

    /**
     * Scans the list of (required and optional) optimization data that
     * characterize the problem.
     *
     * @param optData Optimization data.
     * The following data will be looked for:
     * <ul>
     *  <li>{@link Simplex}</li>
     *  <li>{@link Simplex.TransformFactory}</li>
     *  <li>{@link SimulatedAnnealing}</li>
     *  <li>{@link PopulationSize}</li>
     * </ul>
     */
    @Override
    protected void parseOptimizationData(OptimizationData... optData) {
        // Allow base class to register its own data.
        super.parseOptimizationData(optData);

        // The existing values (as set by the previous call) are reused
        // if not provided in the argument list.
        for (OptimizationData data : optData) {
            if (data instanceof Simplex) {
                initialSimplex = (Simplex) data;
            } else if (data instanceof Simplex.TransformFactory) {
                updateRule = (Simplex.TransformFactory) data;
            } else if (data instanceof SimulatedAnnealing) {
                simulatedAnnealing = (SimulatedAnnealing) data;
            } else if (data instanceof PopulationSize) {
                bestListSize = ((PopulationSize) data).getPopulationSize();
            }
        }
    }

    /**
     * Detects whether the simplex has shrunk below the user-defined
     * tolerance.
     *
     * @param previous Simplex at previous iteration.
     * @param current Simplex at current iteration.
     * @return {@code true} if convergence is considered achieved.
     */
    private boolean hasConverged(Simplex previous,
                                 Simplex current) {
        final ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();

        for (int i = 0; i < current.getSize(); i++) {
            final PointValuePair prev = previous.get(i);
            final PointValuePair curr = current.get(i);

            if (!checker.converged(getIterations(), prev, curr)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @throws MathUnsupportedOperationException if bounds were passed to the
     * {@link #optimize(OptimizationData[]) optimize} method.
     * @throws NullPointerException if no initial simplex or no transform rule
     * was passed to the {@link #optimize(OptimizationData[]) optimize} method.
     */
    private void checkParameters() {
        if (updateRule == null) {
            throw new NullPointerException("No update rule");
        }
        if (initialSimplex == null) {
            throw new NullPointerException("No initial simplex");
        }
        if (getLowerBound() != null ||
            getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT);
        }
    }

    /**
     * Computes the temperature as a function of the acceptance probability
     * and the fitness difference between two of the simplex vertices (usually
     * the best and worst points).
     *
     * @param p1 Simplex point.
     * @param p2 Simplex point.
     * @param prob Acceptance probability.
     * @return the temperature.
     */
    private double temperature(PointValuePair p1,
                               PointValuePair p2,
                               double prob) {
        return -Math.abs(p1.getValue() - p2.getValue()) / Math.log(prob);
    }

    /**
     * Stores the given {@code candidate} if its fitness is better than
     * that of the last (assumed to be the worst) point in {@code list}.
     *
     * @param candidate Point to be stored.
     * @param comp Fitness comparator.
     * @param list Starting points (modified in-place).
     * @param max Maximum size of the {@code list}.
     */
    private static void keepIfBetter(PointValuePair candidate,
                                     Comparator<PointValuePair> comp,
                                     List<PointValuePair> list,
                                     int max) {
        final int listSize = list.size();
        final double[] candidatePoint = candidate.getPoint();
        if (listSize == 0) {
            list.add(candidate);
        } else if (listSize < max) {
            // List is not fully populated yet.
            for (PointValuePair p : list) {
                final double[] pPoint = p.getPoint();
                if (Arrays.equals(pPoint, candidatePoint)) {
                    // Point was already stored.
                    return;
                } else {
                    // Store candidate.
                    list.add(candidate);
                    return;
                }
            }
        } else {
            final int last = max - 1;
            if (comp.compare(candidate, list.get(last)) < 0) {
                for (PointValuePair p : list) {
                    final double[] pPoint = p.getPoint();
                    if (Arrays.equals(pPoint, candidatePoint)) {
                        // Point was already stored.
                        return;
                    }
                }

                // Store better candidate and reorder the list.
                list.set(last, candidate);
                Collections.sort(list, comp);
            }
        }
    }

    /**
     * Computes the smallest distance between the given {@code point}
     * and any of the other points in the {@code list}.
     *
     * @param point Point.
     * @param list List.
     * @return the smallest distance.
     */
    private static double shortestDistance(PointValuePair point,
                                           List<PointValuePair> list) {
        double minDist = Double.POSITIVE_INFINITY;

        final double[] p = point.getPoint();
        for (PointValuePair other : list) {
            final double[] pOther = other.getPoint();
            if (!Arrays.equals(p, pOther)) {
                final double dist = MathArrays.distance(p, pOther);
                if (dist < minDist) {
                    minDist = dist;
                }
            }
        }

        return minDist;
    }

    /**
     * Perform additional optimizations.
     *
     * @param evalFunc Objective function.
     * @param comp Fitness comparator.
     * @param starts Starting points.
     * @return the optimum.
     */
    private PointValuePair bestListSearch(MultivariateFunction evalFunc,
                                          Comparator<PointValuePair> comp,
                                          List<PointValuePair> starts) {
        PointValuePair best = starts.get(0); // Overall best result.

        // Additional local optimizations using each of the best
        // points visited during the main search.
        for (PointValuePair p : starts) {
            // Find shortest distance to the other points.
            final double dist = shortestDistance(p, starts);
            final double[] init = p.getPoint();
            // Create smaller initial simplex.
            final Simplex simplex = Simplex.equalSidesAlongAxes(init.length,
                                                                0.5 * dist);

            final PointValuePair r = directSearch(init,
                                                  simplex,
                                                  evalFunc,
                                                  getConvergenceChecker(),
                                                  getGoalType());
            if (comp.compare(r, best) < 0) {
                best = r; // New overall best.
            }
        }

        return best;
    }

    /**
     * @param init Start point.
     * @param simplex Initial simplex.
     * @param eval Objective function.
     * Note: It is assumed that evaluations of this function are
     * incrementing the main counter.
     * @param checker Convergence checker.
     * @param goalType Whether to minimize or maximize the objective function.
     * @return the optimum.
     */
    private static PointValuePair directSearch(double[] init,
                                               Simplex simplex,
                                               MultivariateFunction eval,
                                               ConvergenceChecker<PointValuePair> checker,
                                               GoalType goalType) {
        final SimplexOptimizer optim = new SimplexOptimizer(checker);
        return optim.optimize(MaxEval.unlimited(),
                              new ObjectiveFunction(eval),
                              goalType,
                              new InitialGuess(init),
                              simplex,
                              new NelderMeadTransform());
    }
}
