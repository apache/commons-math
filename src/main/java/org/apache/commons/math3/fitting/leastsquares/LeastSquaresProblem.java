package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.PointVectorValuePair;

/**
 * The data necessary to define a non-linear least squares problem. Includes the observed
 * values, computed model function, weights, and convergence/divergence criteria.
 *
 * @author Evan Ward
 */
public interface LeastSquaresProblem extends OptimizationProblem<PointVectorValuePair> {

    /**
     * Gets the initial guess.
     *
     * @return the initial guess values.
     */
    double[] getStart();

    /** Get the number of observations (rows in the Jacobian) in this problem.
     *
     * @return the number of scalar observations
     */
    int getObservationSize();

    /** Get the number of parameters (columns in the Jacobian) in this problem.
     *
     * @return the number of scalar parameters
     */
    int getParameterSize();

    /**
     * Evaluate the model at the specified point.
     *
     * @param point the parameter values.
     * @return the model's value and derivative at the given point.
     */
    Evaluation evaluate(double[] point);

    /**
     * Gets the weight matrix of the observations.
     * <p/>
     * TODO Is it possible to leave out this method and have the weights implicit in the
     * {@link Evaluation}?
     *
     * @return the weight matrix.
     */
    RealMatrix getWeight();

    /**Get the square root of the weight matrix.
     * TODO delete this method
     * @return the square root of the weight matrix
     */
    RealMatrix getWeightSquareRoot();

    public interface Evaluation {

        /**
         * Get the covariance matrix of the optimized parameters. <br/> Note that this
         * operation involves the inversion of the <code>J<sup>T</sup>J</code> matrix,
         * where {@code J} is the Jacobian matrix. The {@code threshold} parameter is a
         * way for the caller to specify that the result of this computation should be
         * considered meaningless, and thus trigger an exception.
         *
         * @param threshold Singularity threshold.
         * @return the covariance matrix.
         * @throws org.apache.commons.math3.linear.SingularMatrixException
         *          if the covariance matrix cannot be computed (singular problem).
         */
        double[][] computeCovariances(double threshold);

        /**
         * Computes an estimate of the standard deviation of the parameters. The returned
         * values are the square root of the diagonal coefficients of the covariance
         * matrix, {@code sd(a[i]) ~= sqrt(C[i][i])}, where {@code a[i]} is the optimized
         * value of the {@code i}-th parameter, and {@code C} is the covariance matrix.
         *
         * @param covarianceSingularityThreshold Singularity threshold (see {@link
         *                                       #computeCovariances(double[], double)
         *                                       computeCovariances}).
         * @return an estimate of the standard deviation of the optimized parameters
         * @throws org.apache.commons.math3.linear.SingularMatrixException
         *          if the covariance matrix cannot be computed.
         */
        double[] computeSigma(double covarianceSingularityThreshold);

        /**
         * Computes the normalized cost. It is the square-root of the sum of squared of
         * the residuals, divided by the number of measurements.
         *
         * @return the cost.
         */
        double computeRMS();

        /**
         * Computes the objective (model) function value.
         *
         * @return the objective function value at the specified point.
         * @throws org.apache.commons.math3.exception.TooManyEvaluationsException
         *          if the maximal number of evaluations (of the model vector function) is
         *          exceeded.
         */
        double[] computeValue();

        /**
         * Computes the weighted Jacobian matrix.
         *
         * @return the weighted Jacobian: W<sup>1/2</sup> J.
         * @throws DimensionMismatchException if the Jacobian dimension does not match
         *                                    problem dimension.
         */
        RealMatrix computeWeightedJacobian();

        /**
         * Computes the Jacobian matrix.
         *
         * @return the Jacobian at the specified point.
         */
        double[][] computeJacobian();

        /**
         * Computes the cost.
         *
         * @return the cost.
         * @see #computeResiduals(double[])
         */
        double computeCost();

        /**
         * Computes the residuals. The residual is the difference between the observed
         * (target) values and the model (objective function) value. There is one residual
         * for each element of the vector-valued function.
         *
         * @return the residuals.
         * @throws DimensionMismatchException if {@code params} has a wrong length.
         */
        double[] computeResiduals();


        /**
         * Get the abscissa (independent variables) of this evaluation.
         *
         * @return the point provided to {@link #evaluate(double[])}.
         */
        double[] getPoint();
    }
}
