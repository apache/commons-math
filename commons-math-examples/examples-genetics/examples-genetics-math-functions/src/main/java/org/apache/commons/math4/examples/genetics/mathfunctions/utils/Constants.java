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
package org.apache.commons.math4.examples.genetics.mathfunctions.utils;

/**
 * This abstraction maintains constants used by this module.
 */
public final class Constants {

    /** size of population. **/
    public static final int POPULATION_SIZE = 20;

    /** size of tournament. **/
    public static final int TOURNAMENT_SIZE = 2;

    /** length of chromosome. **/
    public static final int CHROMOSOME_LENGTH = 24;

    /** rate of crossover. **/
    public static final double CROSSOVER_RATE = 1.0;

    /** rate of elitism. **/
    public static final double ELITISM_RATE = 0.25;

    /** rate of mutation. **/
    public static final double AVERAGE_MUTATION_RATE = 0.05;

    /** number of generations with unchanged best fitness. **/
    public static final int GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS = 50;

    /** encoding for console logger. **/
    public static final String ENCODING = "UTF-8";

    /**
     * constructor.
     */
    private Constants() {

    }

}
