package org.apache.commons.math4.clustering.internal.stat;

public class Variance {
    private int n = 0;
    private double dev = 0;
    private double nDev = 0;
    private double m2 = 0;
    private double m1 = 0;

    public void increment(final double d) {
        ++n;
        dev = d - m1;
        nDev = dev / n;
        m1 += nDev;
        m2 += ((double) n - 1) * dev * nDev;
    }

    public double get() {
        return m2;
    }
}
