package org.apache.commons.math4.examples.genetics.mathfunctions.legacy;

import org.apache.commons.math3.genetics.BinaryChromosome;
import org.apache.commons.math3.genetics.BinaryMutation;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math4.examples.genetics.mathfunctions.utils.Constants;
import org.apache.commons.math4.examples.genetics.mathfunctions.utils.GraphPlotter;
import org.apache.commons.math4.genetics.listeners.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.listeners.PopulationStatisticsLogger;

public class Dimension2FunctionOptimizerLegacy {

	public static void main(String[] args) {
		Population initPopulation = getInitialPopulation();

		Dimension2FunctionOptimizerLegacy simulation = new Dimension2FunctionOptimizerLegacy();

		ConvergenceListenerRegistry convergenceListenerRegistry = ConvergenceListenerRegistry.getInstance();
		convergenceListenerRegistry.addConvergenceListener(new PopulationStatisticsLogger());
		convergenceListenerRegistry
				.addConvergenceListener(new GraphPlotter("Convergence Stats", "generation", "value"));

		simulation.optimize(initPopulation);
	}

	public void optimize(Population initial) {

		// initialize a new genetic algorithm
		LegacyGeneticAlgorithm ga = new LegacyGeneticAlgorithm(new OnePointCrossover<Integer>(),
				Constants.CROSSOVER_RATE, new BinaryMutation(), Constants.AVERAGE_MUTATION_RATE,
				new TournamentSelection(Constants.TOURNAMENT_SIZE));

		// stopping condition
		StoppingCondition stopCond = new UnchangedBestFitness(Constants.GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS);

		// run the algorithm
		Population finalPopulation = ga.evolve(initial, stopCond);

		// best chromosome from the final population
		Chromosome bestFinal = finalPopulation.getFittestChromosome();

		System.out.println("*********************************************");
		System.out.println("***********Optimization Result***************");
		System.out.println("*********************************************");

		System.out.println(bestFinal.toString());

	}

	private static Population getInitialPopulation() {
		Population population = new ElitisticListPopulation(Constants.POPULATION_SIZE, Constants.ELITISM_RATE);
		for (int i = 0; i < Constants.POPULATION_SIZE; i++) {
			population.addChromosome(new LegacyBinaryChromosome(
					BinaryChromosome.randomBinaryRepresentation(Constants.CHROMOZOME_LENGTH)));
		}
		return population;
	}

}
