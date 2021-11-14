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
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.utils.RandomProviderManager;

/**
 * This class mutates real-valued chromosome.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class RealValuedMutation<P> extends AbstractListChromosomeMutationPolicy<Double, P> {

    /** minimum value of chromosome gene/allele. **/
    private final double min;

    /** maximum value of chromosome gene/allele. **/
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
            throw new GeneticException(GeneticException.TOO_LARGE, min, max);
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
    protected void checkValidity(Chromosome<P> original) {
        if (!RealValuedChromosome.class.isAssignableFrom(original.getClass())) {
            throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, original.getClass().getSimpleName());
        }
        RealValuedChromosome<P> chromosome = (RealValuedChromosome<P>) original;
        if (chromosome.getMin() != this.min || chromosome.getMax() != this.max) {
            throw new GeneticException(GeneticException.ILLEGAL_RANGE, this.min, this.max, chromosome.getMin(),
                    chromosome.getMax());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Double mutateGene(Double originalValue) {
        return min + RandomProviderManager.getRandomProvider().nextDouble() * (max - min);
    }

}
