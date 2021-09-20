package org.apache.commons.math4.genetics.mutation;

import org.apache.commons.math4.genetics.utils.RandomGenerator;

/**
 * This class mutates real-valued chromosome.
 *
 * @param <P> phenotype of chromosome
 */
public class RealValueMutation<P> extends AbstractListChromosomeMutationPolicy<Double, P> {

    private final double min;

    private final double scale;

    public RealValueMutation() {
        this.min = 0d;
        this.scale = 1d;
    }

    public RealValueMutation(double min, double scale) {
        this.min = min;
        this.scale = scale;
    }

    @Override
    protected Double mutateGene(Double originalValue) {
        return min + RandomGenerator.getRandomGenerator().nextDouble() * scale;
    }

}
