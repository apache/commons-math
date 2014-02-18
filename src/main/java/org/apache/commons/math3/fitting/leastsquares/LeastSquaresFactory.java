package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.util.Pair;

/**
 * A Factory for creating {@link LeastSquaresProblem}s.
 *
 * @version $Id$
 */
public class LeastSquaresFactory {

    /** Prevent instantiation. */
    private LeastSquaresFactory() {
    }

     /**
     * Create a {@link org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem}
     * from the given elements. There will be no weights applied (Identity weights).
     *
     * @param model          the model function. Produces the computed values.
     * @param observed       the observed (target) values
     * @param start          the initial guess.
     * @param checker        convergence checker
     * @param maxEvaluations the maximum number of times to evaluate the model
     * @param maxIterations  the maximum number to times to iterate in the algorithm
     * @return the specified General Least Squares problem.
     */
    public static LeastSquaresProblem create(final MultivariateJacobianFunction model,
                                             final RealVector observed,
                                             final RealVector start,
                                             final ConvergenceChecker<Evaluation> checker,
                                             final int maxEvaluations,
                                             final int maxIterations) {
        return new LeastSquaresProblemImpl(
                model,
                observed,
                start,
                checker,
                maxEvaluations,
                maxIterations
        );
    }

    /**
     * Create a {@link org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem}
     * from the given elements.
     *
     * @param model          the model function. Produces the computed values.
     * @param observed       the observed (target) values
     * @param start          the initial guess.
     * @param weight         the weight matrix
     * @param checker        convergence checker
     * @param maxEvaluations the maximum number of times to evaluate the model
     * @param maxIterations  the maximum number to times to iterate in the algorithm
     * @return the specified General Least Squares problem.
     */
    public static LeastSquaresProblem create(final MultivariateJacobianFunction model,
                                             final RealVector observed,
                                             final RealVector start,
                                             final RealMatrix weight,
                                             final ConvergenceChecker<Evaluation> checker,
                                             final int maxEvaluations,
                                             final int maxIterations) {
        return weightMatrix(
                create(
                        model,
                        observed,
                        start,
                        checker,
                        maxEvaluations,
                        maxIterations
                ),
                weight);
    }

    /**
     * Create a {@link org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem}
     * from the given elements.
     * <p/>
     * This factory method is provided for continuity with previous interfaces. Newer
     * applications should use {@link #create(MultivariateJacobianFunction, RealVector,
     * RealVector, ConvergenceChecker, int, int)}, or {@link #create(MultivariateJacobianFunction,
     * RealVector, RealVector, RealMatrix, ConvergenceChecker, int, int)}.
     *
     * @param model          the model function. Produces the computed values.
     * @param jacobian       the jacobian of the model with respect to the parameters
     * @param observed       the observed (target) values
     * @param start          the initial guess.
     * @param weight         the weight matrix
     * @param checker        convergence checker
     * @param maxEvaluations the maximum number of times to evaluate the model
     * @param maxIterations  the maximum number to times to iterate in the algorithm
     * @return the specified General Least Squares problem.
     */
    public static LeastSquaresProblem create(final MultivariateVectorFunction model,
                                             final MultivariateMatrixFunction jacobian,
                                             final double[] observed,
                                             final double[] start,
                                             final RealMatrix weight,
                                             final ConvergenceChecker<Evaluation> checker,
                                             final int maxEvaluations,
                                             final int maxIterations) {
        return create(
                model(model, jacobian),
                new ArrayRealVector(observed, false),
                new ArrayRealVector(start, false),
                weight,
                checker,
                maxEvaluations,
                maxIterations
        );
    }

    /**
     * Apply a dense weight matrix to the {@link LeastSquaresProblem}.
     *
     * @param problem the unweighted problem
     * @param weights the matrix of weights
     * @return a new {@link LeastSquaresProblem} with the weights applied. The original
     *         {@code problem} is not modified.
     */
    public static LeastSquaresProblem weightMatrix(final LeastSquaresProblem problem,
                                                   final RealMatrix weights) {
        final RealMatrix weightSquareRoot = squareRoot(weights);
        return new LeastSquaresAdapter(problem) {
            @Override
            public Evaluation evaluate(final RealVector point) {
                return new DenseWeightedEvaluation(super.evaluate(point), weightSquareRoot);
            }
        };
    }

    /**
     * Apply a diagonal weight matrix to the {@link LeastSquaresProblem}.
     *
     * @param problem the unweighted problem
     * @param weights the diagonal of the weight matrix
     * @return a new {@link LeastSquaresProblem} with the weights applied. The original
     *         {@code problem} is not modified.
     */
    public static LeastSquaresProblem weightDiagonal(final LeastSquaresProblem problem,
                                                     final RealVector weights) {
        //TODO more efficient implementation
        return weightMatrix(problem, new DiagonalMatrix(weights.toArray()));
    }

    /**
     * Count the evaluations of a particular problem. The {@code counter} will be
     * incremented every time {@link LeastSquaresProblem#evaluate(RealVector)} is called on
     * the <em>returned</em> problem.
     *
     * @param problem the problem to track.
     * @param counter the counter to increment.
     * @return a least squares problem that tracks evaluations
     */
    public static LeastSquaresProblem countEvaluations(final LeastSquaresProblem problem,
                                                       final Incrementor counter) {
        return new LeastSquaresAdapter(problem) {

            public Evaluation evaluate(final RealVector point) {
                counter.incrementCount();
                return super.evaluate(point);
            }

            /* delegate the rest */

        };
    }

    /**
     * View a convergence checker specified for a {@link PointVectorValuePair} as one
     * specified for an {@link Evaluation}.
     *
     * @param checker the convergence checker to adapt.
     * @return a convergence checker that delegates to {@code checker}.
     */
    public static ConvergenceChecker<Evaluation> evaluationChecker(
            final ConvergenceChecker<PointVectorValuePair> checker
    ) {
        return new ConvergenceChecker<Evaluation>() {
            public boolean converged(final int iteration,
                                     final Evaluation previous,
                                     final Evaluation current) {
                return checker.converged(
                        iteration,
                        new PointVectorValuePair(
                                previous.getPoint().toArray(),
                                previous.computeValue().toArray(),
                                false),
                        new PointVectorValuePair(
                                current.getPoint().toArray(),
                                current.computeValue().toArray(),
                                false)
                );
            }
        };
    }

    /**
     * Computes the square-root of the weight matrix.
     *
     * @param m Symmetric, positive-definite (weight) matrix.
     * @return the square-root of the weight matrix.
     */
    private static RealMatrix squareRoot(final RealMatrix m) {
        if (m instanceof DiagonalMatrix) {
            final int dim = m.getRowDimension();
            final RealMatrix sqrtM = new DiagonalMatrix(dim);
            for (int i = 0; i < dim; i++) {
                sqrtM.setEntry(i, i, FastMath.sqrt(m.getEntry(i, i)));
            }
            return sqrtM;
        } else {
            final EigenDecomposition dec = new EigenDecomposition(m);
            return dec.getSquareRoot();
        }
    }

    /**
     * Combine a {@link MultivariateVectorFunction} with a {@link
     * MultivariateMatrixFunction} to produce a {@link MultivariateJacobianFunction}.
     *
     * @param value    the vector value function
     * @param jacobian the Jacobian function
     * @return a function that computes both at the same time
     */
    public static MultivariateJacobianFunction model(
            final MultivariateVectorFunction value,
            final MultivariateMatrixFunction jacobian
    ) {
        return new MultivariateJacobianFunction() {
            public Pair<RealVector, RealMatrix> value(final RealVector point) {
                //TODO get array from RealVector without copying?
                final double[] pointArray = point.toArray();
                //evaluate and return data without copying
                return new Pair<RealVector, RealMatrix>(
                        new ArrayRealVector(value.value(pointArray), false),
                        new Array2DRowRealMatrix(jacobian.value(pointArray), false));
            }
        };
    }
}

