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
package org.apache.commons.math3.optim.nonlinear.vector.jacobian;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
import org.apache.commons.math3.util.MathArrays;

/**
 * This class gives access to the statistical reference datasets provided by the
 * NIST (available
 * <a href="http://www.itl.nist.gov/div898/strd/general/dataarchive.html">here</a>).
 * Instances of this class can be created by invocation of the
 * {@link StatisticalReferenceDatasetFactory}.
 */
@Deprecated
public abstract class StatisticalReferenceDataset {

    /** The name of this dataset. */
    private final String name;

    /** The total number of observations (data points). */
    private final int numObservations;

    /** The total number of parameters. */
    private final int numParameters;

    /** The total number of starting points for the optimizations. */
    private final int numStartingPoints;

    /** The values of the predictor. */
    private final double[] x;

    /** The values of the response. */
    private final double[] y;

    /**
     * The starting values. {@code startingValues[j][i]} is the value of the
     * {@code i}-th parameter in the {@code j}-th set of starting values.
     */
    private final double[][] startingValues;

    /** The certified values of the parameters. */
    private final double[] a;

    /** The certified values of the standard deviation of the parameters. */
    private final double[] sigA;

    /** The certified value of the residual sum of squares. */
    private double residualSumOfSquares;

    /** The least-squares problem. */
    private final LeastSquaresProblem problem;

    /**
     * Creates a new instance of this class from the specified data file. The
     * file must follow the StRD format.
     *
     * @param in the data file
     * @throws IOException if an I/O error occurs
     */
    public StatisticalReferenceDataset(final BufferedReader in)
        throws IOException {

        final ArrayList<String> lines = new ArrayList<String>();
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            lines.add(line);
        }
        int[] index = findLineNumbers("Data", lines);
        if (index == null) {
            throw new AssertionError("could not find line indices for data");
        }
        this.numObservations = index[1] - index[0] + 1;
        this.x = new double[this.numObservations];
        this.y = new double[this.numObservations];
        for (int i = 0; i < this.numObservations; i++) {
            final String line = lines.get(index[0] + i - 1);
            final String[] tokens = line.trim().split(" ++");
            // Data columns are in reverse order!!!
            this.y[i] = Double.parseDouble(tokens[0]);
            this.x[i] = Double.parseDouble(tokens[1]);
        }

        index = findLineNumbers("Starting Values", lines);
        if (index == null) {
            throw new AssertionError(
                                     "could not find line indices for starting values");
        }
        this.numParameters = index[1] - index[0] + 1;

        double[][] start = null;
        this.a = new double[numParameters];
        this.sigA = new double[numParameters];
        for (int i = 0; i < numParameters; i++) {
            final String line = lines.get(index[0] + i - 1);
            final String[] tokens = line.trim().split(" ++");
            if (start == null) {
                start = new double[tokens.length - 4][numParameters];
            }
            for (int j = 2; j < tokens.length - 2; j++) {
                start[j - 2][i] = Double.parseDouble(tokens[j]);
            }
            this.a[i] = Double.parseDouble(tokens[tokens.length - 2]);
            this.sigA[i] = Double.parseDouble(tokens[tokens.length - 1]);
        }
        if (start == null) {
            throw new IOException("could not find starting values");
        }
        this.numStartingPoints = start.length;
        this.startingValues = start;

        double dummyDouble = Double.NaN;
        String dummyString = null;
        for (String line : lines) {
            if (line.contains("Dataset Name:")) {
                dummyString = line
                    .substring(line.indexOf("Dataset Name:") + 13,
                               line.indexOf("(")).trim();
            }
            if (line.contains("Residual Sum of Squares")) {
                final String[] tokens = line.split(" ++");
                dummyDouble = Double.parseDouble(tokens[4].trim());
            }
        }
        if (Double.isNaN(dummyDouble)) {
            throw new IOException(
                                  "could not find certified value of residual sum of squares");
        }
        this.residualSumOfSquares = dummyDouble;

        if (dummyString == null) {
            throw new IOException("could not find dataset name");
        }
        this.name = dummyString;

        this.problem = new LeastSquaresProblem();
    }

    class LeastSquaresProblem {
        public ModelFunction getModelFunction() {
            return new ModelFunction(new MultivariateVectorFunction() {
                    public double[] value(final double[] a) {
                        final int n = getNumObservations();
                        final double[] yhat = new double[n];
                        for (int i = 0; i < n; i++) {
                            yhat[i] = getModelValue(getX(i), a);
                        }
                        return yhat;
                    }
                });
        }

        public ModelFunctionJacobian getModelFunctionJacobian() {
            return new ModelFunctionJacobian(new MultivariateMatrixFunction() {
                    public double[][] value(final double[] a)
                        throws IllegalArgumentException {
                        final int n = getNumObservations();
                        final double[][] j = new double[n][];
                        for (int i = 0; i < n; i++) {
                            j[i] = getModelDerivatives(getX(i), a);
                        }
                        return j;
                    }
                });
        }
    }

    /**
     * Returns the name of this dataset.
     *
     * @return the name of the dataset
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total number of observations (data points).
     *
     * @return the number of observations
     */
    public int getNumObservations() {
        return numObservations;
    }

    /**
     * Returns a copy of the data arrays. The data is laid out as follows <li>
     * {@code data[0][i] = x[i]},</li> <li>{@code data[1][i] = y[i]},</li>
     *
     * @return the array of data points.
     */
    public double[][] getData() {
        return new double[][] {
            MathArrays.copyOf(x), MathArrays.copyOf(y)
        };
    }

    /**
     * Returns the x-value of the {@code i}-th data point.
     *
     * @param i the index of the data point
     * @return the x-value
     */
    public double getX(final int i) {
        return x[i];
    }

    /**
     * Returns the y-value of the {@code i}-th data point.
     *
     * @param i the index of the data point
     * @return the y-value
     */
    public double getY(final int i) {
        return y[i];
    }

    /**
     * Returns the total number of parameters.
     *
     * @return the number of parameters
     */
    public int getNumParameters() {
        return numParameters;
    }

    /**
     * Returns the certified values of the paramters.
     *
     * @return the values of the parameters
     */
    public double[] getParameters() {
        return MathArrays.copyOf(a);
    }

    /**
     * Returns the certified value of the {@code i}-th parameter.
     *
     * @param i the index of the parameter
     * @return the value of the parameter
     */
    public double getParameter(final int i) {
        return a[i];
    }

    /**
     * Reurns the certified values of the standard deviations of the parameters.
     *
     * @return the standard deviations of the parameters
     */
    public double[] getParametersStandardDeviations() {
        return MathArrays.copyOf(sigA);
    }

    /**
     * Returns the certified value of the standard deviation of the {@code i}-th
     * parameter.
     *
     * @param i the index of the parameter
     * @return the standard deviation of the parameter
     */
    public double getParameterStandardDeviation(final int i) {
        return sigA[i];
    }

    /**
     * Returns the certified value of the residual sum of squares.
     *
     * @return the residual sum of squares
     */
    public double getResidualSumOfSquares() {
        return residualSumOfSquares;
    }

    /**
     * Returns the total number of starting points (initial guesses for the
     * optimization process).
     *
     * @return the number of starting points
     */
    public int getNumStartingPoints() {
        return numStartingPoints;
    }

    /**
     * Returns the {@code i}-th set of initial values of the parameters.
     *
     * @param i the index of the starting point
     * @return the starting point
     */
    public double[] getStartingPoint(final int i) {
        return MathArrays.copyOf(startingValues[i]);
    }

    /**
     * Returns the least-squares problem corresponding to fitting the model to
     * the specified data.
     *
     * @return the least-squares problem
     */
    public LeastSquaresProblem getLeastSquaresProblem() {
        return problem;
    }

    /**
     * Returns the value of the model for the specified values of the predictor
     * variable and the parameters.
     *
     * @param x the predictor variable
     * @param a the parameters
     * @return the value of the model
     */
    public abstract double getModelValue(final double x, final double[] a);

    /**
     * Returns the values of the partial derivatives of the model with respect
     * to the parameters.
     *
     * @param x the predictor variable
     * @param a the parameters
     * @return the partial derivatives
     */
    public abstract double[] getModelDerivatives(final double x,
                                                 final double[] a);

    /**
     * <p>
     * Parses the specified text lines, and extracts the indices of the first
     * and last lines of the data defined by the specified {@code key}. This key
     * must be one of
     * </p>
     * <ul>
     * <li>{@code "Starting Values"},</li>
     * <li>{@code "Certified Values"},</li>
     * <li>{@code "Data"}.</li>
     * </ul>
     * <p>
     * In the NIST data files, the line indices are separated by the keywords
     * {@code "lines"} and {@code "to"}.
     * </p>
     *
     * @param lines the line of text to be parsed
     * @return an array of two {@code int}s. First value is the index of the
     *         first line, second value is the index of the last line.
     *         {@code null} if the line could not be parsed.
     */
    private static int[] findLineNumbers(final String key,
                                         final Iterable<String> lines) {
        for (String text : lines) {
            boolean flag = text.contains(key) && text.contains("lines") &&
                           text.contains("to") && text.contains(")");
            if (flag) {
                final int[] numbers = new int[2];
                final String from = text.substring(text.indexOf("lines") + 5,
                                                   text.indexOf("to"));
                numbers[0] = Integer.parseInt(from.trim());
                final String to = text.substring(text.indexOf("to") + 2,
                                                 text.indexOf(")"));
                numbers[1] = Integer.parseInt(to.trim());
                return numbers;
            }
        }
        return null;
    }
}
