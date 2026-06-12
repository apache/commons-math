package org.apache.commons.math4.fitting;

import org.junit.Assert;
import org.junit.Test;

import java.util.SortedMap;
import java.util.TreeMap;


public class ExponentialFittingTest {
    @Test
    public void evaluate() {
        SortedMap<Double, Double> sortedData = new TreeMap<>();
        sortedData.put(0.0, 1.0);
        sortedData.put(1.0, 3.0);
        sortedData.put(2.0, 9.0);
        sortedData.put(3.0, 50.0);
        ExponentialFitting expFit = new ExponentialFitting(sortedData);
        expFit.evaluate();
        Assert.assertEquals(-0.7238210, expFit.getBaseline(), 0.000001);
        Assert.assertEquals( 0.6979045, expFit.getInitial(),  0.000001);
        Assert.assertEquals( 1.4253654, expFit.getBeta(),     0.000001);

        ExponentialFitting expFit2 = new ExponentialFitting();
        // Test unsorted data presentation.
        expFit2.addData(2.0,9.0);
        expFit2.addData(0.0, 1.0);
        expFit2.addData(3.0, 50.0);
        expFit2.addData(1.0, 3.0);
        expFit2.evaluate();
        Assert.assertEquals(-0.723821, expFit2.getBaseline(), 0.000001);
        Assert.assertEquals(0.6979045, expFit2.getInitial(), 0.000001);
        Assert.assertEquals(1.4253654, expFit2.getBeta(), 0.000001);

        // Test unsorted array entry
        ExponentialFitting expFit3 = new ExponentialFitting();
        double[] xArray = {2.0, 3.0, 1.0, 0.0};
        double[] yArray = {9.0, 50.0, 3.0, 1.0};
        expFit3.addData(xArray, yArray);
        expFit3.evaluate();
        Assert.assertEquals(-0.723821, expFit3.getBaseline(), 0.000001);
        Assert.assertEquals(0.6979045, expFit3.getInitial(), 0.000001);
        Assert.assertEquals(1.4253654, expFit3.getBeta(), 0.000001);
    }
}