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
package org.apache.commons.math4.random;

import java.util.Random;
import org.apache.commons.math4.exception.MathUnsupportedOperationException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.RandomSource;
import org.apache.commons.math4.distribution.RealDistribution;
import org.apache.commons.math4.distribution.NormalDistribution;
import org.apache.commons.math4.TestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link JDKRandomAdaptor}.
 */
public class JDKRandomAdaptorTest {

    @Test
    public void testUniform() {
        final RandomSource source = RandomSource.WELL_19937_C;
        final long seed = RandomSource.createLong(); // Random seed.

        final UniformRandomProvider reference = RandomSource.create(source, seed);
        final Random random = new JDKRandomAdaptor(RandomSource.create(source, seed));

        final int n = 3; // Check several times, reusing the same RNG.
        for (int i = 0; i < n; i++) {
            checkUniform(reference, random);
        }
    }

    @Test
    public void testGaussian() {
        final RandomSource source = RandomSource.WELL_19937_C;
        final long seed = RandomSource.createLong(); // Random seed.

        final UniformRandomProvider reference = RandomSource.create(source, seed);
        final RealDistribution.Sampler s = new NormalDistribution(0, 1).createSampler(reference);

        final Random random = new JDKRandomAdaptor(RandomSource.create(source, seed));

        final int n = 11; // Check several times, reusing the same RNG.
        for (int i = 0; i < n; i++) {
            Assert.assertEquals(s.sample(), random.nextGaussian(), 0);
        }
    }

    @Test
    public void testSeedIsIgnored() {
        final RandomSource source = RandomSource.WELL_19937_C;
        final long seed = RandomSource.createLong(); // Random seed.

        Random random;

        random = new JDKRandomAdaptor(RandomSource.create(source, seed));
        final double withoutReseed = random.nextDouble();

        // Same RNG.
        random = new JDKRandomAdaptor(RandomSource.create(source, seed));
        final long differentSeed = seed + 1;
        random.setSeed(differentSeed); // Is seeding ignored?
        final double withReseed = random.nextDouble();

        Assert.assertEquals(withoutReseed, withReseed, 0);
    }

    @Test
    public void testSerializeIsNotSupported() {
        try {
            TestUtils.serializeAndRecover(new JDKRandomAdaptor(RandomSource.create(RandomSource.WELL_512_A)));
        } catch (Exception e) {
            Throwable cause = e.getCause();
            Assert.assertTrue(cause instanceof MathUnsupportedOperationException);
        }
    }

    /**
     * Check uniform random generator.
     *
     * @param rand1 Reference generator.
     * @param rand2 Generator under test.
     */
    private void checkUniform(UniformRandomProvider rand1,
                              Random rand2) {
        final int len = 11;
        final byte[] bytes1 = new byte[len];
        final byte[] bytes2 = new byte[len];
        rand1.nextBytes(bytes1);
        rand2.nextBytes(bytes2);
        Assert.assertArrayEquals(bytes1, bytes2);

        Assert.assertEquals(rand1.nextBoolean(), rand2.nextBoolean());

        Assert.assertEquals(rand1.nextInt(), rand2.nextInt());
        Assert.assertEquals(rand1.nextInt(len), rand2.nextInt(len));

        Assert.assertEquals(rand1.nextLong(), rand2.nextLong());

        Assert.assertEquals(rand1.nextDouble(), rand2.nextDouble(), 0);
        Assert.assertEquals(rand1.nextFloat(), rand2.nextFloat(), 0);
    }
}
