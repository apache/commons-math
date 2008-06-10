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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.math.MathException;
import org.apache.commons.math.util.NumberTransformer;

/**
 * This implementation of DescriptiveStatistics uses commons-beanutils to gather
 * univariate statistics for a List of Java Beans by property.  This 
 * implementation uses beanutils' PropertyUtils to get a simple, nested,
 * indexed, mapped, or combined property from an element of a List.
 * @version $Revision$ $Date$
 */
public class BeanListUnivariateImpl extends ListUnivariateImpl implements Serializable {

    /** Serializable version identifier */
    static final long serialVersionUID = -6428201899045406285L;
    
	/**
	 * propertyName of the property to get from the bean
	 */
	private String propertyName;

	/**
	 * No argument Constructor
	 */
	public BeanListUnivariateImpl(){
	    this(new ArrayList());
	}
	
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
		this.transformer = new NumberTransformer() {

			/**
			 * @see org.apache.commons.math.util.NumberTransformer#transform(java.lang.Object)
			 */
			public double transform(final Object o) throws MathException {
				try {
					return (
						(Number) PropertyUtils.getProperty(
							o,
							getPropertyName()))
						.doubleValue();
				} catch (IllegalAccessException e) {
					throw new MathException(
						"IllegalAccessException in Transformation: "
							+ e.getMessage(),
						e);
				} catch (InvocationTargetException e) {
					throw new MathException(
						"InvocationTargetException in Transformation: "
							+ e.getMessage(),
						e);
				} catch (NoSuchMethodException e) {
					throw new MathException(
						"oSuchMethodException in Transformation: "
							+ e.getMessage(),
						e);
				}
			}
		};
	}

	/**
	  *  Creates a {@link org.apache.commons.beanutils.DynaBean} with a 
	  *  {@link org.apache.commons.beanutils.DynaProperty} named 
	  *  <code>propertyName,</code> sets the value of the property to <code>v</code>
	  *  and adds the DynaBean to the underlying list.
	  *
	  */
	public void addValue(double v)  {
	    DynaProperty[] props = new DynaProperty[] {
	            new DynaProperty(propertyName, Double.class)
	    };
	    BasicDynaClass dynaClass = new BasicDynaClass(null, null, props);
	    DynaBean dynaBean = null;
	    try {
	        dynaBean = dynaClass.newInstance();
	    } catch (Exception ex) {              // InstantiationException, IllegalAccessException
	        throw new RuntimeException(ex);   // should never happen
	    }
		dynaBean.set(propertyName, Double.valueOf(v));
		addObject(dynaBean);
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
