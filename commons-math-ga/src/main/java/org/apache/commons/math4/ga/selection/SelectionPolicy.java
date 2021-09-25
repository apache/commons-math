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
package org.apache.commons.math4.ga.selection;

import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.population.Population;

/**
 * Algorithm used to select a chromosome pair from a population.
 * @param <P> phenotype of chromosome
 * @since 2.0
 */
public interface SelectionPolicy<P> {

    /**
     * Select two chromosomes from the population.
     * @param population the population from which the chromosomes are chosen.
     * @return the selected chromosomes.
     */
    ChromosomePair<P> select(Population<P> population);
}
