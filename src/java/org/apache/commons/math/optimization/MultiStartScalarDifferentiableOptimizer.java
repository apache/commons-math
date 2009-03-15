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

package org.apache.commons.math.optimization;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.random.RandomVectorGenerator;

/** 
 * Special implementation of the {@link ScalarDifferentiableOptimizer} interface adding
 * multi-start features to an existing optimizer.
 * <p>
 * This class wraps a classical optimizer to use it several times in
 * turn with different starting points in order to avoid being trapped
 * into a local extremum when looking for a global one.
 * </p>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class MultiStartScalarDifferentiableOptimizer implements ScalarDifferentiableOptimizer {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 9008747186334431824L;

    /** Underlying classical optimizer. */
    private final ScalarDifferentiableOptimizer optimizer;

    /** Number of evaluations already performed for all starts. */
    private int totalEvaluations;

    /** Maximal number of evaluations allowed. */
    private int maxEvaluations;

    /** Number of starts to go. */
    private int starts;

    /** Random generator for multi-start. */
    private RandomVectorGenerator generator;

    /** Found optima. */
    private ScalarPointValuePair[] optima;

    /**
     * Create a multi-start optimizer from a single-start optimizer
     * @param optimizer single-start optimizer to wrap
     * @param starts number of starts to perform (including the
     * first one), multi-start is disabled if value is less than or
     * equal to 1
     * @param generator random vector generator to use for restarts
     */
    public MultiStartScalarDifferentiableOptimizer(final ScalarDifferentiableOptimizer optimizer,
                                                   final int starts,
                                                   final RandomVectorGenerator generator) {
        this.optimizer        = optimizer;
        this.totalEvaluations = 0;
        this.maxEvaluations   = Integer.MAX_VALUE;
        this.starts           = starts;
        this.generator        = generator;
        this.optima           = null;
    }

    /** Get all the optima found during the last call to {@link
     * #optimize(ScalarObjectiveFunction, GoalType, double[]) optimize}.
     * <p>The optimizer stores all the optima found during a set of
     * restarts. The {@link #optimize(ScalarObjectiveFunction, GoalType,
     * double[]) optimize} method returns the best point only. This
     * method returns all the points found at the end of each starts,
     * including the best one already returned by the {@link
     * #optimize(ScalarObjectiveFunction, GoalType, double[]) optimize}
     * method.
     * </p>
     * <p>
     * The returned array as one element for each start as specified
     * in the constructor. It is ordered with the results from the
     * runs that did converge first, sorted from best to worst
     * objective value (i.e in ascending order if minimizing and in
     * descending order if maximizing), followed by and null elements
     * corresponding to the runs that did not converge. This means all
     * elements will be null if the {@link #optimize(ScalarObjectiveFunction,
     * GoalType, double[]) optimize} method did throw a {@link
     * ConvergenceException ConvergenceException}). This also means that
     * if the first element is non null, it is the best point found across
     * all starts.</p>
     * @return array containing the optima
     * @exception IllegalStateException if {@link #optimize(ScalarObjectiveFunction,
     * GoalType, double[]) optimize} has not been called
     */
    public ScalarPointValuePair[] getOptima() throws IllegalStateException {
        if (optima == null) {
            throw MathRuntimeException.createIllegalStateException("no optimum computed yet");
        }
        return (ScalarPointValuePair[]) optima.clone();
    }

    /** {@inheritDoc} */
    public int getEvaluations() {
        return totalEvaluations;
    }

    /** {@inheritDoc} */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /** {@inheritDoc} */
    public void setConvergenceChecker(ScalarConvergenceChecker checker) {
        optimizer.setConvergenceChecker(checker);
    }

    /** {@inheritDoc} */
    public ScalarConvergenceChecker getConvergenceChecker() {
        return optimizer.getConvergenceChecker();
    }

    /** {@inheritDoc} */
    public ScalarPointValuePair optimize(final ScalarDifferentiableObjectiveFunction f,
                                         final GoalType goalType,
                                         double[] startPoint)
        throws ObjectiveException, OptimizationException {

        optima = new ScalarPointValuePair[starts];
        totalEvaluations = 0;

        // multi-start loop
        for (int i = 0; i < starts; ++i) {

            try {
                optimizer.setMaxEvaluations(maxEvaluations - totalEvaluations);
                optima[i] = optimizer.optimize(f, goalType,
                                               (i == 0) ? startPoint : generator.nextVector());
            } catch (ObjectiveException obe) {
                optima[i] = null;
            } catch (OptimizationException ope) {
                optima[i] = null;
            }

            totalEvaluations += optimizer.getEvaluations();

        }

        // sort the optima from best to worst, followed by null elements
        Arrays.sort(optima, new Comparator<ScalarPointValuePair>() {
            public int compare(final ScalarPointValuePair o1, final ScalarPointValuePair o2) {
                if (o1 == null) {
                    return (o2 == null) ? 0 : +1;
                } else if (o2 == null) {
                    return -1;
                }
                final double v1 = o1.getValue();
                final double v2 = o2.getValue();
                return (goalType == GoalType.MINIMIZE) ?
                        Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        });

        if (optima[0] == null) {
            throw new OptimizationException(
                    "none of the {0} start points lead to convergence",
                    starts);
        }

        // return the found point given the best objective function value
        return optima[0];

    }

}
