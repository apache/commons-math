package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;
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
    public double[] getStart() {
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

    /** {@inheritDoc} */
    public Evaluation evaluate(final double[] point) {
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
    public ConvergenceChecker<PointVectorValuePair> getConvergenceChecker() {
        return problem.getConvergenceChecker();
    }
}
