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
package org.apache.commons.math4.genetics.mutation;

import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.RealValuedChromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.utils.RandomGenerator;

/**
 * This class mutates real-valued chromosome.
 * @param <P> phenotype of chromosome
 */
public class RealValueMutation<P> extends AbstractListChromosomeMutationPolicy<Double, P> {

    /** minimum value of chromosome gene/allele. **/
    private final double min;

    /** maximum value of chromosome gene/allele. **/
    private final double max;

    /**
     * Constructs the mutation operator with normalized range of double values.
     */
    public RealValueMutation() {
        this.min = 0d;
        this.max = 1d;
    }

    /**
     * Constructs the mutation operator with provided range of double values.
     * @param min
     * @param max
     */
    public RealValueMutation(double min, double max) {
        this.min = min;
        this.max = max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkValidity(Chromosome<P> original) {
        if (!RealValuedChromosome.class.isAssignableFrom(original.getClass())) {
            throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, original.getClass().getSimpleName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Double mutateGene(Double originalValue) {
        return min + RandomGenerator.getRandomGenerator().nextDouble() * (max - min);
    }

}
