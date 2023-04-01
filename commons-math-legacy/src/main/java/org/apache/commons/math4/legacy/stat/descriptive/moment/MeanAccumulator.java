package org.apache.commons.math4.legacy.stat.descriptive.moment;

import org.apache.commons.math4.legacy.stat.descriptive.StatisticAccumulator;

import java.util.Arrays;
import java.util.List;
public class MeanAccumulator implements StatisticAccumulator<Mean> {

    private long n;
    private double m1;
    private double dev;
    private double nDev;

    public MeanAccumulator() {
        n = 0;
        m1 = Double.NaN;
        dev = Double.NaN;
        nDev = Double.NaN;
    }

    @Override
    public <U extends StatisticAccumulator<Mean>> void merge(U other) {
        merge(other.get());
    }

    @Override
    public void merge(Mean other) {
        FirstMoment otherFirstMoment = other.moment;
        merge(otherFirstMoment.n, otherFirstMoment.m1, otherFirstMoment.dev, otherFirstMoment.nDev);
    }

    public void merge(MeanAccumulator other) {
        // Faster (since it does not create intermediate Mean object)
        // Use if we know we are merging another type of MeanAccumulator
        merge(other.n, other.m1, other.dev, other.nDev);
    }

    @Override
    public Mean get() {
        // Construct Mean from the FirstMoment
        FirstMoment firstMoment = new FirstMoment(n, m1, dev, nDev);
        return new Mean(firstMoment);
    }

    public void add(final double d) {
        // Borrowed from FirstMoment::increment
        if (n == 0) {
            m1 = 0.0;
        }
        n++;
        double n0 = n;
        dev = d - m1;
        nDev = dev / n0;
        m1 += nDev;
    }

    private void merge(long otherN, double otherM1, double otherDev, double otherNdev) {
        if (otherN == 0) {
            return; // Nothing to merge
        }
        if (n == 0) {
            n = otherN;
            m1 = otherM1;
            dev = otherDev;
            nDev = otherNdev;
        } else {
            n += otherN;

            double n0 = n;
            dev = otherM1 - m1;
            nDev = otherN * dev / n0;
            m1 += nDev;
        }
    }
}

final class ProofOfConcept {

    // PoC: Sample usage
    public static void main(String[] args) {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, -1.0);
        Mean mean = data.parallelStream()
                .collect(MeanAccumulator::new, MeanAccumulator::add, MeanAccumulator::merge)
                .get();
        System.out.println("n::: "+ mean.getN() + " Mean::: " + mean.getResult());
    }
}