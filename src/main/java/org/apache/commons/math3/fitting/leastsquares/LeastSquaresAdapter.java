package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Incrementor;

/**
 * An adapter that delegates to another implementation of {@link LeastSquaresProblem}.
 *
 * @version $Id$
 */
public class LeastSquaresAdapter implements LeastSquaresProblem {

    /** the delegate problem */
    private final LeastSquaresProblem problem;

    /**
     * Delegate the {@link LeastSquaresProblem} interface to the given implementation.
     *
     * @param problem the delegate
     */
    public LeastSquaresAdapter(final LeastSquaresProblem problem) {
        this.problem = problem;
    }

    /** {@inheritDoc} */
    public RealVector getStart() {
        return problem.getStart();
    }

    /** {@inheritDoc} */
    public int getObservationSize() {
        return problem.getObservationSize();
    }

    /** {@inheritDoc} */
    public int getParameterSize() {
        return problem.getParameterSize();
    }

    /** {@inheritDoc}
     * @param point*/
    public Evaluation evaluate(final RealVector point) {
        return problem.evaluate(point);
    }

    /** {@inheritDoc} */
    public Incrementor getEvaluationCounter() {
        return problem.getEvaluationCounter();
    }

    /** {@inheritDoc} */
    public Incrementor getIterationCounter() {
        return problem.getIterationCounter();
    }

    /** {@inheritDoc} */
    public ConvergenceChecker<Evaluation> getConvergenceChecker() {
        return problem.getConvergenceChecker();
    }
}
