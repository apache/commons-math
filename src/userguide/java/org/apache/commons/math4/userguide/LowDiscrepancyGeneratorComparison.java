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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.random.HaltonSequenceGenerator;
import org.apache.commons.math4.random.RandomVectorGenerator;
import org.apache.commons.math4.random.SobolSequenceGenerator;
import org.apache.commons.math4.random.UncorrelatedRandomVectorGenerator;
import org.apache.commons.math4.random.UniformRandomGenerator;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Pair;
import org.apache.commons.math4.userguide.ExampleUtils.ExampleFrame;

/**
 * Plots 2D samples drawn from various pseudo / quasi-random generators.
 */
public class LowDiscrepancyGeneratorComparison {

    public static List<Vector2D> makeCircle(int samples, final RandomVectorGenerator generator) {
        List<Vector2D> points = new ArrayList<>();
        for (double i = 0; i < samples; i++) {
            double[] vector = generator.nextVector();
            Vector2D point = Vector2D.of(vector);
            points.add(point);
        }

        // normalize points first
        points = normalize(points);

        // now test if the sample is within the unit circle
        List<Vector2D> circlePoints = new ArrayList<>();
        for (Vector2D p : points) {
            double criteria = FastMath.pow(p.getX(), 2) + FastMath.pow(p.getY(), 2);
            if (criteria < 1.0) {
                circlePoints.add(p);
            }
        }

        return circlePoints;
    }

    public static List<Vector2D> makeRandom(int samples, RandomVectorGenerator generator) {
        List<Vector2D> points = new ArrayList<>();
        for (double i = 0; i < samples; i++) {
            double[] vector = generator.nextVector();
            Vector2D point = Vector2D.of(vector);
            points.add(point);
        }

        return normalize(points);
    }

    public static List<Vector2D> normalize(final List<Vector2D> input) {
        // find the mininum and maximum x value in the dataset
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        for (Vector2D p : input) {
            minX = FastMath.min(minX, p.getX());
            maxX = FastMath.max(maxX, p.getX());
        }

        double minY, maxY;

        // use the minimum to detect if we either have input values in the range [0, 1] or [-sqrt(3), sqrt(3)]
        if (FastMath.abs(minX) < 0.1) {
            minX = minY = 0.0;
            maxX = maxY = 1.0;
        } else {
            minX = minY = -FastMath.sqrt(3);
            maxX = maxY = FastMath.sqrt(3);
        }

        double rangeX = maxX - minX;
        double rangeY = maxY - minY;
        List<Vector2D> points = new ArrayList<>();
        for (Vector2D p : input) {
            double[] arr = p.toArray();
            // normalize to the range [-1, 1]
            arr[0] = (arr[0] - minX) / rangeX * 2 - 1;
            arr[1] = (arr[1] - minY) / rangeY * 2 - 1;
            points.add(Vector2D.of(arr));
        }
        return points;
    }

    @SuppressWarnings("serial")
    public static class Display extends ExampleFrame {

        public Display() {
            setTitle("Commons-Math: Pseudo/Quasi-random examples");
            setSize(800, 800);

            setLayout(new GridBagLayout());

            int[] datasets = new int[] { 256, 1000, 2500, 1000 };
            List<Pair<String, RandomVectorGenerator>> generators = new ArrayList<Pair<String, RandomVectorGenerator>>();

            generators.add(new Pair<>("Uncorrelated\nUniform(JDK)",
                                      new UncorrelatedRandomVectorGenerator(2, new UniformRandomGenerator(RandomSource.create(RandomSource.JDK)))));
            generators.add(new Pair<>("Independent\nRandom(MT)", new RandomVectorGenerator() {

                final UniformRandomProvider[] rngs = new UniformRandomProvider[] {
                    RandomSource.create(RandomSource.MT, 123456789),
                    RandomSource.create(RandomSource.MT, 987654321)
                };

                public double[] nextVector() {
                    final double[] vector = new double[2];
                    vector[0] = rngs[0].nextDouble();
                    vector[1] = rngs[1].nextDouble();
                    return vector;
                }

            }));
            generators.add(new Pair<>("HaltonSequence", new HaltonSequenceGenerator(2)));
            generators.add(new Pair<>("SobolSequence", new SobolSequenceGenerator(2)));

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.VERTICAL;
            c.gridx = 1;
            c.gridy = 0;
            c.insets = new Insets(2, 2, 2, 2);

            for (Pair<String, RandomVectorGenerator> pair : generators) {
                JTextArea text = new JTextArea(pair.getFirst());
                text.setEditable(false);
                text.setOpaque(false);
                add(text, c);
                c.gridx++;
            }
            int saveY = ++c.gridy;

            c.gridx = 0;
            for (int type = 0; type < 4; type++) {
                JLabel text = new JLabel("n=" + String.valueOf(datasets[type]));
                text.setOpaque(false);
                add(text, c);
                c.gridy++;
            }

            c.gridy = saveY;
            for (int type = 0; type < 4; type++) {
                c.gridx = 1;

                for (Pair<String, RandomVectorGenerator> pair : generators) {
                    List<Vector2D> points = null;
                    int samples = datasets[type];
                    switch (type) {
                        case 0:
                            points = makeRandom(samples, pair.getValue());
                            break;
                        case 1:
                            points = makeRandom(samples, pair.getValue());
                            break;
                        case 2:
                            points = makeRandom(samples, pair.getValue());
                            break;
                        case 3:
                            points = makeCircle(samples, pair.getValue());
                            break;
                    }
                    add(new Plot(points), c);
                    c.gridx++;
                }

                c.gridy++;
            }
        }
    }

    @SuppressWarnings("serial")
    public static class Plot extends JComponent {

        private static double PAD = 10;

        private List<Vector2D> points;

        public Plot(final List<Vector2D> points) {
            this.points = points;
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

            for (Vector2D point : points) {
                Vector2D p = transform(point, w, h);
                double[] arr = p.toArray();
                g2.draw(new Rectangle2D.Double(arr[0] - 1, arr[1] - 1, 2, 2));
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(140, 140);
        }

        private Vector2D transform(Vector2D point, int width, int height) {
            double[] arr = point.toArray();
            return Vector2D.of(PAD + (arr[0] + 1) / 2.0 * (width - 2 * PAD),
                               height - PAD - (arr[1] + 1) / 2.0 * (height - 2 * PAD));
        }
    }

    public static void main(String[] args) {
        ExampleUtils.showExampleFrame(new Display());
    }
}
