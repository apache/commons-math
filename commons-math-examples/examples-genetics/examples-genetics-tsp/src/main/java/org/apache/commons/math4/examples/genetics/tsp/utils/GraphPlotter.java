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

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math4.genetics.listeners.ConvergenceListener;
import org.apache.commons.math4.genetics.model.Population;
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

public class GraphPlotter extends JFrame implements ConvergenceListener {

	private int generation;

	private JFreeChart chart;

	private XYSeriesCollection dataset = new XYSeriesCollection();

	public GraphPlotter(String plotSubject, String xAxisLabel, String yAxisLabel) {
		super(plotSubject);

		JPanel chartPanel = createChartPanel(plotSubject, xAxisLabel, yAxisLabel);
		add(chartPanel, BorderLayout.CENTER);

		setSize(640, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setVisible(true);
	}

	private void addDataPoint(String graphName, int generation, double value) {
		XYSeries series = null;
		try {
			series = dataset.getSeries(graphName);
		} catch (Exception e) {
			series = new XYSeries(graphName);
			dataset.addSeries(series);
		}
		series.add(this.generation++, value);

		setVisible(true);
	}

	private JPanel createChartPanel(String chartTitle, String xAxisLabel, String yAxisLabel) {

		boolean showLegend = true;
		boolean createURL = false;
		boolean createTooltip = false;

		chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL,
				showLegend, createTooltip, createURL);
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		plot.setRenderer(renderer);

		return new ChartPanel(chart);

	}

	@Override
	public void notify(Population population) {
		PopulationStatisticalSummary populationStatisticalSummary = new PopulationStatisticalSummaryImpl(population);
		this.addDataPoint("Average", this.generation, Math.abs(populationStatisticalSummary.getMeanFitness()));
		this.addDataPoint("Best", this.generation, Math.abs(populationStatisticalSummary.getMaxFitness()));
//		this.addDataPoint("Variance", this.generation, populationStatisticalSummary.getFitnessVariance());
	}

}