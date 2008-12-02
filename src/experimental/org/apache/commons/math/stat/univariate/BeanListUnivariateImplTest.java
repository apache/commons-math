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
package org.apache.commons.math.stat.univariate;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.TestUtils;
import org.apache.commons.math.stat.StatUtils;

/**
 * Test cases for the {@link BeanListUnivariateImpl} class.
 *
 * @version $Revision$ $Date$
 */

public final class BeanListUnivariateImplTest extends TestCase {
    
    private double one = 1;
    private float two = 2;
    private int three = 3;
    private double mean = 2;
    private double sumSq = 18;
    private double sum = 8;
    private double var = 0.666666666666666666667;
    private double std = Math.sqrt(var);
    private double n = 4;
    private double min = 1;
    private double max = 3;
    private double skewness = 0;
    private double kurtosis = 0.5;
    private double tolerance = 10E-15;
    
    
    private List patientList = null;
    
    public BeanListUnivariateImplTest(String name) {
        super(name);
    }
    
    public void setUp() {  
        patientList = new ArrayList();

        // Create and add patient bean 1
        VitalStats vs1 = new VitalStats( Double.valueOf(120.0), 
                                         Double.valueOf(96.4) );
        Patient p1 = new Patient( vs1, Integer.valueOf( 35 ) );
        patientList.add( p1 );

        // Create and add patient bean 2
        VitalStats vs2 = new VitalStats( Double.valueOf(70.0), 
                                         Double.valueOf(97.4) );
        Patient p2 = new Patient( vs2, Integer.valueOf( 23 ) );
        patientList.add( p2 );

        // Create and add patient bean 3
        VitalStats vs3 = new VitalStats( Double.valueOf(90.0), 
                                         Double.valueOf(98.6) );
        Patient p3 = new Patient( vs3, Integer.valueOf( 42 ) );
        patientList.add( p3 );
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BeanListUnivariateImplTest.class);
        suite.setName("Frequency Tests");
        return suite;
    }
    
    /** test stats */
    public void testStats() {	
        DescriptiveStatistics u = new BeanListUnivariateImpl( patientList, "age" ); 
        double[] values = {35d, 23d, 42d};
        assertEquals("total count",3,u.getN(),tolerance);
        assertEquals("mean", StatUtils.mean(values), u.getMean(), tolerance);
        assertEquals("min", StatUtils.min(values), u.getMin(), tolerance);
        assertEquals("max", StatUtils.max(values), u.getMax(), tolerance);
        assertEquals("var", StatUtils.variance(values), u.getVariance(), tolerance);       
        u.clear();
        assertEquals("total count",0,u.getN(),tolerance);    
    }   
    
    public void testPropStats() {

        DescriptiveStatistics heartU = new BeanListUnivariateImpl( patientList,
                                          "vitalStats.heartRate" );       

        assertEquals( "Mean heart rate unexpected", 93.333, 
                      heartU.getMean(), 0.001 );
        assertEquals( "Max heart rate unexpected", 120.0, 
                      heartU.getMax(), 0.001 );

        DescriptiveStatistics ageU = new BeanListUnivariateImpl( patientList,
                                                           "age" );

        assertEquals( "Mean age unexpected", 33.333,
                      ageU.getMean(), 0.001 );
        assertEquals( "Max age unexpected", 42.0,
                      ageU.getMax(), 0.001 );

    }
    
    public void testSetPropertyName(){
        BeanListUnivariateImpl u = new BeanListUnivariateImpl(null);
        String expected = "property";
        u.setPropertyName(expected);
        assertEquals(expected, u.getPropertyName());
    }
    
    public void testAddValue() {
        DescriptiveStatistics u = new BeanListUnivariateImpl( patientList, "age" ); 
        u.addValue(10);
        double[] values = {35d, 23d, 42d, 10d};
        assertEquals("total count",4,u.getN(),tolerance);
        assertEquals("mean", StatUtils.mean(values), u.getMean(), tolerance);
        assertEquals("min", StatUtils.min(values), u.getMin(), tolerance);
        assertEquals("max", StatUtils.max(values), u.getMax(), tolerance);
        assertEquals("var", StatUtils.variance(values), u.getVariance(), tolerance);       
        u.clear();
        assertEquals("total count",0,u.getN(),tolerance);      
    }
    
    /** test stats */
    public void testSerialization() {
        
        double[] values = {35d, 23d, 42d};
        
        DescriptiveStatistics u = new BeanListUnivariateImpl( patientList, "age" ); 
        assertEquals("total count",3,u.getN(),tolerance);
        assertEquals("mean", StatUtils.mean(values), u.getMean(), tolerance);
        assertEquals("min", StatUtils.min(values), u.getMin(), tolerance);
        assertEquals("max", StatUtils.max(values), u.getMax(), tolerance);
        assertEquals("var", StatUtils.variance(values), u.getVariance(), tolerance);   
        
        
        DescriptiveStatistics u2 = (DescriptiveStatistics)TestUtils.serializeAndRecover(u); 
        assertEquals("total count",3,u2.getN(),tolerance);
        assertEquals("mean", StatUtils.mean(values), u2.getMean(), tolerance);
        assertEquals("min", StatUtils.min(values), u2.getMin(), tolerance);
        assertEquals("max", StatUtils.max(values), u2.getMax(), tolerance);
        assertEquals("var", StatUtils.variance(values), u2.getVariance(), tolerance);   

        u.clear();
        assertEquals("total count",0,u.getN(),tolerance);    
        
        u2.clear();
        assertEquals("total count",0,u2.getN(),tolerance);
            
    }    
    
    public class VitalStats {

        private Double heartrate;
        private Double temperature;

        public VitalStats() {
        }

        public VitalStats(Double heartrate, Double temperature) {
            setHeartRate( heartrate );
            setTemperature( temperature );
        }

        public Double getHeartRate() {
            return heartrate;
        }

        public void setHeartRate(Double heartrate) {
            this.heartrate = heartrate;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
    }
    
    public class Patient {

        private VitalStats vitalStats;
        private Integer age;

        public Patient() {
        }

        public Patient(VitalStats vitalStats, Integer age) {
            setVitalStats( vitalStats );
            setAge( age );
        }

        public VitalStats getVitalStats() {
            return( vitalStats );
        }

        public void setVitalStats(VitalStats vitalStats) {
            this.vitalStats = vitalStats;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}

