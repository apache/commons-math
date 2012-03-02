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

package org.apache.commons.math3.random;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Collection;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.PascalDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.ResizableDoubleArray;

/**
 * Implements the {@link RandomData} interface using a {@link RandomGenerator}
 * instance to generate non-secure data and a {@link java.security.SecureRandom}
 * instance to provide data for the <code>nextSecureXxx</code> methods. If no
 * <code>RandomGenerator</code> is provided in the constructor, the default is
 * to use a {@link Well19937c} generator. To plug in a different
 * implementation, either implement <code>RandomGenerator</code> directly or
 * extend {@link AbstractRandomGenerator}.
 * <p>
 * Supports reseeding the underlying pseudo-random number generator (PRNG). The
 * <code>SecurityProvider</code> and <code>Algorithm</code> used by the
 * <code>SecureRandom</code> instance can also be reset.
 * </p>
 * <p>
 * For details on the default PRNGs, see {@link java.util.Random} and
 * {@link java.security.SecureRandom}.
 * </p>
 * <p>
 * <strong>Usage Notes</strong>:
 * <ul>
 * <li>
 * Instance variables are used to maintain <code>RandomGenerator</code> and
 * <code>SecureRandom</code> instances used in data generation. Therefore, to
 * generate a random sequence of values or strings, you should use just
 * <strong>one</strong> <code>RandomDataImpl</code> instance repeatedly.</li>
 * <li>
 * The "secure" methods are *much* slower. These should be used only when a
 * cryptographically secure random sequence is required. A secure random
 * sequence is a sequence of pseudo-random values which, in addition to being
 * well-dispersed (so no subsequence of values is an any more likely than other
 * subsequence of the the same length), also has the additional property that
 * knowledge of values generated up to any point in the sequence does not make
 * it any easier to predict subsequent values.</li>
 * <li>
 * When a new <code>RandomDataImpl</code> is created, the underlying random
 * number generators are <strong>not</strong> initialized. If you do not
 * explicitly seed the default non-secure generator, it is seeded with the
 * current time in milliseconds plus the system identity hash code on first use.
 * The same holds for the secure generator. If you provide a <code>RandomGenerator</code>
 * to the constructor, however, this generator is not reseeded by the constructor
 * nor is it reseeded on first use.</li>
 * <li>
 * The <code>reSeed</code> and <code>reSeedSecure</code> methods delegate to the
 * corresponding methods on the underlying <code>RandomGenerator</code> and
 * <code>SecureRandom</code> instances. Therefore, <code>reSeed(long)</code>
 * fully resets the initial state of the non-secure random number generator (so
 * that reseeding with a specific value always results in the same subsequent
 * random sequence); whereas reSeedSecure(long) does <strong>not</strong>
 * reinitialize the secure random number generator (so secure sequences started
 * with calls to reseedSecure(long) won't be identical).</li>
 * <li>
 * This implementation is not synchronized.
 * </ul>
 * </p>
 *
 * @version $Id$
 */
public class RandomDataImpl implements RandomData, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -626730818244969716L;

    /**
     * Used when generating Exponential samples.
     * Table containing the constants
     * q_i = sum_{j=1}^i (ln 2)^j/j! = ln 2 + (ln 2)^2/2 + ... + (ln 2)^i/i!
     * until the largest representable fraction below 1 is exceeded.
     *
     * Note that
     * 1 = 2 - 1 = exp(ln 2) - 1 = sum_{n=1}^infty (ln 2)^n / n!
     * thus q_i -> 1 as i -> infty,
     * so the higher i, the closer to one we get (the series is not alternating).
     *
     * By trying, n = 16 in Java is enough to reach 1.0.
     */
    private static final double[] EXPONENTIAL_SA_QI;

    /** underlying random number generator */
    private RandomGenerator rand = null;

    /** underlying secure random number generator */
    private SecureRandom secRand = null;

    /**
     * Initialize tables
     */
    static {
        /**
         * Filling EXPONENTIAL_SA_QI table.
         * Note that we don't want qi = 0 in the table.
         */
        final double LN2 = FastMath.log(2);
        double qi = 0;
        int i = 1;

        /**
         * MathUtils provides factorials up to 20, so let's use that limit together
         * with Precision.EPSILON to generate the following code (a priori, we know that
         * there will be 16 elements, but instead of hardcoding that, this is
         * prettier):
         */
        final ResizableDoubleArray ra = new ResizableDoubleArray(20);

        while (qi < 1) {
            qi += FastMath.pow(LN2, i) / ArithmeticUtils.factorial(i);
            ra.addElement(qi);
            ++i;
        }

        EXPONENTIAL_SA_QI = ra.getElements();
    }

    /**
     * Construct a RandomDataImpl, using a default random generator as the source
     * of randomness.
     *
     * <p>The default generator is a {@link Well19937c} seeded
     * with {@code System.currentTimeMillis() + System.identityHashCode(this))}.
     * The generator is initialized and seeded on first use.</p>
     */
    public RandomDataImpl() {
    }

    /**
     * Construct a RandomDataImpl using the supplied {@link RandomGenerator} as
     * the source of (non-secure) random data.
     *
     * @param rand the source of (non-secure) random data
     * (may be null, resulting in the default generator)
     * @since 1.1
     */
    public RandomDataImpl(RandomGenerator rand) {
        super();
        this.rand = rand;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Algorithm Description:</strong> hex strings are generated using a
     * 2-step process.
     * <ol>
     * <li>{@code len / 2 + 1} binary bytes are generated using the underlying
     * Random</li>
     * <li>Each binary byte is translated into 2 hex digits</li>
     * </ol>
     * </p>
     *
     * @param len the desired string length.
     * @return the random string.
     * @throws NotStrictlyPositiveException if {@code len <= 0}.
     */
    public String nextHexString(int len) {
        if (len <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.LENGTH, len);
        }

        // Get a random number generator
        RandomGenerator ran = getRan();

        // Initialize output buffer
        StringBuilder outBuffer = new StringBuilder();

        // Get int(len/2)+1 random bytes
        byte[] randomBytes = new byte[(len / 2) + 1];
        ran.nextBytes(randomBytes);

        // Convert each byte to 2 hex digits
        for (int i = 0; i < randomBytes.length; i++) {
            Integer c = Integer.valueOf(randomBytes[i]);

            /*
             * Add 128 to byte value to make interval 0-255 before doing hex
             * conversion. This guarantees <= 2 hex digits from toHexString()
             * toHexString would otherwise add 2^32 to negative arguments.
             */
            String hex = Integer.toHexString(c.intValue() + 128);

            // Make sure we add 2 hex digits for each byte
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            outBuffer.append(hex);
        }
        return outBuffer.toString().substring(0, len);
    }

    /** {@inheritDoc} */
    public int nextInt(int lower, int upper) {
        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                                                lower, upper, false);
        }
        double r = getRan().nextDouble();
        double scaled = r * upper + (1.0 - r) * lower + r;
        return (int) FastMath.floor(scaled);
    }

    /** {@inheritDoc} */
    public long nextLong(long lower, long upper) {
        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                                                lower, upper, false);
        }
        double r = getRan().nextDouble();
        double scaled = r * upper + (1.0 - r) * lower + r;
        return (long)FastMath.floor(scaled);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Algorithm Description:</strong> hex strings are generated in
     * 40-byte segments using a 3-step process.
     * <ol>
     * <li>
     * 20 random bytes are generated using the underlying
     * <code>SecureRandom</code>.</li>
     * <li>
     * SHA-1 hash is applied to yield a 20-byte binary digest.</li>
     * <li>
     * Each byte of the binary digest is converted to 2 hex digits.</li>
     * </ol>
     * </p>
     */
    public String nextSecureHexString(int len) {
        if (len <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.LENGTH, len);
        }

        // Get SecureRandom and setup Digest provider
        SecureRandom secRan = getSecRan();
        MessageDigest alg = null;
        try {
            alg = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            // this should never happen
            throw new MathInternalError(ex);
        }
        alg.reset();

        // Compute number of iterations required (40 bytes each)
        int numIter = (len / 40) + 1;

        StringBuilder outBuffer = new StringBuilder();
        for (int iter = 1; iter < numIter + 1; iter++) {
            byte[] randomBytes = new byte[40];
            secRan.nextBytes(randomBytes);
            alg.update(randomBytes);

            // Compute hash -- will create 20-byte binary hash
            byte[] hash = alg.digest();

            // Loop over the hash, converting each byte to 2 hex digits
            for (int i = 0; i < hash.length; i++) {
                Integer c = Integer.valueOf(hash[i]);

                /*
                 * Add 128 to byte value to make interval 0-255 This guarantees
                 * <= 2 hex digits from toHexString() toHexString would
                 * otherwise add 2^32 to negative arguments
                 */
                String hex = Integer.toHexString(c.intValue() + 128);

                // Keep strings uniform length -- guarantees 40 bytes
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
                outBuffer.append(hex);
            }
        }
        return outBuffer.toString().substring(0, len);
    }

    /**  {@inheritDoc} */
    public int nextSecureInt(int lower, int upper) {
        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                                                lower, upper, false);
        }
        SecureRandom sec = getSecRan();
        double r = sec.nextDouble();
        double scaled = r * upper + (1.0 - r) * lower + r;
        return (int)FastMath.floor(scaled);
    }

    /** {@inheritDoc} */
    public long nextSecureLong(long lower, long upper) {

        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                                                lower, upper, false);
        }
        SecureRandom sec = getSecRan();
        double r = sec.nextDouble();
        double scaled = r * upper + (1.0 - r) * lower + r;
        return (long)FastMath.floor(scaled);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Algorithm Description</strong>:
     * <ul><li> For small means, uses simulation of a Poisson process
     * using Uniform deviates, as described
     * <a href="http://irmi.epfl.ch/cmos/Pmmi/interactive/rng7.htm"> here.</a>
     * The Poisson process (and hence value returned) is bounded by 1000 * mean.</li>
     *
     * <li> For large means, uses the rejection algorithm described in <br/>
     * Devroye, Luc. (1981).<i>The Computer Generation of Poisson Random Variables</i>
     * <strong>Computing</strong> vol. 26 pp. 197-207.</li></ul></p>
     */
    public long nextPoisson(double mean) {
        if (mean <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.MEAN, mean);
        }

        final double pivot = 40.0d;
        if (mean < pivot) {
            final RandomGenerator generator = getRan();
            double p = FastMath.exp(-mean);
            long n = 0;
            double r = 1.0d;
            double rnd = 1.0d;

            while (n < 1000 * mean) {
                rnd = generator.nextDouble();
                r = r * rnd;
                if (r >= p) {
                    n++;
                } else {
                    return n;
                }
            }
            return n;
        } else {
            final double lambda = FastMath.floor(mean);
            final double lambdaFractional = mean - lambda;
            final double logLambda = FastMath.log(lambda);
            final double logLambdaFactorial = ArithmeticUtils.factorialLog((int) lambda);
            final long y2 = lambdaFractional < Double.MIN_VALUE ? 0 : nextPoisson(lambdaFractional);
            final double delta = FastMath.sqrt(lambda * FastMath.log(32 * lambda / FastMath.PI + 1));
            final double halfDelta = delta / 2;
            final double twolpd = 2 * lambda + delta;
            final double a1 = FastMath.sqrt(FastMath.PI * twolpd) * FastMath.exp(1 / 8 * lambda);
            final double a2 = (twolpd / delta) * FastMath.exp(-delta * (1 + delta) / twolpd);
            final double aSum = a1 + a2 + 1;
            final double p1 = a1 / aSum;
            final double p2 = a2 / aSum;
            final double c1 = 1 / (8 * lambda);

            double x = 0;
            double y = 0;
            double v = 0;
            int a = 0;
            double t = 0;
            double qr = 0;
            double qa = 0;
            for (;;) {
                final double u = nextUniform(0.0, 1);
                if (u <= p1) {
                    final double n = nextGaussian(0d, 1d);
                    x = n * FastMath.sqrt(lambda + halfDelta) - 0.5d;
                    if (x > delta || x < -lambda) {
                        continue;
                    }
                    y = x < 0 ? FastMath.floor(x) : FastMath.ceil(x);
                    final double e = nextExponential(1d);
                    v = -e - (n * n / 2) + c1;
                } else {
                    if (u > p1 + p2) {
                        y = lambda;
                        break;
                    } else {
                        x = delta + (twolpd / delta) * nextExponential(1d);
                        y = FastMath.ceil(x);
                        v = -nextExponential(1d) - delta * (x + 1) / twolpd;
                    }
                }
                a = x < 0 ? 1 : 0;
                t = y * (y + 1) / (2 * lambda);
                if (v < -t && a == 0) {
                    y = lambda + y;
                    break;
                }
                qr = t * ((2 * y + 1) / (6 * lambda) - 1);
                qa = qr - (t * t) / (3 * (lambda + a * (y + 1)));
                if (v < qa) {
                    y = lambda + y;
                    break;
                }
                if (v > qr) {
                    continue;
                }
                if (v < y * logLambda - ArithmeticUtils.factorialLog((int) (y + lambda)) + logLambdaFactorial) {
                    y = lambda + y;
                    break;
                }
            }
            return y2 + (long) y;
        }
    }

    /** {@inheritDoc} */
    public double nextGaussian(double mu, double sigma) {

        if (sigma <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.STANDARD_DEVIATION, sigma);
        }
        return sigma * getRan().nextGaussian() + mu;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <strong>Algorithm Description</strong>: Uses the Algorithm SA (Ahrens)
     * from p. 876 in:
     * [1]: Ahrens, J. H. and Dieter, U. (1972). Computer methods for
     * sampling from the exponential and normal distributions.
     * Communications of the ACM, 15, 873-882.
     * </p>
     */
    public double nextExponential(double mean) {

        if (mean <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.MEAN, mean);
        }

        // Step 1:
        double a = 0;
        double u = this.nextUniform(0, 1);

        // Step 2 and 3:
        while (u < 0.5) {
            a += EXPONENTIAL_SA_QI[0];
            u *= 2;
        }

        // Step 4 (now u >= 0.5):
        u += u - 1;

        // Step 5:
        if (u <= EXPONENTIAL_SA_QI[0]) {
            return mean * (a + u);
        }

        // Step 6:
        int i = 0; // Should be 1, be we iterate before it in while using 0
        double u2 = this.nextUniform(0, 1);
        double umin = u2;

        // Step 7 and 8:
        do {
            ++i;
            u2 = this.nextUniform(0, 1);

            if (u2 < umin) {
                umin = u2;
            }

            // Step 8:
        } while (u > EXPONENTIAL_SA_QI[i]); // Ensured to exit since EXPONENTIAL_SA_QI[MAX] = 1

        return mean * (a + umin * EXPONENTIAL_SA_QI[0]);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <strong>Algorithm Description</strong>: scales the output of
     * Random.nextDouble(), but rejects 0 values (i.e., will generate another
     * random double if Random.nextDouble() returns 0). This is necessary to
     * provide a symmetric output interval (both endpoints excluded).
     * </p>
     *
     * @throws MathIllegalArgumentException if one of the bounds is infinite or
     * {@code NaN} or either bound is infinite or NaN
     */
    public double nextUniform(double lower, double upper) {

        return nextUniform(lower, upper, false);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <strong>Algorithm Description</strong>: if the lower bound is excluded,
     * scales the output of Random.nextDouble(), but rejects 0 values (i.e.,
     * will generate another random double if Random.nextDouble() returns 0).
     * This is necessary to provide a symmetric output interval (both
     * endpoints excluded).
     * </p>
     *
     * @throws MathIllegalArgumentException if one of the bounds is infinite or
     * {@code NaN}
     * @since 3.0
     */
    public double nextUniform(double lower, double upper,
        boolean lowerInclusive) {

        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND,
                                                lower, upper, false);
        }

        if (Double.isInfinite(lower) || Double.isInfinite(upper)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INFINITE_BOUND);
        }

        if (Double.isNaN(lower) || Double.isNaN(upper)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NAN_NOT_ALLOWED);
        }

        final RandomGenerator generator = getRan();

        // ensure nextDouble() isn't 0.0
        double u = generator.nextDouble();
        while (!lowerInclusive && u <= 0.0) {
            u = generator.nextDouble();
        }

        return u * upper + (1.0 - u) * lower;
    }

    /**
     * Generates a random value from the {@link BetaDistribution Beta Distribution}.
     * This implementation uses {@link #nextInversionDeviate(RealDistribution) inversion}
     * to generate random values.
     *
     * @param alpha first distribution shape parameter
     * @param beta second distribution shape parameter
     * @return random value sampled from the beta(alpha, beta) distribution
     * @since 2.2
     */
    public double nextBeta(double alpha, double beta) {
        return nextInversionDeviate(new BetaDistribution(alpha, beta));
    }

    /**
     * Generates a random value from the {@link BinomialDistribution Binomial Distribution}.
     * This implementation uses {@link #nextInversionDeviate(RealDistribution) inversion}
     * to generate random values.
     *
     * @param numberOfTrials number of trials of the Binomial distribution
     * @param probabilityOfSuccess probability of success of the Binomial distribution
     * @return random value sampled from the Binomial(numberOfTrials, probabilityOfSuccess) distribution
     * @since 2.2
     */
    public int nextBinomial(int numberOfTrials, double probabilityOfSuccess) {
        return nextInversionDeviate(new BinomialDistribution(numberOfTrials, probabilityOfSuccess));
    }

    /**
     * Generates a random value from the {@link CauchyDistribution Cauchy Distribution}.
     * This implementation uses {@link #nextInversionDeviate(RealDistribution) inversion}
     * to generate random values.
     *
     * @param median the median of the Cauchy distribution
     * @param scale the scale parameter of the Cauchy distribution
     * @return random value sampled from the Cauchy(median, scale) distribution
     * @since 2.2
     */
    public double nextCauchy(double median, double scale) {
        return nextInversionDeviate(new CauchyDistribution(median, scale));
    }

    /**
     * Generates a random value from the {@link ChiSquaredDistribution ChiSquare Distribution}.
     * This implementation uses {@link #nextInversionDeviate(RealDistribution) inversion}
     * to generate random values.
     *
     * @param df the degrees of freedom of the ChiSquare distribution
     * @return random value sampled from the ChiSquare(df) distribution
     * @since 2.2
     */
    public double nextChiSquare(double df) {
        return nextInversionDeviate(new ChiSquaredDistribution(df));
    }

    /**
     * Generates a random value from the {@link FDistribution F Distribution}.
     * This implementation uses {@link #nextInversionDeviate(RealDistribution) inversion}
     * to generate random values.
     *
     * @param numeratorDf the numerator degrees of freedom of the F distribution
     * @param denominatorDf the denominator degrees of freedom of the F distribution
     * @return random value sampled from the F(numeratorDf, denominatorDf) distribution
     * @since 2.2
     */
    public double nextF(double numeratorDf, double denominatorDf) {
        return nextInversionDeviate(new FDistribution(numeratorDf, denominatorDf));
    }

    /**
     * <p>Generates a random value from the
     * {@link org.apache.commons.math3.distribution.GammaDistribution Gamma Distribution}.</p>
     *
     * <p>This implementation uses the following algorithms: </p>
     *
     * <p>For 0 < shape < 1: <br/>
     * Ahrens, J. H. and Dieter, U., <i>Computer methods for
     * sampling from gamma, beta, Poisson and binomial distributions.</i>
     * Computing, 12, 223-246, 1974.</p>
     *
     * <p>For shape >= 1: <br/>
     * Marsaglia and Tsang, <i>A Simple Method for Generating
     * Gamma Variables.</i> ACM Transactions on Mathematical Software,
     * Volume 26 Issue 3, September, 2000.</p>
     *
     * @param shape the median of the Gamma distribution
     * @param scale the scale parameter of the Gamma distribution
     * @return random value sampled from the Gamma(shape, scale) distribution
     * @since 2.2
     */
    public double nextGamma(double shape, double scale) {
        if (shape < 1) {
            // [1]: p. 228, Algorithm GS

            while (true) {
                // Step 1:
                final double u = this.nextUniform(0, 1);
                final double bGS = 1 + shape/FastMath.E;
                final double p = bGS*u;

                if (p <= 1) {
                    // Step 2:

                    final double x = FastMath.pow(p, 1/shape);
                    final double u2 = this.nextUniform(0.0, 1);

                    if (u2 > FastMath.exp(-x)) {
                        // Reject
                        continue;
                    } else {
                        return scale*x;
                    }
                } else {
                    // Step 3:

                    final double x = -1 * FastMath.log((bGS-p)/shape);
                    final double u2 = this.nextUniform(0, 1);

                    if (u2 > FastMath.pow(x, shape - 1)) {
                        // Reject
                        continue;
                    } else {
                        return scale*x;
                    }
                }
            }
        }

        // Now shape >= 1

        final RandomGenerator generator = this.getRan();
        final double d = shape - 0.333333333333333333;
        final double c = 1.0 / (3*FastMath.sqrt(d));

        while (true) {
            final double x = generator.nextGaussian();
            final double v = (1+c*x)*(1+c*x)*(1+c*x);

            if (v <= 0) {
                continue;
            }

            final double xx = x*x;
            final double u = this.nextUniform(0, 1);

            // Squeeze
            if (u < 1 - 0.0331*xx*xx) {
                return scale*d*v;
            }

            if (FastMath.log(u) < 0.5*xx + d*(1 - v + FastMath.log(v))) {
                return scale*d*v;
            }
        }
    }

    /**
     * Generates a random value from the {@link HypergeometricDistribution Hypergeometric Distribution}.
     * This implementation uses {@link #nextInversionDeviate(IntegerDistribution) inversion}
     * to generate random values.
     *
     * @param populationSize the population size of the Hypergeometric distribution
     * @param numberOfSuccesses number of successes in the population of the Hypergeometric distribution
     * @param sampleSize the sample size of the Hypergeometric distribution
     * @return random value sampled from the Hypergeometric(numberOfSuccesses, sampleSize) distribution
     * @since 2.2
     */
    public int nextHypergeometric(int populationSize, int numberOfSuccesses, int sampleSize) {
        return nextInversionDeviate(new HypergeometricDistribution(populationSize, numberOfSuccesses, sampleSize));
    }

    /**
     * Generates a random value from the {@link PascalDistribution Pascal Distribution}.
     * This implementation uses {@link #nextInversionDeviate(IntegerDistribution) inversion}
     * to generate random values.
     *
     * @param r the number of successes of the Pascal distribution
     * @param p the probability of success of the Pascal distribution
     * @return random value sampled from the Pascal(r, p) distribution
     * @since 2.2
     */
    public int nextPascal(int r, double p) {
        return nextInversionDeviate(new PascalDistribution(r, p));
    }

    /**
     * Generates a random value from the {@link TDistribution T Distribution}.
     * This implementation uses {@link #nextInversionDeviate(RealDistribution) inversion}
     * to generate random values.
     *
     * @param df the degrees of freedom of the T distribution
     * @return random value from the T(df) distribution
     * @since 2.2
     */
    public double nextT(double df) {
        return nextInversionDeviate(new TDistribution(df));
    }

    /**
     * Generates a random value from the {@link WeibullDistribution Weibull Distribution}.
     * This implementation uses {@link #nextInversionDeviate(RealDistribution) inversion}
     * to generate random values.
     *
     * @param shape the shape parameter of the Weibull distribution
     * @param scale the scale parameter of the Weibull distribution
     * @return random value sampled from the Weibull(shape, size) distribution
     * @since 2.2
     */
    public double nextWeibull(double shape, double scale) {
        return nextInversionDeviate(new WeibullDistribution(shape, scale));
    }

    /**
     * Generates a random value from the {@link ZipfDistribution Zipf Distribution}.
     * This implementation uses {@link #nextInversionDeviate(IntegerDistribution) inversion}
     * to generate random values.
     *
     * @param numberOfElements the number of elements of the ZipfDistribution
     * @param exponent the exponent of the ZipfDistribution
     * @return random value sampled from the Zipf(numberOfElements, exponent) distribution
     * @since 2.2
     */
    public int nextZipf(int numberOfElements, double exponent) {
        return nextInversionDeviate(new ZipfDistribution(numberOfElements, exponent));
    }

    /**
     * Returns the RandomGenerator used to generate non-secure random data.
     * <p>
     * Creates and initializes a default generator if null. Uses a {@link Well19937c}
     * generator with {@code System.currentTimeMillis() + System.identityHashCode(this))} as the default seed.
     * </p>
     *
     * @return the Random used to generate random data
     * @since 1.1
     */
    private RandomGenerator getRan() {
        if (rand == null) {
            initRan();
        }
        return rand;
    }

    /**
     * Sets the default generator to a {@link Well19937c} generator seeded with
     * {@code System.currentTimeMillis() + System.identityHashCode(this))}.
     */
    private void initRan() {
        rand = new Well19937c(System.currentTimeMillis() + System.identityHashCode(this));
    }

    /**
     * Returns the SecureRandom used to generate secure random data.
     * <p>
     * Creates and initializes if null.  Uses
     * {@code System.currentTimeMillis() + System.identityHashCode(this)} as the default seed.
     * </p>
     *
     * @return the SecureRandom used to generate secure random data
     */
    private SecureRandom getSecRan() {
        if (secRand == null) {
            secRand = new SecureRandom();
            secRand.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
        }
        return secRand;
    }

    /**
     * Reseeds the random number generator with the supplied seed.
     * <p>
     * Will create and initialize if null.
     * </p>
     *
     * @param seed
     *            the seed value to use
     */
    public void reSeed(long seed) {
        if (rand == null) {
            initRan();
        }
        rand.setSeed(seed);
    }

    /**
     * Reseeds the secure random number generator with the current time in
     * milliseconds.
     * <p>
     * Will create and initialize if null.
     * </p>
     */
    public void reSeedSecure() {
        if (secRand == null) {
            secRand = new SecureRandom();
        }
        secRand.setSeed(System.currentTimeMillis());
    }

    /**
     * Reseeds the secure random number generator with the supplied seed.
     * <p>
     * Will create and initialize if null.
     * </p>
     *
     * @param seed
     *            the seed value to use
     */
    public void reSeedSecure(long seed) {
        if (secRand == null) {
            secRand = new SecureRandom();
        }
        secRand.setSeed(seed);
    }

    /**
     * Reseeds the random number generator with
     * {@code System.currentTimeMillis() + System.identityHashCode(this))}.
     */
    public void reSeed() {
        if (rand == null) {
            initRan();
        }
        rand.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
    }

    /**
     * Sets the PRNG algorithm for the underlying SecureRandom instance using
     * the Security Provider API. The Security Provider API is defined in <a
     * href =
     * "http://java.sun.com/j2se/1.3/docs/guide/security/CryptoSpec.html#AppA">
     * Java Cryptography Architecture API Specification & Reference.</a>
     * <p>
     * <strong>USAGE NOTE:</strong> This method carries <i>significant</i>
     * overhead and may take several seconds to execute.
     * </p>
     *
     * @param algorithm
     *            the name of the PRNG algorithm
     * @param provider
     *            the name of the provider
     * @throws NoSuchAlgorithmException
     *             if the specified algorithm is not available
     * @throws NoSuchProviderException
     *             if the specified provider is not installed
     */
    public void setSecureAlgorithm(String algorithm, String provider)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        secRand = SecureRandom.getInstance(algorithm, provider);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Uses a 2-cycle permutation shuffle. The shuffling process is described <a
     * href="http://www.maths.abdn.ac.uk/~igc/tch/mx4002/notes/node83.html">
     * here</a>.
     * </p>
     */
    public int[] nextPermutation(int n, int k) {
        if (k > n) {
            throw new NumberIsTooLargeException(LocalizedFormats.PERMUTATION_EXCEEDS_N,
                                                k, n, true);
        }
        if (k <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.PERMUTATION_SIZE,
                                                   k);
        }

        int[] index = getNatural(n);
        shuffle(index, n - k);
        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = index[n - i - 1];
        }

        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <strong>Algorithm Description</strong>: Uses a 2-cycle permutation
     * shuffle to generate a random permutation of <code>c.size()</code> and
     * then returns the elements whose indexes correspond to the elements of the
     * generated permutation. This technique is described, and proven to
     * generate random samples <a
     * href="http://www.maths.abdn.ac.uk/~igc/tch/mx4002/notes/node83.html">
     * here</a>
     * </p>
     */
    public Object[] nextSample(Collection<?> c, int k) {

        int len = c.size();
        if (k > len) {
            throw new NumberIsTooLargeException(LocalizedFormats.SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE,
                                                k, len, true);
        }
        if (k <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, k);
        }

        Object[] objects = c.toArray();
        int[] index = nextPermutation(len, k);
        Object[] result = new Object[k];
        for (int i = 0; i < k; i++) {
            result[i] = objects[index[i]];
        }
        return result;
    }

    /**
     * Generate a random deviate from the given distribution using the
     * <a href="http://en.wikipedia.org/wiki/Inverse_transform_sampling"> inversion method.</a>
     *
     * @param distribution Continuous distribution to generate a random value from
     * @return a random value sampled from the given distribution
     * @since 2.2
     */
    public double nextInversionDeviate(RealDistribution distribution) {
        return distribution.inverseCumulativeProbability(nextUniform(0, 1));

    }

    /**
     * Generate a random deviate from the given distribution using the
     * <a href="http://en.wikipedia.org/wiki/Inverse_transform_sampling"> inversion method.</a>
     *
     * @param distribution Integer distribution to generate a random value from
     * @return a random value sampled from the given distribution
     * @since 2.2
     */
    public int nextInversionDeviate(IntegerDistribution distribution) {
        return distribution.inverseCumulativeProbability(nextUniform(0, 1));
    }

    // ------------------------Private methods----------------------------------

    /**
     * Uses a 2-cycle permutation shuffle to randomly re-order the last elements
     * of list.
     *
     * @param list
     *            list to be shuffled
     * @param end
     *            element past which shuffling begins
     */
    private void shuffle(int[] list, int end) {
        int target = 0;
        for (int i = list.length - 1; i >= end; i--) {
            if (i == 0) {
                target = 0;
            } else {
                target = nextInt(0, i);
            }
            int temp = list[target];
            list[target] = list[i];
            list[i] = temp;
        }
    }

    /**
     * Returns an array representing n.
     *
     * @param n
     *            the natural number to represent
     * @return array with entries = elements of n
     */
    private int[] getNatural(int n) {
        int[] natural = new int[n];
        for (int i = 0; i < n; i++) {
            natural[i] = i;
        }
        return natural;
    }

}
