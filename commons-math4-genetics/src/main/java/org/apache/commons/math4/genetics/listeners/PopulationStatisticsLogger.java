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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.math4.genetics.exception.GeneticException;
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
        final PopulationStatisticalSummary populationStatisticalSummary = new PopulationStatisticalSummaryImpl(
                population);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
            writer.write("*******************Population statistics*******************");
            writer.newLine();
            writer.write("Mean Fitness : " + populationStatisticalSummary.getMeanFitness());
            writer.newLine();
            writer.write("Max Fitness : " + populationStatisticalSummary.getMaxFitness());
            writer.newLine();
            writer.write("Fitness Variance : " + populationStatisticalSummary.getFitnessVariance());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new GeneticException("Error while logging", e);
        }
    }

}
