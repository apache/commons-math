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

package org.apache.commons.math4.examples.sofm.chineserings;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math4.neuralnet.twod.NeuronSquareMesh2D;

/**
 * Application class.
 */
@Command(description = "Run the application",
         mixinStandardHelpOptions = true)
public final class StandAlone implements Callable<Void> {
    /** The number of rows. */
    @Option(names = { "-r" }, paramLabel = "numRows",
            description = "Number of rows of the 2D SOFM (default: ${DEFAULT-VALUE}).")
    private int numRows = 15;
    /** The number of columns. */
    @Option(names = { "-c" }, paramLabel = "numCols",
            description = "Number of columns of the 2D SOFM (default: ${DEFAULT-VALUE}).")
    private int numCols = 15;
    /** The number of samples. */
    @Option(names = { "-s" }, paramLabel = "numSamples",
            description = "Number of samples for the training (default: ${DEFAULT-VALUE}).")
    private long numSamples = 100000;
    /** The output file. */
    @Option(names = { "-o" }, paramLabel = "outputFile", required = true,
            description = "Output file name.")
    private String outputFile = null;

    /**
     * Program entry point.
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

        final ChineseRingsClassifier classifier = new ChineseRingsClassifier(rings, numRows, numCols);
        classifier.createSequentialTask(numSamples).run();
        printResult(outputFile, classifier);

        return null;
    }

    /**
     * Prints various quality measures of the map to files.
     *
     * @param fileName File name.
     * @param sofm Classifier.
     * @throws UnsupportedEncodingException If UTF-8 encoding does not exist.
     * @throws FileNotFoundException If the file cannot be created.
     */
    private static void printResult(String fileName,
                                    ChineseRingsClassifier sofm)
                                    throws FileNotFoundException, UnsupportedEncodingException {
        final NeuronSquareMesh2D.DataVisualization result = sofm.computeQualityIndicators();

        try (PrintWriter out = new PrintWriter(fileName, StandardCharsets.UTF_8.name())) {
            out.println("# Number of samples: " + result.getNumberOfSamples());
            out.println("# Quantization error: " + result.getMeanQuantizationError());
            out.println("# Topographic error: " + result.getMeanTopographicError());
            out.println();

            printImage("Quantization error", result.getQuantizationError(), out);
            printImage("Topographic error", result.getTopographicError(), out);
            printImage("Normalized hits", result.getNormalizedHits(), out);
            printImage("U-matrix", result.getUMatrix(), out);
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
