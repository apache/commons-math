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
package org.apache.commons.math3.userguide;

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

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.LevyDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.ParetoDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.userguide.ExampleUtils.ExampleFrame;
import org.apache.commons.math3.util.FastMath;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;
import com.xeiam.xchart.XChartPanel;

/**
 * Plots 2D samples drawn from various pseudo / quasi-random generators.
 */
public class RealDistributionComparison {

    public static void addPDFSeries(Chart chart, RealDistribution distribution, String desc, int lowerBound, int upperBound) {
        // generates Log data
        List<Number> xData = new ArrayList<Number>();
        List<Number> yData = new ArrayList<Number>();
        int samples = 100;
        double stepSize = (upperBound - lowerBound) / (double) samples;
        for (double x = lowerBound; x <= upperBound; x += stepSize) {
            try {
                double density = distribution.density(x);
                if (! Double.isInfinite(density) && ! Double.isNaN(density)) {
                    xData.add(x);
                    yData.add(density);
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

    public static void addCDFSeries(Chart chart, RealDistribution distribution, String desc, int lowerBound, int upperBound) {
        // generates Log data
        List<Number> xData = new ArrayList<Number>();
        List<Number> yData = new ArrayList<Number>();
        int samples = 100;
        double stepSize = (upperBound - lowerBound) / (double) samples;
        for (double x = lowerBound; x <= upperBound; x += stepSize) {
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
    
    public static JComponent createComponent(String distributionName, int minX, int maxX, String[] seriesText, RealDistribution... series) {
        JComponent container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        
        container.add(new JLabel(distributionName));
        
        Chart chart = createChart("PDF", minX, maxX, LegendPosition.InsideNE);
        int i = 0;
        for (RealDistribution d : series) {
            addPDFSeries(chart, d, seriesText[i++], minX, maxX);
        }
        container.add(new XChartPanel(chart));

        chart = createChart("CDF", minX, maxX, LegendPosition.InsideSE);
        i = 0;
        for (RealDistribution d : series) {
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
            setTitle("Commons-Math: Real distributions overview");
            setSize(1320, 920);
            
            container = new JPanel();
            container.setLayout(new GridBagLayout());
            
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.VERTICAL;
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(2, 2, 2, 2);

            JComponent comp = null;

            comp = createComponent("Normal", -5, 5,
                                   new String[] { "μ=0,σ\u00B2=0.2", "μ=0,σ\u00B2=1", "μ=0,σ\u00B2=5", "μ=-2,σ\u00B2=0.5" },
                                   new NormalDistribution(0, FastMath.sqrt(0.2)),
                                   new NormalDistribution(),
                                   new NormalDistribution(0, FastMath.sqrt(5)),
                                   new NormalDistribution(-2, FastMath.sqrt(0.5)));
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Beta", 0, 1,
                                   new String[] { "α=β=0.5", "α=5,β=1", "α=1,β=3", "α=2,β=2", "α=2,β=5" },
                                   new BetaDistribution(0.5, 0.5),
                                   new BetaDistribution(5, 1),
                                   new BetaDistribution(1, 3),
                                   new BetaDistribution(2, 2),
                                   new BetaDistribution(2, 5));
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Cauchy", -5, 5,
                                   new String[] { "x=0,γ=0.5", "x=0,γ=1", "x=0,γ=2", "x=-2,γ=1" },
                                   new CauchyDistribution(0, 0.5),
                                   new CauchyDistribution(0, 1),
                                   new CauchyDistribution(0, 2),
                                   new CauchyDistribution(-2, 1));
            container.add(comp, c);
            
            c.gridx++;
            comp = createComponent("ChiSquared", 0, 5,
                                   new String[] { "k=1", "k=2", "k=3", "k=4", "k=6" },
                                   new ChiSquaredDistribution(1),
                                   new ChiSquaredDistribution(2),
                                   new ChiSquaredDistribution(3),
                                   new ChiSquaredDistribution(4),
                                   new ChiSquaredDistribution(6));
            container.add(comp, c);

            c.gridy++;
            c.gridx = 0;
            comp = createComponent("Exponential", 0, 5,
                                   new String[] { "λ=0.5", "λ=1", "λ=1.5", "λ=2.5" },
                                   new ExponentialDistribution(0.5),
                                   new ExponentialDistribution(1),
                                   new ExponentialDistribution(1.5),
                                   new ExponentialDistribution(2.5));                               
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Fisher-Snedecor", 0, 5,
                                   new String[] { "d1=1,d2=1", "d1=2,d2=1", "d1=5,d2=2", "d1=100,d2=1", "d1=100,d2=100" },
                                   new FDistribution(1, 1),
                                   new FDistribution(2, 1),
                                   new FDistribution(5, 2),
                                   new FDistribution(100, 1),
                                   new FDistribution(100, 100));
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Gamma", 0, 20,
                                   new String[] { "k=1,θ=2", "k=2,θ=2", "k=3,θ=2", "k=5,θ=1", "k=9,θ=0.5" },
                                   new GammaDistribution(1, 2),
                                   new GammaDistribution(2, 2),
                                   new GammaDistribution(3, 2),
                                   new GammaDistribution(5, 1),
                                   new GammaDistribution(9, 0.5));
            container.add(comp, c);

            c.gridx++;
            RandomGenerator rng = new MersenneTwister(0);
            comp = createComponent("Levy", 0, 3,
                                   new String[] { "c=0.5", "c=1", "c=2", "c=4", "c=8" },
                                   new LevyDistribution(rng, 0, 0.5),
                                   new LevyDistribution(rng, 0, 1),
                                   new LevyDistribution(rng, 0, 2),
                                   new LevyDistribution(rng, 0, 4),
                                   new LevyDistribution(rng, 0, 8));
            container.add(comp, c);

            c.gridy++;
            c.gridx = 0;
            comp = createComponent("Log-Normal", 0, 3,
                                   new String[] { "μ=0,σ\u00B2=10", "μ=0,σ\u00B2=1.5", "μ=0,σ\u00B2=1", "μ=0,σ\u00B2=0.5", "μ=0,σ\u00B2=0.25", "μ=0,σ\u00B2=0.125" },
                                   new LogNormalDistribution(0, 10),
                                   new LogNormalDistribution(0, 1.5),
                                   new LogNormalDistribution(0, 1),
                                   new LogNormalDistribution(0, 0.5),
                                   new LogNormalDistribution(0, 0.25),
                                   new LogNormalDistribution(0, 0.125));
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Pareto", 0, 5,
                                   new String[] { "x=1,α=1", "x=1,α=2", "x=1,α=3", "x=1,α=10" },
                                   new ParetoDistribution(1, 1),
                                   new ParetoDistribution(1, 2),
                                   new ParetoDistribution(1, 3),
                                   new ParetoDistribution(1, 10));                               
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Student-T", -5, 5,
                                   new String[] { "df=1", "df=2", "df=5", "df=10000" },
                                   new TDistribution(1),
                                   new TDistribution(2),
                                   new TDistribution(5),
                                   new TDistribution(10000));                               
            container.add(comp, c);

            c.gridx++;
            comp = createComponent("Weibull", 0, 3,
                                   new String[] { "λ=0.5,k=1", "λ=1,k=1", "λ=1.5,k=1", "λ=5,k=1" },
                                   new WeibullDistribution(0.5, 1),
                                   new WeibullDistribution(1, 1),
                                   new WeibullDistribution(1.5, 1),
                                   new WeibullDistribution(5, 1));
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
