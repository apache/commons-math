/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math3.ml.neuralnet.sofm;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.neuralnet.FeatureInitializer;
import org.apache.commons.math3.ml.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.apache.commons.math3.ml.neuralnet.Network;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.OffsetFeatureInitializer;
import org.apache.commons.math3.ml.neuralnet.UpdateAction;
import org.apache.commons.math3.ml.neuralnet.oned.NeuronString;
import org.apache.commons.math3.util.Precision;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link KohonenUpdateAction} class.
 */
public class KohonenUpdateActionTest {
    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     */
    @Test
    public void testUpdate() {
        final FeatureInitializer init
            = new OffsetFeatureInitializer(FeatureInitializerFactory.uniform(0, 0.1));
        final FeatureInitializer[] initArray = { init };

        final int netSize = 3;
        final Network net = new NeuronString(netSize, false, initArray).getNetwork();
        final DistanceMeasure dist = new EuclideanDistance();
        final LearningFactorFunction learning
            = LearningFactorFunctionFactory.exponentialDecay(1, 0.1, 100);
        final NeighbourhoodSizeFunction neighbourhood
            = NeighbourhoodSizeFunctionFactory.exponentialDecay(3, 1, 100);
        final UpdateAction update = new KohonenUpdateAction(dist, learning, neighbourhood);

        // The following test ensures that, after one "update",
        // 1. when the initial learning rate equal to 1, the best matching
        //    neuron's features are mapped to the input's features,
        // 2. when the initial neighbourhood is larger than the network's size,
        //    all neuron's features get closer to the input's features.

        final double[] features = new double[] { 0.3 };
        final double[] distancesBefore = new double[netSize];
        int count = 0;
        for (Neuron n : net) {
            distancesBefore[count++] = dist.compute(n.getFeatures(), features);
        }
        final Neuron bestBefore = MapUtils.findBest(features, net, dist);

        // Initial distance from the best match is larger than zero.
        Assert.assertTrue(dist.compute(bestBefore.getFeatures(), features) >= 0.2);

        update.update(net, features);

        final double[] distancesAfter = new double[netSize];
        count = 0;
        for (Neuron n : net) {
            distancesAfter[count++] = dist.compute(n.getFeatures(), features);
        }
        final Neuron bestAfter = MapUtils.findBest(features, net, dist);

        Assert.assertEquals(bestBefore, bestAfter);
        // Distance is now zero.
        Assert.assertEquals(0, dist.compute(bestAfter.getFeatures(), features), Precision.EPSILON);

        for (int i = 0; i < netSize; i++) {
            // All distances have decreased.
            Assert.assertTrue(distancesAfter[i] < distancesBefore[i]);
        }
    }
}
