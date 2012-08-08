package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

/**
 * Implementation of the multivariate normal (Gaussian) distribution.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Multivariate_normal_distribution">
 * Multivariate normal distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/MultivariateNormalDistribution.html">
 * Multivariate normal distribution (MathWorld)</a>
 */
public class MultivariateNormalDistribution
    extends AbstractMultivariateRealDistribution {
    /** Vector of means. */
    private final double[] means;
    /** Covariance matrix. */
    private final RealMatrix covarianceMatrix;
    /** The matrix inverse of the covariance matrix. */
    private final RealMatrix covarianceMatrixInverse;
    /** The determinant of the covariance matrix. */
    private final double covarianceMatrixDeterminant;
    /** Matrix used in computation of samples. */
    private final RealMatrix samplingMatrix;

    /**
     * Creates a multivariate normal distribution with the given mean vector and
     * covariance matrix.
     * <br/>
     * The number of dimensions is equal to the length of the mean vector
     * and to the number of rows and columns of the covariance matrix.
     * It is frequently written as "p" in formulae.
     * 
     * @param means Vector of means.
     * @param covariances Covariance matrix.
     */
    public MultivariateNormalDistribution(final double[] means,
                                          final double[][] covariances)
        throws SingularMatrixException,
               DimensionMismatchException,
               NonPositiveDefiniteMatrixException {
        this(new Well19937c(), means, covariances);
    }

    /**
     * Creates a multivariate normal distribution with the given mean vector and
     * covariance matrix.
     * <br/>
     * The number of dimensions is equal to the length of the mean vector
     * and to the number of rows and columns of the covariance matrix.
     * It is frequently written as "p" in formulae.
     * 
     * @param rng Random Number Generator.
     * @param means Vector of means.
     * @param covariances Covariance matrix.
     */
    public MultivariateNormalDistribution(RandomGenerator rng,
                                          final double[] means,
                                          final double[][] covariances)
            throws SingularMatrixException,
                   DimensionMismatchException,
                   NonPositiveDefiniteMatrixException {
        super(rng, means.length);

        final int dim = means.length;

        if (covariances.length != dim) {
            throw new DimensionMismatchException(covariances.length, dim);
        }

        for (int i = 0; i < dim; i++) {
            if (dim != covariances[i].length) {
                throw new DimensionMismatchException(covariances[i].length, dim);
            }
        }

        this.means = MathArrays.copyOf(means);

        covarianceMatrix = new Array2DRowRealMatrix(covariances);

        // Covariance matrix eigen decomposition.
        final EigenDecomposition covMatDec = new EigenDecomposition(covarianceMatrix);

        // Compute and store the inverse.
        covarianceMatrixInverse = covMatDec.getSolver().getInverse();
        // Compute and store the determinant.
        covarianceMatrixDeterminant = covMatDec.getDeterminant();

        // Eigenvalues of the covariance matrix.
        final double[] covMatEigenvalues = covMatDec.getRealEigenvalues();

        for (int i = 0; i < covMatEigenvalues.length; i++) {
            if (covMatEigenvalues[i] < 0) {
                throw new NonPositiveDefiniteMatrixException(covMatEigenvalues[i], i, 0);
            }
        }

        // Matrix where each column is an eigenvector of the covariance matrix.
        final Array2DRowRealMatrix covMatEigenvectors = new Array2DRowRealMatrix(dim, dim);
        for (int v = 0; v < dim; v++) {
            final double[] evec = covMatDec.getEigenvector(v).toArray();
            covMatEigenvectors.setColumn(v, evec);
        }

        final RealMatrix tmpMatrix = covMatEigenvectors.transpose();

        // Scale each eigenvector by the square root of its eigenvalue.
        for (int row = 0; row < dim; row++) {
            final double factor = FastMath.sqrt(covMatEigenvalues[row]);
            for (int col = 0; col < dim; col++) {
                tmpMatrix.multiplyEntry(row, col, factor);
            }
        }

        samplingMatrix = covMatEigenvectors.multiply(tmpMatrix);
    }

    /**
     * Gets the mean vector.
     * 
     * @return the mean vector.
     */
    public double[] getMeans() {
        return MathArrays.copyOf(means);
    }

    /**
     * Gets the covariance matrix.
     * 
     * @return the covariance matrix.
     */
    public RealMatrix getCovariances() {
        return covarianceMatrix.copy();
    }

    /** {@inheritDoc} */
    public double density(final double[] vals) throws DimensionMismatchException {
        final int dim = getDimensions();
        if (vals.length != dim) {
            throw new DimensionMismatchException(vals.length, dim);
        }

        final double kernel = getKernel(vals);

        return FastMath.pow(2 * FastMath.PI, -dim / 2) *
            FastMath.pow(covarianceMatrixDeterminant, -0.5) *
            FastMath.exp(kernel);
    }

    /**
     * Gets the square root of each element on the diagonal of the covariance
     * matrix.
     * 
     * @return the standard deviations.
     */
    public double[] getStandardDeviations() {
        final int dim = getDimensions();
        final double[] std = new double[dim];
        final double[][] s = covarianceMatrix.getData();
        for (int i = 0; i < dim; i++) {
            std[i] = FastMath.sqrt(s[i][i]);
        }
        return std;
    }

    /** {@inheritDoc} */
    public double[] sample() {
        final int dim = getDimensions();
        final double[] normalVals = new double[dim];

        for (int i = 0; i < dim; i++) {
            normalVals[i] = random.nextGaussian();
        }

        final double[] vals = samplingMatrix.operate(normalVals);

        for (int i = 0; i < dim; i++) {
            vals[i] += means[i];
        }

        return vals;
    }

    /**
     * Precomputes some of the multiplications used for determining densities.
     * 
     * @param values Values at which to compute density.
     * @return the multiplication factor of density calculations.
     */
    private double getKernel(final double[] values) {
        double k = 0;
        for (int col = 0; col < values.length; col++) {
            for (int v = 0; v < values.length; v++) {
                k += covarianceMatrixInverse.getEntry(v, col)
                    * FastMath.pow(values[v] - means[v], 2);
            }
        }
        return -0.5 * k;
    }
}
