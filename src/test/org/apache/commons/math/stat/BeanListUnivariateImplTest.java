/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.math.beans.*;

/**
 * Test cases for the {@link BeanListUnivariateImpl} class.
 *
 * @version $Revision: 1.12 $ $Date: 2004/02/21 21:35:17 $
 */

public final class BeanListUnivariateImplTest extends TestCase {
    
    private List patientList = null;
    private double tolerance = Double.MIN_VALUE;
    
    public BeanListUnivariateImplTest(String name) {
        super(name);
    }
    
    public void setUp() {  
        patientList = new ArrayList();

        // Create and add patient bean 1
        VitalStats vs1 = new VitalStats( new Double(120.0), 
                                         new Double(96.4) );
        Patient p1 = new Patient( vs1, new Integer( 35 ) );
        patientList.add( p1 );

        // Create and add patient bean 2
        VitalStats vs2 = new VitalStats( new Double(70.0), 
                                         new Double(97.4) );
        Patient p2 = new Patient( vs2, new Integer( 23 ) );
        patientList.add( p2 );

        // Create and add patient bean 3
        VitalStats vs3 = new VitalStats( new Double(90.0), 
                                         new Double(98.6) );
        Patient p3 = new Patient( vs3, new Integer( 42 ) );
        patientList.add( p3 );
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BeanListUnivariateImplTest.class);
        suite.setName("Frequency Tests");
        return suite;
    }
    
    /** test stats */
    public void testStats() {
    	
        DescriptiveStatistics u = new BeanListUnivariateImpl( patientList ); 

        assertEquals("total count",3,u.getN(),tolerance);

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
}

