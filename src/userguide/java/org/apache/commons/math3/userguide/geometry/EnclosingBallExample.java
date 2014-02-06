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
package org.apache.commons.math3.userguide.geometry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;

import org.apache.commons.math3.geometry.enclosing.Encloser;
import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import org.apache.commons.math3.geometry.enclosing.WelzlEncloser;
import org.apache.commons.math3.geometry.euclidean.twod.DiskGenerator;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.userguide.ExampleUtils;
import org.apache.commons.math3.userguide.ExampleUtils.ExampleFrame;

/**
 * Shows how to generate the convex hull of a point cloud in 2D.
 */
public class EnclosingBallExample {

    public static List<Vector2D> createRandomPoints(int size) {
        RandomGenerator random = new MersenneTwister();

        // create the cloud container
        List<Vector2D> points = new ArrayList<Vector2D>(size);
        // fill the cloud with a random distribution of points
        for (int i = 0; i < size; i++) {
            points.add(new Vector2D(random.nextDouble() - 0.5, random.nextDouble() - 0.5));
        }
        return points;
    }

    @SuppressWarnings("serial")
    public static class Display extends ExampleFrame {
        
        public Display() {
            setTitle("Commons-Math: Enclosing Ball example");
            setSize(400, 400);
            
            setLayout(new FlowLayout());

            Collection<Vector2D> cloud = createRandomPoints(150);
            Encloser<Euclidean2D, Vector2D> encloser = new WelzlEncloser<Euclidean2D, Vector2D>(1e-10, new DiskGenerator());
            EnclosingBall<Euclidean2D, Vector2D> ball = encloser.enclose(cloud);

            add(new Plot(cloud, ball));
        }
    }

    @SuppressWarnings("serial")
    public static class Plot extends JComponent {

        private static double PAD = 10;

        private Iterable<Vector2D> cloud;
        private EnclosingBall<Euclidean2D, Vector2D> ball;

        public Plot(Iterable<Vector2D> cloud, EnclosingBall<Euclidean2D, Vector2D> ball) {
            this.cloud = cloud;
            this.ball = ball;
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
            
            for (Vector2D point : cloud) {
                drawPoint(g2, point, w, h);
            }
            
            g.setColor(Color.RED);
            drawCircle(g2, ball.getCenter(), ball.getRadius(), w, h);
        }        

        private void drawPoint(Graphics2D g2, Vector2D point, int width, int height) {
            Vector2D p = transform(point, width, height);
            double[] arr = p.toArray();
            g2.draw(new Rectangle2D.Double(arr[0] - 1, arr[1] - 1, 2, 2));
        }

        public void drawCircle(Graphics2D g2, Vector2D center, double radius, int width, int height) {
            Vector2D c = transform(center, width, height);
            double[] arr = c.toArray();
            double rx = (radius) / 2 * (width - 2 * PAD);
            double ry = (radius) / 2 * (height - 2 * PAD);
            g2.draw(new Ellipse2D.Double(arr[0] - rx, arr[1] - ry, rx * 2, ry * 2));
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(300, 300);
        }

        private Vector2D transform(Vector2D point, int width, int height) {
            double[] arr = point.toArray();
            return new Vector2D(new double[] { PAD + (arr[0] + 0.5) / 2 * (width - 2 * PAD) + width / 4,
                                               height - PAD - (arr[1] + 0.5) / 2 * (height - 2 * PAD) - height / 4 });
        }
    }

    public static void main(String[] args) {
        ExampleUtils.showExampleFrame(new Display());
    }
}
