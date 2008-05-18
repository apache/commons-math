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
 * Abstract base class for implementations of MultipleLinearRegression.
 */
public abstract class AbstractMultipleLinearRegression implements
        MultipleLinearRegression {

    protected RealMatrix X;
    protected RealMatrix Y;

    /**
     * Adds y sample data.
     * 
     * @param y the [n,1] array representing the y sample
     */
    protected void addYSampleData(double[] y){
        this.Y = new RealMatrixImpl(y);
    }

    /**
     * Adds x sample data.
     * 
     * @param x the [n,k] array representing the x sample
     */
    protected void addXSampleData(double[][] x){
        this.X = new RealMatrixImpl(x);
    }

    public double[] estimateRegressionParameters(){
        RealMatrix b = calculateBeta();
        return b.getColumn(0);
    }    
    
    public double[] estimateResiduals(){
        RealMatrix b = calculateBeta();
        RealMatrix e = Y.subtract(X.multiply(b));
        return e.getColumn(0);
    }
    
    public double[][] estimateRegressionParametersVariance() {
        return calculateBetaVariance().getData();
    }

    public double estimateRegressandVariance() {
        return calculateYVariance();
    }
    
    /**
     * Calculates the beta of multiple linear regression in matrix notation.
     */
    protected abstract RealMatrix calculateBeta();    
    
    /**
     * Calculates the beta variance of multiple linear regression in matrix notation.
     */
    protected abstract RealMatrix calculateBetaVariance();
    
    /**
     * Calculates the Y variance of multiple linear regression.
     */
    protected abstract double calculateYVariance();

    /**
     * Calculates the residuals of multiple linear regression in matrix notation.
     * <pre>
     * u = y - X*b
     * </pre>
     * 
     * @return The residuals [n,1] matrix 
     */
    protected RealMatrix calculateResiduals() {
        RealMatrix b = calculateBeta();
        return Y.subtract(X.multiply(b));
    }
    
}
