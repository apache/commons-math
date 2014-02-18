package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Applies a dense weight matrix to an evaluation.
 *
 * @version $Id$
 */
class DenseWeightedEvaluation extends AbstractEvaluation {

    /** the unweighted evaluation */
    private final Evaluation unweighted;
    /** reference to the weight square root matrix */
    private final RealMatrix weightSqrt;

    /**
     * Create a weighted evaluation from an unweighted one.
     *
     * @param unweighted the evalutation before weights are applied
     * @param weightSqrt the matrix square root of the weight matrix
     */
    DenseWeightedEvaluation(final Evaluation unweighted,
                            final RealMatrix weightSqrt) {
        // weight square root is square, nR=nC=number of observations
        super(weightSqrt.getColumnDimension());
        this.unweighted = unweighted;
        this.weightSqrt = weightSqrt;
    }

    /* apply weights */

    /** {@inheritDoc} */
    public RealMatrix computeJacobian() {
        return weightSqrt.multiply(this.unweighted.computeJacobian());
    }

    /** {@inheritDoc} */
    public RealVector computeResiduals() {
        return this.weightSqrt.operate(this.unweighted.computeResiduals());
    }

    /** {@inheritDoc} */
    public RealVector computeValue() {
        return this.weightSqrt.operate(unweighted.computeValue());
    }

    /* delegate */

    /** {@inheritDoc} */
    public RealVector getPoint() {
        return unweighted.getPoint();
    }

}
