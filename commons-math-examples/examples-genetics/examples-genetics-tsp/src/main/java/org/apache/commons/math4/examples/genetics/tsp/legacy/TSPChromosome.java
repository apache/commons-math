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
package org.apache.commons.math4.examples.genetics.tsp.legacy;

import java.util.List;

import org.apache.commons.math3.genetics.RandomKey;
import org.apache.commons.math4.examples.genetics.tsp.commons.City;
import org.apache.commons.math4.examples.genetics.tsp.commons.DistanceMatrix;

/**
 * This class represents chromosome for tsp problem.
 */
public class TSPChromosome extends RandomKey<City> {

    /** list of cities. **/
    private final List<City> cities;

    /**
     * constructor.
     * @param representation internal representation of chromosome
     * @param cities         list of cities
     */
    public TSPChromosome(List<Double> representation, List<City> cities) {
        super(representation);
        this.cities = cities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double fitness() {
        final List<City> permutatedNodes = decode(cities);
        return -calculateTotalDistance(permutatedNodes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TSPChromosome newFixedLengthChromosome(List<Double> representation) {
        return new TSPChromosome(representation, cities);
    }

    private double calculateTotalDistance(List<City> permutedCities) {
        double totalDistance = 0.0;
        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < permutedCities.size(); i++) {
            index1 = i;
            index2 = (i == permutedCities.size() - 1) ? 0 : i + 1;
            totalDistance += calculateNodeDistance(permutedCities.get(index1), permutedCities.get(index2));
        }
        return totalDistance;
    }

    private double calculateNodeDistance(City node1, City node2) {
        return DistanceMatrix.getInstance().getDistance(node1, node2);
    }

}
