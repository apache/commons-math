package org.apache.commons.math4.examples.genetics.mathfunctions;

import java.util.List;

import org.apache.commons.math4.genetics.model.BinaryChromosome;
import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.FitnessFunction;

public class Dimension2FitnessFunction implements FitnessFunction {

	@Override
	public double compute(Chromosome chromosome) {
		BinaryChromosome binaryChromosome = (BinaryChromosome) chromosome;
		List<Integer> alleles = binaryChromosome.getRepresentation();

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

}
