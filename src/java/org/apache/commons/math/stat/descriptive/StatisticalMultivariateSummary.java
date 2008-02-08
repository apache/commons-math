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
package org.apache.commons.math.stat.descriptive;

import org.apache.commons.math.linear.RealMatrix;

/**
 *  Reporting interface for basic multivariate statistics.
 *
 * @since 1.2
 * @version $Revision: 480440 $ $Date: 2006-11-29 08:14:12 +0100 (mer., 29 nov. 2006) $
 */
public interface StatisticalMultivariateSummary {
    /** 
     * Returns the dimension of the data
     * @return The dimension of the data
     */
    public int getDimension();
    /** 
     * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
     * arithmetic mean </a> of the available values 
     * @return The mean or null if no values have been added.
     */
    public abstract double[] getMean();
    /** 
     * Returns the covariance of the available values.
     * @return The covariance, null if no values have been added 
     * or a zeroed matrix for a single value set.  
     */
    public abstract RealMatrix getCovariance();
    /** 
     * Returns the standard deviation of the available values.
     * @return The standard deviation, null if no values have been added 
     * or a zeroed array for a single value set. 
     */
    public abstract double[] getStandardDeviation();
    /** 
     * Returns the maximum of the available values
     * @return The max or null if no values have been added.
     */
    public abstract double[] getMax();
    /** 
    * Returns the minimum of the available values
    * @return The min or null if no values have been added.
    */
    public abstract double[] getMin();
    /** 
     * Returns the number of available values
     * @return The number of available values
     */
    public abstract long getN();
    /**
     * Returns the sum of the values that have been added.
     * @return The sum or null if no values have been added
     */
    public abstract double[] getSum();
    /**
     * Returns the sum of the squares of the values that have been added.
     * @return The sum or null if no values have been added
     */
    public abstract double[] getSumSq();
    /**
     * Returns the sum of the logarithms of the values that have been added.
     * @return The sum or null if no values have been added
     */
    public abstract double[] getSumLog();
}