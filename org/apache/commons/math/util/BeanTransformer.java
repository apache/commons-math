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
package org.apache.commons.math.util;

import java.beans.Expression;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.math.MathException;

/**
 * Uses PropertyUtils to map a Bean getter to a double value.
 * @version $Revision$ $Date$
 */
public class BeanTransformer implements NumberTransformer {

    /**
     * The propertyName for this Transformer
     */
    private String propertyName = null;
    
    private String propertyGetter = null;

    /**
     * Create a BeanTransformer
     */
    public BeanTransformer() {
        super();
    }

    /**
     * Create a BeanTransformer with a specific PropertyName.
     * @param property The property.
     */
    public BeanTransformer(final String property) {
        super();
        setPropertyName(property);
    }

    /**
     * Get the property String
     * @return the Property Name String
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Set the propertyString
     * @param string The string to set the property to.
     */
    public void setPropertyName(final String string) {
        this.propertyName = string;
        this.propertyGetter = "get" + string.substring(0,1).toUpperCase() + string.substring(1);
    }

    
    /**
     * @see org.apache.commons.math.util.NumberTransformer#transform(java.lang.Object)
     */
    public double transform(final Object o) throws MathException {
        Expression expr = new Expression(o, propertyGetter, new Object[0]);
        Object result;
        try {
            expr.execute();
            result = expr.getValue();
        } catch (IllegalAccessException e) {
			throw new MathException("IllegalAccessException in Transformation: " + e.getMessage(), e);
        } catch (InvocationTargetException e) {
			throw new MathException("InvocationTargetException in Transformation: " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
			throw new MathException("NoSuchMethodException in Transformation: " + e.getMessage(), e);
        } catch (ClassCastException e) {
            throw new MathException("ClassCastException in Transformation: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new MathException("Exception in Transformation: " + e.getMessage(), e);
        }
        
        return ((Number) result).doubleValue();
    }
}