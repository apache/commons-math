package org.apache.commons.math4.genetics;

import org.apache.commons.math4.genetics.utils.ChromosomeRepresentationUtils;
import org.junit.Test;

public class RealValuedChromosomeTest {

    @Test
    public void testNewChromosome() {
        for (int i = 0; i < 10; i++) {
            new RealValuedChromosome<>(ChromosomeRepresentationUtils.randomDoubleRepresentation(10, 0, 2), c1 -> {
                return 1;
            }, new DummyListChromosomeDecoder<>("1"));
        }
    }

    @Test
    public void testRandomChromosome() {
        for (int i = 0; i < 10; i++) {
            RealValuedChromosome.randomChromosome(5, c -> {
                return 0;
            }, new DummyListChromosomeDecoder<>("0"), 0, 2);
        }
    }

}
