package org.apache.commons.math4.genetics.decoder;

import java.util.List;
import java.util.Objects;

import org.apache.commons.math4.genetics.BinaryChromosome;
import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.utils.ChromosomeRepresentationUtils;
import org.junit.Assert;
import org.junit.Test;

public class TransparentListChromosomeDecoderTest {

    @Test
    public void testDecode() {
        List<Integer> rp = ChromosomeRepresentationUtils.randomBinaryRepresentation(10);
        Chromosome<List<Integer>> chromosome = new BinaryChromosome<>(rp, c -> {
            return 0;
        }, new TransparentListChromosomeDecoder<>());
        List<Integer> decodedRp = chromosome.decode();
        Assert.assertTrue(Objects.equals(rp, decodedRp));
    }

}
