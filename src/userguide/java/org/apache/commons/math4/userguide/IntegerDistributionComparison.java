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
package org.apache.commons.math4.userguide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.math4.distribution.BinomialDistribution;
import org.apache.commons.math4.distribution.GeometricDistribution;
import org.apache.commons.math4.distribution.HypergeometricDistribution;
import org.apache.commons.math4.distribution.IntegerDistribution;
import org.apache.commons.math4.distribution.PascalDistribution;
import org.apache.commons.math4.distribution.PoissonDistribution;
import org.apache.commons.math4.distribution.UniformIntegerDistribution;
import org.apache.commons.math4.distribution.ZipfDistribution;
import org.apache.commons.math4.userguide.ExampleUtils.ExampleFrame;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.XChartPanel;

/**
 * Displays pdf/cdf for integer distributions.
 */
public class IntegerDistributionComparison {

    public static void addPDFSeries(Chart chart, IntegerDistribution distribution, String desc, int lowerBound, int upperBound) {
        // generates Log data
        List<Number> xData = new ArrayList<Number>();
        List<Number> yData = new ArrayList<Number>();
        for (int x = lowerBound; x <= upperBound; x += 1) {
            try {
                double probability = distribution.probability(x);
                if (! Double.isInfinite(probability) && ! Double.isNaN(probability)) {
                    xData.add(x);
                    yData.add(probability);
                }
            } catch (Exception e) {
                // ignore
                // some distributions may reject certain values depending on the parameter settings
            }
        }

        Series series = chart.addSeries(desc, xData, yData);
        series.setMarker(SeriesMarker.NONE);
        series.setLineStyle(new BasicStroke(1.2f));
    }

    public static void addCDFSeries(Chart chart, IntegerDistribution distribution, String desc,
                                    int lowerBound, int upperBound) {
        // generates Log data
        List<Number> xData = new ArrayList<Number>();
        List<Number> yData = new ArrayList<Number>();
        for (int x = lowerBound; x <= upperBound; x += 1) {
          double density = distribution.cumulativeProbability(x);
          if (! Double.isInfinite(density) && ! Double.isNaN(density)) {
              xData.add(x);
              yData.add(density);
          }
        }

        Series series = chart.addSeries(desc, xData, yData);
        series.setMarker(SeriesMarker.NONE);
        series.setLineStyle(new BasicStroke(1.2f));
    }

    public static Chart createChart(String title, int minX, int maxX, LegendPosition position) {
        Chart chart = new ChartBuilder().width(235).height(200).build();

        // Customize Chart
        chart.setChartTitle(title);
        chart.getStyleManager().setChartTitleVisible(true);
        chart.getStyleManager().setChartTitleFont(new Font("Arial", Font.PLAIN, 10));
        chart.getStyleManager().setLegendPosition(position);
        chart.getStyleManager().setLegendVisible(true);
        chart.getStyleManager().setLegendFont(new Font("Arial", Font.PLAIN, 10));
        chart.getStyleManager().setLegendPadding(6);
        chart.getStyleManager().setLegendSeriesLineLength(6);
        chart.getStyleManager().setAxisTickLabelsFont(new Font("Arial", Font.PLAIN, 9));
        
        chart.getStyleManager().setXAxisMin(minX);
        chart.getStyleManager().setXAxisMax(maxX);
        chart.getStyleManager().setChartBackgroundColor(Color.white);
        chart.getStyleManager().setChartPadding(4);
        
        chart.getStyleManager().setChartType(ChartType.Line);
        return chart;
    }
    
    public static JComponent createComponent(String distributionName, int minX, int maxX, String[] seriesText,
                                             IntegerDistribution... series) {
        JComponent container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        
        container.add(new JLabel(distributionName));
        
        Chart chart = createChart("PDF", minX, maxX, LegendPosition.InsideNE);
        int i = 0;
        for (IntegerDistribution d : series) {
            addPDFSeries(chart, d, seriesText[i++], minX, maxX);
        }
        container.add(new XChartPanel(chart));

        chart = createChart("CDF", minX, maxX, LegendPosition.InsideSE);
        i = 0;
        for (IntegerDistribution d : series) {
            addCDFSeries(chart, d, seriesText[i++], minX, maxX);
        }
        container.add(new XChartPanel(chart));

        container.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        return container;
    }
    
    @SuppressWarnings("serial")
    public static class Display extends ExampleFrame {
        
        private JComponent container;

        public Display() {
            setTitle("Commons-Math: Integer distributions overview");
            setSize(1320, 920);
            
            container = new JPanel();
            container.setLayout(new GridBagLayout());
            
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.VERTICAL;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(2, 2, 2, 2);

            JComponent comp = null;

            comp = createComponent("Binomial", 0, 40,
                                   new String[] { "p=0.5,n=20", "p=0.7,n=20", "p=0.5,n=40" },
                                   new BinomialDistribution(20, 0.5),
                                   new BinomialDistribution(20, 0.7),
                                   new BinomialDistribution(40, 0.5));
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Geometric", 0, 10,
                                   new String[] { "p=0.2", "p=0.5", "p=0.8" },
                                   new GeometricDistribution(0.2),
                                   new GeometricDistribution(0.5),
                                   new GeometricDistribution(0.8));
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Hypergeometric", 0, 10,
                                   new String[] { "p=0.3", "p=0.5", "p=0.75" },
                                   new HypergeometricDistribution(100, 6, 20),
                                   new HypergeometricDistribution(100, 10, 20),
                                   new HypergeometricDistribution(100, 15, 20));
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Pascal", 0, 50,
                                   new String[] { "p=0.3", "p=0.5", "p=0.7" },
                                   new PascalDistribution(10, 0.3),
                                   new PascalDistribution(10, 0.5),
                                   new PascalDistribution(10, 0.7));
            container.add(comp, c);

            c.gridy++;
            c.gridx = 0;
            comp = createComponent("Poisson", 0, 20,
                                   new String[] { "λ=1", "λ=4", "λ=10" },
                                   new PoissonDistribution(1),
                                   new PoissonDistribution(4),
                                   new PoissonDistribution(10));
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Uniform", 0, 30,
                                   new String[] { "l=1,u=10", "l=5,u=20", "l=1,u=25" },
                                   new UniformIntegerDistribution(1, 10),
                                   new UniformIntegerDistribution(5, 20),
                                   new UniformIntegerDistribution(1, 25));
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Zipf", 0, 15,
                                   new String[] { "n=10,e=0.5", "n=10,e=1", "n=10,e=2", "n=10,e=5" },
                                   new ZipfDistribution(10, 0.5),
                                   new ZipfDistribution(10, 1),
                                   new ZipfDistribution(10, 2),
                                   new ZipfDistribution(10, 5));
            container.add(comp, c);

            JScrollPane scrollPane = new JScrollPane(container);
            add(scrollPane);
            
        }

        @Override
        public Component getMainPanel() {
            return container;
        }

    }

    public static void main(String[] args) {
        ExampleUtils.showExampleFrame(new Display());
    }
}
