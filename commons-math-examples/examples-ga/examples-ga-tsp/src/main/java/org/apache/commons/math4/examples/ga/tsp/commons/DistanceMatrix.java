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
package org.apache.commons.math4.examples.ga.tsp.commons;

import java.util.List;

import org.apache.commons.math4.examples.ga.tsp.utils.Constants;

/**
 * This class represents the distance matrix between cities.
 */
public final class DistanceMatrix {

    /** instance of the class. **/
    private static final DistanceMatrix INSTANCE = new DistanceMatrix();

    /** distances between cities. **/
    private double[][] distances;

    private DistanceMatrix() {
        initialize(Constants.CITIES);
    }

    /**
     * Returns distances between two cities.
     * @param city1 first city
     * @param city2 second city
     * @return distance
     */
    public double getDistance(City city1, City city2) {
        return distances[city1.getIndex() - 1][city2.getIndex() - 1];
    }

    /**
     * Initializes the distance matrix.
     * @param cities list of cities
     */
    private void initialize(List<City> cities) {
        final int len = cities.size();
        this.distances = new double[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                distances[i][j] = Math.pow(Math.pow(cities.get(i).getX() - cities.get(j).getX(), 2) +
                        Math.pow(cities.get(i).getY() - cities.get(j).getY(), 2), .5);
            }
        }
    }

    /**
     * Returns the instance of this class.
     * @return instance
     */
    public static DistanceMatrix getInstance() {
        return INSTANCE;
    }

}
