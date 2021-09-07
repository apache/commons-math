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

public interface Constants {

	int POPULATION_SIZE = 100;

	int TOURNAMENT_SIZE = 5;

	int CHROMOZOME_LENGTH = 24;

	double CROSSOVER_RATE = 1.0;

	double ELITISM_RATE = 0.25;

	double AVERAGE_MUTATION_RATE = 0.05;

	int GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS = 50;

}
