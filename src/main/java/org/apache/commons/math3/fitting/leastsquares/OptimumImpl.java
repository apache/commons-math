package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * A pedantic implementation of {@link Optimum}.
 *
 * @version $Id$
 */
class OptimumImpl implements Optimum {

    /** abscissa and ordinate */
    private final Evaluation value;
    /** number of evaluations to compute this optimum */
    private final int evaluations;
    /** number of iterations to compute this optimum */
    private final int iterations;

    /**
     * Construct an optimum from an evaluation and the values of the counters.
     *
     * @param value       the function value
     * @param evaluations number of times the function was evaluated
     * @param iterations  number of iterations of the algorithm
     */
    OptimumImpl(final Evaluation value, final int evaluations, final int iterations) {
        this.value = value;
        this.evaluations = evaluations;
        this.iterations = iterations;
    }

    /* auto-generated implementations */

    /** {@inheritDoc} */
    public int getEvaluations() {
        return evaluations;
    }

    /** {@inheritDoc} */
    public int getIterations() {
        return iterations;
    }

    /** {@inheritDoc} */
    public double[][] computeCovariances(double threshold) {
        return value.computeCovariances(threshold);
    }

    /** {@inheritDoc} */
    public double[] computeSigma(double covarianceSingularityThreshold) {
        return value.computeSigma(covarianceSingularityThreshold);
    }

    /** {@inheritDoc} */
    public double computeRMS() {
        return value.computeRMS();
    }

    /** {@inheritDoc} */
    public double[] computeValue() {
        return value.computeValue();
    }

    /** {@inheritDoc} */
    public RealMatrix computeJacobian() {
        return value.computeJacobian();
    }

    /** {@inheritDoc} */
    public double computeCost() {
        return value.computeCost();
    }

    /** {@inheritDoc} */
    public double[] computeResiduals() {
        return value.computeResiduals();
    }

    /** {@inheritDoc} */
    public double[] getPoint() {
        return value.getPoint();
    }
}
