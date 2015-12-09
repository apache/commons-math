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
package org.apache.commons.math3.genetics;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of ListChromosome for testing purposes
 */
public class DummyListChromosome extends AbstractListChromosome<Integer> {
    public DummyListChromosome(final Integer[] representation) {
        super(representation);
    }

    public DummyListChromosome(final List<Integer> representation) {
        super(representation);
    }

    public double fitness() {
        // Not important.
        return 0;
    }

    @Override
    protected void checkValidity(final List<Integer> chromosomeRepresentation) throws InvalidRepresentationException {
        // Not important.
    }

    @Override
    public AbstractListChromosome<Integer> newFixedLengthChromosome(final List<Integer> chromosomeRepresentation) {
        return new DummyListChromosome(chromosomeRepresentation);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getRepresentation() == null ? 0 : getRepresentation().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DummyListChromosome)) {
            return false;
        }
        final DummyListChromosome other = (DummyListChromosome) obj;
        if (getRepresentation() == null) {
            if (other.getRepresentation() != null) {
                return false;
            }
        }
        final Integer[] rep = getRepresentation().toArray(new Integer[getRepresentation().size()]);
        final Integer[] otherRep = other.getRepresentation().toArray(new Integer[other.getRepresentation().size()]);
        return Arrays.equals(rep, otherRep);
    }
}
