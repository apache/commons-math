package org.apache.commons.math4.examples.genetics.tsp.utils;

public class Node {

	private int index;

	private double x;

	private double y;

	public Node(int index, double x, double y) {
		this.index = index;
		this.x = x;
		this.y = y;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return " " + index + " ";
	}
}
