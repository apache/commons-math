/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat.univariate;

/**
 * UnivariateStatistic interface provides methods to evaluate
 * double[] based content using an implemented statistical approach.
 * The interface provides two "stateless" simple methods to calculate
 * a statistic from a double[] based parameter.
 * @version $Revision: 1.12 $ $Date: 2004/02/21 21:35:15 $
 */
public interface UnivariateStatistic {

    /**
     * Evaluates the double[] returning the result of the evaluation.
     * @param values Is a double[] containing the values
     * @return the result of the evaluation or Double.NaN
     * if the array is empty
     */
    double evaluate(double[] values);

    /**
     * Evaluates part of a double[] returning the result
     * of the evaluation.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the result of the evaluation or Double.NaN
     * if the array is empty
     */
    double evaluate(double[] values, int begin, int length);

}