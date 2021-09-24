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
package org.apache.commons.math4.genetics.decoder;

import org.apache.commons.math4.genetics.chromosome.AbstractListChromosome;
import org.apache.commons.math4.genetics.chromosome.Chromosome;
import org.apache.commons.math4.genetics.exception.GeneticException;

/**
 * An abstract Decoder of ListChromosome.
 * @param <T> genotype fo chromosome
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public abstract class AbstractListChromosomeDecoder<T, P> implements Decoder<P> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public P decode(Chromosome<P> chromosome) {
        checkValidity(chromosome);

        return decode((AbstractListChromosome<T, P>) chromosome);
    }

    /**
     * Checks validity of {@link Chromosome}.
     * @param chromosome the {@link Chromosome}
     */
    protected void checkValidity(Chromosome<P> chromosome) {
        if (!AbstractListChromosome.class.isAssignableFrom(chromosome.getClass())) {
            throw new GeneticException(GeneticException.ILLEGAL_ARGUMENT, chromosome.getClass().getSimpleName());
        }
    }

    /**
     * Decodes the chromosome genotype and returns the phenotype.
     * @param chromosome The list chromosome to decode
     * @return decoded phenotype of chromosome
     */
    protected abstract P decode(AbstractListChromosome<T, P> chromosome);

}
