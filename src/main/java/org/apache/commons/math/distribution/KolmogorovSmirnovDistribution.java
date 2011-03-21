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
 * {@code P(D<sub>n</sup> &lt; d)}
 * where {@code D<sub>n</sup> = sup_x | G(x) - Gn (x) |} for the 
 * theoretical cdf G and the emperical cdf Gn.
 * 
 * This implementation is based on [1] with certain quick 
 * decisions for extreme values given in [2].
 * 
 * In short, when wanting to evaluate {@code P(D<sub>n</sup> &lt; d)}, 
 * the method in [1] is to write {@code d = (k - h) / n} for positive 
 * integer {@code k} and {@code 0 <= h < 1}. Then 
 * {@code P(D<sub>n</sup> &lt; d) = (n!/n^n) * t_kk}
 * where {@code t_kk} is the (k, k)'th entry in the special matrix {@code H^n}, 
 * i.e. {@code H} to the {@code n}'th power. 
 * 
 * See also <a href="http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test">
 * Kolmogorov-Smirnov test on Wikipedia</a> for details.
 * 
 * References: 
 * [1] Evaluating Kolmogorov's Distribution by George Marsaglia, Wai
 * Wan Tsang, Jingbo Wang http://www.jstatsoft.org/v08/i18/paper
 * 
 * [2] <a href="http://www.iro.umontreal.ca/~lecuyer/myftp/papers/ksdist.pdf">
 * Computing the Two-Sided Kolmogorov-Smirnov Distribution</a> by Richard Simard
 * and Pierre L'Ecuyer
 * 
 * Note that [1] contains an error in computing h, refer to 
 * <a href="https://issues.apache.org/jira/browse/MATH-437">MATH-437</a> for details.
 * 
 * @version $Revision$ $Date$
 */
public interface KolmogorovSmirnovDistribution {
    
    public double cdf(double d);
    
}
