/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This TansformerMap automates the transformation of of mixed object types.
 * It provides a means to set NumberTransformers that will be selected 
 * based on the Class of the object handed to the Maps
 * <code>double transform(Object o)</code> method.
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:11:23 $
 */
public class TransformerMap implements NumberTransformer {

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
    public double transform(Object o) {
        double value = Double.NaN;

        try {
            if (o instanceof Number || o instanceof String) {
                value = defaultTransformer.transform(o);
            } else {
                NumberTransformer trans =
                    (NumberTransformer) this.getTransformer(o.getClass());
                if (trans != null) {
                    value = trans.transform(o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

}