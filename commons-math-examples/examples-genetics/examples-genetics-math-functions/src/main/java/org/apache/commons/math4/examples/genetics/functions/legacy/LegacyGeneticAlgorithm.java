package org.apache.commons.math4.examples.genetics.functions.legacy;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.CrossoverPolicy;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.MutationPolicy;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.SelectionPolicy;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math4.examples.genetics.functions.Dimension2FitnessFunction;
import org.apache.commons.math4.genetics.listeners.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.model.ListPopulation;

public class LegacyGeneticAlgorithm extends GeneticAlgorithm {

	private int generationsEvolved;

	public LegacyGeneticAlgorithm(CrossoverPolicy crossoverPolicy, double crossoverRate, MutationPolicy mutationPolicy,
			double mutationRate, SelectionPolicy selectionPolicy) throws OutOfRangeException {
		super(crossoverPolicy, crossoverRate, mutationPolicy, mutationRate, selectionPolicy);
	}

	@Override
	public Population evolve(Population initial, StoppingCondition condition) {
		Population current = initial;
		generationsEvolved = 0;
		while (!condition.isSatisfied(current)) {
			ConvergenceListenerRegistry.getInstance().notifyAll(transform(current));
			current = nextGeneration(current);
			generationsEvolved++;
		}
		return current;
	}

	private org.apache.commons.math4.genetics.model.Population transform(Population population) {
		org.apache.commons.math4.genetics.model.Population newPopulation = new ListPopulation(
				population.getPopulationLimit());
		for (Chromosome chromosome : population) {
			org.apache.commons.math4.genetics.model.BinaryChromosome binaryChromosome = new org.apache.commons.math4.genetics.model.BinaryChromosome(
					((LegacyBinaryChromosome) chromosome).getRepresentation(), new Dimension2FitnessFunction());
			newPopulation.addChromosome(binaryChromosome);
		}
		return newPopulation;
	}

}