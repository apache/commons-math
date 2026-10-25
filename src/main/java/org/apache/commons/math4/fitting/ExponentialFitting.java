/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.fitting;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.InsufficientDataException;
import org.apache.commons.math4.exception.util.DummyLocalizable;
import org.apache.commons.math4.linear.BlockRealMatrix;
import org.apache.commons.math4.linear.MatrixUtils;
import org.apache.commons.math4.linear.RealMatrix;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

/**
 * Fits points to an exponential model using integral technique described in
 * https://fr.scribd.com/doc/14674814/Regressions-et-equations-integrales
 * written by Jacquelin
 *
 * Model fit can be queried for initial value, beta, and baseline to equation formed by
 * y=Initial*exp(Beta*x)+Baseline   or y = a2 + b2 * exp(c2*x)
 * The accuracy of the fit rises with the volume of data supplied.
 */

public class ExponentialFitting {

    public double getBaseline() {
        return a2;
    }

    public double getInitial() {
        return b2;
    }

    public double getBeta() {
        return c2;
    }

    private double a2;
    private double b2;
    private double c2;
    private boolean isSorted = false;
    private SortedMap<Double, Double>data;

    public ExponentialFitting(final SortedMap<Double, Double> _dataset) {
        data = _dataset;
    }

    public ExponentialFitting() {
        data = new TreeMap();
    }

    public void addData(double x, double y) {
        data.put(x, y);
    }

    public void addData(Map<Double, Double> _mapData) {
        _mapData.entrySet().forEach(e -> data.put(e.getKey(), e.getValue()));
    }

    public void addData(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new DimensionMismatchException(new DummyLocalizable("Array lengths must be equal"), x.length, y.length);
        }
        IntStream.range(0, x.length).forEach(i -> data.put(x[i], y[i]));
    }

    public void evaluate() {
        if (data.size() < 3) {
            throw new InsufficientDataException(new DummyLocalizable("Three data points minimum required"));
        }
        final double[] x = data.keySet().stream().mapToDouble(Double::doubleValue).toArray();
        final double[] y = data.values().stream().mapToDouble(Double::doubleValue).toArray();
        c2 = ExtractBeta(x, y);//Beta
        RealMatrix result = extractInitalAndBaseline(x, y, c2);
        a2 = result.getEntry(0, 0);//BaseLine
        b2 = result.getEntry(1, 0);//Initial
    }

    private double ExtractBeta(double[] _x, double[] _y) {
        double Sk;
        double SkPrevious = 0.0;
        double sumX2 = 0.0;
        double sumXS = 0.0;
        double sumS2 = 0.0;
        double sumXY = 0.0;
        double sumYS = 0.0;
        SkPrevious = 0.0;
        for (int i = 1; i < _x.length; i++) {
            Sk = SkPrevious + (_y[i] + _y[i - 1]) / 2 * (_x[i] - _x[i - 1]);
            double difX = _x[i] - _x[0];
            double difY = _y[i] - _y[0];
            sumX2 += difX * difX;
            sumXS += difX * Sk;
            sumS2 += Sk * Sk;
            sumXY += difX * difY;
            sumYS += difY * Sk;
            SkPrevious = Sk;
        }

        RealMatrix main = new BlockRealMatrix(2, 2);
        main.setEntry(0, 0, sumX2);
        main.setEntry(0, 1, sumXS);
        main.setEntry(1, 0, sumXS);
        main.setEntry(1, 1, sumS2);
        RealMatrix small = new BlockRealMatrix(2, 1);
        small.setEntry(0, 0, sumXY);
        small.setEntry(1, 0, sumYS);
        return MatrixUtils.inverse(main).multiply(small).getEntry(1, 0);//Beta;
    }

    private RealMatrix extractInitalAndBaseline(double[] _x, double[] _y, double _beta) {
        double sumTheta = 0.0;
        double sumThetaY = 0.0;
        double sumTheta2 = 0.0;
        double theta = 0.0;
        double sumY = 0.0;
        for (int i = 0; i < _x.length; i++) {
            sumY += _y[i];
            theta = Math.exp(_beta * (_x[i] - _x[0]));
            sumTheta += theta;
            sumThetaY += theta * _y[i];
            sumTheta2 += theta * theta;
        }
        RealMatrix main = new BlockRealMatrix(2, 2);
        RealMatrix small = new BlockRealMatrix(2, 1);
        main.setEntry(0, 0, _x.length);
        main.setEntry(0, 1, sumTheta);
        main.setEntry(1, 0, sumTheta);
        main.setEntry(1, 1, sumTheta2);
        small.setEntry(0, 0, sumY);
        small.setEntry(1, 0, sumThetaY);
        return MatrixUtils.inverse(main).multiply(small);
    }

}
