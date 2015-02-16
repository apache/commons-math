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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.commons.math3.geometry.enclosing.Encloser;
import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import org.apache.commons.math3.geometry.enclosing.WelzlEncloser;
import org.apache.commons.math3.geometry.euclidean.twod.DiskGenerator;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHullGenerator2D;
import org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.userguide.ExampleUtils;
import org.apache.commons.math3.userguide.ExampleUtils.ExampleFrame;
import org.apache.commons.math3.util.FastMath;
import org.piccolo2d.PCamera;
import org.piccolo2d.PCanvas;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PMouseWheelZoomEventHandler;
import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;

/**
 * Simple example illustrating some parts of the geometry package.
 * 
 * TODO: 
 *  - select tolerance level
 *  - allow editing of the point set
 */
public class GeometryExample {

    public static List<Vector2D> createRandomPoints(int size) {
        RandomGenerator random = new MersenneTwister();

        // create the cloud container
        List<Vector2D> points = new ArrayList<Vector2D>(size);
        // fill the cloud with a random distribution of points
        for (int i = 0; i < size; i++) {
            points.add(new Vector2D(FastMath.round(random.nextDouble() * 400 + 100),
                    FastMath.round(random.nextDouble() * 400 + 100)));
        }
        
        return points;
    }

    public static List<Vector2D> createCircle(int samples) {
        List<Vector2D> points = new ArrayList<Vector2D>();
        final Vector2D center = new Vector2D(300, 300);
        double range = 2.0 * FastMath.PI;
        double step = range / (samples + 1);
        for (double angle = 0; angle < range; angle += step) {
            Vector2D circle = new Vector2D(FastMath.cos(angle), FastMath.sin(angle));
            points.add(circle.scalarMultiply(200).add(center));
        }
        
        return points;
    }

    public static List<Vector2D> createCross() {
        List<Vector2D> points = new ArrayList<Vector2D>();
        
        for (int i = 100; i < 500; i += 10) {
            points.add(new Vector2D(300, i));
            points.add(new Vector2D(i, 300));
        }

        return points;
    }

    public static PCanvas createCanvas() {
        final PCanvas canvas = new PCanvas();
        final PCamera camera = canvas.getCamera();
        
        final PText tooltipNode = new PText();
        tooltipNode.setPickable(false);
        camera.addChild(tooltipNode);

        camera.addInputEventListener(new PBasicInputEventHandler() {
            public void mouseMoved(final PInputEvent event) {
                updateToolTip(event);
            }

            public void mouseDragged(final PInputEvent event) {
                updateToolTip(event);
            }

            public void updateToolTip(final PInputEvent event) {
                final PNode n = event.getPickedNode();
                final Object object = (Object) n.getAttribute("tooltip");
                if (object != null) {
                    final String tooltipString = object.toString();
                    final Point2D p = event.getCanvasPosition();

                    event.getPath().canvasToLocal(p, camera);

                    tooltipNode.setText(tooltipString);
                    tooltipNode.setOffset(p.getX() + 8, p.getY() - 8);
                } else {
                    tooltipNode.setText(null);
                }
            }
        });

        // uninstall default zoom event handler
        canvas.removeInputEventListener(canvas.getZoomEventHandler());

        // install mouse wheel zoom event handler
        final PMouseWheelZoomEventHandler mouseWheelZoomEventHandler = new PMouseWheelZoomEventHandler();
        canvas.addInputEventListener(mouseWheelZoomEventHandler);

        return canvas;
    }

    @SuppressWarnings("serial")
    public static class Display extends ExampleFrame {

        private List<Vector2D> points;
        private PCanvas canvas;
        private JComponent container;
        private JComponent controlPanel;

        public Display() {
            setTitle("Commons Math: Geometry Examples");
            setSize(800, 700);

            container = new JPanel(new BorderLayout());
            canvas = createCanvas();
            container.add(canvas);
            container.setBorder(BorderFactory.createLineBorder(Color.black, 1));

            controlPanel = new JPanel();
            JButton random = new JButton("Randomize");
            controlPanel.add(random);

            random.addActionListener(new ActionListener() {

//                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.getLayer().removeAllChildren();
                    
                    points = createRandomPoints(1000);
                    paintConvexHull();
                }
            });

            JButton circle = new JButton("Circle");
            controlPanel.add(circle);

            circle.addActionListener(new ActionListener() {

//                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.getLayer().removeAllChildren();
                    
                    points = createCircle(100);
                    paintConvexHull();
                }
            });

            JButton cross = new JButton("Cross");
            controlPanel.add(cross);

            cross.addActionListener(new ActionListener() {

//                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.getLayer().removeAllChildren();
                    
                    points = createCross();
                    paintConvexHull();
                }
            });

            JSplitPane splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, container, controlPanel);
            splitpane.setDividerLocation(600);

            add(splitpane);
            
            points = createRandomPoints(1000);
            paintConvexHull();
        }

        @Override
        public Component getMainPanel() {
            return container;
        }
        
        public void paintConvexHull() {
            PNode pointSet = new PNode();
            for (Vector2D point : points) {
                final PNode node = PPath.createEllipse(point.getX() - 1, point.getY() - 1, 2, 2);
                node.addAttribute("tooltip", point);
                node.setPaint(Color.gray);
                pointSet.addChild(node);
            }

            canvas.getLayer().addChild(pointSet);

            ConvexHullGenerator2D generator = new MonotoneChain(true, 1e-6);
            ConvexHull2D hull = generator.generate(points); //AklToussaintHeuristic.reducePoints(points));

            PNode hullNode = new PNode();
            for (Vector2D vertex : hull.getVertices()) {
                final PPath node = PPath.createEllipse(vertex.getX() - 1, vertex.getY() - 1, 2, 2);
                node.addAttribute("tooltip", vertex);
                node.setPaint(Color.red);
                node.setStrokePaint(Color.red);
                hullNode.addChild(node);
            }

            for (Segment line : hull.getLineSegments()) {
                final PPath node = PPath.createLine(line.getStart().getX(), line.getStart().getY(),
                                                    line.getEnd().getX(), line.getEnd().getY());
                node.setPickable(false);
                node.setPaint(Color.red);
                node.setStrokePaint(Color.red);
                hullNode.addChild(node);
            }

            canvas.getLayer().addChild(hullNode);

            Encloser<Euclidean2D, Vector2D> encloser =
                    new WelzlEncloser<Euclidean2D, Vector2D>(1e-10, new DiskGenerator());
            EnclosingBall<Euclidean2D, Vector2D> ball = encloser.enclose(points);

            final double radius = ball.getRadius();
            PPath ballCenter =
                    PPath.createEllipse(ball.getCenter().getX() - 1, ball.getCenter().getY() - 1, 2, 2);
            ballCenter.setStrokePaint(Color.blue);
            ballCenter.setPaint(Color.blue);
            canvas.getLayer().addChild(0, ballCenter);

            PPath ballNode =
                    PPath.createEllipse(ball.getCenter().getX() - radius, ball.getCenter().getY() - radius,
                                        radius * 2, radius * 2);
            ballNode.setTransparency(1.0f);
            ballNode.setStrokePaint(Color.blue);
            canvas.getLayer().addChild(0, ballNode);
        }
    }

    public static void main(final String[] argv) {
        ExampleUtils.showExampleFrame(new Display());
    }
}
