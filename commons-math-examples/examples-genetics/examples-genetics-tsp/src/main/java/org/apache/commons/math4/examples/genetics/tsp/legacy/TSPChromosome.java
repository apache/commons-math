package org.apache.commons.math4.examples.genetics.tsp.legacy;

import java.util.List;

import org.apache.commons.math3.genetics.InvalidRepresentationException;
import org.apache.commons.math3.genetics.RandomKey;
import org.apache.commons.math4.examples.genetics.tsp.utils.DistanceMatrix;
import org.apache.commons.math4.examples.genetics.tsp.utils.Node;

public class TSPChromosome extends RandomKey<Node> {

	private List<Node> nodes;

	public TSPChromosome(List<Double> representation, List<Node> nodes) throws InvalidRepresentationException {
		super(representation);
		this.nodes = nodes;
	}

	@Override
	public double fitness() {
		List<Node> permutatedNodes = decode(nodes);
		return -calculateTotalDistance(permutatedNodes);
	}

	@Override
	public TSPChromosome newFixedLengthChromosome(List<Double> representation) {
		return new TSPChromosome(representation, nodes);
	}

	@Override
	public List<Double> getRepresentation() {
		return super.getRepresentation();
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

	public List<Node> getNodes() {
		return nodes;
	}
}