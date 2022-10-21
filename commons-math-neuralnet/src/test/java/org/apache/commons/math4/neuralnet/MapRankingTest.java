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

package org.apache.commons.math4.neuralnet;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import org.apache.commons.math4.neuralnet.oned.NeuronString;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link MapRanking} class.
 */
public class MapRankingTest {

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     */
    @Test
    public void testFindClosestNeuron() {
        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
        final FeatureInitializer init
            = new OffsetFeatureInitializer(FeatureInitializerFactory.uniform(rng, -0.1, 0.1));
        final FeatureInitializer[] initArray = {init};

        final MapRanking ranking = new MapRanking(new NeuronString(3, false, initArray).getNetwork(),
                                                  new EuclideanDistance());

        final Set<Neuron> allBest = new HashSet<>();
        final Set<Neuron> best = new HashSet<>();
        double[][] features;

        // The following tests ensures that
        // 1. the same neuron is always selected when the input feature is
        //    in the range of the initializer,
        // 2. different network's neuron have been selected by inputs features
        //    that belong to different ranges.

        best.clear();
        features = new double[][] {
            {-1 },
            {0.4 },
        };
        for (double[] f : features) {
            best.addAll(ranking.rank(f, 1));
        }
        Assert.assertEquals(1, best.size());
        allBest.addAll(best);

        best.clear();
        features = new double[][] {
            {0.6 },
            {1.4 },
        };
        for (double[] f : features) {
            best.addAll(ranking.rank(f, 1));
        }
        Assert.assertEquals(1, best.size());
        allBest.addAll(best);

        best.clear();
        features = new double[][] {
            {1.6 },
            {3 },
        };
        for (double[] f : features) {
            best.addAll(ranking.rank(f, 1));
        }
        Assert.assertEquals(1, best.size());
        allBest.addAll(best);

        Assert.assertEquals(3, allBest.size());
    }

    @Test
    public void testRankPrecondition() {
        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
        final FeatureInitializer init
            = new OffsetFeatureInitializer(FeatureInitializerFactory.uniform(rng, -0.1, 0.1));
        final FeatureInitializer[] initArray = {init};

        final EuclideanDistance distance = new EuclideanDistance();
        final Network network = new NeuronString(3, false, initArray).getNetwork();
        final MapRanking mapRanking = new MapRanking(network, distance);

        assertThrows(IllegalArgumentException.class, () ->
                mapRanking.rank(new double[]{-1}, 0)
        );
    }

    @Test
    public void testSort() {
        final Set<Neuron> list = new HashSet<>();

        for (int i = 0; i < 4; i++) {
            list.add(new Neuron(i, new double[] {i - 0.5}));
        }

        final MapRanking rank = new MapRanking(list, new EuclideanDistance());
        final List<Neuron> sorted = rank.rank(new double[] {3.4});

        final long[] expected = new long[] {3, 2, 1, 0};
        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals(expected[i], sorted.get(i).getIdentifier());
        }
    }

}
