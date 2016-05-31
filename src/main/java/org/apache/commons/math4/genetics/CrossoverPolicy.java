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
package org.apache.commons.math4.genetics;

import org.apache.commons.math4.exception.MathIllegalArgumentException;

/**
 * Policy used to create a pair of new chromosomes by performing a crossover
 * operation on a source pair of chromosomes.
 *
 * @since 2.0
 */
public interface CrossoverPolicy {

    /**
     * Perform a crossover operation on the given chromosomes.
     *
     * @param first the first chromosome.
     * @param second the second chromosome.
     * @return the pair of new chromosomes that resulted from the crossover.
     * @throws MathIllegalArgumentException if the given chromosomes are not compatible with this {@link CrossoverPolicy}
     */
    ChromosomePair crossover(Chromosome first, Chromosome second) throws MathIllegalArgumentException;
}
