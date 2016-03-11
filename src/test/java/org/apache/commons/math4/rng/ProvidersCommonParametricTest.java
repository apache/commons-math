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
package org.apache.commons.math4.rng;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Assume;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.InsufficientDataException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.distribution.RealDistribution;
import org.apache.commons.math4.distribution.UniformRealDistribution;
import org.apache.commons.math4.stat.inference.KolmogorovSmirnovTest;
import org.apache.commons.math4.stat.inference.ChiSquareTest;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.rng.internal.util.NumberFactory;

/**
 * Test for tests which all generators must pass.
 */
@RunWith(value=Parameterized.class)
public class ProvidersCommonParametricTest {
    /** RNG under test. */
    private final UniformRandomProvider generator;
    /** RNG specifier. */
    private final RandomSource originalSource;
    /** Seed (constructor's first parameter). */
    private final Object originalSeed;
    /** Constructor's additional parameters. */
    private final Object[] originalArgs;

    /**
     * Initializes generator instance.
     *
     * @param rng RNG to be tested.
     */
    public ProvidersCommonParametricTest(ProvidersList.Data data) {
        originalSource = data.getSource();
        originalSeed = data.getSeed();
        originalArgs = data.getArgs();
        generator = RandomSource.create(originalSource, originalSeed, originalArgs);
    }

    @Parameters(name = "{index}: data={0}")
    public static Iterable<ProvidersList.Data[]> getList() {
        return ProvidersList.list();
    }


    // Precondition tests

    @Test(expected=MathIllegalArgumentException.class)
    public void testPreconditionNextInt1() {
        generator.nextInt(-1);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testPreconditionNextInt2() {
        generator.nextInt(0);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testPreconditionNextLong1() {
        generator.nextLong(-1);
    }

    @Test(expected=MathIllegalArgumentException.class)
    public void testPreconditionNextLong2() {
        generator.nextLong(0);
    }

    @Test(expected=OutOfRangeException.class)
    public void testPreconditionNextBytes1() {
        final int size = 10;
        final int num = 1;
        final byte[] buf = new byte[size];
        generator.nextBytes(buf, -1, num);
    }
    @Test(expected=OutOfRangeException.class)
    public void testPreconditionNextBytes2() {
        final int size = 10;
        final byte[] buf = new byte[size];
        generator.nextBytes(buf, size, 0);
    }
    @Test(expected=OutOfRangeException.class)
    public void testPreconditionNextBytes3() {
        final int size = 10;
        final int offset = 2;
        final byte[] buf = new byte[size];
        generator.nextBytes(buf, offset, size - offset + 1);
    }
    @Test(expected=OutOfRangeException.class)
    public void testPreconditionNextBytes4() {
        final int size = 10;
        final int offset = 1;
        final byte[] buf = new byte[size];
        generator.nextBytes(buf, offset, -1);
    }


    // Uniformity tests
 
    @Test
    public void testUniformNextBytesFullBuffer() {
        // Value chosen to exercise all the code lines in the
        // "nextBytes" methods.
        final int size = 23;
        final byte[] buffer = new byte[size];

        final Runnable nextMethod = new Runnable() {
            @Override
            public void run() {
                generator.nextBytes(buffer);
            }
        };

        final double smallAlpha = 1e-3;
        Assert.assertTrue(isUniformNextBytes(buffer, 0, size, nextMethod, smallAlpha));
    }

    @Test
    public void testUniformNextBytesPartialBuffer() {
        final int totalSize = 1234;
        final int offset = 567;
        final int size = 89;

        final byte[] buffer = new byte[totalSize];

        final Runnable nextMethod = new Runnable() {
            @Override
            public void run() {
                generator.nextBytes(buffer, offset, size);
            }
        };

        // Test should pass for the part of the buffer where values are put.
        final double smallAlpha = 1e-3;
        Assert.assertTrue("Test can fail randomly due to sampling error with probability " + smallAlpha,
                          isUniformNextBytes(buffer, offset, offset + size, nextMethod, smallAlpha));

        // Test must fail for the parts of the buffer where no values are put.
        final double largeAlpha = 0.5;
        Assert.assertFalse(isUniformNextBytes(buffer, 0, offset, nextMethod, largeAlpha));
        Assert.assertFalse(isUniformNextBytes(buffer, offset + size, buffer.length, nextMethod, largeAlpha));
    }

    @Test
    public void testUniformNextIntegerInRange() {
        checkNextIntegerInRange(4, 1000);
        checkNextIntegerInRange(10, 1000);
        checkNextIntegerInRange(12, 1000);
        checkNextIntegerInRange(31, 1000);
        checkNextIntegerInRange(32, 1000);
        checkNextIntegerInRange(2016128993, 1000);
        checkNextIntegerInRange(1834691456, 1000);
        checkNextIntegerInRange(869657561, 1000);
        checkNextIntegerInRange(1570504788, 1000);
    }

    @Test
    public void testUniformNextLongInRange() {
        checkNextLongInRange(4, 1000);
        checkNextLongInRange(11, 1000);
        checkNextLongInRange(19, 1000);
        checkNextLongInRange(31, 1000);
        checkNextLongInRange(32, 1000);

        final long q = Long.MAX_VALUE / 4;
        checkNextLongInRange(q, 1000);
        checkNextLongInRange(2 * q, 1000);
        checkNextLongInRange(3 * q, 1000);
    }

    @Test
    public void testUniformNextFloat() {
        final double[] sample = new double[1000];
        for (int i = 0; i < sample.length; i++) {
            sample[i] = generator.nextFloat();
        }
        final RealDistribution uniformDistribution = new UniformRealDistribution(0, 1);
        final KolmogorovSmirnovTest ks = new KolmogorovSmirnovTest();
        Assert.assertFalse(generator.toString(),
                           ks.kolmogorovSmirnovTest(uniformDistribution, sample, 0.01));
    }

    @Test
    public void testUniformNextDouble() {
        final double[] sample = new double[1000];
        for (int i = 0; i < sample.length; i++) {
            sample[i] = generator.nextDouble();
        }
        final RealDistribution uniformDistribution = new UniformRealDistribution(0, 1);
        final KolmogorovSmirnovTest ks = new KolmogorovSmirnovTest();
        Assert.assertFalse(generator.toString(),
                           ks.kolmogorovSmirnovTest(uniformDistribution, sample, 0.01));
    }

    @Test
    public void testUniformNextIntRandomWalk() {
        final Callable<Boolean> nextMethod = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return generator.nextInt() >= 0;
            }
        };

        checkRandomWalk(1000, nextMethod);
    }

    @Test
    public void testUniformNextLongRandomWalk() {
        final Callable<Boolean> nextMethod = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return generator.nextLong() >= 0;
            }
        };

        checkRandomWalk(1000, nextMethod);
    }

    @Test
    public void testUniformNextBooleanRandomWalk() {
        final Callable<Boolean> nextMethod = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return generator.nextBoolean();
            }
        };

        checkRandomWalk(1000, nextMethod);
    }

    // Seeding tests.

    @Test(expected=MathUnsupportedOperationException.class)
    public void testUnsupportedSeedType() {
        final byte seed = 123;
        RandomSource.create(originalSource, seed, originalArgs);
    }

    @Test
    public void testAllSeedTypes() {
        final Integer intSeed = -12131415;
        final Long longSeed = -1213141516171819L;
        final int[] intArraySeed = new int[] { 0, 11, -22, 33, -44, 55, -66, 77, -88, 99 };
        final long[] longArraySeed = new long[] { 11111L, -222222L, 3333333L, -44444444L };

        final Object[] seeds = new Object[] { intSeed, longSeed, intArraySeed, longArraySeed };

        int nonNativeSeedCount = 0;
        int seedCount = 0;
        for (Object s : seeds) {
            ++seedCount;
            if (!(originalSource.isNativeSeed(s))) {
                ++nonNativeSeedCount;
            }

            Assert.assertNotEquals(intSeed, originalSeed);
            RandomSource.create(originalSource, s, originalArgs);
        }

        Assert.assertEquals(4, seedCount);
        Assert.assertEquals(3, nonNativeSeedCount);
    }

    // State save and restore tests.

    @Test
    public void testStateSettable() {
        // Should be fairly large in order to ensure that all the internal
        // state is away from its initial settings.
        final int n = 10000;

        // Save.
        final RandomSource.State state = RandomSource.saveState(generator);
        // Store some values.
        final List<Number> listOrig = makeList(n);
        // Discard a few more.
        final List<Number> listDiscard = makeList(n);
        Assert.assertTrue(listDiscard.size() != 0);
        Assert.assertFalse(listOrig.equals(listDiscard));
        // Reset.
        RandomSource.restoreState(generator, state);
        // Replay.
        final List<Number> listReplay = makeList(n);
        Assert.assertFalse(listOrig == listReplay);
        // Check that the restored state is the same as the orginal.
        Assert.assertTrue(listOrig.equals(listReplay));
    }

    @Test
    public void testSerializedState()
        throws IOException,
               ClassNotFoundException {
        // Large "n" is not necessary here as we only test the serialization.
        final int n = 100;

        // Save and serialize.
        final RandomSource.State stateOrig = RandomSource.saveState(generator);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(stateOrig);

        // Store some values.
        final List<Number> listOrig = makeList(n);

        // Discard a few more.
        final List<Number> listDiscard = makeList(n);
        Assert.assertTrue(listDiscard.size() != 0);
        Assert.assertFalse(listOrig.equals(listDiscard));

        // Retrieve from serialized stream.
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        final RandomSource.State stateNew = (RandomSource.State) ois.readObject();

        Assert.assertTrue(stateOrig != stateNew);

        // Reset.
        RandomSource.restoreState(generator, stateNew);

        // Replay.
        final List<Number> listReplay = makeList(n);
        Assert.assertFalse(listOrig == listReplay);

        // Check that the serialized data recreated the orginal state.
        Assert.assertTrue(listOrig.equals(listReplay));
    }

    @Test(expected=ClassCastException.class)
    public void testStateWrongClass() {
        // Try to restore with an invalid state.
        RandomSource.restoreState(generator, new RandomSource.State() {});
    }

    @Test(expected=InsufficientDataException.class)
    public void testStateWrongSize() {
        // We don't know what is the state of "java.lang.Random": skipping.
        Assume.assumeTrue(generator.toString().indexOf("JDKRandom") == -1);

        final RandomSource.State state = RandomSource.saveState(new DummyGenerator());
        // Try to restore with an invalid state (wrong size).
        RandomSource.restoreState(generator, state);
    }

    ///// Support methods below.

    /**
     * Populates a list with random numbers.
     *
     * @param n Loop counter.
     * @return a list containing {@code 11 * n} random numbers.
     */
    private List<Number> makeList(int n) {
        final List<Number> list = new ArrayList<Number>();

        for (int i = 0; i < n; i++) {
            // Append 11 values.
            list.add(generator.nextInt());
            list.add(generator.nextInt(21));
            list.add(generator.nextInt(436));
            list.add(generator.nextLong());
            list.add(generator.nextLong(157894));
            list.add(generator.nextLong(5745833));
            list.add(generator.nextFloat());
            list.add(generator.nextFloat());
            list.add(generator.nextDouble());
            list.add(generator.nextDouble());
            list.add(generator.nextDouble());
        }

        return list;
    }

    /**
     * Checks that the generator values can be placed into 256 bins with
     * approximately equal number of counts.
     * Test allows to select only part of the buffer for performong the
     * statistics.
     *
     * @param buffer Buffer to be filled.
     * @param first First element (included) of {@code buffer} range for
     * which statistics must be taken into account.
     * @param last Last element (excluded) of {@code buffer} range for
     * which statistics must be taken into account.
     * for which statistics must be taken into account.
     * @param nextMethod Method that fills the given {@code buffer}.
     * @param alpha Probability for chi-square test.
     * @return {@code true} if the distribution is uniform.
     */
    private boolean isUniformNextBytes(byte[] buffer,
                                       int first,
                                       int last,
                                       Runnable nextMethod,
                                       double alpha) {
        final int sampleSize = 100000;

        // Number of possible values.
        final int byteRange = 256;
        // To transform a byte value into its bin index.
        final int byteRangeOffset = 128;

        // Bins.
        final long[] count = new long[byteRange];
        final double[] expected = new double[byteRange];

        for (int i = 0; i < byteRange; i++) {
            expected[i] = sampleSize / (double) byteRange;
        }

        try {
            for (int k = 0; k < sampleSize; k++) {
                nextMethod.run();

                for (int i = first; i < last; i++) {
                    final byte b = buffer[i];
                    ++count[b + byteRangeOffset];
                }
            }
        } catch (Exception e) {
            // Should never happen.
            throw new RuntimeException("Unexpected");
        }

        final boolean reject = new ChiSquareTest().chiSquareTest(expected, count, alpha);
        return !reject;
    }

    /**
     * Checks that the generator values can be placed into 2 bins with
     * approximately equal number of counts.
     * The test uses the expectation from a fixed-step "random walk".
     *
     * @param nextMethod Method that returns {@code true} if the generated
     * values are to be placed in the first bin, {@code false} if it must
     * go to the second bin.
     */
    private void checkRandomWalk(int sampleSize,
                                 Callable<Boolean> nextMethod) {
        int walk = 0;

        try {
            for (int k = 0; k < sampleSize; ++k) {
                if (nextMethod.call()) {
                    ++walk;
                } else {
                    --walk;
                }
            }
        } catch (Exception e) {
            // Should never happen.
            throw new RuntimeException("Unexpected");
        }

        final double actual = FastMath.abs(walk);
        final double max = FastMath.sqrt(sampleSize) * 2.576;
        Assert.assertTrue(generator + ": Walked too far astray: " + actual +
                          " > " + max +
                          " (test will fail randomly about 1 in 100 times)",
                          actual < max);
    }

    /**
     * Tests uniformity of the distribution produced by the {@code nextInt(int)}.
     *
     * @param max Upper bound.
     * @param sampleSize Number of random values generated.
     */
    private void checkNextIntegerInRange(final int max,
                                         int sampleSize) {
        final Callable<Integer> nextMethod = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return generator.nextInt(max);
            }
        };

        checkNextInRange(max, sampleSize, nextMethod);
    }

    /**
     * Tests uniformity of the distribution produced by the {@code nextLong(long)}.
     *
     * @param max Upper bound.
     * @param sampleSize Number of random values generated.
     */
    private void checkNextLongInRange(final long max,
                                      int sampleSize) {
        final Callable<Long> nextMethod = new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return generator.nextLong(max);
            }
        };

        checkNextInRange(max, sampleSize, nextMethod);
    }

    /**
     * Tests uniformity of the distribution produced by the given
     * {@code nextMethod}.
     * It performs a chi-square test of homogeneity of the observed
     * distribution with the expected uniform distribution.
     * Tests are performed at the 1% level and an average failure rate
     * higher than 2% (i.e. more than 20 null hypothesis rejections)
     * causes the test case to fail.
     *
     * @param max Upper bound.
     * @param nextMethod method to call (either "nextInt(max)"
     * @param sampleSize Number of random values generated.
     */
    private <T extends Number> void checkNextInRange(T max,
                                                     int sampleSize,
                                                     Callable<T> nextMethod) {
        final ChiSquareTest testStatistic = new ChiSquareTest();
        final int numTests = 1000;
        final long n = max.longValue();

        // Set up bins.
        long[] binUpperBounds;
        if (n < 32) {
            binUpperBounds = new long[(int) n];
            for (int k = 0; k < n; k++) {
                binUpperBounds[k] = k + 1;
            }
        } else {
            final int numBins = 10;
            binUpperBounds = new long[numBins];
            final long step = n / numBins;
            for (int k = 0; k < numBins; k++) {
                binUpperBounds[k] = (k + 1) * step;
            }
        }

        // Run the tests.
        int numFailures = 0;

        final int binCount = binUpperBounds.length;
        final long[] observed = new long[binCount];
        final double[] expected = new double[binCount];

        long previousUpperBound = 0;
        for (int k = 0; k < binCount; k++) {
            final long range = binUpperBounds[k] - previousUpperBound;
            expected[k] = sampleSize * (range / (double) n);
            previousUpperBound = binUpperBounds[k];
        }

        try {
            for (int i = 0; i < numTests; i++) {
                Arrays.fill(observed, 0);
                for (int j = 0; j < sampleSize; j++) {
                    final long value = nextMethod.call().longValue();
                    Assert.assertTrue("Range", (value >= 0) && (value < n));

                    for (int k = 0; k < binCount; k++) {
                        if (value < binUpperBounds[k]) {
                            ++observed[k];
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Should never happen.
            throw new RuntimeException("Unexpected");
        }

        if (testStatistic.chiSquareTest(expected, observed) < 0.01) {
            ++numFailures;
        }

        if ((double) numFailures / (double) numTests > 0.02) {
            Assert.fail(generator + ": Too many failures for n = " + n +
                        " (" + numFailures + " out of " + numTests + " tests failed)");
        }
    }

    /**
     * @param rng Generator.
     * @param chunkSize Size of the small buffer.
     * @param numChunks Number of chunks that make the large buffer.
     */
    static void checkNextBytesChunks(UniformRandomProvider rng,
                                     int chunkSize,
                                     int numChunks) {
        final byte[] b1 = new byte[chunkSize * numChunks];
        final byte[] b2 = new byte[chunkSize];

        final RandomSource.State state = RandomSource.saveState(rng);

        // Generate the chunks in a single call.
        rng.nextBytes(b1);

        // Reset to previous state.
        RandomSource.restoreState(rng, state);

        // Generate the chunks in consecutive calls.
        for (int i = 0; i < numChunks; i++) {
            rng.nextBytes(b2);
        }

        // Store last "chunkSize" bytes of b1 into b3.
        final byte[] b3 = new byte[chunkSize];
        System.arraycopy(b1, b1.length - b3.length, b3, 0, b3.length);

        // Sequence of calls must be the same.
        Assert.assertArrayEquals("chunkSize=" + chunkSize + " numChunks=" + numChunks,
                                 b2, b3);
    }
}

/**
 * Dummy class for checking that restoring fails when an invalid state is used.
 */
class DummyGenerator extends org.apache.commons.math4.rng.internal.source32.IntProvider {
    /** State. */
    private int state;

    /** {@inheritDoc} */
    @Override
    public int next() {
        return 4; // https://www.xkcd.com/221/
    }

    /** {@inheritDoc} */
    @Override
    protected byte[] getStateInternal() {
        return NumberFactory.makeByteArray(state);
    }

    /** {@inheritDoc} */
    @Override
    protected void setStateInternal(byte[] s) {
        state = NumberFactory.makeInt(s);
    }
}
