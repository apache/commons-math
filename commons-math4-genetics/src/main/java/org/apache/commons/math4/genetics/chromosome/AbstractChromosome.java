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
package org.apache.commons.math4.genetics.chromosome;

import java.util.Objects;

import org.apache.commons.math4.genetics.decoder.Decoder;
import org.apache.commons.math4.genetics.fitness.FitnessFunction;
import org.apache.commons.math4.genetics.utils.ValidationUtils;

/**
 * Individual in a population. Chromosomes are compared based on their fitness.
 * <p>
 * The chromosomes are IMMUTABLE, and so their fitness is also immutable and
 * therefore it can be cached.
 *
 * @param <P> The phenotype of chromosome. The type should override hashCode()
 *            and equals() methods.
 * @since 4.0
 */
public abstract class AbstractChromosome<P> implements Chromosome<P> {

    /** Value assigned when no fitness has been computed yet. */
    private static final double NO_FITNESS = Double.NEGATIVE_INFINITY;

    /** Cached value of the fitness of this chromosome. */
    private double fitness = NO_FITNESS;

    /** Fitness function to evaluate fitness of chromosome. **/
    private final FitnessFunction<P> fitnessFunction;

    /** decoder to deode the chromosome's genotype representation. **/
    private final Decoder<P> decoder;

    /**
     * constructor.
     * @param fitnessFunction The {@link FitnessFunction}
     * @param decoder         The {@link Decoder}
     */
    public AbstractChromosome(final FitnessFunction<P> fitnessFunction, final Decoder<P> decoder) {
        ValidationUtils.checkForNull("fitness-function", fitnessFunction);
        ValidationUtils.checkForNull("decoder", decoder);
        this.fitnessFunction = fitnessFunction;
        this.decoder = decoder;
    }

    /**
     * returns fitness function.
     * @return fitnessFunction
     */
    protected FitnessFunction<P> getFitnessFunction() {
        return fitnessFunction;
    }

    /**
     * Returns the decoder instance.
     * @return decoder
     */
    protected Decoder<P> getDecoder() {
        return decoder;
    }

    /**
     * Access the fitness of this chromosome. The bigger the fitness, the better the
     * chromosome.
     * <p>
     * Computation of fitness is usually very time-consuming task, therefore the
     * fitness is cached.
     * @return the fitness
     */
    @Override
    public double evaluate() {
        if (this.fitness == NO_FITNESS) {
            // no cache - compute the fitness
            this.fitness = fitnessFunction.compute(decode());
        }
        return this.fitness;
    }

    /**
     * Decodes the chromosome genotype and returns the phenotype.
     * @return phenotype
     */
    @Override
    public P decode() {
        return this.decoder.decode(this);
    }

    /**
     * Compares two chromosomes based on their fitness. The bigger the fitness, the
     * better the chromosome.
     * @param another another chromosome to compare
     * @return
     *         <ul>
     *         <li>-1 if <code>another</code> is better than <code>this</code></li>
     *         <li>1 if <code>another</code> is worse than <code>this</code></li>
     *         <li>0 if the two chromosomes have the same fitness</li>
     *         </ul>
     */
    @Override
    public int compareTo(final Chromosome<P> another) {
        return Double.compare(evaluate(), another.evaluate());
    }

    /**
     * Returns <code>true</code> iff <code>another</code> has the same
     * representation and therefore the same fitness. By default, it returns false
     * -- override it in your implementation if you need it.
     * @param another chromosome to compare
     * @return true if <code>another</code> is equivalent to this chromosome
     */
    protected boolean isSame(final AbstractChromosome<P> another) {
        final P decodedChromosome = decode();
        final P otherDecodedChromosome = another.decode();
        return decodedChromosome.equals(otherDecodedChromosome);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("(f=%s %s)", evaluate(), decode());
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(decode());
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractChromosome<P> other = (AbstractChromosome<P>) obj;

        return isSame(other);
    }

}
