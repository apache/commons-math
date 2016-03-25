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
package org.apache.commons.math4.userguide.clustering;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.math4.ml.clustering.CentroidCluster;
import org.apache.commons.math4.ml.clustering.Clusterable;
import org.apache.commons.math4.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math4.userguide.ExampleUtils;
import org.apache.commons.math4.userguide.ExampleUtils.ExampleFrame;

/**
 * This example shows how clustering can be applied to images.
 */
@SuppressWarnings("serial")
public class ImageClusteringExample {

    public static class Display extends ExampleFrame {
        
        private BufferedImage referenceImage;
        private BufferedImage clusterImage;
        
        private Raster referenceRaster;
        
        private ImagePainter painter;
        
        private JSpinner clusterSizeSpinner;

        public Display() throws Exception {
            setTitle("Commons-Math: Image Clustering Example");
            setSize(900, 350);
            
            setLayout(new FlowLayout());

            Box bar = Box.createHorizontalBox();

            referenceImage = ExampleUtils.resizeImage(
                    ImageIO.read(new File("resources/ColorfulBird.jpg")),
                    350,
                    240,
                    BufferedImage.TYPE_INT_RGB);

            referenceRaster = referenceImage.getData();

            clusterImage = new BufferedImage(referenceImage.getWidth(),
                                             referenceImage.getHeight(),
                                             BufferedImage.TYPE_INT_RGB);

            JLabel picLabel = new JLabel(new ImageIcon(referenceImage));
            bar.add(picLabel);

            painter = new ImagePainter(clusterImage.getWidth(), clusterImage.getHeight());
            bar.add(painter);

            JPanel controlBox = new JPanel();
            controlBox.setLayout(new GridLayout(5, 1));
            controlBox.setBorder(BorderFactory.createLineBorder(Color.black, 1));
            
            JPanel sizeBox = new JPanel();
            JLabel sizeLabel = new JLabel("Clusters:");
            sizeBox.add(sizeLabel);
            
            SpinnerNumberModel model = new SpinnerNumberModel(3, 2, 10, 1);
            clusterSizeSpinner = new JSpinner(model);
            
            sizeLabel.setLabelFor(clusterSizeSpinner);
            sizeBox.add(clusterSizeSpinner);
            controlBox.add(sizeBox, BorderLayout.NORTH);

            JButton startButton = new JButton("Cluster");
            startButton.setActionCommand("cluster");
            controlBox.add(startButton, BorderLayout.CENTER);

            bar.add(controlBox);

            add(bar);

            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clusterImage();
                }
            });
        }
        
        private void clusterImage() {
            List<PixelClusterable> pixels = new ArrayList<PixelClusterable>();
            for (int row = 0; row < referenceImage.getHeight(); row++) {
                for (int col = 0; col < referenceImage.getWidth(); col++) {
                    pixels.add(new PixelClusterable(col, row));
                }
            }
            
            int clusterSize = ((Number) clusterSizeSpinner.getValue()).intValue();
            KMeansPlusPlusClusterer<PixelClusterable> clusterer =
                    new KMeansPlusPlusClusterer<PixelClusterable>(clusterSize);
            List<CentroidCluster<PixelClusterable>> clusters = clusterer.cluster(pixels);
            
            WritableRaster raster = clusterImage.getRaster();
            for (CentroidCluster<PixelClusterable> cluster : clusters) {
                double[] color = cluster.getCenter().getPoint();
                for (PixelClusterable pixel : cluster.getPoints()) {
                    raster.setPixel(pixel.x, pixel.y, color);
                }
            }
            
            Display.this.repaint();
        }

        private class PixelClusterable implements Clusterable {
            
            private final int x;
            private final int y;
            private double[] color;
            
            public PixelClusterable(int x, int y) {
                this.x = x;
                this.y = y;
                this.color = null;
            }

            @Override
            public double[] getPoint() {
                if (color == null) {
                    color = referenceRaster.getPixel(x, y, (double[]) null);
                }
                return color;
            }

        }

        private class ImagePainter extends Component {
            
            private int width;
            private int height;

            public ImagePainter(int width, int height) {
                this.width = width;
                this.height = height;
            }

            public Dimension getPreferredSize() {
                return new Dimension(width, height);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            public void paint(Graphics g) {
                g.drawImage(clusterImage, 0, 0, this);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ExampleUtils.showExampleFrame(new Display());
    }
}
