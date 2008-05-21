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
package org.apache.commons.math.stat.regression;

import org.apache.commons.math.linear.RealMatrix;


/**
 * The OLS implementation of the multiple linear regression.
 * 
 * OLS assumes the covariance matrix of the error to be diagonal and with equal variance.
 * <pre>
 * u ~ N(0, sigma^2*I)
 * </pre>
 * 
 * Estimated by OLS, 
 * <pre>
 * b=(X'X)^-1X'y
 * </pre>
 * whose variance is
 * <pre>
 * Var(b)=MSE*(X'X)^-1, MSE=u'u/(n-k)
 * </pre>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class OLSMultipleLinearRegression extends AbstractMultipleLinearRegression {

    /**
     * {@inheritDoc}
     */
    public void addData(double[] y, double[][] x, double[][] covariance) {
        validateSampleData(x, y);
        addYSampleData(y);
        addXSampleData(x);
    }
    
    /**
     * Calculates beta by OLS.
     * <pre>
     * b=(X'X)^-1X'y
     * </pre> 
     * @return beta
     */
    protected RealMatrix calculateBeta() {
        RealMatrix XTX = X.transpose().multiply(X);
        return XTX.inverse().multiply(X.transpose()).multiply(Y);
    }

    /**
     * Calculates the variance on the beta by OLS.
     * <pre>
     *  Var(b)=(X'X)^-1
     * </pre>
     * @return The beta variance
     */
    protected RealMatrix calculateBetaVariance() {
        RealMatrix XTX = X.transpose().multiply(X);
        return XTX.inverse();
    }
    

    /**
     * Calculates the variance on the Y by OLS.
     * <pre>
     *  Var(y)=Tr(u'u)/(n-k)
     * </pre>
     * @return The Y variance
     */
    protected double calculateYVariance() {
        RealMatrix u = calculateResiduals();
        RealMatrix sse = u.transpose().multiply(u);
        return sse.getTrace()/(X.getRowDimension()-X.getColumnDimension());
    }

}
