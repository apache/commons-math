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

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.math.MathException;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Uses PropertyUtils to map a Bean getter to a double value.
 * @version $Revision: 1.4 $ $Date: 2004/02/21 21:35:18 $
 */
public class BeanTransformer implements NumberTransformer {

    /**
     * The propertyName for this Transformer
     */
    private String propertyName;

    /**
     * Create a BeanTransformer
     */
    public BeanTransformer() {
        this(null);
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
        propertyName = string;
    }

    /**
     * @see org.apache.commons.math.util.NumberTransformer#transform(java.lang.Object)
     */
    public double transform(final Object o) throws MathException {
        try {
			return ((Number) PropertyUtils.getProperty(o, getPropertyName())).doubleValue();
        } catch (IllegalAccessException e) {
			throw new MathException("IllegalAccessException in Transformation: " + e.getMessage(), e);
        } catch (InvocationTargetException e) {
			throw new MathException("InvocationTargetException in Transformation: " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
			throw new MathException("oSuchMethodException in Transformation: " + e.getMessage(), e);
        }
    }
}