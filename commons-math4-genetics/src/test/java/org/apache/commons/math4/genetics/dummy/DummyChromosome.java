package org.apache.commons.math4.genetics.dummy;

import org.apache.commons.math4.genetics.AbstractChromosome;

public class DummyChromosome extends AbstractChromosome<String> {

    public DummyChromosome() {
        super(c -> {
            return 0;
        }, c -> {
            return "0";
        });
    }

}
