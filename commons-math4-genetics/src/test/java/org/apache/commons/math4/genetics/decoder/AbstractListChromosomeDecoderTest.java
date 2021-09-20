package org.apache.commons.math4.genetics.decoder;

import org.apache.commons.math4.genetics.AbstractListChromosome;
import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.dummy.DummyChromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.junit.Test;

public class AbstractListChromosomeDecoderTest {

    @Test(expected = GeneticException.class)
    public void testDecodeWithInvalidChromosomeInstance() {
        Decoder<String> decoder = new AbstractListChromosomeDecoder<Integer, String>() {

            @Override
            protected String decode(AbstractListChromosome<Integer, String> chromosome) {
                return null;
            }
        };
        Chromosome<String> ch = new DummyChromosome();
        decoder.decode(ch);
    }

}
