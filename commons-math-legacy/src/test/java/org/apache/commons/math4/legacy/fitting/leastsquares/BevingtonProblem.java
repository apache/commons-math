/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.legacy.fitting.leastsquares;

import org.apache.commons.math4.legacy.analysis.MultivariateMatrixFunction;
import org.apache.commons.math4.legacy.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.core.jdkmath.JdkMath;

import java.util.ArrayList;
import java.util.List;

class BevingtonProblem {
    private List<Double> time;
    private List<Double> count;

    BevingtonProblem() {
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
                        params[1] * JdkMath.exp(-t / params[3]) +
                        params[2] * JdkMath.exp(-t / params[4]);
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
                    jacobian[i][1] = JdkMath.exp(-tOp3);
                    jacobian[i][2] = JdkMath.exp(-tOp4);
                    jacobian[i][3] = params[1] * JdkMath.exp(-tOp3) * tOp3 / p3;
                    jacobian[i][4] = params[2] * JdkMath.exp(-tOp4) * tOp4 / p4;
                }
                return jacobian;
            }
        };
    }
}
