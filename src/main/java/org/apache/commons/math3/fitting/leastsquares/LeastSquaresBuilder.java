package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;

/** @author Evan Ward */
public class LeastSquaresBuilder {

    private int maxEvaluations;
    private int maxIterations;
    private ConvergenceChecker<PointVectorValuePair> checker;
    private MultivariateVectorFunction model;
    private MultivariateMatrixFunction jacobian;
    private double[] target;
    private double[] start;
    private RealMatrix weight;


    public LeastSquaresProblem build(){
        return LeastSquaresFactory.create(model, jacobian, target, start, weight, checker, maxEvaluations, maxIterations);
    }

    public LeastSquaresBuilder maxEvaluations(final int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    public LeastSquaresBuilder maxIterations(final int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    public LeastSquaresBuilder checker(final ConvergenceChecker<PointVectorValuePair> checker) {
        this.checker = checker;
        return this;
    }

    public LeastSquaresBuilder model(final MultivariateVectorFunction model) {
        this.model = model;
        return this;
    }

    public LeastSquaresBuilder jacobian(final MultivariateMatrixFunction jacobian) {
        this.jacobian = jacobian;
        return this;
    }

    public LeastSquaresBuilder target(final double[] target) {
        this.target = target;
        return this;
    }

    public LeastSquaresBuilder start(final double[] start) {
        this.start = start;
        return this;
    }

    public LeastSquaresBuilder weight(final RealMatrix weight) {
        this.weight = weight;
        return this;
    }

}
