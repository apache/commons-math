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

import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.internal.exception.GeneticException;

/**
 * Mutation for {@link BinaryChromosome}s. Randomly changes few genes.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class BinaryMutation<P> extends IntegralValuedMutation<P> {

    public BinaryMutation() {
        super(0, 2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkValidity(Chromosome<P> original) {
        super.checkValidity(original);
        if (!BinaryChromosome.class.isAssignableFrom(original.getClass())) {
            throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, original.getClass().getSimpleName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer mutateGene(Integer originalValue) {
        return originalValue == 0 ? 1 : 0;
    }

}
