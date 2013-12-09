package org.apache.commons.math3.userguide.genetics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

public class PolygonChromosome extends AbstractListChromosome<Polygon> {

    private static BufferedImage refImage;
    private static BufferedImage testImage;

    public static void setRefImage(BufferedImage ref) {
        refImage = ref;
    }

    public static void setTestImage(BufferedImage image) {
        testImage = image;
    }

    public PolygonChromosome(List<Polygon> representation) {
        super(representation);
    }

    @Override
    protected void checkValidity(List<Polygon> chromosomeRepresentation) throws InvalidRepresentationException {
        // do nothing
    }

    @Override
    public AbstractListChromosome<Polygon> newFixedLengthChromosome(List<Polygon> chromosomeRepresentation) {
        return new PolygonChromosome(chromosomeRepresentation);
    }

    public List<Polygon> getPolygonRepresentation() {
        return getRepresentation();
    }

    public double fitness() {

        Graphics2D g2 = testImage.createGraphics();
        
        int width = testImage.getWidth();
        int height = testImage.getHeight();

        draw(g2, width, height);
        g2.dispose();

        int[] refPixels = refImage.getData().getPixels(0, 0, refImage.getWidth(), refImage.getHeight(), (int[]) null);
        int[] pixels = testImage.getData().getPixels(0, 0, testImage.getWidth(), testImage.getHeight(), (int[]) null);

        int diff = 0;
        int p = width * height * 4 - 1;
        int idx = 0;

        do {
            if (idx++ % 4 != 0) {
                int dp = pixels[p] - refPixels[p];
                if (dp < 0) {
                    diff -= dp;
                } else {
                    diff += dp;
                }
            }
        } while(--p > 0);

        return (1.0 - diff / (width * height * 3.0 * 256));
    }

    public void draw(Graphics2D g, int width, int height) {
        g.setBackground(Color.RED);
        g.clearRect(0, 0, width, height);

        //if (true) return;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        List<Polygon> polygons = getPolygonRepresentation();
        for (Polygon p : polygons) {
            g.setColor(new Color(p.data[0], p.data[1], p.data[2], p.data[3]));

            GeneralPath path = new GeneralPath();
            path.moveTo(p.data[4] * width, p.data[5] * height);
            
            int polygonLength = (p.data.length - 4) / 2;
            for (int j = 1; j < polygonLength; j++) {
                path.lineTo(p.data[4 + j * 2] * width, p.data[5 + j * 2] * height);
            }
            path.closePath();

            g.fill(path);
        }
    }

    @Override
    public String toString() {
        return String.format("(f=%s)", getFitness());
    }
    
    public static Chromosome randomChromosome(int polygonLength, int polygonCount) {
        List<Polygon> list = new ArrayList<Polygon>(polygonCount);
        for (int j = 0; j < polygonCount; j++) {
            list.add(Polygon.randomPolygon(polygonLength));
        }
        return new PolygonChromosome(list);
    }

}
