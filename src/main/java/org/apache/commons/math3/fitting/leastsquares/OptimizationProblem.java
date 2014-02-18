package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Incrementor;

/**
 * Common settings for all optimization problems. Includes divergence and convergence
 * criteria.
 *
 * @param <PAIR> The type of value the {@link #getConvergenceChecker() convergence
 *               checker} will operate on. It should include the value of the model
 *               function and point where it was evaluated.
 * @version $Id$
 */
public interface OptimizationProblem<PAIR> {
    /**
     * Get a independent Incrementor that counts up to the maximum number of evaluations
     * and then throws an exception.
     *
     * @return a counter for the evaluations.
     */
    Incrementor getEvaluationCounter();

    /**
     * Get a independent Incrementor that counts up to the maximum number of iterations
     * and then throws an exception.
     *
     * @return a counter for the evaluations.
     */
    Incrementor getIterationCounter();

    /**
     * Gets the convergence checker.
     *
     * @return the object used to check for convergence.
     */
    ConvergenceChecker<PAIR> getConvergenceChecker();
}
