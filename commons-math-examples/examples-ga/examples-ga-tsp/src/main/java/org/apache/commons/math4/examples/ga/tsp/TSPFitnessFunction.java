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
package org.apache.commons.math4.examples.ga.tsp;

import java.util.List;


import org.apache.commons.math4.examples.ga.tsp.commons.City;
import org.apache.commons.math4.examples.ga.tsp.commons.DistanceMatrix;
import org.apache.commons.math4.ga.fitness.FitnessFunction;

/**
 * This class represents the fitness function for tsp.
 */
public class TSPFitnessFunction implements FitnessFunction<List<City>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public double compute(List<City> cities) {
        double totalDistance = 0.0;
        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < cities.size(); i++) {
            index1 = i;
            index2 = (i == cities.size() - 1) ? 0 : i + 1;
            totalDistance += calculateNodeDistance(cities.get(index1), cities.get(index2));
        }
        return -totalDistance;
    }

    private double calculateNodeDistance(City node1, City node2) {
        final DistanceMatrix distanceMatrix = DistanceMatrix.getInstance();
        return distanceMatrix.getDistance(node1, node2);
    }

}
