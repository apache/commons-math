/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.ga.mutation;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.RealValuedChromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticIllegalArgumentException;
import org.apache.commons.math4.ga.utils.RandomProviderManager;

/**
 * Mutation operator for {@link RealValuedChromosome}. Mutates the randomly
 * selected genes with a new random real value chosen within specified min an
 * max.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class RealValuedMutation<P> extends AbstractListChromosomeMutationPolicy<Double, P> {

    /** minimum value of chromosome gene/allele. **/
    private final double min;
    /** maximum exclusive value of chromosome gene/allele. **/
    private final double max;

    /**
     * Constructs the mutation operator with normalized range of double values.
     */
    public RealValuedMutation() {
        this.min = 0d;
        this.max = 1d;
    }

    /**
     * Constructs the mutation operator with provided range of double values.
     * @param min minimum inclusive value of allele
     * @param max maximum exclusive value of allele
     */
    public RealValuedMutation(double min, double max) {
        this.min = min;
        this.max = max;
        if (min >= max) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.TOO_LARGE, min, max);
        }
    }

    /**
     * Returns the minimum acceptable value.
     * @return minimum
     */
    public double getMin() {
        return min;
    }

    /**
     * Returns the maximum acceptable value.
     * @return maximum
     */
    public double getMax() {
        return max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RealValuedChromosome<P> mutate(Chromosome<P> original, double mutationRate) {
        // check for validity.
        checkValidity(original);
        return (RealValuedChromosome<P>) super.mutate(original, mutationRate);
    }

    /**
     * This method validates the input chromosome.
     * @param original chromosome
     */
    private void checkValidity(Chromosome<P> original) {
        if (!RealValuedChromosome.class.isAssignableFrom(original.getClass())) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.ILLEGAL_ARGUMENT,
                    original.getClass().getSimpleName());
        }
        final RealValuedChromosome<P> chromosome = (RealValuedChromosome<P>) original;
        if (chromosome.getMin() != this.min || chromosome.getMax() != this.max) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.ILLEGAL_RANGE, this.min, this.max,
                    chromosome.getMin(), chromosome.getMax());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Double mutateGene(Double originalValue) {
        Double mutatedValue = 0.0;
        do {
            mutatedValue = min + RandomProviderManager.getRandomProvider().nextDouble() * (max - min);
        } while (mutatedValue.equals(originalValue));

        return mutatedValue;
    }

}
