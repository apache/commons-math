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

import org.apache.commons.math.exception.ConvergenceException;
import org.apache.commons.math.exception.MaxCountExceededException;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.exception.NullArgumentException;

/**
 * An interface for Mann-Whitney U test (also called Wilcoxon rank-sum test).
 *
 * @version $Id$
 */
public interface MannWhitneyUTest {

    /**
     * Computes the <a
     * href="http://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U"> Mann-Whitney
     * U statistic</a> comparing mean for two independent samples possibly of
     * different length.
     * <p>
     * This statistic can be used to perform a Mann-Whitney U test evaluating
     * the null hypothesis that the two independent samples has equal mean.
     * </p>
     * <p>
     * Let X<sub>i</sub> denote the i'th individual of the first sample and
     * Y<sub>j</sub> the j'th individual in the second sample. Note that the
     * samples would often have different length.
     * </p>
     * <p>
     * <strong>Preconditions</strong>:
     * <ul>
     * <li>All observations in the two samples are independent.</li>
     * <li>The observations are at least ordinal (continuous are also ordinal).</li>
     * </ul>
     * </p>
     *
     * @param x the first sample
     * @param y the second sample
     * @return Mann-Whitney U statistic (maximum of U<sup>x</sup> and U<sup>y</sup>)
     * @throws NullArgumentException if {@code x} or {@code y} are {@code null}.
     * @throws NoDataException if {@code x} or {@code y} are zero-length.
     */
    double mannWhitneyU(final double[] x, final double[] y)
        throws NullArgumentException, NoDataException;

    /**
     * Returns the asymptotic <i>observed significance level</i>, or <a href=
     * "http://www.cas.lancs.ac.uk/glossary_v1.1/hyptest.html#pvalue">
     * p-value</a>, associated with a <a
     * href="http://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U"> Mann-Whitney
     * U statistic</a> comparing mean for two independent samples.
     * <p>
     * Let X<sub>i</sub> denote the i'th individual of the first sample and
     * Y<sub>j</sub> the j'th individual in the second sample. Note that the
     * samples would often have different length.
     * </p>
     * <p>
     * <strong>Preconditions</strong>:
     * <ul>
     * <li>All observations in the two samples are independent.</li>
     * <li>The observations are at least ordinal (continuous are also ordinal).</li>
     * </ul>
     * </p>
     *
     * @param x the first sample
     * @param y the second sample
     * @return asymptotic p-value
     * @throws NullArgumentException if {@code x} or {@code y} are {@code null}.
     * @throws NoDataException if {@code x} or {@code y} are zero-length.
     * @throws ConvergenceException if the p-value can not be computed due to a
     * convergence error
     * @throws MaxCountExceededException if the maximum number of iterations
     * is exceeded
     */
    double mannWhitneyUTest(final double[] x, final double[] y)
        throws NullArgumentException, NoDataException,
        ConvergenceException, MaxCountExceededException;
}
