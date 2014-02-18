package org.apache.commons.math3.fitting.leastsquares;


/**
 * An algorithm that can be applied to a non-linear least squares problem.
 *
 * @version $Id$
 */
public interface LeastSquaresOptimizer {

    /**
     * Solve the non-linear least squares problem.
     *
     *
     * @param leastSquaresProblem the problem definition, including model function and
     *                            convergence criteria.
     * @return The optimum.
     */
    Optimum optimize(LeastSquaresProblem leastSquaresProblem);

    /**
     * The optimum found by the optimizer. This object contains the point, its value, and
     * some metadata.
     */
    //TODO Solution?
    interface Optimum extends LeastSquaresProblem.Evaluation {

        /**
         * Get the number of times the model was evaluated in order to produce this
         * optimum.
         *
         * @return the number of model (objective) function evaluations
         */
        int getEvaluations();

        /**
         * Get the number of times the algorithm iterated in order to produce this
         * optimum. In general least squares it is common to have one {@link
         * #getEvaluations() evaluation} per iterations.
         *
         * @return the number of iterations
         */
        int getIterations();

    }

}
