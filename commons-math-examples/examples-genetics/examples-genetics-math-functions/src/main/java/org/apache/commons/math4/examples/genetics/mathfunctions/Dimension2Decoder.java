package org.apache.commons.math4.examples.genetics.mathfunctions;

import java.util.List;

import org.apache.commons.math4.genetics.AbstractListChromosome;
import org.apache.commons.math4.genetics.BinaryChromosome;
import org.apache.commons.math4.genetics.decoder.AbstractListChromosomeDecoder;

public class Dimension2Decoder extends AbstractListChromosomeDecoder<Integer, Coordinate> {

    @Override
    protected Coordinate decode(AbstractListChromosome<Integer, Coordinate> chromosome) {
        BinaryChromosome<Coordinate> binaryChromosome = (BinaryChromosome<Coordinate>) chromosome;
        List<Integer> alleles = binaryChromosome.getRepresentation();

        StringBuilder allelesStr = new StringBuilder();
        for (Integer allele : alleles) {
            allelesStr.append(Integer.toBinaryString(allele));
        }

        double x = Integer.parseInt(allelesStr.substring(0, 12), 2) / 100.0;
        double y = Integer.parseInt(allelesStr.substring(12, 24), 2) / 100.0;

        return new Coordinate(x, y);
    }

}
