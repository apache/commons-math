package org.apache.commons.math4.genetics.decoder;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.genetics.RealValuedChromosome;
import org.junit.Assert;
import org.junit.Test;

public class RandomKeyDecoderTest {

    @Test
    public void testDecodeChromosomeOfP() {

        List<String> sequence = Arrays.asList(new String[] {"a", "b", "c", "d", "e"});
        Double[] keys = new Double[] {0.4, 0.1, 0.5, 0.8, 0.2};

        RandomKeyDecoder<String> decoder = new RandomKeyDecoder<>(sequence);
        RealValuedChromosome<List<String>> chromosome = new RealValuedChromosome<>(keys, c -> {
            return 0;
        }, decoder);
        List<String> decodedSequence = chromosome.decode();

        Assert.assertEquals("b", decodedSequence.get(0));
        Assert.assertEquals("e", decodedSequence.get(1));
        Assert.assertEquals("a", decodedSequence.get(2));
        Assert.assertEquals("c", decodedSequence.get(3));
        Assert.assertEquals("d", decodedSequence.get(4));

    }

}
