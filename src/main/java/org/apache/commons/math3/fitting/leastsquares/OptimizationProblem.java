package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Incrementor;

/** @author Evan Ward */
public interface OptimizationProblem<PAIR> {
    /**
     * Get a independent Incrementor that counts up to {@link #getMaxEvaluations()} and
     * then throws an exception.
     *
     * @return a counter for the evaluations.
     */
    Incrementor getEvaluationCounter();

    /**
     * Get a independent Incrementor that counts up to {@link #getMaxIterations()} and
     * then throws an exception.
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
