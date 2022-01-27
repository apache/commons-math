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

package org.apache.commons.math4.examples.kmeans.image;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.math4.legacy.ml.clustering.Clusterable;
import org.apache.commons.math4.legacy.ml.clustering.Cluster;

/**
 * Retrieve pixel contents from an image file.
 */
final class ImageData {
    /** Image data. */
    private final Raster data;
    /** Pixel dataset. */
    private final List<PixelClusterable> pixels = new ArrayList<>();

    /**
     * @param image Image.
     */
    private ImageData(BufferedImage image) {
        data = image.getData();

        // Build dataset.
        for (int row = 0; row < image.getHeight(); row++) {
            for (int col = 0; col < image.getWidth(); col++) {
                pixels.add(new PixelClusterable(col, row));
            }
        }
    }

    /**
     * Load from file.
     *
     * @param file Graphics file.
     * @return a new instance.
     */
    static ImageData load(String file) {
        try {
            return new ImageData(Imaging.getBufferedImage(new File(file)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ImageReadException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create clustered image and write it to a file in PNG format.
     *
     * @param clusters Clusters.
     * @param outputPrefix Prefix of the output file.
     * Graphics format extension will be appended.
     */
    void write(List<? extends Cluster<PixelClusterable>> clusters,
               String outputPrefix) {
        final BufferedImage imageC = new BufferedImage(data.getWidth(),
                                                       data.getHeight(),
                                                       BufferedImage.TYPE_INT_RGB);

        final WritableRaster raster = imageC.getRaster();

        for (Cluster<PixelClusterable> cluster : clusters) {
            final double[] color = cluster.centroid().getPoint();
            for (PixelClusterable pixel : cluster.getPoints()) {
                raster.setPixel(pixel.x, pixel.y, color);
            }
        }

        try {
            final ImageFormat format = ImageFormats.PNG;
            Imaging.writeImage(imageC,
                               new File(outputPrefix + format.getDefaultExtension()),
                               format);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ImageWriteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the dataset.
     */
    Collection<PixelClusterable> getPixels() {
        return Collections.unmodifiableCollection(pixels);
    }

    /**
     * Bridge that presents a pixel as clusterable data.
     * Instances are mutable; they keep a reference to the original
     * image data.
     */
    class PixelClusterable implements Clusterable {
        /** Pixel abscissa. */
        private final int x;
        /** Pixel ordinate. */
        private final int y;
        /** Pixel color. */
        private double[] color;

        /**
         * @param x Abscissa.
         * @param y Ordinate.
         */
        PixelClusterable(int x,
                         int y) {
            this.x = x;
            this.y = y;
            this.color = null;
        }

        /** {@inheritDoc} */
        @Override
        public double[] getPoint() {
            if (color == null) {
                color = data.getPixel(x, y, (double[]) null);
            }
            return color;
        }
    }
}
