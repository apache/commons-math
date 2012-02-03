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
package org.apache.commons.math.stat.inference;

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.OutOfRangeException;

import java.util.Collection;

/**
 * An interface for one-way ANOVA (analysis of variance).
 *
 * <p> Tests for differences between two or more categories of univariate data
 * (for example, the body mass index of accountants, lawyers, doctors and
 * computer programmers).  When two categories are given, this is equivalent to
 * the {@link org.apache.commons.math.stat.inference.TTest}.
 * </p>
 *
 * @since 1.2
 * @version $Id$
 */
public interface OneWayAnova {

    /**
     * Computes the ANOVA F-value for a collection of <code>double[]</code>
     * arrays.
     *
     * <p><strong>Preconditions</strong>: <ul>
     * <li>The categoryData <code>Collection</code> must contain
     * <code>double[]</code> arrays.</li>
     * <li> There must be at least two <code>double[]</code> arrays in the
     * <code>categoryData</code> collection and each of these arrays must
     * contain at least two values.</li></ul></p>
     *
     * @param categoryData <code>Collection</code> of <code>double[]</code>
     * arrays each containing data for one category
     * @return Fvalue
     * @throws NullArgumentException if <code>categoryData</code> is <code>null</code>
     * @throws DimensionMismatchException if the length of the <code>categoryData</code>
     * array is less than 2 or a contained <code>double[]</code> array does not have
     * at least two values
     */
    double anovaFValue(Collection<double[]> categoryData)
        throws NullArgumentException, DimensionMismatchException;

    /**
     * Computes the ANOVA P-value for a collection of <code>double[]</code>
     * arrays.
     *
     * <p><strong>Preconditions</strong>: <ul>
     * <li>The categoryData <code>Collection</code> must contain
     * <code>double[]</code> arrays.</li>
     * <li> There must be at least two <code>double[]</code> arrays in the
     * <code>categoryData</code> collection and each of these arrays must
     * contain at least two values.</li></ul></p>
     *
     * @param categoryData <code>Collection</code> of <code>double[]</code>
     * arrays each containing data for one category
     * @return Pvalue
     * @throws NullArgumentException if <code>categoryData</code> is <code>null</code>
     * @throws DimensionMismatchException if the length of the <code>categoryData</code>
     * array is less than 2 or a contained <code>double[]</code> array does not have
     * at least two values
     */
    double anovaPValue(Collection<double[]> categoryData)
        throws NullArgumentException, DimensionMismatchException;

    /**
     * Performs an ANOVA test, evaluating the null hypothesis that there
     * is no difference among the means of the data categories.
     *
     * <p><strong>Preconditions</strong>: <ul>
     * <li>The categoryData <code>Collection</code> must contain
     * <code>double[]</code> arrays.</li>
     * <li> There must be at least two <code>double[]</code> arrays in the
     * <code>categoryData</code> collection and each of these arrays must
     * contain at least two values.</li>
     * <li>alpha must be strictly greater than 0 and less than or equal to 0.5.
     * </li></ul></p>
     *
     * @param categoryData <code>Collection</code> of <code>double[]</code>
     * arrays each containing data for one category
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with
     * confidence 1 - alpha
     * @throws NullArgumentException if <code>categoryData</code> is <code>null</code>
     * @throws DimensionMismatchException if the length of the <code>categoryData</code>
     * array is less than 2 or a contained <code>double[]</code> array does not have
     * at least two values
     * @throws OutOfRangeException if <code>alpha</code> is not in the range (0, 0.5]
     */
    boolean anovaTest(Collection<double[]> categoryData, double alpha)
        throws NullArgumentException, DimensionMismatchException, OutOfRangeException;

}
