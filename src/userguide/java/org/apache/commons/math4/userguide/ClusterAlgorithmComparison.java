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
package org.apache.commons.math4.userguide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.sampling.ListSampler;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformContinuousDistribution;
import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.geometry.euclidean.twod.Vector2D;

import org.apache.commons.math4.ml.clustering.Cluster;
import org.apache.commons.math4.ml.clustering.Clusterable;
import org.apache.commons.math4.ml.clustering.Clusterer;
import org.apache.commons.math4.ml.clustering.DBSCANClusterer;
import org.apache.commons.math4.ml.clustering.DoublePoint;
import org.apache.commons.math4.ml.clustering.FuzzyKMeansClusterer;
import org.apache.commons.math4.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math4.random.SobolSequenceGenerator;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Pair;
import org.apache.commons.math4.userguide.ExampleUtils.ExampleFrame;

/**
 * Plots clustering results for various algorithms and datasets.
 * Based on
 * <a href="http://scikit-learn.org/stable/auto_examples/cluster/plot_cluster_comparison.html">scikit learn</a>.
 */
public class ClusterAlgorithmComparison {

    public static List<Vector2D> makeCircles(int samples,
                                             boolean shuffle,
                                             double noise,
                                             double factor,
                                             UniformRandomProvider rng) {
        if (factor < 0 || factor > 1) {
            throw new IllegalArgumentException();
        }

        ContinuousDistribution.Sampler dist = new NormalDistribution(0.0, noise).createSampler(rng);

        List<Vector2D> points = new ArrayList<>();
        double range = 2.0 * FastMath.PI;
        double step = range / (samples / 2.0 + 1);
        for (double angle = 0; angle < range; angle += step) {
            Vector2D outerCircle = Vector2D.of(FastMath.cos(angle), FastMath.sin(angle));
            Vector2D innerCircle = outerCircle.multiply(factor);

            points.add(outerCircle.add(generateNoiseVector(dist)));
            points.add(innerCircle.add(generateNoiseVector(dist)));
        }

        if (shuffle) {
            ListSampler.shuffle(rng, points);
        }

        return points;
    }

    public static List<Vector2D> makeMoons(int samples,
                                           boolean shuffle,
                                           double noise,
                                           UniformRandomProvider rng) {
        ContinuousDistribution.Sampler dist = new NormalDistribution(0.0, noise).createSampler(rng);

        int nSamplesOut = samples / 2;
        int nSamplesIn = samples - nSamplesOut;

        List<Vector2D> points = new ArrayList<>();
        double range = FastMath.PI;
        double step = range / (nSamplesOut / 2.0);
        for (double angle = 0; angle < range; angle += step) {
            Vector2D outerCircle = Vector2D.of(FastMath.cos(angle), FastMath.sin(angle));
            points.add(outerCircle.add(generateNoiseVector(dist)));
        }

        step = range / (nSamplesIn / 2.0);
        for (double angle = 0; angle < range; angle += step) {
            Vector2D innerCircle = Vector2D.of(1 - FastMath.cos(angle), 1 - FastMath.sin(angle) - 0.5);
            points.add(innerCircle.add(generateNoiseVector(dist)));
        }

        if (shuffle) {
            ListSampler.shuffle(rng, points);
        }

        return points;
    }

    public static List<Vector2D> makeBlobs(int samples,
                                           int centers,
                                           double clusterStd,
                                           double min,
                                           double max,
                                           boolean shuffle,
                                           UniformRandomProvider rng) {
        ContinuousDistribution.Sampler uniform = new UniformContinuousDistribution(min, max).createSampler(rng);
        ContinuousDistribution.Sampler gauss = new NormalDistribution(0.0, clusterStd).createSampler(rng);

        Vector2D[] centerPoints = new Vector2D[centers];
        for (int i = 0; i < centers; i++) {
            centerPoints[i] = Vector2D.of(uniform.sample(), uniform.sample());
        }

        int[] nSamplesPerCenter = new int[centers];
        int count = samples / centers;
        Arrays.fill(nSamplesPerCenter, count);

        for (int i = 0; i < samples % centers; i++) {
            nSamplesPerCenter[i]++;
        }

        List<Vector2D> points = new ArrayList<>();
        for (int i = 0; i < centers; i++) {
            for (int j = 0; j < nSamplesPerCenter[i]; j++) {
                points.add(centerPoints[i].add(generateNoiseVector(gauss)));
            }
        }

        if (shuffle) {
            ListSampler.shuffle(rng, points);
        }

        return points;
    }

    public static List<Vector2D> makeRandom(int samples) {
        SobolSequenceGenerator generator = new SobolSequenceGenerator(2);
        generator.skipTo(999999);
        List<Vector2D> points = new ArrayList<>();
        for (double i = 0; i < samples; i++) {
            double[] vector = generator.nextVector();
            vector[0] = vector[0] * 2 - 1;
            vector[1] = vector[1] * 2 - 1;
            Vector2D point = Vector2D.of(vector);
            points.add(point);
        }

        return points;
    }

    public static Vector2D generateNoiseVector(ContinuousDistribution.Sampler distribution) {
        return Vector2D.of(distribution.sample(), distribution.sample());
    }

    public static List<DoublePoint> normalize(final List<Vector2D> input,
                                              double minX,
                                              double maxX,
                                              double minY,
                                              double maxY) {
        double rangeX = maxX - minX;
        double rangeY = maxY - minY;
        List<DoublePoint> points = new ArrayList<>();
        for (Vector2D p : input) {
            double[] arr = p.toArray();
            arr[0] = (arr[0] - minX) / rangeX * 2 - 1;
            arr[1] = (arr[1] - minY) / rangeY * 2 - 1;
            points.add(new DoublePoint(arr));
        }
        return points;
    }

    @SuppressWarnings("serial")
    public static class Display extends ExampleFrame {

        public Display() {
            setTitle("Commons-Math: Cluster algorithm comparison");
            setSize(800, 800);

            setLayout(new GridBagLayout());

            int nSamples = 1500;

            final long seed = RandomSource.createLong(); // Random seed.
            UniformRandomProvider rng = RandomSource.create(RandomSource.WELL_19937_C, seed);
            List<List<DoublePoint>> datasets = new ArrayList<List<DoublePoint>>();

            datasets.add(normalize(makeCircles(nSamples, true, 0.04, 0.5, rng), -1, 1, -1, 1));
            datasets.add(normalize(makeMoons(nSamples, true, 0.04, rng), -1, 2, -1, 1));
            datasets.add(normalize(makeBlobs(nSamples, 3, 1.0, -10, 10, true, rng), -12, 12, -12, 12));
            datasets.add(normalize(makeRandom(nSamples), -1, 1, -1, 1));

            List<Pair<String, Clusterer<DoublePoint>>> algorithms = new ArrayList<>();

            algorithms.add(new Pair<String, Clusterer<DoublePoint>>("KMeans\n(k=2)",
                                                                    new KMeansPlusPlusClusterer<>(2)));
            algorithms.add(new Pair<String, Clusterer<DoublePoint>>("KMeans\n(k=3)",
                                                                    new KMeansPlusPlusClusterer<>(3)));
            algorithms.add(new Pair<String, Clusterer<DoublePoint>>("FuzzyKMeans\n(k=3, fuzzy=2)",
                                                                    new FuzzyKMeansClusterer<>(3, 2)));
            algorithms.add(new Pair<String, Clusterer<DoublePoint>>("FuzzyKMeans\n(k=3, fuzzy=10)",
                                                                    new FuzzyKMeansClusterer<>(3, 10)));
            algorithms.add(new Pair<String, Clusterer<DoublePoint>>("DBSCAN\n(eps=.1, min=3)",
                                                                    new DBSCANClusterer<>(0.1, 3)));

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.VERTICAL;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(2, 2, 2, 2);

            for (Pair<String, Clusterer<DoublePoint>> pair : algorithms) {
                JLabel text = new JLabel("<html><body>" + pair.getFirst().replace("\n", "<br>"));
                add(text, c);
                c.gridx++;
            }
            c.gridy++;

            for (List<DoublePoint> dataset : datasets) {
                c.gridx = 0;
                for (Pair<String, Clusterer<DoublePoint>> pair : algorithms) {
                    long start = System.currentTimeMillis();
                    List<? extends Cluster<DoublePoint>> clusters = pair.getSecond().cluster(dataset);
                    long end = System.currentTimeMillis();
                    add(new ClusterPlot(clusters, end - start), c);
                    c.gridx++;
                }
                c.gridy++;
            }
        }
    }

    @SuppressWarnings("serial")
    public static class ClusterPlot extends JComponent {

        private static double PAD = 10;

        private List<? extends Cluster<DoublePoint>> clusters;
        private long duration;

        public ClusterPlot(final List<? extends Cluster<DoublePoint>> clusters, long duration) {
            this.clusters = clusters;
            this.duration = duration;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.clearRect(0, 0, w, h);

            g2.setPaint(Color.black);
            g2.drawRect(0, 0, w - 1, h - 1);

            int index = 0;
            Color[] colors = new Color[] { Color.red, Color.blue, Color.green.darker() };
            for (Cluster<DoublePoint> cluster : clusters) {
                g2.setPaint(colors[index++]);
                for (DoublePoint point : cluster.getPoints()) {
                    Clusterable p = transform(point, w, h);
                    double[] arr = p.getPoint();
                    g2.fill(new Ellipse2D.Double(arr[0] - 1, arr[1] - 1, 3, 3));
                }

                Clusterable p = transform(cluster.centroid(), w, h);
                double[] arr = p.getPoint();
                Shape s = new Ellipse2D.Double(arr[0] - 4, arr[1] - 4, 8, 8);
                g2.fill(s);
                g2.setPaint(Color.black);
                g2.draw(s);
            }

            g2.setPaint(Color.black);
            g2.drawString(String.format("%.2f s", duration / 1e3), w - 40, h - 5);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(150, 150);
        }

        private Clusterable transform(Clusterable point, int width, int height) {
            double[] arr = point.getPoint();
            return new DoublePoint(new double[] { PAD + (arr[0] + 1) / 2.0 * (width - 2 * PAD),
                                                  height - PAD - (arr[1] + 1) / 2.0 * (height - 2 * PAD) });
        }
    }

    public static void main(String[] args) {
        ExampleUtils.showExampleFrame(new Display());
    }
}
