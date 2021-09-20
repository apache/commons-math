package org.apache.commons.math4.examples.genetics.tsp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math4.examples.genetics.tsp.utils.Constants;
import org.apache.commons.math4.examples.genetics.tsp.utils.DistanceMatrix;
import org.apache.commons.math4.examples.genetics.tsp.utils.GraphPlotter;
import org.apache.commons.math4.genetics.GeneticAlgorithm;
import org.apache.commons.math4.genetics.ListPopulation;
import org.apache.commons.math4.genetics.Population;
import org.apache.commons.math4.genetics.RealValuedChromosome;
import org.apache.commons.math4.genetics.convergencecond.StoppingCondition;
import org.apache.commons.math4.genetics.convergencecond.UnchangedBestFitness;
import org.apache.commons.math4.genetics.crossover.OnePointCrossover;
import org.apache.commons.math4.genetics.decoder.RandomKeyDecoder;
import org.apache.commons.math4.genetics.listener.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.listener.PopulationStatisticsLogger;
import org.apache.commons.math4.genetics.mutation.RealValueMutation;
import org.apache.commons.math4.genetics.selection.TournamentSelection;
import org.apache.commons.math4.genetics.utils.ChromosomeRepresentationUtils;
import org.apache.commons.math4.genetics.utils.ConsoleLogger;

public class TSPOptimizer {

    private static final String filePath = "western_sahara.txt";

    public static void main(String[] args) {
        try {
            List<Node> nodes = getTravelNodes(filePath);

            Population<List<Node>> initPopulation = getInitialPopulation(nodes);

            TSPOptimizer optimizer = new TSPOptimizer();

            ConvergenceListenerRegistry<List<Node>> convergenceListenerRegistry = ConvergenceListenerRegistry
                    .getInstance();
            convergenceListenerRegistry.addConvergenceListener(new PopulationStatisticsLogger<List<Node>>());
            convergenceListenerRegistry
                    .addConvergenceListener(new GraphPlotter("Convergence", "generation", "total-distance"));

            optimizer.optimizeSGA(initPopulation, nodes);

            Thread.sleep(5000);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void optimizeSGA(Population<List<Node>> initial, List<Node> nodes) throws IOException {

        // initialize a new genetic algorithm
        GeneticAlgorithm<List<Node>> ga = new GeneticAlgorithm<List<Node>>(new OnePointCrossover<Integer, List<Node>>(),
                Constants.CROSSOVER_RATE, new RealValueMutation<List<Node>>(), Constants.AVERAGE_MUTATION_RATE,
                new TournamentSelection<List<Node>>(Constants.TOURNAMENT_SIZE));

        // stopping condition
        StoppingCondition<List<Node>> stopCond = new UnchangedBestFitness<List<Node>>(
                Constants.GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS);

        // run the algorithm
        Population<List<Node>> finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        RealValuedChromosome<List<Node>> bestFinal = (RealValuedChromosome<List<Node>>) finalPopulation
                .getFittestChromosome();

        double fitness = bestFinal.evaluate();

        ConsoleLogger.log("*********************************************");
        ConsoleLogger.log("*********************************************");
        ConsoleLogger.log("***********Optimization Result***************");
        ConsoleLogger.log("*********************************************");

        ConsoleLogger.log(bestFinal.decode().toString());
        ConsoleLogger.log("Best Fitness: %.6f", fitness);

    }

    private static Population<List<Node>> getInitialPopulation(List<Node> nodes) {
        Population<List<Node>> simulationPopulation = new ListPopulation<List<Node>>(Constants.POPULATION_SIZE);

        DistanceMatrix.getInstance().initialize(nodes);

        for (int i = 0; i < Constants.POPULATION_SIZE; i++) {
            simulationPopulation.addChromosome(new RealValuedChromosome<>(
                    ChromosomeRepresentationUtils.randomPermutation(Constants.CHROMOSOME_LENGTH),
                    new TSPFitnessFunction(), new RandomKeyDecoder<Node>(nodes)));
        }

        return simulationPopulation;
    }

    private static List<Node> getTravelNodes(String filePath) throws IOException {
        List<Node> nodes = new ArrayList<Node>();
        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(' ');
        try (CSVParser parser = new CSVParser(
                new InputStreamReader(TSPOptimizer.class.getClassLoader().getResourceAsStream(filePath)), csvFormat);) {
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