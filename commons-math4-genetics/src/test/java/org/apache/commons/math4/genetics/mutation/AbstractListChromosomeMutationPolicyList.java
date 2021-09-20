package org.apache.commons.math4.genetics.mutation;

import org.apache.commons.math4.genetics.utils.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;

public class AbstractListChromosomeMutationPolicyList {

    @Test
    public void testGetMutableGeneIndexes() {
        AbstractListChromosomeMutationPolicy<Integer, String> chromosomeMutationPolicy = new AbstractListChromosomeMutationPolicy<Integer, String>() {

            @Override
            protected Integer mutateGene(Integer originalValue) {
                return RandomGenerator.getRandomGenerator().nextInt(2);
            }
        };
        Assert.assertEquals(1, chromosomeMutationPolicy.getMutableGeneIndexes(10, .1).size());
    }

}
