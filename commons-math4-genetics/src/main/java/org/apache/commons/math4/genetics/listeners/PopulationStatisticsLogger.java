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

package org.apache.commons.math4.genetics.listeners;

import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;
import org.apache.commons.math4.genetics.stats.internal.PopulationStatisticalSummaryImpl;

/**
 * Logs population statistics during the convergence process.
 */
public final class PopulationStatisticsLogger implements ConvergenceListener {

    /**
     * Logs the population statistics as console message.
     */
    @Override
    public void notify(Population population) {
        final PopulationStatisticalSummary populationStatisticalSummary = new PopulationStatisticalSummaryImpl(population);
        System.out.println("*******************Population statistics*******************");
        System.out.println("Mean Fitness : " + populationStatisticalSummary.getMeanFitness());
        System.out.println("Max Fitness : " + populationStatisticalSummary.getMaxFitness());
        System.out.println("Fitness Variance : " + populationStatisticalSummary.getFitnessVariance());
    }

}
