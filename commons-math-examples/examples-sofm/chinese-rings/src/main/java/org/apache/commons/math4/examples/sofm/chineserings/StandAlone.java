/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math4.examples.sofm.chineserings;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math4.legacy.ml.neuralnet.twod.NeuronSquareMesh2D;

/**
 * Application class.
 */
@Command(description = "Run the application",
         mixinStandardHelpOptions = true)
public class StandAlone implements Callable<Void> {
    @Option(names = { "-r" }, paramLabel = "numRows",
            description = "Number of rows of the 2D SOFM (default: ${DEFAULT-VALUE}).")
    private int _numRows = 15;
    @Option(names = { "-c" }, paramLabel = "numCols",
            description = "Number of columns of the 2D SOFM (default: ${DEFAULT-VALUE}).")
    private int _numCols = 15;
    @Option(names = { "-s" }, paramLabel = "numSamples",
            description = "Number of samples for the training (default: ${DEFAULT-VALUE}).")
    private long _numSamples = 100000;
    @Option(names = { "-o" }, paramLabel = "outputFile", required = true,
            description = "Output file name.")
    private String _outputFile = null;

    /**
     * Program entry point.  All solver parameters must be provided
     * through a {@link java.util.Properties Properties} file.
     *
     * @param args Command line arguments and options.
     */
    public static void main(String[] args) {
        CommandLine.call(new StandAlone(), args);
    }

    @Override
    public Void call() throws Exception {
        final ChineseRings rings = new ChineseRings(Vector3D.of(1, 2, 3),
                                                    25, 2,
                                                    20, 1,
                                                    2000, 1500);

        final ChineseRingsClassifier classifier = new ChineseRingsClassifier(rings, _numRows, _numCols);
        classifier.createSequentialTask(_numSamples).run();
        printResult(_outputFile, classifier);

        return null;
    }

    /**
     * Prints various quality measures of the map to files.
     *
     * @param fileName File name.
     * @param sofm Classifier.
     */
    private static void printResult(String fileName,
                                    ChineseRingsClassifier sofm) {
        final NeuronSquareMesh2D.DataVisualization result = sofm.computeQualityIndicators();

        try (final PrintWriter out = new PrintWriter(fileName)) {
            out.println("# Number of samples: " + result.getNumberOfSamples());
            out.println("# Quantization error: " + result.getMeanQuantizationError());
            out.println("# Topographic error: " + result.getMeanTopographicError());
            out.println();

            printImage("Quantization error", result.getQuantizationError(), out);
            printImage("Topographic error", result.getTopographicError(), out);
            printImage("Normalized hits", result.getNormalizedHits(), out);
            printImage("U-matrix", result.getUMatrix(), out);
        } catch (IOException e) {
            // Do nothing.
        }
    }

    /**
     * @param desc Data description.
     * @param image Data.
     * @param out Output stream.
     */
    private static void printImage(String desc,
                                   double[][] image,
                                   PrintWriter out) {
        out.println("# " + desc);
        final int nR = image.length;
        final int nC = image[0].length;
        for (int i = 0; i < nR; i++) {
            for (int j = 0; j < nC; j++) {
                out.print(image[i][j] + " ");
            }
            out.println();
        }
        out.println();
    }
}
