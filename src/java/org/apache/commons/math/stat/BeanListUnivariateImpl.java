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
package org.apache.commons.math.stat;

import java.util.List;

import org.apache.commons.math.util.BeanTransformer;

/**
 * This implementation of StoreUnivariate uses commons-beanutils to gather
 * univariate statistics for a List of Java Beans by property.  This 
 * implementation uses beanutils' PropertyUtils to get a simple, nested,
 * indexed, mapped, or combined property from an element of a List.
 * @version $Revision: 1.8 $ $Date: 2003/10/13 08:10:56 $
 */
public class BeanListUnivariateImpl extends ListUnivariateImpl {

    /**
     * propertyName of the property to get from the bean
     */
    private String propertyName;

    /**
     * Construct a BeanListUnivariate with specified
     * backing list
     * @param list Backing List
     */
    public BeanListUnivariateImpl(List list) {
        this(list, null);
    }

    /**
     * Construct a BeanListUnivariate with specified
     * backing list and propertyName
     * @param list Backing List
     * @param propertyName Bean propertyName
     */
    public BeanListUnivariateImpl(List list, String propertyName) {
        super(list);
        setPropertyName(propertyName);
    }

    /**
     * @return propertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @param propertyName Name of Property
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
        setTransformer(new BeanTransformer(propertyName));
    }

    /**
      * @see org.apache.commons.math.stat.Univariate#addValue(double)
      */
    public void addValue(double v) {
        String msg =
            "The BeanListUnivariateImpl does not accept values " +
            "through the addValue method.  Because elements of this list " +
            "are JavaBeans, one must be sure to set the 'propertyName' " +
            "property and add new Beans to the underlying list via the " +
            "addBean(Object bean) method";
        throw new UnsupportedOperationException(msg);
    }

    /**
     * Adds a bean to this list. 
     *
     * @param bean Bean to add to the list
     */
    public void addObject(Object bean) {
        list.add(bean);
    }

}
