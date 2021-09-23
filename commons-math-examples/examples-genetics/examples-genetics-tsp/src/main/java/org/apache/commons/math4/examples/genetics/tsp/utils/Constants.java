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

package org.apache.commons.math4.examples.genetics.tsp.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math4.examples.genetics.tsp.commons.City;

/**
 * This class contains all required constants for this example.
 */
public final class Constants {

    /** size of population. **/
    public static final int POPULATION_SIZE = 100;

    /** size of tournament. **/
    public static final int TOURNAMENT_SIZE = 5;

    /** length of chromosome. **/
    public static final int CHROMOSOME_LENGTH = 14;

    /** rate of crossover. **/
    public static final double CROSSOVER_RATE = 1.0;

    /** rate of elitism. **/
    public static final double ELITISM_RATE = 0.25;

    /** rate of mutation. **/
    public static final double AVERAGE_MUTATION_RATE = 0.05;

    /** maximum number of generations with unchanged best fitness. **/
    public static final int GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS = 50;

    /** list of cities. **/
    public static final List<City> CITIES = Collections.unmodifiableList(
            Arrays.asList(new City[] {new City(1, 0, 0), new City(2, 1, 0), new City(3, 2, 0), new City(4, 3, 0),
                new City(5, 3, 1), new City(6, 3, 2), new City(7, 3, 3), new City(8, 2, 3), new City(9, 1, 3),
                new City(10, 0, 3), new City(11, 1, 2), new City(12, 2, 2), new City(13, 2, 1), new City(14, 1, 1)}));

    /** encoding for console logger. **/
    public static final String ENCODING = "UTF-8";

    private Constants() {

    }

}
