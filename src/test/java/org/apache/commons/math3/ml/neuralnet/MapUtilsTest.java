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

package org.apache.commons.math3.ml.neuralnet;

import java.util.Set;
import java.util.HashSet;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.neuralnet.oned.NeuronString;
import org.junit.Test;
import org.junit.Assert;

/**
 * Tests for {@link MapUtils} class.
 */
public class MapUtilsTest {
    /*
     * Test assumes that the network is
     *
     *  0-----1-----2
     */
    @Test
    public void testFindClosestNeuron() {
        final FeatureInitializer init
            = new OffsetFeatureInitializer(FeatureInitializerFactory.uniform(-0.1, 0.1));
        final FeatureInitializer[] initArray = { init };

        final Network net = new NeuronString(3, false, initArray).getNetwork();
        final DistanceMeasure dist = new EuclideanDistance();

        final Set<Neuron> allBest = new HashSet<Neuron>();
        final Set<Neuron> best = new HashSet<Neuron>();
        double[][] features;

        // The following tests ensures that
        // 1. the same neuron is always selected when the input feature is
        //    in the range of the initializer,
        // 2. different network's neuron have been selected by inputs features
        //    that belong to different ranges.

        best.clear();
        features = new double[][] {
            { -1 },
            { 0.4 },
        };
        for (double[] f : features) {
            best.add(MapUtils.findBest(f, net, dist));
        }
        Assert.assertEquals(1, best.size());
        allBest.addAll(best);

        best.clear();
        features = new double[][] {
            { 0.6 },
            { 1.4 },
        };
        for (double[] f : features) {
            best.add(MapUtils.findBest(f, net, dist));
        }
        Assert.assertEquals(1, best.size());
        allBest.addAll(best);

        best.clear();
        features = new double[][] {
            { 1.6 },
            { 3 },
        };
        for (double[] f : features) {
            best.add(MapUtils.findBest(f, net, dist));
        }
        Assert.assertEquals(1, best.size());
        allBest.addAll(best);

        Assert.assertEquals(3, allBest.size());
    }

    @Test
    public void testSort() {
        final Set<Neuron> list = new HashSet<Neuron>();

        for (int i = 0; i < 4; i++) {
            list.add(new Neuron(i, new double[] { i - 0.5 }));
        }

        final Neuron[] sorted = MapUtils.sort(new double[] { 3.4 },
                                              list,
                                              new EuclideanDistance());

        final long[] expected = new long[] { 3, 2, 1, 0 };
        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals(expected[i], sorted[i].getIdentifier());
        }
    }
}
