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
import org.apache.commons.math4.ga.chromosome.IntegralValuedChromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticIllegalArgumentException;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.simple.ThreadLocalRandomSource;

/**
 * Mutation operator for {@link IntegralValuedChromosome}. Randomly changes few
 * gene's value with random values chosen between specified min and max.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class IntegralValuedMutation<P> extends AbstractListChromosomeMutationPolicy<Integer, P> {

    /** minimum acceptable value of allele. **/
    private final int min;
    /** maximum acceptable value of allele. **/
    private final int max;

    /**
     * @param min minimum value of allele
     * @param max maximum(exclusive) value of allele
     */
    public IntegralValuedMutation(final int min, final int max) {
        super(RandomSource.XO_RO_SHI_RO_128_PP);
        this.min = min;
        this.max = max;

        // To perform mutation for an IntegralValuedChromosome the minimum difference
        // between
        // max and min should be 2.
        if ((max - min) < 2) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.TOO_LARGE, min, max);
        }

    }

    /**
     * Constructs an IntegralValuedMutation policy with given minimum, maximum
     * values and random source.
     * @param min          minimum value of allele
     * @param max          maximum(exclusive) value of allele
     * @param randomSource random source to instantiate UniformRandomProvider.
     */
    public IntegralValuedMutation(final int min, final int max, final RandomSource randomSource) {
        super(randomSource);
        this.min = min;
        this.max = max;

        // To perform mutation for an IntegralValuedChromosome the minimum difference
        // between
        // max and min should be 2.
        if ((max - min) < 2) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.TOO_LARGE, min, max);
        }

    }

    /**
     * Returns the minimum acceptable value.
     * @return minimum
     */
    public int getMin() {
        return min;
    }

    /**
     * Returns the maximum acceptable value.
     * @return maximum
     */
    public int getMax() {
        return max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntegralValuedChromosome<P> mutate(Chromosome<P> original, double mutationRate) {
        // check for validity
        checkValidity(original);

        return (IntegralValuedChromosome<P>) super.mutate(original, mutationRate);
    }

    /**
     * This method validates input chromosome.
     * @param original chromosome
     */
    private void checkValidity(Chromosome<P> original) {
        if (!(original instanceof IntegralValuedChromosome)) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.ILLEGAL_ARGUMENT,
                    original.getClass().getSimpleName());
        }
        final IntegralValuedChromosome<P> chromosome = (IntegralValuedChromosome<P>) original;
        if (chromosome.getMin() != this.min || chromosome.getMax() != this.max) {
            throw new GeneticIllegalArgumentException(GeneticIllegalArgumentException.ILLEGAL_RANGE, this.min, this.max,
                    chromosome.getMin(), chromosome.getMax());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer mutateGene(Integer originalValue) {
        Integer mutatedValue = 0;
        do {
            mutatedValue = min + ThreadLocalRandomSource.current(getRandomSource()).nextInt(max - min);
        } while (mutatedValue.equals(originalValue));

        return mutatedValue;
    }

}
