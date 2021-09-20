package org.apache.commons.math4.genetics.crossover;

import org.apache.commons.math4.genetics.AbstractChromosome;
import org.apache.commons.math4.genetics.AbstractListChromosome;
import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.ChromosomePair;
import org.apache.commons.math4.genetics.dummy.DummyListChromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.ChromosomeRepresentationUtils;
import org.junit.Test;

public class AbstractListChromosomeCrossoverPolicyTest {

    @Test(expected = GeneticException.class)
    public void testCrossoverWithNonListChromosome() {

        CrossoverPolicy<String> crossoverPolicy = new AbstractListChromosomeCrossoverPolicy<Integer, String>() {

            @Override
            protected ChromosomePair<String> mate(AbstractListChromosome<Integer, String> first,
                    AbstractListChromosome<Integer, String> second) {
                return new ChromosomePair<>(first, second);
            }
        };
        Chromosome<String> ch1 = new AbstractChromosome<String>(c -> {
            return 0;
        }, c -> {
            return "0";
        }) {
        };

        Chromosome<String> ch2 = new AbstractChromosome<String>(c -> {
            return 1;
        }, c -> {
            return "1";
        }) {
        };

        crossoverPolicy.crossover(ch1, ch2, 1.0);
    }

    @Test(expected = GeneticException.class)
    public void testCrossoverWithUnEqualLengthChromosome() {

        CrossoverPolicy<String> crossoverPolicy = new AbstractListChromosomeCrossoverPolicy<Integer, String>() {

            @Override
            protected ChromosomePair<String> mate(AbstractListChromosome<Integer, String> first,
                    AbstractListChromosome<Integer, String> second) {
                return new ChromosomePair<>(first, second);
            }
        };
        Chromosome<String> ch1 = new DummyListChromosome(ChromosomeRepresentationUtils.randomBinaryRepresentation(10));

        Chromosome<String> ch2 = new DummyListChromosome(ChromosomeRepresentationUtils.randomBinaryRepresentation(20));

        crossoverPolicy.crossover(ch1, ch2, 1.0);
    }

}
