package org.apache.commons.math3.userguide.geometry;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.GrahamScan2D;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.userguide.ExampleUtils;
import org.apache.commons.math3.userguide.ExampleUtils.ExampleFrame;

/**
 * Plots 2D samples drawn from various pseudo / quasi-random generators.
 */
public class ConvexHullExample {

    public static List<Vector2D> createRandomPoints(int size) {
        RandomGenerator random = new MersenneTwister(10);

        // create the cloud container
        List<Vector2D> points = new ArrayList<Vector2D>(size);
        // fill the cloud with a random distribution of points
        for (int i = 0; i < size; i++) {
            points.add(new Vector2D(random.nextDouble() * 2.0 - 1.0, random.nextDouble() * 2.0 - 1.0));
        }
        return points;
    }

    @SuppressWarnings("serial")
    public static class Display extends ExampleFrame {
        
        public Display() {
            setTitle("Commons-Math: Convex Hull examples");
            setSize(400, 400);
            
            setLayout(new FlowLayout());
            
            GrahamScan2D generator = new GrahamScan2D();
            Collection<Vector2D> cloud = createRandomPoints(150);
            ConvexHull2D hull = generator.generate(cloud);

            add(new Plot(cloud, hull));
        }
    }

    @SuppressWarnings("serial")
    public static class Plot extends JComponent {

        private static double PAD = 10;

        private Iterable<Vector2D> cloud;
        private ConvexHull2D hull;

        public Plot(Iterable<Vector2D> cloud, ConvexHull2D hull) {
            this.cloud = cloud;
            this.hull = hull;
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
            Vector2D firstPoint = hull.getVertices()[0];
            Vector2D lastPoint = null;
            for (Vector2D point : hull.getVertices()) {
                drawPoint(g2, point, w, h);
                if (lastPoint != null) {
                    drawLine(g2, lastPoint, point, w, h);
                }
                lastPoint = point;
            }

            drawLine(g2, lastPoint, firstPoint, w, h);
        }        

        private void drawPoint(Graphics2D g2, Vector2D point, int width, int height) {
            Vector2D p = transform(point, width, height);
            double[] arr = p.toArray();
            g2.draw(new Rectangle2D.Double(arr[0] - 1, arr[1] - 1, 2, 2));
        }

        public void drawLine(Graphics2D g2, Vector2D point1, Vector2D point2, int width, int height) {
            Vector2D p1 = transform(point1, width, height);
            double[] arr1 = p1.toArray();
            Vector2D p2 = transform(point2, width, height);
            double[] arr2 = p2.toArray();            
            g2.draw(new Line2D.Double(arr1[0], arr1[1], arr2[0], arr2[1]));
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(300, 300);
        }

        private Vector2D transform(Vector2D point, int width, int height) {
            double[] arr = point.toArray();
            return new Vector2D(new double[] { PAD + (arr[0] + 1) / 2.0 * (width - 2 * PAD),
                                               height - PAD - (arr[1] + 1) / 2.0 * (height - 2 * PAD) });
        }
    }

    public static void main(String[] args) {
        ExampleUtils.showExampleFrame(new Display());
    }
}
