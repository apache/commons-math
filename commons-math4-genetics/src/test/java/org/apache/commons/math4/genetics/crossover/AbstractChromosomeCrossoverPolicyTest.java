package org.apache.commons.math4.genetics.crossover;

import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.ChromosomePair;
import org.apache.commons.math4.genetics.dummy.DummyChromosome;
import org.junit.Assert;
import org.junit.Test;

public class AbstractChromosomeCrossoverPolicyTest {

    @Test
    public void testCrossoverProbability() {

        CrossoverPolicy<String> crossoverPolicy = new AbstractChromosomeCrossoverPolicy<String>() {
            @Override
            protected ChromosomePair<String> crossover(Chromosome<String> first, Chromosome<String> second) {
                return null;
            }
        };

        Chromosome<String> ch1 = new DummyChromosome();

        Chromosome<String> ch2 = new DummyChromosome();

        Assert.assertNull(crossoverPolicy.crossover(ch1, ch2, 1.0));
        Assert.assertNotNull(crossoverPolicy.crossover(ch1, ch2, 0.0));
    }

}
