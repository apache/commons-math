package org.apache.commons.math4.examples.genetics.mathfunctions.legacy;

import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.BinaryChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;



public class LegacyBinaryChromosome extends BinaryChromosome {

	public LegacyBinaryChromosome(List<Integer> representation) throws InvalidRepresentationException {
		super(representation);
	}

	@Override
	public double fitness() {
		List<Integer> alleles = getRepresentation();

		StringBuilder allelesStr = new StringBuilder();
		for (Integer allele : alleles) {
			allelesStr.append(Integer.toBinaryString(allele));
		}

		double x = Integer.parseInt(allelesStr.substring(0, 12), 2) / 100.0;
		double y = Integer.parseInt(allelesStr.substring(12, 24), 2) / 100.0;
		double computedValue = Math.pow((Math.pow(x, 2) + Math.pow(y, 2)), .25)
				* (Math.pow(Math.sin(50 * Math.pow((Math.pow(x, 2) + Math.pow(y, 2)), .1)), 2) + 1);

		return computedValue * (-1.0);
	}

	@Override
	public AbstractListChromosome<Integer> newFixedLengthChromosome(List<Integer> chromosomeRepresentation) {
		return new LegacyBinaryChromosome(chromosomeRepresentation);
	}

	@Override
	public List<Integer> getRepresentation() {
		return super.getRepresentation();
	}
}
