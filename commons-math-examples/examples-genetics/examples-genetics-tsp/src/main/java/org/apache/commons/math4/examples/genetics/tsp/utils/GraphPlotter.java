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
package org.apache.commons.math4.examples.genetics.tsp.utils;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math4.examples.genetics.tsp.commons.City;
import org.apache.commons.math4.genetics.listener.ConvergenceListener;
import org.apache.commons.math4.genetics.population.Population;
import org.apache.commons.math4.genetics.stats.PopulationStatisticalSummary;
import org.apache.commons.math4.genetics.stats.internal.PopulationStatisticalSummaryImpl;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class represents the graph plotter during optimization.
 */
public class GraphPlotter extends JFrame implements ConvergenceListener<List<City>> {

    /**
     * Generated serialversionId.
     */
    private static final long serialVersionUID = -5683904006424006584L;

    /** collection of 2-D series. **/
    private final XYSeriesCollection dataset = new XYSeriesCollection();

    /**
     * constructor.
     * @param plotSubject subject of plot
     * @param xAxisLabel  x axis label
     * @param yAxisLabel  y axis label
     */
    public GraphPlotter(String plotSubject, String xAxisLabel, String yAxisLabel) {
        super(plotSubject);

        final JPanel chartPanel = createChartPanel(plotSubject, xAxisLabel, yAxisLabel);
        add(chartPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    /**
     * Adds data point to graph.
     * @param graphName  name of graph
     * @param generation generation, to be plotted along x axis
     * @param value      value, to be plotted along y axis
     */
    private void addDataPoint(String graphName, int generation, double value) {
        XYSeries series = null;

        if (!containsGraph(graphName)) {
            series = new XYSeries(graphName);
            dataset.addSeries(series);
        } else {
            series = dataset.getSeries(graphName);
        }
        series.add(generation, value);

        setVisible(true);
    }

    /**
     * Checks if the graph with the given name already exists.
     * @param graphName name of the graph
     * @return true/false
     */
    @SuppressWarnings("unchecked")
    private boolean containsGraph(String graphName) {
        final List<XYSeries> seriesList = dataset.getSeries();
        if (seriesList == null || seriesList.isEmpty()) {
            return false;
        }
        for (XYSeries series : seriesList) {
            if (series.getKey().compareTo(graphName) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates chart panel.
     * @param chartTitle chart title
     * @param xAxisLabel x axis label
     * @param yAxisLabel y axis label
     * @return panel
     */
    private JPanel createChartPanel(String chartTitle, String xAxisLabel, String yAxisLabel) {

        final boolean showLegend = true;
        final boolean createURL = false;
        final boolean createTooltip = false;

        final JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset,
                PlotOrientation.VERTICAL, showLegend, createTooltip, createURL);
        final XYPlot plot = chart.getXYPlot();
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        plot.setRenderer(renderer);

        return new ChartPanel(chart);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notify(int generation, Population<List<City>> population) {
        PopulationStatisticalSummary<List<City>> populationStatisticalSummary = new PopulationStatisticalSummaryImpl<>(
                population);
        this.addDataPoint("Average", generation, populationStatisticalSummary.getMeanFitness());
        this.addDataPoint("Best", generation, populationStatisticalSummary.getMaxFitness());
    }

}
