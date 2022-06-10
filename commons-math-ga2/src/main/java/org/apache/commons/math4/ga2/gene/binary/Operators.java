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

import org.apache.commons.math4.ga2.GeneticOperator;

/**
 * Genetic operators factory.
 * It creates instances that operate on "binary" genotypes.
 */
public final class Operators {
    /** Prevent instantiation of utility class. */
    private Operators() {}

    /**
     * @param probability Probability that a gene flip will occur.
     * @return a mutation operator.
     */
    public static GeneticOperator<Chromosome> mutation(double probability) {
        return new Mutation(probability);
    }
    /**
     * @param n Number of crossover points.
     * @return an n-point crossover operator.
     */
    public static GeneticOperator<Chromosome> nPointCrossover(int n) {
        return new NPointCrossover(n);
    }
}
