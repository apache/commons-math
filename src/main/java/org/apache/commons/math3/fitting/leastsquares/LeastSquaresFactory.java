package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.util.Incrementor;

/** @author Evan Ward */
public class LeastSquaresFactory {

    /**
     * Create a {@link org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem}
     * from the given elements.
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
                                             final ConvergenceChecker<PointVectorValuePair> checker,
                                             final int maxEvaluations,
                                             final int maxIterations) {
        return new LeastSquaresProblemImpl(
                maxEvaluations,
                maxIterations,
                checker,
                observed,
                weight,
                model,
                jacobian,
                start
        );
    }

    /**
     * Count the evaluations of a particular problem. The {@code counter} will be
     * incremented every time {@link LeastSquaresProblem#evaluate(double[])} is called on
     * the <em>returned</em> problem.
     *
     * @param problem the problem to track.
     * @param counter the counter to increment.
     * @return a least squares problem that tracks evaluations
     */
    public static LeastSquaresProblem countEvaluations(final LeastSquaresProblem problem,
                                                       final Incrementor counter) {
        //TODO adapter?
        return new LeastSquaresProblem() {

            public Evaluation evaluate(double[] point) {
                counter.incrementCount();
                return problem.evaluate(point);
            }

            /* delegate the rest */

            public double[] getStart() {
                return problem.getStart();
            }

            public int getObservationSize() {
                return problem.getObservationSize();
            }

            public int getParameterSize() {
                return problem.getParameterSize();
            }

            public RealMatrix getWeight() {
                return problem.getWeight();
            }

            public RealMatrix getWeightSquareRoot() {
                return problem.getWeightSquareRoot();
            }

            public Incrementor getEvaluationCounter() {
                return problem.getEvaluationCounter();
            }

            public Incrementor getIterationCounter() {
                return problem.getIterationCounter();
            }

            public ConvergenceChecker<PointVectorValuePair> getConvergenceChecker() {
                return problem.getConvergenceChecker();
            }
        };
    }

}
