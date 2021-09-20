package org.apache.commons.math4.genetics;

import org.apache.commons.math4.genetics.decoder.AbstractListChromosomeDecoder;

public class DummyListChromosomeDecoder<T> extends AbstractListChromosomeDecoder<T, String> {

    private String value;

    public DummyListChromosomeDecoder(String value) {
        this.value = value;
    }

    @Override
    protected String decode(AbstractListChromosome<T, String> chromosome) {
        return value;
    }

}
