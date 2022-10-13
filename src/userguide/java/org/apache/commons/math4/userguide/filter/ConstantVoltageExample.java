/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.apache.commons.math4.userguide.filter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import org.apache.commons.math4.filter.DefaultMeasurementModel;
import org.apache.commons.math4.filter.DefaultProcessModel;
import org.apache.commons.math4.filter.KalmanFilter;
import org.apache.commons.math4.filter.MeasurementModel;
import org.apache.commons.math4.filter.ProcessModel;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.ArrayRealVector;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.RealVector;
import org.apache.commons.math4.random.GaussianRandomGenerator;
import org.apache.commons.math4.userguide.ExampleUtils;
import org.apache.commons.math4.userguide.ExampleUtils.ExampleFrame;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.XChartPanel;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;

public class ConstantVoltageExample {

    public static class VoltMeter {

        private final double initialVoltage;
        private final double processNoise;
        private final double measurementNoise;
        private final GaussianRandomGenerator rng;

        private double voltage;

        public VoltMeter(double voltage, double processNoise, double measurementNoise, int seed) {
            this.initialVoltage = voltage;
            this.voltage = voltage;
            this.processNoise = processNoise;
            this.measurementNoise = measurementNoise;
            rng = new GaussianRandomGenerator(RandomSource.create(RandomSource.WELL_19937_C, seed));
        }

        /**
         * Returns the real voltage without any measurement noise.
         *
         * @return the real voltage
         */
        public double getVoltage() {
            return voltage;
        }

        public double getMeasuredVoltage() {
            return getVoltage() + rng.nextNormalizedDouble() * measurementNoise;
        }

        public void step() {
            // we apply only the process noise
            voltage = initialVoltage + rng.nextNormalizedDouble() * processNoise;
        }
    }

    /** constant voltage test */
    public static void constantVoltageTest(Chart chart1, Chart chart2) {

        final double voltage = 1.25d;
        final double measurementNoise = 0.2d; // measurement noise (V) - std dev
        final double processNoise = 1e-5d;

        final VoltMeter voltMeter = new VoltMeter(voltage, processNoise, measurementNoise, 2);

        // the state transition matrix -> constant
        final RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });

        // the control matrix -> no control input
        final RealMatrix B = new Array2DRowRealMatrix(new double[] { 0d });

        // the measurement matrix -> we measure the voltage directly
        final RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });

        // the initial state vector -> slightly wrong
        final RealVector x0 = new ArrayRealVector(new double[] { 1.45 });

        // the process covariance matrix
        final RealMatrix Q = new Array2DRowRealMatrix(new double[] { processNoise * processNoise });

        // the initial error covariance -> assume a large error at the beginning
        final RealMatrix P0 = new Array2DRowRealMatrix(new double[] { 0.1 });

        // the measurement covariance matrix -> put the "real" variance
        RealMatrix R = new Array2DRowRealMatrix(new double[] { measurementNoise * measurementNoise });

        final ProcessModel pm = new DefaultProcessModel(A, B, Q, x0, P0);
        final MeasurementModel mm = new DefaultMeasurementModel(H, R);
        final KalmanFilter filter = new KalmanFilter(pm, mm);

        final List<Number> xAxis = new ArrayList<>();
        final List<Number> realVoltageSeries = new ArrayList<>();
        final List<Number> measuredVoltageSeries = new ArrayList<>();
        final List<Number> kalmanVoltageSeries = new ArrayList<>();

        final List<Number> covSeries = new ArrayList<>();

        for (int i = 0; i < 300; i++) {
            xAxis.add(i);

            voltMeter.step();

            realVoltageSeries.add(voltMeter.getVoltage());

            // get the measured voltage from the volt meter
            final double measuredVoltage = voltMeter.getMeasuredVoltage();
            measuredVoltageSeries.add(measuredVoltage);

            kalmanVoltageSeries.add(filter.getStateEstimation()[0]);
            covSeries.add(filter.getErrorCovariance()[0][0]);

            filter.predict();
            filter.correct(new double[] { measuredVoltage });
        }

        chart1.setYAxisTitle("Voltage");
        chart1.setXAxisTitle("Iteration");

        Series dataset = chart1.addSeries("real", xAxis, realVoltageSeries);
        dataset.setMarker(SeriesMarker.NONE);

        dataset = chart1.addSeries("measured", xAxis, measuredVoltageSeries);
        dataset.setLineStyle(SeriesLineStyle.DOT_DOT);
        dataset.setMarker(SeriesMarker.NONE);

        dataset = chart1.addSeries("filtered", xAxis, kalmanVoltageSeries);
        dataset.setLineColor(Color.red);
        dataset.setLineStyle(SeriesLineStyle.DASH_DASH);
        dataset.setMarker(SeriesMarker.NONE);

        // Error covariance chart

        chart2.setYAxisTitle("(Voltage)²");
        chart2.setXAxisTitle("Iteration");

        dataset = chart2.addSeries("cov", xAxis, covSeries);
        dataset.setLineColor(Color.black);
        dataset.setLineStyle(SeriesLineStyle.SOLID);
        dataset.setMarker(SeriesMarker.NONE);
    }

    public static Chart createChart(String title, int width, int height,
                                    LegendPosition position, boolean legendVisible) {
        Chart chart = new ChartBuilder().width(width).height(height).build();

        // Customize Chart
        chart.setChartTitle(title);
        chart.getStyleManager().setChartTitleVisible(true);
        chart.getStyleManager().setChartTitleFont(new Font("Arial", Font.PLAIN, 12));
        chart.getStyleManager().setLegendPosition(position);
        chart.getStyleManager().setLegendVisible(legendVisible);
        chart.getStyleManager().setLegendFont(new Font("Arial", Font.PLAIN, 12));
        chart.getStyleManager().setLegendPadding(6);
        chart.getStyleManager().setLegendSeriesLineLength(10);
        chart.getStyleManager().setAxisTickLabelsFont(new Font("Arial", Font.PLAIN, 10));

        chart.getStyleManager().setChartBackgroundColor(Color.white);
        chart.getStyleManager().setChartPadding(4);

        chart.getStyleManager().setChartType(ChartType.Line);
        return chart;
    }

    public static JComponent createComponent() {
        JComponent container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));

        Chart chart1 = createChart("Voltage", 550, 450, LegendPosition.InsideNE, true);
        Chart chart2 = createChart("Error Covariance", 450, 450, LegendPosition.InsideNE, false);

        constantVoltageTest(chart1, chart2);

        container.add(new XChartPanel(chart1));
        container.add(new XChartPanel(chart2));

        container.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        return container;
    }

    @SuppressWarnings("serial")
    public static class Display extends ExampleFrame {

        private JComponent container;

        public Display() {
            setTitle("Commons-Math: Kalman Filter example");
            setSize(1100, 700);

            container = new JPanel();

            JComponent comp = createComponent();
            container.add(comp);

            add(container);
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
