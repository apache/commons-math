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
package org.apache.commons.math4.ga.crossover;

import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.utils.RandomProviderManager;

/**
 * OnePoint Crossover Policy for Binary chromosomes.
 * @param <P> the phenotype
 */
public class OnePointBinaryCrossover<P> extends AbstractChromosomeCrossoverPolicy<P> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected ChromosomePair<P> crossover(Chromosome<P> first, Chromosome<P> second) {

        if (!(first instanceof BinaryChromosome<?> && second instanceof BinaryChromosome<?>)) {
            throw new GeneticException(GeneticException.INVALID_FIXED_LENGTH_CHROMOSOME);
        }
        final BinaryChromosome<P> firstChromosome = (BinaryChromosome<P>) first;
        final BinaryChromosome<P> secondChromosome = (BinaryChromosome<P>) second;

        final long alleleCount = firstChromosome.getLength();

        // array representations of the parents
        final long[] parent1Rep = firstChromosome.getRepresentation();
        final long[] parent2Rep = secondChromosome.getRepresentation();

        // and of the children
        final long[] child1Rep = new long[parent1Rep.length];
        final long[] child2Rep = new long[parent2Rep.length];

        // select a crossover point at random (0 and length makes no sense)
        final long crossoverIndex = 1 + (RandomProviderManager.getRandomProvider().nextLong(alleleCount - 1));

        final int offset = (int) (alleleCount % Long.SIZE == 0 ? 0 : Long.SIZE - alleleCount % Long.SIZE);
        final long offsettedCrossoverIndex = crossoverIndex + offset;

        final int crossoverBlockIndex = (int) (offsettedCrossoverIndex / Long.SIZE);
        final int crossoverBlockAlleleIndex = (int) offsettedCrossoverIndex % Long.SIZE;

        if (crossoverBlockAlleleIndex == 0) {
            // if the offsetted-crossover index is divisible by
            // Long.SIZE then first copy all
            // elements of previous array elements.
            for (int i = 0; i < crossoverBlockIndex; i++) {
                child1Rep[i] = parent1Rep[i];
                child2Rep[i] = parent2Rep[i];
            }
            // copy all elements from crossover block index.
            for (int i = crossoverBlockIndex; i < parent1Rep.length; i++) {
                child1Rep[i] = parent2Rep[i];
                child2Rep[i] = parent1Rep[i];
            }
        } else {
            // copy all parent array elements to child till crossover block index - 1.
            for (int i = 0; i < crossoverBlockIndex; i++) {
                child1Rep[i] = parent1Rep[i];
                child2Rep[i] = parent2Rep[i];
            }
            // do exchange of alleles of the array element indexed at crossover block index.
            final long parent1CrossoverBlockRep = parent1Rep[crossoverBlockIndex];
            final long parent2CrossoverBlockRep = parent2Rep[crossoverBlockIndex];
            final long leftMask = Long.MIN_VALUE >> crossoverBlockAlleleIndex - 1;
            final long rightMask = crossoverBlockAlleleIndex != 1 ?
                    (long) Math.pow(2, Long.SIZE - crossoverBlockAlleleIndex) - 1 :
                    Long.MAX_VALUE;

            final long child1CrossoverBlockRep = (parent1CrossoverBlockRep & leftMask) |
                    (parent2CrossoverBlockRep & rightMask);
            final long child2CrossoverBlockRep = (parent2CrossoverBlockRep & leftMask) |
                    (parent1CrossoverBlockRep & rightMask);

            child1Rep[crossoverBlockIndex] = child1CrossoverBlockRep;
            child2Rep[crossoverBlockIndex] = child2CrossoverBlockRep;

            // Copy all the alleles which belong to array elements having index >
            // crossover block index.
            if (crossoverBlockIndex < parent1Rep.length - 1) {
                for (int i = crossoverBlockIndex + 1; i < parent1Rep.length; i++) {
                    child1Rep[i] = parent2Rep[i];
                    child2Rep[i] = parent1Rep[i];
                }
            }
        }

        final BinaryChromosome<P> childChromosome1 = new BinaryChromosome<>(child1Rep, alleleCount,
                firstChromosome.getFitnessFunction(), firstChromosome.getDecoder());
        final BinaryChromosome<P> childChromosome2 = new BinaryChromosome<>(child2Rep, alleleCount,
                secondChromosome.getFitnessFunction(), secondChromosome.getDecoder());

        return new ChromosomePair<>(childChromosome1, childChromosome2);
    }

}
