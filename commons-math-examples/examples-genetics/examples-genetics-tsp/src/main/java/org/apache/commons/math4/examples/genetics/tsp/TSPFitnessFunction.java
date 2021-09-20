package org.apache.commons.math4.examples.genetics.tsp;

import java.util.List;

import org.apache.commons.math4.examples.genetics.tsp.utils.DistanceMatrix;
import org.apache.commons.math4.genetics.FitnessFunction;

public class TSPFitnessFunction implements FitnessFunction<List<Node>> {

    @Override
    public double compute(List<Node> nodes) {
        return -calculateTotalDistance(nodes);
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
