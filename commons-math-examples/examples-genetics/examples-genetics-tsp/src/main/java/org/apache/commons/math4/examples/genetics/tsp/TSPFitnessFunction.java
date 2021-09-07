package org.apache.commons.math4.examples.genetics.tsp;

import java.util.List;

import org.apache.commons.math4.examples.genetics.tsp.utils.DistanceMatrix;
import org.apache.commons.math4.examples.genetics.tsp.utils.Node;
import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.FitnessFunction;
import org.apache.commons.math4.genetics.model.RandomKey;

public class TSPFitnessFunction implements FitnessFunction {

	private List<Node> nodes;

	public TSPFitnessFunction(List<Node> nodes) {
		this.nodes = nodes;
	}

	@Override
	public double compute(Chromosome chromosome) {
		if (!(chromosome instanceof RandomKey<?>)) {
			throw new IllegalArgumentException("Invalid chromosome instance");
		}
		RandomKey<Node> tspChromosome = (RandomKey<Node>) chromosome;
		List<Node> permutatedNodes = tspChromosome.decode(nodes);

		return -calculateTotalDistance(permutatedNodes);
	}

	private double calculateTotalDistance(List<Node> nodes) {
		double totalDistance = 0.0;
		int index1 = 0;
		int index2 = 0;
		for (int i = 0; i < nodes.size(); i++) {
			index1 = i;
			index2 = (i == nodes.size() - 1) ? 0 : i + 1;
			totalDistance += calculateNodeDistance(nodes.get(index1), nodes.get(index2));
		}
		return totalDistance;
	}

	private double calculateNodeDistance(Node node1, Node node2) {
		DistanceMatrix distanceMatrix = DistanceMatrix.getInstance();
		double distance = distanceMatrix.getDistance(node1, node2);

		return distance;
	}

}
