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

import org.apache.commons.math4.filter.DefaultMeasurementModel;
import org.apache.commons.math4.filter.DefaultProcessModel;
import org.apache.commons.math4.filter.KalmanFilter;
import org.apache.commons.math4.filter.MeasurementModel;
import org.apache.commons.math4.filter.ProcessModel;
import org.apache.commons.math4.linear.MatrixUtils;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.RealVector;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well19937c;
import org.apache.commons.math4.util.FastMath;
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

public class CannonballExample {

    public static class Cannonball {
    
        private final double[] gravity = { 0, -9.81 };
        private final double[] velocity;
        private final double[] location;
        
        private final double timeslice;
        private final double measurementNoise;
        
        private final RandomGenerator rng;
        
        public Cannonball(double timeslice, double angle, double initialVelocity, double measurementNoise, int seed) {
            this.timeslice = timeslice;
            
            final double angleInRadians = FastMath.toRadians(angle);
            this.velocity = new double[] {
                    initialVelocity * FastMath.cos(angleInRadians),
                    initialVelocity * FastMath.sin(angleInRadians)
            };
            
            this.location = new double[] { 0, 0 };
            
            this.measurementNoise = measurementNoise;
            this.rng = new Well19937c(seed);
        }
        
        public double getX() {
            return location[0];
        }
        
        public double getY() {
            return location[1];
        }

        public double getMeasuredX() {
            return location[0] + rng.nextGaussian() * measurementNoise;
        }

        public double getMeasuredY() {
            return location[1] + rng.nextGaussian() * measurementNoise;
        }

        public double getXVelocity() {
            return velocity[0];
        }
        
        public double getYVelocity() {
            return velocity[1];
        }
        
        public void step() {
            // Break gravitational force into a smaller time slice.
            double[] slicedGravity = gravity.clone();
            for ( int i = 0; i < slicedGravity.length; i++ ) {
                slicedGravity[i] *= timeslice;
            }
            
            // Apply the acceleration to velocity.
            double[] slicedVelocity = velocity.clone();
            for ( int i = 0; i < velocity.length; i++ ) {
                velocity[i] += slicedGravity[i];
                slicedVelocity[i] = velocity[i] * timeslice;
                location[i] += slicedVelocity[i];
            }

            // Cannonballs shouldn't go into the ground.
            if ( location[1] < 0 ) {
                location[1] = 0;
            }
        }
    }
    
    public static void cannonballTest(Chart chart) {
        
        // time interval for each iteration
        final double dt = 0.1;
        // the number of iterations to run
        final int iterations = 144;
        // measurement noise (m)
        final double measurementNoise = 30;
        // initial velocity of the cannonball
        final double initialVelocity = 100;
        // shooting angle
        final double angle = 45;

        // the cannonball itself
        final Cannonball cannonball = new Cannonball(dt, angle, initialVelocity, measurementNoise, 1000);
        
        // A = [ 1, dt, 0,  0 ]  =>  x(n+1) = x(n) + vx(n)
        //     [ 0,  1, 0,  0 ]  => vx(n+1) =        vx(n)
        //     [ 0,  0, 1, dt ]  =>  y(n+1) =              y(n) + vy(n)
        //     [ 0,  0, 0,  1 ]  => vy(n+1) =                     vy(n)
        final RealMatrix A = MatrixUtils.createRealMatrix(new double[][] {
                { 1, dt, 0,  0 },
                { 0,  1, 0,  0 },
                { 0,  0, 1, dt },
                { 0,  0, 0,  1 }       
        });

        // The control vector, which adds acceleration to the kinematic equations.
        // 0          =>  x(n+1) =  x(n+1)
        // 0          => vx(n+1) = vx(n+1)
        // -9.81*dt^2 =>  y(n+1) =  y(n+1) - 1/2 * 9.81 * dt^2
        // -9.81*dt   => vy(n+1) = vy(n+1) - 9.81 * dt
        final RealVector controlVector =
                MatrixUtils.createRealVector(new double[] { 0, 0, 0.5 * -9.81 * dt * dt, -9.81 * dt } );

        // The control matrix B only update y and vy, see control vector
        final RealMatrix B = MatrixUtils.createRealMatrix(new double[][] {
                { 0, 0, 0, 0 },
                { 0, 0, 0, 0 },
                { 0, 0, 1, 0 },
                { 0, 0, 0, 1 }
        });

        // After state transition and control, here are the equations:
        //
        //  x(n+1) = x(n) + vx(n)
        // vx(n+1) = vx(n)
        //  y(n+1) = y(n) + vy(n) - 0.5 * 9.81 * dt^2
        // vy(n+1) = vy(n) + -9.81 * dt
        //
        // Which, if you recall, are the equations of motion for a parabola.

        // We only observe the x/y position of the cannonball
        final RealMatrix H = MatrixUtils.createRealMatrix(new double[][] {
                { 1, 0, 0, 0 },
                { 0, 0, 0, 0 },
                { 0, 0, 1, 0 },
                { 0, 0, 0, 0 }
        });
        
        // This is our guess of the initial state.  I intentionally set the Y value
        // wrong to illustrate how fast the Kalman filter will pick up on that.
        final double speedX = cannonball.getXVelocity();
        final double speedY = cannonball.getYVelocity();
        final RealVector initialState = MatrixUtils.createRealVector(new double[] { 0, speedX, 100, speedY } );

        // The initial error covariance matrix, the variance = noise^2
        final double var = measurementNoise * measurementNoise;
        final RealMatrix initialErrorCovariance = MatrixUtils.createRealMatrix(new double[][] {
                { var,    0,   0,    0 },
                {   0, 1e-3,   0,    0 },
                {   0,    0, var,    0 },
                {   0,    0,   0, 1e-3 }
        });

        // we assume no process noise -> zero matrix
        final RealMatrix Q = MatrixUtils.createRealMatrix(4, 4);
        
        // the measurement covariance matrix
        final RealMatrix R = MatrixUtils.createRealMatrix(new double[][] {
                { var,    0,   0,    0 },
                {   0, 1e-3,   0,    0 },
                {   0,    0, var,    0 },
                {   0,    0,   0, 1e-3 }
        });

        final ProcessModel pm = new DefaultProcessModel(A, B, Q, initialState, initialErrorCovariance);
        final MeasurementModel mm = new DefaultMeasurementModel(H, R);
        final KalmanFilter filter = new KalmanFilter(pm, mm);

        final List<Number> realX = new ArrayList<Number>();
        final List<Number> realY = new ArrayList<Number>();
        final List<Number> measuredX = new ArrayList<Number>();
        final List<Number> measuredY = new ArrayList<Number>();
        final List<Number> kalmanX = new ArrayList<Number>();
        final List<Number> kalmanY = new ArrayList<Number>();
        
        for (int i = 0; i < iterations; i++) {

            // get real location
            realX.add(cannonball.getX());
            realY.add(cannonball.getY());

            // get measured location
            final double mx = cannonball.getMeasuredX();
            final double my = cannonball.getMeasuredY();

            measuredX.add(mx);
            measuredY.add(my);

            // iterate the cannon simulation to the next timeslice.
            cannonball.step();

            final double[] state = filter.getStateEstimation();
            kalmanX.add(state[0]);
            kalmanY.add(state[2]);

            // update the kalman filter with the measurements
            filter.predict(controlVector);
            filter.correct(new double[] { mx, 0, my, 0 } );
        }

        chart.setXAxisTitle("Distance (m)");
        chart.setYAxisTitle("Height (m)");

        Series dataset = chart.addSeries("true", realX, realY);
        dataset.setMarker(SeriesMarker.NONE);
        
        dataset = chart.addSeries("measured", measuredX, measuredY);
        dataset.setLineStyle(SeriesLineStyle.DOT_DOT);
        dataset.setMarker(SeriesMarker.NONE);

        dataset = chart.addSeries("kalman", kalmanX, kalmanY);
        dataset.setLineColor(Color.red);
        dataset.setLineStyle(SeriesLineStyle.DASH_DASH);
        dataset.setMarker(SeriesMarker.NONE);
    }

    public static Chart createChart(String title, LegendPosition position) {
        Chart chart = new ChartBuilder().width(650).height(450).build();

        // Customize Chart
        chart.setChartTitle(title);
        chart.getStyleManager().setChartTitleVisible(true);
        chart.getStyleManager().setChartTitleFont(new Font("Arial", Font.PLAIN, 12));
        chart.getStyleManager().setLegendPosition(position);
        chart.getStyleManager().setLegendVisible(true);
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
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        Chart chart = createChart("Cannonball", LegendPosition.InsideNE);
        cannonballTest(chart);
        container.add(new XChartPanel(chart));
        
        container.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        return container;
    }

    @SuppressWarnings("serial")
    public static class Display extends ExampleFrame {
        
        private JComponent container;

        public Display() {
            setTitle("Commons Math: Kalman Filter - Cannonball");
            setSize(800, 600);
            
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
