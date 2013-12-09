package org.apache.commons.math3.userguide.genetics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

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

        List<Polygon> newRepr = new ArrayList<Polygon>(repr.size());
        for (Polygon p : repr) {
            newRepr.add(p.mutate(mutationRate, mutationAmount));
        }
        return new PolygonChromosome(newRepr);
    }
}
