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

import java.math.BigDecimal;

import org.apache.commons.math.TestUtils;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.2 $ $Date: 2003/10/13 08:07:11 $
 */
public class DefaultTransformerTest extends TestCase {
    /**
     * 
     */
    public void testTransformDouble(){
        double expected = 1.0;
        Double input = new Double(expected);
        DefaultTransformer t = new DefaultTransformer();
        assertEquals(expected, t.transform(input), 1.0e-4);
    }
    
    /**
     * 
     */
    public void testTransformNull(){
        double expected = Double.NaN;
        DefaultTransformer t = new DefaultTransformer();
        TestUtils.assertEquals(expected, t.transform(null), 1.0e-4);
    }
    
    /**
     * 
     */
    public void testTransformInteger(){
        double expected = 1.0;
        Integer input = new Integer(1);
        DefaultTransformer t = new DefaultTransformer();
        assertEquals(expected, t.transform(input), 1.0e-4);
    }        
    
    /**
     * 
     */
    public void testTransformBigDecimal(){
        double expected = 1.0;
        BigDecimal input = new BigDecimal("1.0");
        DefaultTransformer t = new DefaultTransformer();
        assertEquals(expected, t.transform(input), 1.0e-4);
    }        
    
    /**
     * 
     */
    public void testTransformString(){
        double expected = 1.0;
        String input = "1.0";
        DefaultTransformer t = new DefaultTransformer();
        assertEquals(expected, t.transform(input), 1.0e-4);
    }
    
    /**
     * 
     */
    public void testTransformObject(){
        double expected = Double.NaN;
        Boolean input = Boolean.TRUE;
        DefaultTransformer t = new DefaultTransformer();
        TestUtils.assertEquals(expected, t.transform(input), 1.0e-4);
    }
}
