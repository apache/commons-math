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

package org.apache.commons.math4.ga.listener;

import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.stats.PopulationStatisticalSummary;
import org.apache.commons.math4.ga.stats.internal.PopulationStatisticalSummaryImpl;
import org.apache.commons.math4.ga.utils.ConsoleLogger;

/**
 * Logs population statistics during the convergence process.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public final class PopulationStatisticsLogger<P> implements ConvergenceListener<P> {

    /** instance of consolelogger. **/
    private ConsoleLogger consoleLogger;

    public PopulationStatisticsLogger(String encoding) {
        this.consoleLogger = ConsoleLogger.getInstance(encoding);
    }

    /**
     * Logs the population statistics to console during the process of convergence.
     */
    @Override
    public void notify(int generation, Population<P> population) {
        final PopulationStatisticalSummary<P> populationStatisticalSummary = new PopulationStatisticalSummaryImpl<>(
                population);
        consoleLogger.log(
                "Population statistics for generation %d ::: Mean Fitness: %f, Max Fitness: %f, Fitness Variance: %f",
                generation, populationStatisticalSummary.getMeanFitness(), populationStatisticalSummary.getMaxFitness(),
                populationStatisticalSummary.getFitnessVariance());
    }

}