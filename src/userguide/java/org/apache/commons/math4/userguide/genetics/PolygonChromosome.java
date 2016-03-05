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
package org.apache.commons.math4.userguide.genetics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.genetics.AbstractListChromosome;
import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.InvalidRepresentationException;

/**
 * A simple chromosome representing a list of polygons.
 */
public class PolygonChromosome extends AbstractListChromosome<Polygon> {

    /** The reference image for fitness testing. */
    private static BufferedImage refImage;
    /** The image buffer used to draw the current chromosome during fitness testing. */
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

    /**
     * Return the internal representation, which is needed for our custom mutation policy.
     *
     * @return the list of polygons
     */
    public List<Polygon> getPolygonRepresentation() {
        return getRepresentation();
    }

    /**
     * Calculate the fitness function for this chromosome.
     * <p>
     * For this purpose, we first draw the polygons on the test buffer, and
     * then compare the resulting image pixel by pixel with the reference image.
     */
    public double fitness() {

        Graphics2D g2 = testImage.createGraphics();
        
        int width = testImage.getWidth();
        int height = testImage.getHeight();

        draw(g2, width, height);
        g2.dispose();

        int[] refPixels = refImage.getData().getPixels(0, 0, refImage.getWidth(), refImage.getHeight(), (int[]) null);
        int[] testPixels = testImage.getData().getPixels(0, 0, testImage.getWidth(), testImage.getHeight(), (int[]) null);

        int diff = 0;
        int p = width * height * 4 - 1; // 4 channels: rgba
        int idx = 0;

        do {
            if (idx++ % 4 != 0) { // ignore the alpha channel for fitness
                int dp = testPixels[p] - refPixels[p];
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
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        List<Polygon> polygons = getPolygonRepresentation();
        for (Polygon p : polygons) {
            p.draw(g, width, height);
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
