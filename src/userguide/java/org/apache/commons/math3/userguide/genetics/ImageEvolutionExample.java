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
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math3.genetics.UniformCrossover;

/**
 * Based on http://www.nihilogic.dk/labs/evolving-images/
 */
@SuppressWarnings("serial")
public class ImageEvolutionExample extends JComponent {

    public static final int   POPULATION_SIZE  = 40;
    public static final int   TOURNAMENT_ARITY = 2;
    public static final float MUTATION_RATE    = 0.02f;
    public static final float MUTATION_CHANGE  = 0.1f;

    public static final int POLYGON_LENGTH = 6;
    public static final int POLYGON_COUNT = 100;

    private GeneticAlgorithm ga;
    private Population currentPopulation;
    private Chromosome bestFit;

    private Thread internalThread;
    private volatile boolean noStopRequested;

    private BufferedImage ref;
    private BufferedImage buf;
    
    private ImagePainter painter;

    public ImageEvolutionExample() throws Exception {
        super();
        setLayout(new FlowLayout());

        Box bar = Box.createHorizontalBox();

        ref = ImageIO.read(new File("resources/canvas_small.png"));

        JLabel picLabel = new JLabel(new ImageIcon(ref));
        bar.add(picLabel);

        painter = new ImagePainter(ref);
        bar.add(painter);

        buf = new BufferedImage(ref.getWidth(), ref.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        // TODO: improve this
        PolygonChromosome.setRefImage(ref);
        PolygonChromosome.setTestImage(buf);

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

    private Population getInitialPopulation() {
        List<Chromosome> popList = new LinkedList<Chromosome>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            popList.add(PolygonChromosome.randomChromosome(POLYGON_LENGTH, POLYGON_COUNT));
        }
        return new ElitisticListPopulation(popList, popList.size(), 0.25);
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
                try {
                    double lastBestFit = Double.MIN_VALUE;
                    int evolution = 0;
                    while (noStopRequested) {
                        currentPopulation = ga.nextGeneration(currentPopulation);

                        System.out.println("generation: " + evolution++ + ": " + bestFit.toString());
                        bestFit = currentPopulation.getFittestChromosome();

                        if (lastBestFit > bestFit.getFitness()) {
                            System.out.println("gotcha");
                        }
                        lastBestFit = bestFit.getFitness();
                        painter.repaint();
                    }
                } catch (Exception x) {
                    // in case ANY exception slips through
                    x.printStackTrace();
                }
            }
        };

        internalThread = new Thread(r);
        internalThread.start();
    }

    private static void createAndShowGUI() throws Exception {
        // Create and set up the window.
        JFrame frame = new JFrame("Image Evolution Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(new ImageEvolutionExample());

        // Display the window.
        frame.pack();
        frame.setSize(new Dimension(500, 300));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class ImagePainter extends Component {
        BufferedImage ref;

        public ImagePainter(final BufferedImage ref) {
            this.ref = ref;
        }

        public Dimension getPreferredSize() {
            return new Dimension(ref.getWidth(), ref.getHeight());
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
