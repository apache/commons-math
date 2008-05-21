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
import org.apache.commons.math.linear.RealMatrixImpl;


/**
 * The GLS implementation of the multiple linear regression.
 * 
 * GLS assumes a general covariance matrix Omega of the error
 * <pre>
 * u ~ N(0, Omega)
 * </pre>
 * 
 * Estimated by GLS, 
 * <pre>
 * b=(X' Omega^-1 X)^-1X'Omega^-1 y
 * </pre>
 * whose variance is
 * <pre>
 * Var(b)=(X' Omega^-1 X)^-1
 * </pre>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class GLSMultipleLinearRegression extends AbstractMultipleLinearRegression {
    
    /** Covariance matrix. */
    private RealMatrix Omega;

    /**
     * {@inheritDoc}
     */
    public void addData(double[] y, double[][] x, double[][] covariance) {
        validateSampleData(x, y);
        addYSampleData(y);
        addXSampleData(x);
        validateCovarianceData(x, covariance);
        addCovarianceData(covariance);
    }

    /**
     * Add the covariance data.
     * 
     * @param omega the [n,n] array representing the covariance
     */
    protected void addCovarianceData(double[][] omega){
        this.Omega = new RealMatrixImpl(omega);
    }
    
    /**
     * Calculates beta by GLS.
     * <pre>
     *  b=(X' Omega^-1 X)^-1X'Omega^-1 y
     * </pre>
     * @return beta
     */
    protected RealMatrix calculateBeta() {
        RealMatrix OI = Omega.inverse();
        RealMatrix XT = X.transpose();
        RealMatrix XTOIX = XT.multiply(OI).multiply(X);
        return XTOIX.inverse().multiply(XT).multiply(OI).multiply(Y);
    }

    /**
     * Calculates the variance on the beta by GLS.
     * <pre>
     *  Var(b)=(X' Omega^-1 X)^-1
     * </pre>
     * @return The beta variance matrix
     */
    protected RealMatrix calculateBetaVariance() {
        RealMatrix XTOIX = X.transpose().multiply(Omega.inverse()).multiply(X);
        return XTOIX.inverse();
    }

    /**
     * Calculates the variance on the y by GLS.
     * <pre>
     *  Var(y)=Tr(u' Omega^-1 u)/(n-k)
     * </pre>
     * @return The Y variance
     */
    protected double calculateYVariance() {
        RealMatrix u = calculateResiduals();
        RealMatrix sse =  u.transpose().multiply(Omega.inverse()).multiply(u);
        return sse.getTrace()/(X.getRowDimension()-X.getColumnDimension());
    }
    
}
