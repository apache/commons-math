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
package org.apache.commons.math3.stat.clustering;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class DBSCANClustererTest {

    @Test
    public void testCluster() {
        // Test data generated using: http://people.cs.nctu.edu.tw/~rsliang/dbscan/testdatagen.html
        final EuclideanDoublePoint[] points = new EuclideanDoublePoint[] {
                new EuclideanDoublePoint(new double[] { 83.08303244924173, 58.83387754182331 }),
                new EuclideanDoublePoint(new double[] { 45.05445510940626, 23.469642649637535 }),
                new EuclideanDoublePoint(new double[] { 14.96417921432294, 69.0264096390456 }),
                new EuclideanDoublePoint(new double[] { 73.53189604333602, 34.896145021310076 }),
                new EuclideanDoublePoint(new double[] { 73.28498173551634, 33.96860806993209 }),
                new EuclideanDoublePoint(new double[] { 73.45828098873608, 33.92584423092194 }),
                new EuclideanDoublePoint(new double[] { 73.9657889183145, 35.73191006924026 }),
                new EuclideanDoublePoint(new double[] { 74.0074097183533, 36.81735596177168 }),
                new EuclideanDoublePoint(new double[] { 73.41247541410848, 34.27314856695011 }),
                new EuclideanDoublePoint(new double[] { 73.9156256353017, 36.83206791547127 }),
                new EuclideanDoublePoint(new double[] { 74.81499205809087, 37.15682749846019 }),
                new EuclideanDoublePoint(new double[] { 74.03144880081527, 37.57399178552441 }),
                new EuclideanDoublePoint(new double[] { 74.51870941207744, 38.674258946906775 }),
                new EuclideanDoublePoint(new double[] { 74.50754595105536, 35.58903978415765 }),
                new EuclideanDoublePoint(new double[] { 74.51322752749547, 36.030572259100154 }),
                new EuclideanDoublePoint(new double[] { 59.27900996617973, 46.41091720294207 }),
                new EuclideanDoublePoint(new double[] { 59.73744793841615, 46.20015558367595 }),
                new EuclideanDoublePoint(new double[] { 58.81134076672606, 45.71150126331486 }),
                new EuclideanDoublePoint(new double[] { 58.52225539437495, 47.416083617601544 }),
                new EuclideanDoublePoint(new double[] { 58.218626647023484, 47.36228902172297 }),
                new EuclideanDoublePoint(new double[] { 60.27139669447206, 46.606106348801404 }),
                new EuclideanDoublePoint(new double[] { 60.894962462363765, 46.976924697402865 }),
                new EuclideanDoublePoint(new double[] { 62.29048673878424, 47.66970563563518 }),
                new EuclideanDoublePoint(new double[] { 61.03857608977705, 46.212924720020965 }),
                new EuclideanDoublePoint(new double[] { 60.16916214139201, 45.18193661351688 }),
                new EuclideanDoublePoint(new double[] { 59.90036905976012, 47.555364347063005 }),
                new EuclideanDoublePoint(new double[] { 62.33003634144552, 47.83941489877179 }),
                new EuclideanDoublePoint(new double[] { 57.86035536718555, 47.31117930193432 }),
                new EuclideanDoublePoint(new double[] { 58.13715479685925, 48.985960494028404 }),
                new EuclideanDoublePoint(new double[] { 56.131923963548616, 46.8508904252667 }),
                new EuclideanDoublePoint(new double[] { 55.976329887053, 47.46384037658572 }),
                new EuclideanDoublePoint(new double[] { 56.23245975235477, 47.940035191131756 }),
                new EuclideanDoublePoint(new double[] { 58.51687048212625, 46.622885352699086 }),
                new EuclideanDoublePoint(new double[] { 57.85411081905477, 45.95394361577928 }),
                new EuclideanDoublePoint(new double[] { 56.445776311447844, 45.162093662656844 }),
                new EuclideanDoublePoint(new double[] { 57.36691949656233, 47.50097194337286 }),
                new EuclideanDoublePoint(new double[] { 58.243626387557015, 46.114052729681134 }),
                new EuclideanDoublePoint(new double[] { 56.27224595635198, 44.799080066150054 }),
                new EuclideanDoublePoint(new double[] { 57.606924816500396, 46.94291057763621 }),
                new EuclideanDoublePoint(new double[] { 30.18714230041951, 13.877149710431695 }),
                new EuclideanDoublePoint(new double[] { 30.449448810657486, 13.490778346545994 }),
                new EuclideanDoublePoint(new double[] { 30.295018390286714, 13.264889000216499 }),
                new EuclideanDoublePoint(new double[] { 30.160201832884923, 11.89278262341395 }),
                new EuclideanDoublePoint(new double[] { 31.341509791789576, 15.282655921997502 }),
                new EuclideanDoublePoint(new double[] { 31.68601630325429, 14.756873246748 }),
                new EuclideanDoublePoint(new double[] { 29.325963742565364, 12.097849250072613 }),
                new EuclideanDoublePoint(new double[] { 29.54820742388256, 13.613295356975868 }),
                new EuclideanDoublePoint(new double[] { 28.79359608888626, 10.36352064087987 }),
                new EuclideanDoublePoint(new double[] { 31.01284597092308, 12.788479208014905 }),
                new EuclideanDoublePoint(new double[] { 27.58509216737002, 11.47570110601373 }),
                new EuclideanDoublePoint(new double[] { 28.593799561727792, 10.780998203903437 }),
                new EuclideanDoublePoint(new double[] { 31.356105766724795, 15.080316198524088 }),
                new EuclideanDoublePoint(new double[] { 31.25948503636755, 13.674329151166603 }),
                new EuclideanDoublePoint(new double[] { 32.31590076372959, 14.95261758659035 }),
                new EuclideanDoublePoint(new double[] { 30.460413702763617, 15.88402809202671 }),
                new EuclideanDoublePoint(new double[] { 32.56178203062154, 14.586076852632686 }),
                new EuclideanDoublePoint(new double[] { 32.76138648530468, 16.239837325178087 }),
                new EuclideanDoublePoint(new double[] { 30.1829453331884, 14.709592407103628 }),
                new EuclideanDoublePoint(new double[] { 29.55088173528202, 15.0651247180067 }),
                new EuclideanDoublePoint(new double[] { 29.004155302187428, 14.089665298582986 }),
                new EuclideanDoublePoint(new double[] { 29.339624439831823, 13.29096065578051 }),
                new EuclideanDoublePoint(new double[] { 30.997460327576846, 14.551914158277214 }),
                new EuclideanDoublePoint(new double[] { 30.66784126125276, 16.269703107886016 })
        };

        final DBSCANClusterer<EuclideanDoublePoint> transformer =
                new DBSCANClusterer<EuclideanDoublePoint>(2.0, 5);
        final List<Cluster<EuclideanDoublePoint>> clusters = transformer.cluster(Arrays.asList(points));

        final List<EuclideanDoublePoint> clusterOne =
                Arrays.asList(points[3], points[4], points[5], points[6], points[7], points[8], points[9], points[10],
                              points[11], points[12], points[13], points[14]);
        final List<EuclideanDoublePoint> clusterTwo =
                Arrays.asList(points[15], points[16], points[17], points[18], points[19], points[20], points[21],
                              points[22], points[23], points[24], points[25], points[26], points[27], points[28],
                              points[29], points[30], points[31], points[32], points[33], points[34], points[35],
                              points[36], points[37], points[38]);
        final List<EuclideanDoublePoint> clusterThree =
                Arrays.asList(points[39], points[40], points[41], points[42], points[43], points[44], points[45],
                              points[46], points[47], points[48], points[49], points[50], points[51], points[52],
                              points[53], points[54], points[55], points[56], points[57], points[58], points[59],
                              points[60], points[61], points[62]);

        boolean cluster1Found = false;
        boolean cluster2Found = false;
        boolean cluster3Found = false;
        Assert.assertEquals(3, clusters.size());
        for (final Cluster<EuclideanDoublePoint> cluster : clusters) {
            if (cluster.getPoints().containsAll(clusterOne)) {
                cluster1Found = true;
            }
            if (cluster.getPoints().containsAll(clusterTwo)) {
                cluster2Found = true;
            }
            if (cluster.getPoints().containsAll(clusterThree)) {
                cluster3Found = true;
            }
        }
        Assert.assertTrue(cluster1Found);
        Assert.assertTrue(cluster2Found);
        Assert.assertTrue(cluster3Found);
    }

    @Test
    public void testSingleLink() {
        final EuclideanIntegerPoint[] points = {
                new EuclideanIntegerPoint(new int[] {10, 10}), // A
                new EuclideanIntegerPoint(new int[] {12, 9}),
                new EuclideanIntegerPoint(new int[] {10, 8}),
                new EuclideanIntegerPoint(new int[] {8, 8}),
                new EuclideanIntegerPoint(new int[] {8, 6}),
                new EuclideanIntegerPoint(new int[] {7, 7}),
                new EuclideanIntegerPoint(new int[] {5, 6}),  // B
                new EuclideanIntegerPoint(new int[] {14, 8}), // C
                new EuclideanIntegerPoint(new int[] {7, 15}), // N - Noise, should not be present
                new EuclideanIntegerPoint(new int[] {17, 8}), // D - single-link connected to C should not be present

        };

        final DBSCANClusterer<EuclideanIntegerPoint> clusterer = new DBSCANClusterer<EuclideanIntegerPoint>(3, 3);
        List<Cluster<EuclideanIntegerPoint>> clusters = clusterer.cluster(Arrays.asList(points));

        Assert.assertEquals(1, clusters.size());

        final List<EuclideanIntegerPoint> clusterOne =
                Arrays.asList(points[0], points[1], points[2], points[3], points[4], points[5], points[6], points[7]);
        Assert.assertTrue(clusters.get(0).getPoints().containsAll(clusterOne));
    }

    @Test
    public void testGetEps() {
        final DBSCANClusterer<EuclideanDoublePoint> transformer = new DBSCANClusterer<EuclideanDoublePoint>(2.0, 5);
        Assert.assertEquals(2.0, transformer.getEps(), 0.0);
    }

    @Test
    public void testGetMinPts() {
        final DBSCANClusterer<EuclideanDoublePoint> transformer = new DBSCANClusterer<EuclideanDoublePoint>(2.0, 5);
        Assert.assertEquals(5, transformer.getMinPts());
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testNegativeEps() {
        new DBSCANClusterer<EuclideanDoublePoint>(-2.0, 5);
    }

    @Test(expected = MathIllegalArgumentException.class)
    public void testNegativeMinPts() {
        new DBSCANClusterer<EuclideanDoublePoint>(2.0, -5);
    }

    @Test(expected = NullArgumentException.class)
    public void testNullDataset() {
        DBSCANClusterer<EuclideanDoublePoint> clusterer = new DBSCANClusterer<EuclideanDoublePoint>(2.0, 5);
        clusterer.cluster(null);
    }

}
