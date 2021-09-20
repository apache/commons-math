package org.apache.commons.math4.genetics.convergencecond;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.ListPopulation;
import org.apache.commons.math4.genetics.Population;
import org.apache.commons.math4.genetics.stats.internal.PopulationStatisticalSummaryImpl;
import org.junit.Assert;
import org.junit.Test;

public class UnchangedBestFitnessTest {

    @Test
    public void testIsSatisfied() {

        final int noOfGenerationsWithUnchangedBestFitness = 5;
        StoppingCondition<String> stoppingCondition = new UnchangedBestFitness<>(
                noOfGenerationsWithUnchangedBestFitness);

        double[] fitnesses = new double[10];
        for (int i = 0; i < 10; i++) {
            fitnesses[i] = i;
        }
        List<Chromosome<String>> chromosomes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final double fitness = fitnesses[i];
            Chromosome<String> ch = new Chromosome<String>() {

                @Override
                public int compareTo(Chromosome<String> o) {
                    double diff = this.evaluate() - o.evaluate();
                    return diff > 0 ? 1 : (diff < 0 ? -1 : 0);
                }

                @Override
                public double evaluate() {
                    return fitness;
                }

                @Override
                public String decode() {
                    return "Fixed";
                }
            };
            chromosomes.add(ch);
        }
        Population<String> pop = new ListPopulation<>(chromosomes, 10);

        double initialMaxFitness = new PopulationStatisticalSummaryImpl<>(pop).getMaxFitness();

        int counter = 0;
        while (!stoppingCondition.isSatisfied(pop)) {
            counter++;
        }

        double maxFitnessAfterConvergence = new PopulationStatisticalSummaryImpl<>(pop).getMaxFitness();

        Assert.assertEquals(initialMaxFitness, maxFitnessAfterConvergence, .001);
        Assert.assertEquals(noOfGenerationsWithUnchangedBestFitness, counter);
    }

}
