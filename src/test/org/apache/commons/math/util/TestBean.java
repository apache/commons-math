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

package org.apache.commons.math.util;

/**
 * @version $Revision$ $Date$
 */
public class TestBean {
    private Double x = new Double(1.0);
    
    private String y = "1.0";
    
    private Double z = new Double(2.0);
    
    /**
     * 
     */
    public Double getX() {
        return x;
    }

    /**
     * 
     */
    public String getY() {
        return y;
    }

    /**
     * 
     */
    public void setX(Double double1) {
        x = double1;
    }

    /**
     * 
     */
    public void setY(String string) {
        y = string;
    }
    
    /**
     * 
     */
    public Double getZ() {
        throw new RuntimeException();
    }

    /**
     * 
     */
    public void setZ(Double double1) {
        z = double1;
    }

}
