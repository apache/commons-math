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
package org.apache.commons.math4.util;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.numbers.core.ArithmeticUtils;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.numbers.combinatorics.Factorial;
import org.apache.commons.numbers.combinatorics.BinomialCoefficient;

/**
 * Combinatorial utilities.
 *
 * @since 3.3
 */
public final class CombinatoricsUtils {
    /** Stirling numbers of the second kind. */
    static final AtomicReference<long[][]> STIRLING_S2 = new AtomicReference<> (null);

    /** Private constructor (class contains only static methods). */
    private CombinatoricsUtils() {}

    /**
     * Returns the <a
     * href="http://mathworld.wolfram.com/StirlingNumberoftheSecondKind.html">
     * Stirling number of the second kind</a>, "{@code S(n,k)}", the number of
     * ways of partitioning an {@code n}-element set into {@code k} non-empty
     * subsets.
     * <p>
     * The preconditions are {@code 0 <= k <= n } (otherwise
     * {@code NotPositiveException} is thrown)
     * </p>
     * @param n the size of the set
     * @param k the number of non-empty subsets
     * @return {@code S(n,k)}
     * @throws NotPositiveException if {@code k < 0}.
     * @throws NumberIsTooLargeException if {@code k > n}.
     * @throws MathArithmeticException if some overflow happens, typically for n exceeding 25 and
     * k between 20 and n-2 (S(n,n-1) is handled specifically and does not overflow)
     * @since 3.1
     */
    public static long stirlingS2(final int n, final int k)
        throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
        if (k < 0) {
            throw new NotPositiveException(k);
        }
        if (k > n) {
            throw new NumberIsTooLargeException(k, n, true);
        }

        long[][] stirlingS2 = STIRLING_S2.get();

        if (stirlingS2 == null) {
            // the cache has never been initialized, compute the first numbers
            // by direct recurrence relation

            // as S(26,9) = 11201516780955125625 is larger than Long.MAX_VALUE
            // we must stop computation at row 26
            final int maxIndex = 26;
            stirlingS2 = new long[maxIndex][];
            stirlingS2[0] = new long[] { 1l };
            for (int i = 1; i < stirlingS2.length; ++i) {
                stirlingS2[i] = new long[i + 1];
                stirlingS2[i][0] = 0;
                stirlingS2[i][1] = 1;
                stirlingS2[i][i] = 1;
                for (int j = 2; j < i; ++j) {
                    stirlingS2[i][j] = j * stirlingS2[i - 1][j] + stirlingS2[i - 1][j - 1];
                }
            }

            // atomically save the cache
            STIRLING_S2.compareAndSet(null, stirlingS2);

        }

        if (n < stirlingS2.length) {
            // the number is in the small cache
            return stirlingS2[n][k];
        } else {
            // use explicit formula to compute the number without caching it
            if (k == 0) {
                return 0;
            } else if (k == 1 || k == n) {
                return 1;
            } else if (k == 2) {
                return (1l << (n - 1)) - 1l;
            } else if (k == n - 1) {
                return BinomialCoefficient.value(n, 2);
            } else {
                // definition formula: note that this may trigger some overflow
                long sum = 0;
                long sign = ((k & 0x1) == 0) ? 1 : -1;
                for (int j = 1; j <= k; ++j) {
                    sign = -sign;
                    sum += sign * BinomialCoefficient.value(k, j) * ArithmeticUtils.pow(j, n);
                    if (sum < 0) {
                        // there was an overflow somewhere
                        throw new MathArithmeticException(LocalizedFormats.ARGUMENT_OUTSIDE_DOMAIN,
                                                          n, 0, stirlingS2.length - 1);
                    }
                }
                return sum / Factorial.value(k);
            }
        }
    }
}
