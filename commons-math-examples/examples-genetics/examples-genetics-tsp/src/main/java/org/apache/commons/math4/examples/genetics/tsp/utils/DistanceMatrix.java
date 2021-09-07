package org.apache.commons.math4.examples.genetics.tsp.utils;

import java.util.List;

public class DistanceMatrix {

	private double[][] distances;

	private static DistanceMatrix instance = new DistanceMatrix();

	private DistanceMatrix() {
	}

	public double getDistance(Node node1, Node node2) {
		return distances[node1.getIndex() - 1][node2.getIndex() - 1];
	}

	public void initialize(List<Node> nodes) {
		int len = nodes.size();
		this.distances = new double[len][len];
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				distances[i][j] = Math.pow((Math.pow(nodes.get(i).getX() - nodes.get(j).getX(), 2)
						+ Math.pow(nodes.get(i).getY() - nodes.get(j).getY(), 2)), .5);
			}
		}
	}

	public static DistanceMatrix getInstance() {
		return instance;
	}

}