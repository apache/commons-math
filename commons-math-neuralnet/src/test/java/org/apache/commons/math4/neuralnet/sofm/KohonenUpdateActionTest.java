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

package org.apache.commons.math4.neuralnet.sofm;

import org.junit.Test;
import org.junit.Assert;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import org.apache.commons.math4.neuralnet.DistanceMeasure;
import org.apache.commons.math4.neuralnet.EuclideanDistance;
import org.apache.commons.math4.neuralnet.FeatureInitializer;
import org.apache.commons.math4.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math4.neuralnet.MapRanking;
import org.apache.commons.math4.neuralnet.Network;
import org.apache.commons.math4.neuralnet.Neuron;
import org.apache.commons.math4.neuralnet.OffsetFeatureInitializer;
import org.apache.commons.math4.neuralnet.UpdateAction;
import org.apache.commons.math4.neuralnet.oned.NeuronString;

/**
 * Tests for {@link KohonenUpdateAction} class.
 */
public class KohonenUpdateActionTest {
    private final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();

    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     */
    @Test
    public void testUpdate() {
        final FeatureInitializer init
            = new OffsetFeatureInitializer(FeatureInitializerFactory.uniform(rng, 0, 0.1));
        final FeatureInitializer[] initArray = {init};

        final int netSize = 3;
        final Network net = new NeuronString(netSize, false, initArray).getNetwork();
        final DistanceMeasure dist = new EuclideanDistance();
        final LearningFactorFunction learning
            = LearningFactorFunctionFactory.exponentialDecay(1, 0.1, 100);
        final NeighbourhoodSizeFunction neighbourhood
            = NeighbourhoodSizeFunctionFactory.exponentialDecay(3, 1, 100);
        final UpdateAction update = new KohonenUpdateAction(dist, learning, neighbourhood);
        final MapRanking rank = new MapRanking(net, dist);

        // The following test ensures that, after one "update",
        // 1. when the initial learning rate equal to 1, the best matching
        //    neuron's features are mapped to the input's features,
        // 2. when the initial neighbourhood is larger than the network's size,
        //    all neuron's features get closer to the input's features.

        final double[] features = new double[] {0.3};
        final double[] distancesBefore = getDistances(net, dist, features);
        final Neuron bestBefore = rank.rank(features, 1).get(0);

        // Initial distance from the best match is larger than zero.
        Assert.assertTrue(dist.applyAsDouble(bestBefore.getFeatures(), features) >= 0.2);

        update.update(net, features);

        final double[] distancesAfter = getDistances(net, dist, features);
        final Neuron bestAfter = rank.rank(features, 1).get(0);

        Assert.assertEquals(bestBefore, bestAfter);
        // Distance is now zero.
        Assert.assertEquals(0, dist.applyAsDouble(bestAfter.getFeatures(), features), 1e-16);

        for (int i = 0; i < netSize; i++) {
            // All distances have decreased.
            Assert.assertTrue(distancesAfter[i] < distancesBefore[i]);
        }
    }

    /**
     * Gets the distance of each Neuron to the specified features.
     * Distances are returned ordered by the Neuron ID.
     *
     * @param net Network
     * @param dist Distance measure
     * @param features Feature vector
     * @return the distances
     */
    private static double[] getDistances(Network net,
                                         DistanceMeasure dist,
                                         double[] features) {
        return net.getNeurons()
                  .stream()
                  .sorted((a, b) -> Long.compare(a.getIdentifier(), b.getIdentifier()))
                  .mapToDouble(n -> dist.applyAsDouble(n.getFeatures(), features))
                  .toArray();
    }
}
