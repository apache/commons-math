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

package org.apache.commons.math4.examples.ga.mathfunctions.dimension2;

import org.apache.commons.math4.ga.fitness.FitnessFunction;

/**
 * This class represents the mathematical fitness function for optimizing a 2
 * dimension mathematical function.
 */
public class Dimension2FitnessFunction implements FitnessFunction<Dimension2Coordinate> {

    /**
     * Computes the fitness value based on the decoded chromosome.
     * @param coordinate The {@link Dimension2Coordinate}
     * @return the fitness value
     */
    @Override
    public double compute(Dimension2Coordinate coordinate) {
        return -Math.pow(Math.pow(coordinate.getX(), 2) + Math.pow(coordinate.getY(), 2), .25) *
                (Math.pow(Math.sin(50 * Math.pow(Math.pow(coordinate.getX(), 2) + Math.pow(coordinate.getY(), 2), .1)),
                        2) + 1);
    }

}
