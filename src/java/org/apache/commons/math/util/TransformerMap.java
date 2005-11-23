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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.MathException;

/**
 * This TansformerMap automates the transformation of of mixed object types.
 * It provides a means to set NumberTransformers that will be selected 
 * based on the Class of the object handed to the Maps
 * <code>double transform(Object o)</code> method.
 * @version $Revision$ $Date$
 */
public class TransformerMap implements NumberTransformer, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -942772950698439883L;
    
    /**
     * A default Number Transformer for Numbers and numeric Strings.
     */
    private NumberTransformer defaultTransformer = null;

    /**
     * The internal Map.
     */
    private Map map = null;

    /**
     * 
     */
    public TransformerMap() {
        map = new HashMap();
        defaultTransformer = new DefaultTransformer();
    }

    /**
     * Tests if a Class is present in the TransformerMap.
     * @param key Class to check
     * @return true|false
     */
    public boolean containsClass(Class key) {
        return map.containsKey(key);
    }

    /**
     * Tests if a NumberTransformer is present in the TransformerMap.
     * @param value NumberTransformer to check
     * @return true|false
     */
    public boolean containsTransformer(NumberTransformer value) {
        return map.containsValue(value);
    }

    /**
     * Returns the Transformer that is mapped to a class
     * if mapping is not present, this returns null.
     * @param key The Class of the object
     * @return the mapped NumberTransformer or null.
     */
    public NumberTransformer getTransformer(Class key) {
        return (NumberTransformer) map.get(key);
    }

    /**
     * Sets a Class to Transformer Mapping in the Map. If
     * the Class is already present, this overwrites that
     * mapping.
     * @param key The Class
     * @param transformer The NumberTransformer
     * @return the replaced transformer if one is present
     */
    public Object putTransformer(Class key, NumberTransformer transformer) {
        return map.put(key, transformer);
    }

    /**
     * Removes a Class to Transformer Mapping in the Map.
     * @param key The Class
     * @return the removed transformer if one is present or
     * null if none was present.
     */
    public Object removeTransformer(Class key) {
        return map.remove(key);
    }

    /**
     * Clears all the Class to Transformer mappings.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns the Set of Classes used as keys in the map.
     * @return Set of Classes
     */
    public Set classes() {
        return map.keySet();
    }

    /**
     * Returns the Set of NumberTransformers used as values 
     * in the map.
     * @return Set of NumberTransformers
     */
    public Collection transformers() {
        return map.values();
    }

    /**
     * Attempts to transform the Object against the map of
     * NumberTransformers. Otherwise it returns Double.NaN.
     * 
     * @see org.apache.commons.math.util.NumberTransformer#transform(java.lang.Object)
     */
    public double transform(Object o) throws MathException {
        double value = Double.NaN;

        if (o instanceof Number || o instanceof String) {
            value = defaultTransformer.transform(o);
        } else {
            NumberTransformer trans = getTransformer(o.getClass());
            if (trans != null) {
                value = trans.transform(o);
            }
        }

        return value;
    }

}