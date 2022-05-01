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

package org.apache.commons.math4.ga2.gene.binary;

import java.util.BitSet;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.ga2.AbstractMutation;

/**
 * Genetic operator.
 * Class is immutable.
 */
/* package-private */ class Mutation extends AbstractMutation<Chromosome> {
    /**
     * @param probability Probability that a gene will mutate.
     */
    Mutation(double probability) {
        super(probability);
    }

    /** {@inheritDoc} */
    @Override
    protected Chromosome apply(Chromosome parent,
                               UniformRandomProvider rng) {
        final int size = parent.size();
        final BitSet c = parent.asBitSet();
        for (int i = 0; i < size; i++) {
            if (rng.nextDouble() < getProbability()) {
                c.flip(i);
            }
        }

        return Chromosome.from(c, size);
    }
}
