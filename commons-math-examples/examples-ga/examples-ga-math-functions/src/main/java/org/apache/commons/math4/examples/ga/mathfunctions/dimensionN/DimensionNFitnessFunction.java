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

package org.apache.commons.math4.examples.ga.mathfunctions.dimensionN;

import org.apache.commons.math4.ga.fitness.FitnessFunction;

/**
 * This class represents the mathematical fitness function for optimizing a 2
 * dimension mathematical function.
 */
public class DimensionNFitnessFunction implements FitnessFunction<DimensionNCoordinate> {

    /**
     * Computes the fitness value based on the decoded chromosome.
     * @param coordinate The {@link DimensionNCoordinate}
     * @return the fitness value
     */
    @Override
    public double compute(DimensionNCoordinate coordinate) {
        double sumOfSquare = 0.0;
        for (Double value : coordinate.getValues()) {
            sumOfSquare += Math.pow(value, 2);
        }
        return -Math.pow(sumOfSquare, .25) * (Math.pow(Math.sin(50 * Math.pow(sumOfSquare, .1)), 2) + 1);
    }

}
