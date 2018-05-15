package org.apache.commons.math4.fitting.leastsquares;

import org.apache.commons.math4.analysis.MultivariateMatrixFunction;
import org.apache.commons.math4.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.util.FastMath;

import java.util.ArrayList;
import java.util.List;

class BevingtonProblem {
    private List<Double> time;
    private List<Double> count;

    public BevingtonProblem() {
        time = new ArrayList<>();
        count = new ArrayList<>();
    }

    public void addPoint(double t, double c) {
        time.add(t);
        count.add(c);
    }

    public MultivariateVectorFunction getModelFunction() {
        return new MultivariateVectorFunction() {
            @Override
            public double[] value(double[] params) {
                double[] values = new double[time.size()];
                for (int i = 0; i < values.length; ++i) {
                    final double t = time.get(i);
                    values[i] = params[0] +
                        params[1] * FastMath.exp(-t / params[3]) +
                        params[2] * FastMath.exp(-t / params[4]);
                }
                return values;
            }
        };
    }

    public MultivariateMatrixFunction getModelFunctionJacobian() {
        return new MultivariateMatrixFunction() {
            @Override
            public double[][] value(double[] params) {
                double[][] jacobian = new double[time.size()][5];

                for (int i = 0; i < jacobian.length; ++i) {
                    final double t = time.get(i);
                    jacobian[i][0] = 1;

                    final double p3 =  params[3];
                    final double p4 =  params[4];
                    final double tOp3 = t / p3;
                    final double tOp4 = t / p4;
                    jacobian[i][1] = FastMath.exp(-tOp3);
                    jacobian[i][2] = FastMath.exp(-tOp4);
                    jacobian[i][3] = params[1] * FastMath.exp(-tOp3) * tOp3 / p3;
                    jacobian[i][4] = params[2] * FastMath.exp(-tOp4) * tOp4 / p4;
                }
                return jacobian;
            }
        };
    }
}
