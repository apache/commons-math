package org.apache.commons.math4.examples.genetics.functions;

import org.apache.commons.math4.examples.genetics.functions.utils.Constants;
import org.apache.commons.math4.examples.genetics.functions.utils.GraphPlotter;
import org.apache.commons.math4.genetics.GeneticAlgorithm;
import org.apache.commons.math4.genetics.listeners.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.listeners.PopulationStatisticsLogger;
import org.apache.commons.math4.genetics.model.BinaryChromosome;
import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.ListPopulation;
import org.apache.commons.math4.genetics.model.Population;
import org.apache.commons.math4.genetics.operators.BinaryMutation;
import org.apache.commons.math4.genetics.operators.OnePointCrossover;
import org.apache.commons.math4.genetics.operators.StoppingCondition;
import org.apache.commons.math4.genetics.operators.TournamentSelection;
import org.apache.commons.math4.genetics.operators.UnchangedBestFitness;

public class Dimension2FunctionOptimizer {

	public static void main(String[] args) {
		Population initPopulation = getInitialPopulation();

		Dimension2FunctionOptimizer simulation = new Dimension2FunctionOptimizer();

		ConvergenceListenerRegistry convergenceListenerRegistry = ConvergenceListenerRegistry.getInstance();
		convergenceListenerRegistry.addConvergenceListener(new PopulationStatisticsLogger());
		convergenceListenerRegistry.addConvergenceListener(new GraphPlotter("Convergence Stats", "generation", "value"));

		simulation.optimize(initPopulation);
	}

	public void optimize(Population initial) {

		// initialize a new genetic algorithm
		GeneticAlgorithm ga = new GeneticAlgorithm(new OnePointCrossover<Integer>(), Constants.CROSSOVER_RATE,
				new BinaryMutation(), Constants.AVERAGE_MUTATION_RATE,
				new TournamentSelection(Constants.TOURNAMENT_SIZE), Constants.ELITISM_RATE);

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
		Population population = new ListPopulation(Constants.POPULATION_SIZE);
		for (int i = 0; i < Constants.POPULATION_SIZE; i++) {
			population.addChromosome(
					BinaryChromosome.randomChromosome(Constants.CHROMOZOME_LENGTH, new Dimension2FitnessFunction()));
		}
		return population;
	}

}
