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

package org.apache.commons.math4.neuralnet.twod.util;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import org.apache.commons.math4.neuralnet.Network;
import org.apache.commons.math4.neuralnet.FeatureInitializer;
import org.apache.commons.math4.neuralnet.FeatureInitializerFactory;
import org.apache.commons.math4.neuralnet.SquareNeighbourhood;
import org.apache.commons.math4.neuralnet.twod.NeuronSquareMesh2D;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link LocationFinder}.
 */
public class LocationFinderTest {
    private final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
    private final FeatureInitializer init = FeatureInitializerFactory.uniform(rng, 0, 2);

    /*
     * Test assumes that the network is
     *
     *  0-----1
     *  |     |
     *  |     |
     *  2-----3
     */
    @Test
    public void test2x2Network() {
        final FeatureInitializer[] initArray = {init};
        final NeuronSquareMesh2D map = new NeuronSquareMesh2D(2, false,
                                                              2, false,
                                                              SquareNeighbourhood.VON_NEUMANN,
                                                              initArray);
        final LocationFinder finder = new LocationFinder(map);
        final Network net = map.getNetwork();
        LocationFinder.Location loc;

        loc = finder.getLocation(net.getNeuron(0));
        Assert.assertEquals(0, loc.getRow());
        Assert.assertEquals(0, loc.getColumn());

        loc = finder.getLocation(net.getNeuron(1));
        Assert.assertEquals(0, loc.getRow());
        Assert.assertEquals(1, loc.getColumn());

        loc = finder.getLocation(net.getNeuron(2));
        Assert.assertEquals(1, loc.getRow());
        Assert.assertEquals(0, loc.getColumn());

        loc = finder.getLocation(net.getNeuron(3));
        Assert.assertEquals(1, loc.getRow());
        Assert.assertEquals(1, loc.getColumn());
    }
}
