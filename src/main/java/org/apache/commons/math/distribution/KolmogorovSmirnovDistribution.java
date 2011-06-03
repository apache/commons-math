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

package org.apache.commons.math.distribution;

/**
 * Treats the distribution of the two-sided
 * {@code P(D}<sub>{@code n}</sub>{@code < d)}
 * where {@code D}<sub>{@code n}</sub>{@code = sup_x | G(x) - Gn (x) |} for the
 * theoretical cdf G and the emperical cdf Gn.
 *
 * @version $Id$
 */
public interface KolmogorovSmirnovDistribution {

    /**
     * Calculates {@code P(D}<sub>n</sub> {@code < d)}.
     *
     * @param d statistic
     * @return the two-sided probability of {@code P(D}<sub>n</sub> {@code < d)}
     */
    double cdf(double d);

}
