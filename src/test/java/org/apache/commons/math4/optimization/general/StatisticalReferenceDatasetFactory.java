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
package org.apache.commons.math3.optimization.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

/**
 * A factory to create instances of {@link StatisticalReferenceDataset} from
 * available resources.
 */
@Deprecated
public class StatisticalReferenceDatasetFactory {

    private StatisticalReferenceDatasetFactory() {
        // Do nothing
    }

    /**
     * Creates a new buffered reader from the specified resource name.
     *
     * @param name the name of the resource
     * @return a buffered reader
     * @throws IOException if an I/O error occurred
     */
    public static BufferedReader createBufferedReaderFromResource(final String name)
        throws IOException {
        final InputStream resourceAsStream;
        resourceAsStream = StatisticalReferenceDatasetFactory.class
            .getResourceAsStream(name);
        if (resourceAsStream == null) {
            throw new IOException("could not find resource " + name);
        }
        return new BufferedReader(new InputStreamReader(resourceAsStream));
    }

    public static StatisticalReferenceDataset createKirby2()
        throws IOException {
        final BufferedReader in = createBufferedReaderFromResource("Kirby2.dat");
        StatisticalReferenceDataset dataset = null;
        try {
            dataset = new StatisticalReferenceDataset(in) {

                @Override
                public DerivativeStructure getModelValue(final double x, final DerivativeStructure[] a) {
                    final DerivativeStructure p = a[0].add(a[1].add(a[2].multiply(x)).multiply(x));
                    final DerivativeStructure q = a[3].add(a[4].multiply(x)).multiply(x).add(1.0);
                    return p.divide(q);
                }

            };
        } finally {
            in.close();
        }
        return dataset;
    }

    public static StatisticalReferenceDataset createHahn1()
        throws IOException {
        final BufferedReader in = createBufferedReaderFromResource("Hahn1.dat");
        StatisticalReferenceDataset dataset = null;
        try {
            dataset = new StatisticalReferenceDataset(in) {

                @Override
                public DerivativeStructure getModelValue(final double x, final DerivativeStructure[] a) {
                    final DerivativeStructure p = a[0].add(a[1].add(a[2].add(a[3].multiply(x)).multiply(x)).multiply(x));
                    final DerivativeStructure q = a[4].add(a[5].add(a[6].multiply(x)).multiply(x)).multiply(x).add(1.0);
                    return p.divide(q);
                }

            };
        } finally {
            in.close();
        }
        return dataset;
    }

    public static StatisticalReferenceDataset createMGH17()
        throws IOException {
        final BufferedReader in = createBufferedReaderFromResource("MGH17.dat");
        StatisticalReferenceDataset dataset = null;
        try {
            dataset = new StatisticalReferenceDataset(in) {

                @Override
                public DerivativeStructure getModelValue(final double x, final DerivativeStructure[] a) {
                    return a[0].add(a[1].multiply(a[3].multiply(-x).exp())).add(a[2].multiply(a[4].multiply(-x).exp()));
                }

            };
        } finally {
            in.close();
        }
        return dataset;
    }

    public static StatisticalReferenceDataset createLanczos1()
        throws IOException {
        final BufferedReader in =
            createBufferedReaderFromResource("Lanczos1.dat");
        StatisticalReferenceDataset dataset = null;
        try {
            dataset = new StatisticalReferenceDataset(in) {

                @Override
                public DerivativeStructure getModelValue(final double x, final DerivativeStructure[] a) {
                    return a[0].multiply(a[3].multiply(-x).exp()).add(
                                a[1].multiply(a[4].multiply(-x).exp())).add(
                                     a[2].multiply(a[5].multiply(-x).exp()));
                }

            };
        } finally {
            in.close();
        }
        return dataset;
    }

    /**
     * Returns an array with all available reference datasets.
     *
     * @return the array of datasets
     * @throws IOException if an I/O error occurs
     */
    public StatisticalReferenceDataset[] createAll()
        throws IOException {
        return new StatisticalReferenceDataset[] {
            createKirby2(), createMGH17()
        };
    }
}
