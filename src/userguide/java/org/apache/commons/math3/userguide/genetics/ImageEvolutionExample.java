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
package org.apache.commons.math3.userguide.genetics;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math3.genetics.UniformCrossover;
import org.apache.commons.math3.userguide.ExampleUtils;
import org.apache.commons.math3.userguide.ExampleUtils.ExampleFrame;

/**
 * This example shows a more advanced use of a genetic algorithm: approximate a raster image
 * with ~100 semi-transparent polygons of length 6.
 * <p>
 * The fitness function is quite simple yet expensive to compute:
 * 
 *   - draw the polygons of a chromosome to an image
 *   - compare each pixel with the corresponding reference image
 * <p>
 * To improve the speed of the calculation, we calculate the fitness not on the original image size,
 * but rather on a scaled down version, which is sufficient to demonstrate the power of such a genetic algorithm.
 * <p>
 * TODO:
 *  - improve user interface
 *    - make algorithm parameters configurable
 *    - add a gallery of results after x iterations / minutes (either automatic or based on button click)
 *    - allow loading / selection of other images
 *    - add logging in the user interface, e.g. number of generations, time spent, ...
 * 
 * @see <a href="http://www.nihilogic.dk/labs/evolving-images/">Evolving Images with JavaScript and canvas (Nihilogic)</a>
 */
@SuppressWarnings("serial")
public class ImageEvolutionExample {

    public static final int   POPULATION_SIZE  = 40;
    public static final int   TOURNAMENT_ARITY = 5;
    public static final float MUTATION_RATE    = 0.02f;
    public static final float MUTATION_CHANGE  = 0.1f;

    public static final int POLYGON_LENGTH = 6;
    public static final int POLYGON_COUNT = 100;

    public static class Display extends ExampleFrame {
        
        private GeneticAlgorithm ga;
        private Population currentPopulation;
        private Chromosome bestFit;

        private Thread internalThread;
        private volatile boolean noStopRequested;

        private BufferedImage ref;
        
        private BufferedImage referenceImage;
        private BufferedImage testImage;
        
        private ImagePainter painter;

        public Display() throws Exception {
            setTitle("Commons-Math: Image Evolution Example");
            setSize(600, 400);
            
            setLayout(new FlowLayout());

            Box bar = Box.createHorizontalBox();

            ref = ImageIO.read(new File("resources/monalisa.png"));
            //ref = ImageIO.read(new File("resources/feather-small.gif"));

            referenceImage = resizeImage(ref, 50, 50, BufferedImage.TYPE_INT_ARGB);
            testImage = new BufferedImage(referenceImage.getWidth(), referenceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

            JLabel picLabel = new JLabel(new ImageIcon(ref));
            bar.add(picLabel);

            painter = new ImagePainter(ref.getWidth(), ref.getHeight());
            bar.add(painter);

            // set the images used for calculating the fitness function:
            //   refImage  - the reference image
            //   testImage - the test image to draw the current chromosome
            PolygonChromosome.setRefImage(referenceImage);
            PolygonChromosome.setTestImage(testImage);

            add(bar);

            JButton startButton = new JButton("Start");
            startButton.setActionCommand("start");
            add(startButton);

            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (isAlive()) {
                        stopRequest();
                    } else {
                        startEvolution();
                    }
                }
            });

            // initialize a new genetic algorithm
            ga = new GeneticAlgorithm(new UniformCrossover<Polygon>(0.5), 1.0,
                                      new RandomPolygonMutation(MUTATION_RATE, MUTATION_CHANGE), 1.0,
                                      new TournamentSelection(TOURNAMENT_ARITY));

            // initial population
            currentPopulation = getInitialPopulation();
            bestFit = currentPopulation.getFittestChromosome();
        }
        
        public boolean isAlive() {
            return internalThread != null && internalThread.isAlive();
        }

        public void stopRequest() {
            noStopRequested = false;
            internalThread.interrupt();
            internalThread = null;
        }

        public void startEvolution() {
            noStopRequested = true;
            Runnable r = new Runnable() {
                public void run() {
                    int evolution = 0;
                    while (noStopRequested) {
                        currentPopulation = ga.nextGeneration(currentPopulation);

                        System.out.println("generation: " + evolution++ + ": " + bestFit.toString());
                        bestFit = currentPopulation.getFittestChromosome();

                        painter.repaint();
                    }
                }
            };

            internalThread = new Thread(r);
            internalThread.start();
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
                PolygonChromosome chromosome = (PolygonChromosome) bestFit;
                chromosome.draw((Graphics2D) g, ref.getWidth(), ref.getHeight());
            }

        }

    }

    public static void main(String[] args) throws Exception {
        ExampleUtils.showExampleFrame(new Display());
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type) throws IOException {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private static Population getInitialPopulation() {
        List<Chromosome> popList = new LinkedList<Chromosome>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            popList.add(PolygonChromosome.randomChromosome(POLYGON_LENGTH, POLYGON_COUNT));
        }
        return new ElitisticListPopulation(popList, popList.size(), 0.25);
    }

}
