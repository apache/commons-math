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

package org.apache.commons.math3.distribution;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link KolmogorovSmirnovDistribution}.
 *
 */
@Deprecated
public class KolmogorovSmirnovDistributionTest {

    private static final double TOLERANCE = 10e-10;

    @Test
    public void testCumulativeDensityFunction() {

        KolmogorovSmirnovDistribution dist;

        /* The code below is generated using the R-script located in
         * /src/test/R/KolmogorovSmirnovDistributionTestCases.R
         */

        /* R version 2.11.1 (2010-05-31) */


        /* formatC(.C("pkolmogorov2x", p = as.double(0.005), n = as.integer(200), PACKAGE = "stats")$p, 40) gives
         * 4.907829957616471622388047046469198862537e-86
         */
        dist = new KolmogorovSmirnovDistribution(200);
        Assert.assertEquals(4.907829957616471622388047046469198862537e-86, dist.cdf(0.005, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.02), n = as.integer(200), PACKAGE = "stats")$p, 40) gives
         * 5.151982014280041957199687829849210629618e-06
         */
        dist = new KolmogorovSmirnovDistribution(200);
        Assert.assertEquals(5.151982014280041957199687829849210629618e-06, dist.cdf(0.02, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.031111), n = as.integer(200), PACKAGE = "stats")$p, 40) gives
         * 0.01291614648162886340443389343590752105229
         */
        dist = new KolmogorovSmirnovDistribution(200);
        Assert.assertEquals(0.01291614648162886340443389343590752105229, dist.cdf(0.031111, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.04), n = as.integer(200), PACKAGE = "stats")$p, 40) gives
         * 0.1067137011362679355208626930107129737735
         */
        dist = new KolmogorovSmirnovDistribution(200);
        Assert.assertEquals(0.1067137011362679355208626930107129737735, dist.cdf(0.04, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.005), n = as.integer(341), PACKAGE = "stats")$p, 40) gives
         * 1.914734701559404553985102395145063418825e-53
         */
        dist = new KolmogorovSmirnovDistribution(341);
        Assert.assertEquals(1.914734701559404553985102395145063418825e-53, dist.cdf(0.005, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.02), n = as.integer(341), PACKAGE = "stats")$p, 40) gives
         * 0.001171328985781981343872182321774744195864
         */
        dist = new KolmogorovSmirnovDistribution(341);
        Assert.assertEquals(0.001171328985781981343872182321774744195864, dist.cdf(0.02, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.031111), n = as.integer(341), PACKAGE = "stats")$p, 40) gives
         * 0.1142955196267499418105728636874118819833
         */
        dist = new KolmogorovSmirnovDistribution(341);
        Assert.assertEquals(0.1142955196267499418105728636874118819833, dist.cdf(0.031111, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.04), n = as.integer(341), PACKAGE = "stats")$p, 40) gives
         * 0.3685529520496805266915885113121476024389
         */
        dist = new KolmogorovSmirnovDistribution(341);
        Assert.assertEquals(0.3685529520496805266915885113121476024389, dist.cdf(0.04, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.005), n = as.integer(389), PACKAGE = "stats")$p, 40) gives
         * 1.810657144595055888918455512707637574637e-47
         */
        dist = new KolmogorovSmirnovDistribution(389);
        Assert.assertEquals(1.810657144595055888918455512707637574637e-47, dist.cdf(0.005, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.02), n = as.integer(389), PACKAGE = "stats")$p, 40) gives
         * 0.003068542559702356568168690742481885536108
         */
        dist = new KolmogorovSmirnovDistribution(389);
        Assert.assertEquals(0.003068542559702356568168690742481885536108, dist.cdf(0.02, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.031111), n = as.integer(389), PACKAGE = "stats")$p, 40) gives
         * 0.1658291700122746237244797384846606291831
         */
        dist = new KolmogorovSmirnovDistribution(389);
        Assert.assertEquals(0.1658291700122746237244797384846606291831, dist.cdf(0.031111, false), TOLERANCE);

        /* formatC(.C("pkolmogorov2x", p = as.double(0.04), n = as.integer(389), PACKAGE = "stats")$p, 40) gives
         * 0.4513143712128902529379104180407011881471
         */
        dist = new KolmogorovSmirnovDistribution(389);
        Assert.assertEquals(0.4513143712128902529379104180407011881471, dist.cdf(0.04, false), TOLERANCE);

    }

}
