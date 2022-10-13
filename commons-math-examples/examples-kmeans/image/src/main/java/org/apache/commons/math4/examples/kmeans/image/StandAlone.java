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

package org.apache.commons.math4.examples.kmeans.image;

import java.util.concurrent.Callable;
import java.time.Instant;
import java.time.Duration;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.math4.legacy.ml.distance.DistanceMeasure;
import org.apache.commons.math4.legacy.ml.distance.EuclideanDistance;
import org.apache.commons.math4.legacy.ml.clustering.Clusterer;
import org.apache.commons.math4.legacy.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math4.legacy.ml.clustering.ElkanKMeansPlusPlusClusterer;

/**
 * Application class.
 */
@Command(description = "Run the application",
         mixinStandardHelpOptions = true)
public final class StandAlone implements Callable<Void> {
    /** The k parameter. */
    @Option(names = { "-k" }, paramLabel = "K", required = true,
            description = "Number of clusters.")
    private int numClusters;
    /** The maximal number of iterations. */
    @Option(names = { "-i", "--iterations" }, paramLabel = "N",
            description = "Allowed number of iterations (default: ${DEFAULT-VALUE}).")
    private int maxIter = 2000;
    /** The input file. */
    @Option(names = { "--image" }, paramLabel = "FILE", required = true,
            description = "Input file name.")
    private String inputFile;
    /** The output prefix. */
    @Option(names = { "-o", "--output" }, paramLabel = "PREFIX", required = true,
            description = "Prefix (path) for the output files.")
    private String outputPrefix;

    /**
     * Program entry point.
     *
     * @param args Command line arguments and options.
     */
    public static void main(String[] args) {
        CommandLine.call(new StandAlone(), args);
    }

    @Override
    public Void call() {
        final ImageData image = ImageData.load(inputFile);
        final UniformRandomProvider rng = RandomSource.MWC_256.create();
        final DistanceMeasure distance = new EuclideanDistance();

        cluster(image,
                new ElkanKMeansPlusPlusClusterer<>(numClusters,
                                                   maxIter,
                                                   distance,
                                                   rng),
                "elkan");
        cluster(image,
                new KMeansPlusPlusClusterer<>(numClusters,
                                              maxIter,
                                              distance,
                                              rng),
                "kmeans");

        return null;
    }

    /**
     * Perform clustering and write results.
     *
     * @param image Input.
     * @param algo Algorithm to do the clustering.
     * @param id Identifier for output file name.
     */
    private void cluster(ImageData image,
                         Clusterer<ImageData.PixelClusterable> algo,
                         String id) {
        final String dot = ".";
        final String out = new StringBuilder()
            .append(outputPrefix)
            .append(dot)
            .append("k_")
            .append(numClusters)
            .append(dot)
            .append(id)
            .append(dot)
            .toString();

        final Instant start = Instant.now();
        image.write(algo.cluster(image.getPixels()), out);
        //CHECKSTYLE: stop all
        System.out.println("time=" + Duration.between(start, Instant.now()).toMillis());
        //CHECKSTYLE: resume all
    }
}
