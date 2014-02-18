package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

/**
 * An implementation of {@link Evaluation} that is designed for extension. All of the
 * methods implemented here use the methods that are left unimplemented.
 * <p/>
 * TODO cache results?
 *
 * @version $Id$
 */
abstract class AbstractEvaluation implements Evaluation {

    /** number of observations */
    private final int observationSize;

    /**
     * Constructor.
     *
     * @param observationSize the number of observation. Needed for {@link
     *                        #computeRMS()}.
     */
    AbstractEvaluation(final int observationSize) {
        this.observationSize = observationSize;
    }

    /** {@inheritDoc} */
    public double[][] computeCovariances(double threshold) {
        // Set up the Jacobian.
        final RealMatrix j = this.computeJacobian();

        // Compute transpose(J)J.
        final RealMatrix jTj = j.transpose().multiply(j);

        // Compute the covariances matrix.
        final DecompositionSolver solver
                = new QRDecomposition(jTj, threshold).getSolver();
        return solver.getInverse().getData();
    }

    /** {@inheritDoc} */
    public double[] computeSigma(double covarianceSingularityThreshold) {
        final double[][] cov = this.computeCovariances(covarianceSingularityThreshold);
        final int nC = cov.length;
        final double[] sig = new double[nC];
        for (int i = 0; i < nC; ++i) {
            sig[i] = FastMath.sqrt(cov[i][i]);
        }
        return sig;
    }

    /** {@inheritDoc} */
    public double computeRMS() {
        final double cost = this.computeCost();
        return FastMath.sqrt(cost * cost / this.observationSize);
    }

    /** {@inheritDoc} */
    public double computeCost() {
        final ArrayRealVector r = new ArrayRealVector(this.computeResiduals());
        return FastMath.sqrt(r.dotProduct(r));
    }

}
