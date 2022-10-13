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
package org.apache.commons.math4.userguide.genetics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.MutationPolicy;

public class RandomPolygonMutation implements MutationPolicy {

    private float mutationRate;
    private float mutationAmount;

    public RandomPolygonMutation(float mutationRate, float mutationAmount) {
        this.mutationRate = mutationRate;
        this.mutationAmount = mutationAmount;
    }

    public Chromosome mutate(Chromosome chromosome) {
        if (!(chromosome instanceof PolygonChromosome)) {
            throw new IllegalArgumentException();
        }

        PolygonChromosome polygons = (PolygonChromosome) chromosome;
        List<Polygon> repr = polygons.getPolygonRepresentation();

        List<Polygon> newRepr = new ArrayList<>(repr.size());
        for (Polygon p : repr) {
            newRepr.add(p.mutate(mutationRate, mutationAmount));
        }
        return new PolygonChromosome(newRepr);
    }
}
