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
package org.apache.commons.math4.transform;

import java.util.function.UnaryOperator;

import org.apache.commons.numbers.core.ArithmeticUtils;

/**
 * <a href="http://www.archive.chipcenter.com/dsp/DSP000517F1.html">Fast Hadamard Transform</a> (FHT).
 * <p>
 * The FHT can also transform integer vectors into integer vectors.
 * However, this transform cannot be inverted directly, due to a scaling
 * factor that may lead to rational results (for example, the inverse
 * transform of integer vector (0, 1, 0, 1) is vector (1/2, -1/2, 0, 0).
 */
public class FastHadamardTransform implements RealTransform {
    /** Operation to be performed. */
    private final UnaryOperator<double[]> op;

    /**
     * Default constructor.
     */
    public FastHadamardTransform() {
        this(false);
    }

    /**
     * @param inverse Whether to perform the inverse transform.
     */
    public FastHadamardTransform(final boolean inverse) {
        op = create(inverse);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the length of the data array is
     * not a power of two.
     */
    @Override
    public double[] apply(final double[] f) {
        return op.apply(f);
    }

    /**
     * Returns the forward transform of the given data set.
     * The integer transform cannot be inverted directly, due to a scaling
     * factor which may lead to double results.
     *
     * @param f Data array to be transformed (signal).
     * @return the transformed array (spectrum).
     * @throws IllegalArgumentException if the length of the data array is
     * not a power of two.
     */
    public int[] apply(final int[] f) {
        return fht(f);
    }

    /**
     * FHT uses only subtraction and addition.
     * It requires {@code N * log2(N) = n * 2^n} additions.
     *
     * <h3>Short Table of manual calculation for N=8</h3>
     * <ol>
     * <li><b>x</b> is the input vector to be transformed,</li>
     * <li><b>y</b> is the output vector (Fast Hadamard transform of <b>x</b>),</li>
     * <li>a and b are helper rows.</li>
     * </ol>
     * <table style="text-align: center" border="">
     * <caption>manual calculation for N=8</caption>
     * <tbody style="text-align: center">
     * <tr>
     *     <th>x</th>
     *     <th>a</th>
     *     <th>b</th>
     *     <th>y</th>
     * </tr>
     * <tr>
     *     <th>x<sub>0</sub></th>
     *     <td>a<sub>0</sub> = x<sub>0</sub> + x<sub>1</sub></td>
     *     <td>b<sub>0</sub> = a<sub>0</sub> + a<sub>1</sub></td>
     *     <td>y<sub>0</sub> = b<sub>0</sub >+ b<sub>1</sub></td>
     * </tr>
     * <tr>
     *     <th>x<sub>1</sub></th>
     *     <td>a<sub>1</sub> = x<sub>2</sub> + x<sub>3</sub></td>
     *     <td>b<sub>0</sub> = a<sub>2</sub> + a<sub>3</sub></td>
     *     <td>y<sub>0</sub> = b<sub>2</sub> + b<sub>3</sub></td>
     * </tr>
     * <tr>
     *     <th>x<sub>2</sub></th>
     *     <td>a<sub>2</sub> = x<sub>4</sub> + x<sub>5</sub></td>
     *     <td>b<sub>0</sub> = a<sub>4</sub> + a<sub>5</sub></td>
     *     <td>y<sub>0</sub> = b<sub>4</sub> + b<sub>5</sub></td>
     * </tr>
     * <tr>
     *     <th>x<sub>3</sub></th>
     *     <td>a<sub>3</sub> = x<sub>6</sub> + x<sub>7</sub></td>
     *     <td>b<sub>0</sub> = a<sub>6</sub> + a<sub>7</sub></td>
     *     <td>y<sub>0</sub> = b<sub>6</sub> + b<sub>7</sub></td>
     * </tr>
     * <tr>
     *     <th>x<sub>4</sub></th>
     *     <td>a<sub>0</sub> = x<sub>0</sub> - x<sub>1</sub></td>
     *     <td>b<sub>0</sub> = a<sub>0</sub> - a<sub>1</sub></td>
     *     <td>y<sub>0</sub> = b<sub>0</sub> - b<sub>1</sub></td>
     * </tr>
     * <tr>
     *     <th>x<sub>5</sub></th>
     *     <td>a<sub>1</sub> = x<sub>2</sub> - x<sub>3</sub></td>
     *     <td>b<sub>0</sub> = a<sub>2</sub> - a<sub>3</sub></td>
     *     <td>y<sub>0</sub> = b<sub>2</sub> - b<sub>3</sub></td>
     * </tr>
     * <tr>
     *     <th>x<sub>6</sub></th>
     *     <td>a<sub>2</sub> = x<sub>4</sub> - x<sub>5</sub></td>
     *     <td>b<sub>0</sub> = a<sub>4</sub> - a<sub>5</sub></td>
     *     <td>y<sub>0</sub> = b<sub>4</sub> - b<sub>5</sub></td>
     * </tr>
     * <tr>
     *     <th>x<sub>7</sub></th>
     *     <td>a<sub>3</sub> = x<sub>6</sub> - x<sub>7</sub></td>
     *     <td>b<sub>0</sub> = a<sub>6</sub> - a<sub>7</sub></td>
     *     <td>y<sub>0</sub> = b<sub>6</sub> - b<sub>7</sub></td>
     * </tr>
     * </tbody>
     * </table>
     *
     * <h3>How it works</h3>
     * <ol>
     * <li>Construct a matrix with {@code N} rows and {@code n + 1} columns,
     * {@code hadm[n+1][N]}.<br>
     * <em>(If I use [x][y] it always means [row-offset][column-offset] of a
     * Matrix with n rows and m columns. Its entries go from M[0][0]
     * to M[n][N])</em></li>
     * <li>Place the input vector {@code x[N]} in the first column of the
     * matrix {@code hadm}.</li>
     * <li>The entries of the submatrix {@code D_top} are calculated as follows
     *     <ul>
     *         <li>{@code D_top} goes from entry {@code [0][1]} to
     *         {@code [N / 2 - 1][n + 1]},</li>
     *         <li>the columns of {@code D_top} are the pairwise mutually
     *         exclusive sums of the previous column.</li>
     *     </ul>
     * </li>
     * <li>The entries of the submatrix {@code D_bottom} are calculated as
     * follows
     *     <ul>
     *         <li>{@code D_bottom} goes from entry {@code [N / 2][1]} to
     *         {@code [N][n + 1]},</li>
     *         <li>the columns of {@code D_bottom} are the pairwise differences
     *         of the previous column.</li>
     *     </ul>
     * </li>
     * <li>The computation of {@code D_top} and {@code D_bottom} are best
     * understood with the above example (for {@code N = 8}).
     * <li>The output vector {@code y} is now in the last column of
     * {@code hadm}.</li>
     * <li><em>Algorithm from <a href="http://www.archive.chipcenter.com/dsp/DSP000517F1.html">chipcenter</a>.</em></li>
     * </ol>
     * <h3>Visually</h3>
     * <table border="" style="text-align: center">
     * <caption>chipcenter algorithm</caption>
     * <tbody style="text-align: center">
     * <tr>
     *     <td></td><th>0</th><th>1</th><th>2</th><th>3</th>
     *     <th>&hellip;</th>
     *     <th>n + 1</th>
     * </tr>
     * <tr>
     *     <th>0</th>
     *     <td>x<sub>0</sub></td>
     *     <td colspan="5" rowspan="5" align="center" valign="middle">
     *         &uarr;<br>
     *         &larr; D<sub>top</sub> &rarr;<br>
     *         &darr;
     *     </td>
     * </tr>
     * <tr><th>1</th><td>x<sub>1</sub></td></tr>
     * <tr><th>2</th><td>x<sub>2</sub></td></tr>
     * <tr><th>&hellip;</th><td>&hellip;</td></tr>
     * <tr><th>N / 2 - 1</th><td>x<sub>N/2-1</sub></td></tr>
     * <tr>
     *     <th>N / 2</th>
     *     <td>x<sub>N/2</sub></td>
     *     <td colspan="5" rowspan="5" align="center" valign="middle">
     *         &uarr;<br>
     *         &larr; D<sub>bottom</sub> &rarr;<br>
     *         &darr;
     *     </td>
     * </tr>
     * <tr><th>N / 2 + 1</th><td>x<sub>N/2+1</sub></td></tr>
     * <tr><th>N / 2 + 2</th><td>x<sub>N/2+2</sub></td></tr>
     * <tr><th>&hellip;</th><td>&hellip;</td></tr>
     * <tr><th>N</th><td>x<sub>N</sub></td></tr>
     * </tbody>
     * </table>
     *
     * @param x Data to be transformed.
     * @return the transformed array.
     * @throws IllegalArgumentException if the length of the data array is
     * not a power of two.
     */
    private double[] fht(double[] x) {
        final int n = x.length;

        if (!ArithmeticUtils.isPowerOfTwo(n)) {
            throw new TransformException(TransformException.NOT_POWER_OF_TWO,
                                         n);
        }

        final int halfN = n / 2;

        // Instead of creating a matrix with p+1 columns and n rows, we use two
        // one dimension arrays which we are used in an alternating way.
        double[] yPrevious = new double[n];
        double[] yCurrent  = x.clone();

        // iterate from left to right (column)
        for (int j = 1; j < n; j <<= 1) {

            // switch columns
            final double[] yTmp = yCurrent;
            yCurrent  = yPrevious;
            yPrevious = yTmp;

            // iterate from top to bottom (row)
            for (int i = 0; i < halfN; ++i) {
                // Dtop: the top part works with addition
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI] + yPrevious[twoI + 1];
            }
            for (int i = halfN; i < n; ++i) {
                // Dbottom: the bottom part works with subtraction
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI - n] - yPrevious[twoI - n + 1];
            }
        }

        return yCurrent;
    }

    /**
     * Returns the forward transform of the specified integer data set.
     * FHT only uses subtraction and addition.
     *
     * @param x Data to be transformed.
     * @return the transformed array.
     * @throws IllegalArgumentException if the length of the data array is
     * not a power of two.
     */
    private int[] fht(int[] x) {
        final int n = x.length;
        if (!ArithmeticUtils.isPowerOfTwo(n)) {
            throw new TransformException(TransformException.NOT_POWER_OF_TWO,
                                         n);
        }

        final int halfN = n / 2;

        // Instead of creating a matrix with p+1 columns and n rows, we use two
        // one dimension arrays which we are used in an alternating way.

        int[] yPrevious = new int[n];
        int[] yCurrent  = x.clone();

        // iterate from left to right (column)
        for (int j = 1; j < n; j <<= 1) {
            // switch columns
            final int[] yTmp = yCurrent;
            yCurrent  = yPrevious;
            yPrevious = yTmp;

            // iterate from top to bottom (row)
            for (int i = 0; i < halfN; ++i) {
                // Dtop: the top part works with addition
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI] + yPrevious[twoI + 1];
            }
            for (int i = halfN; i < n; ++i) {
                // Dbottom: the bottom part works with subtraction
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI - n] - yPrevious[twoI - n + 1];
            }
        }

        // return the last computed output vector y
        return yCurrent;
    }

    /**
     * Factory method.
     *
     * @param inverse Whether to perform the inverse transform.
     * @return the transform operator.
     */
    private UnaryOperator<double[]> create(final boolean inverse) {
        if (inverse) {
            return f -> TransformUtils.scaleInPlace(fht(f), 1d / f.length);
        } else {
            return f -> fht(f);
        }
    }
}
