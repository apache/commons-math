package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;

/**
 * A mutable builder for {@link LeastSquaresProblem}s.
 *
 * @version $Id$
 * @see LeastSquaresFactory
 */
public class LeastSquaresBuilder {

    /** max evaluations */
    private int maxEvaluations;
    /** max iterations */
    private int maxIterations;
    /** convergence checker */
    private ConvergenceChecker<PointVectorValuePair> checker;
    /** model function */
    private MultivariateVectorFunction model;
    /** Jacobian function */
    private MultivariateMatrixFunction jacobian;
    /** observed values */
    private double[] target;
    /** initial guess */
    private double[] start;
    /** weight matrix */
    private RealMatrix weight;


    /**
     * Construct a {@link LeastSquaresProblem} from the data in this builder.
     *
     * @return a new {@link LeastSquaresProblem}.
     */
    public LeastSquaresProblem build() {
        return LeastSquaresFactory.create(model, jacobian, target, start, weight, checker, maxEvaluations, maxIterations);
    }

    /**
     * Configure the max evaluations.
     *
     * @param maxEvaluations the maximum number of evaluations permitted.
     * @return this
     */
    public LeastSquaresBuilder maxEvaluations(final int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    /**
     * Configure the max iterations.
     *
     * @param maxIterations the maximum number of iterations permitted.
     * @return this
     */
    public LeastSquaresBuilder maxIterations(final int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    /**
     * Configure the convergence checker.
     *
     * @param checker the convergence checker.
     * @return this
     */
    public LeastSquaresBuilder checker(final ConvergenceChecker<PointVectorValuePair> checker) {
        this.checker = checker;
        return this;
    }

    /**
     * Configure the model function.
     *
     * @param model the model function
     * @return this
     */
    public LeastSquaresBuilder model(final MultivariateVectorFunction model) {
        this.model = model;
        return this;
    }

    /**
     * Configure the Jacobian function.
     *
     * @param jacobian the Jacobian function
     * @return this
     */
    public LeastSquaresBuilder jacobian(final MultivariateMatrixFunction jacobian) {
        this.jacobian = jacobian;
        return this;
    }

    /**
     * Configure the observed data.
     *
     * @param target the observed data.
     * @return this
     */
    public LeastSquaresBuilder target(final double[] target) {
        this.target = target;
        return this;
    }

    /**
     * Configure the initial guess.
     *
     * @param start the initial guess.
     * @return this
     */
    public LeastSquaresBuilder start(final double[] start) {
        this.start = start;
        return this;
    }

    /**
     * Configure the weight matrix.
     *
     * @param weight the weight matrix
     * @return this
     */
    public LeastSquaresBuilder weight(final RealMatrix weight) {
        this.weight = weight;
        return this;
    }

}
