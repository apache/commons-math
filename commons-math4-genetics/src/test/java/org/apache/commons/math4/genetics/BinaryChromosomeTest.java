package org.apache.commons.math4.genetics;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.junit.Assert;
import org.junit.Test;

public class BinaryChromosomeTest {

    @Test(expected = GeneticException.class)
    public void testInvalidConstructor() {
        Integer[][] reprs = new Integer[][] {new Integer[] {0, 1, 0, 1, 2}, new Integer[] {0, 1, 0, 1, -1}};

        for (Integer[] repr : reprs) {
            new BinaryChromosome<>(repr, c -> {
                return 0;
            }, new DummyListChromosomeDecoder<>("0"));
            Assert.fail("Exception not caught");
        }
    }

    @Test
    public void testRandomConstructor() {
        for (int i = 0; i < 20; i++) {
            BinaryChromosome.<String>randomChromosome(10, c -> {
                return 1;
            }, new DummyListChromosomeDecoder<>("1"));
        }
    }

}
