package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
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
    private ConvergenceChecker<Evaluation> checker;
    /** model function */
    private MultivariateJacobianFunction model;
    /** observed values */
    private RealVector target;
    /** initial guess */
    private RealVector start;
    /** weight matrix */
    private RealMatrix weight;


    /**
     * Construct a {@link LeastSquaresProblem} from the data in this builder.
     *
     * @return a new {@link LeastSquaresProblem}.
     */
    public LeastSquaresProblem build() {
        return LeastSquaresFactory.create(model, target, start, weight, checker, maxEvaluations, maxIterations);
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
    public LeastSquaresBuilder checker(final ConvergenceChecker<Evaluation> checker) {
        this.checker = checker;
        return this;
    }

    /**
     * Configure the convergence checker.
     * <p/>
     * This function is an overloaded version of {@link #checker(ConvergenceChecker)}.
     *
     * @param checker the convergence checker.
     * @return this
     */
    public LeastSquaresBuilder checkerPair(final ConvergenceChecker<PointVectorValuePair> checker) {
        return this.checker(LeastSquaresFactory.evaluationChecker(checker));
    }

    /**
     * Configure the model function.
     *
     * @param value the model function value
     * @param jacobian the Jacobian of {@code value}
     * @return this
     */
    public LeastSquaresBuilder model(final MultivariateVectorFunction value,
                                     final MultivariateMatrixFunction jacobian) {
        return model(LeastSquaresFactory.model(value, jacobian));
    }

    /**
     * Configure the model function.
     *
     * @param model the model function value and Jacobian
     * @return this
     */
    public LeastSquaresBuilder model(final MultivariateJacobianFunction model) {
        this.model = model;
        return this;
    }

    /**
     * Configure the observed data.
     *
     * @param target the observed data.
     * @return this
     */
    public LeastSquaresBuilder target(final RealVector target) {
        this.target = target;
        return this;
    }

    /**
     * Configure the observed data.
     *
     * @param target the observed data.
     * @return this
     */
    public LeastSquaresBuilder target(final double[] target) {
        return target(new ArrayRealVector(target, false));
    }

    /**
     * Configure the initial guess.
     *
     * @param start the initial guess.
     * @return this
     */
    public LeastSquaresBuilder start(final RealVector start) {
        this.start = start;
        return this;
    }

    /**
     * Configure the initial guess.
     *
     * @param start the initial guess.
     * @return this
     */
    public LeastSquaresBuilder start(final double[] start) {
        return start(new ArrayRealVector(start, false));
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
