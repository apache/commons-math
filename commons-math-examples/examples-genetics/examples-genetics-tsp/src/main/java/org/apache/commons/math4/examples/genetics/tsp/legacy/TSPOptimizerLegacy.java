package org.apache.commons.math4.examples.genetics.tsp.legacy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.RandomKey;
import org.apache.commons.math3.genetics.RandomKeyMutation;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math4.examples.genetics.tsp.utils.Constants;
import org.apache.commons.math4.examples.genetics.tsp.utils.DistanceMatrix;
import org.apache.commons.math4.examples.genetics.tsp.utils.GraphPlotter;
import org.apache.commons.math4.examples.genetics.tsp.utils.Node;
import org.apache.commons.math4.genetics.listeners.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.listeners.PopulationStatisticsLogger;

public class TSPOptimizerLegacy {

	public static void main(String[] args) {
		try {
			String filePath = "western_sahara.txt";
			List<Node> nodes = getTravelNodes(filePath);

			Population initPopulation = getInitialPopulation(nodes);

			TSPOptimizerLegacy optimizer = new TSPOptimizerLegacy();

			ConvergenceListenerRegistry convergenceListenerRegistry = ConvergenceListenerRegistry.getInstance();
			convergenceListenerRegistry.addConvergenceListener(new PopulationStatisticsLogger());
			convergenceListenerRegistry
					.addConvergenceListener(new GraphPlotter("Convergence", "generation", "total-distance"));

			optimizer.optimize(initPopulation, nodes);

			Thread.sleep(5000);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void optimize(Population initial, List<Node> nodes) throws IOException {

		// initialize a new genetic algorithm
		GeneticAlgorithm ga = new LegacyGeneticAlgorithm(new OnePointCrossover<Integer>(), Constants.CROSSOVER_RATE,
				new RandomKeyMutation(), Constants.AVERAGE_MUTATION_RATE,
				new TournamentSelection(Constants.TOURNAMENT_SIZE));

		// stopping condition
		StoppingCondition stopCond = new UnchangedBestFitness(Constants.GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS);

		// run the algorithm
		Population finalPopulation = ga.evolve(initial, stopCond);

		// best chromosome from the final population
		RandomKey<Node> bestFinal = (RandomKey<Node>) finalPopulation.getFittestChromosome();

		double fitness = bestFinal.getFitness();

		System.out.println("*********************************************");
		System.out.println("***********Optimization Result***************");
		System.out.println("*********************************************");

		System.out.println(bestFinal.decode(nodes).toString());
		System.out.printf("Best Fitness: %.6f", fitness);

	}

	private static Population getInitialPopulation(List<Node> nodes) {
		Population simulationPopulation = new ElitisticListPopulation(Constants.POPULATION_SIZE, .25);
		DistanceMatrix.getInstance().initialize(nodes);

		for (int i = 0; i < Constants.POPULATION_SIZE; i++) {
			TSPChromosome chromosome = new TSPChromosome(RandomKey.randomPermutation(nodes.size()), nodes);
			simulationPopulation.addChromosome(chromosome);
		}

		return simulationPopulation;
	}

	private static List<Node> getTravelNodes(String filePath) throws IOException {
		List<Node> nodes = new ArrayList<Node>();
		CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(' ');
		try (CSVParser parser = new CSVParser(
				new InputStreamReader(TSPOptimizerLegacy.class.getClassLoader().getResourceAsStream(filePath)),
				csvFormat);) {
			CSVRecord record = null;
			Iterator<CSVRecord> itr = parser.iterator();
			while (itr.hasNext()) {
				record = itr.next();
				Node node = new Node(Integer.parseInt(record.get(0)), Double.parseDouble(record.get(1)),
						Double.parseDouble(record.get(2)));
				nodes.add(node);
			}
		}
		return nodes;
	}

}