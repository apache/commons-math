/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2004 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Geronimo" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Geronimo", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
 *
 * ====================================================================
 */

package org.apache.commons.math.complex;

import junit.framework.TestCase;

public class ComplexFormatTest extends TestCase {
 
	ComplexFormat complexFormat = null;
	ComplexFormat complexFormatJ = null;

    public ComplexFormatTest(String name) {
        super( name );
    }

	protected void setUp() throws Exception {
		complexFormat = new ComplexFormat();
		complexFormatJ = new ComplexFormat("j");
	}
   
    public void testSimpleNoDecimals() {
        Complex c = new Complex(1, 1);
        assertEquals( complexFormat.format( c ), "1 + 1i" );
    }

	public void testSimpleWithDecimals() {
		Complex c = new Complex(1.23, 1.43);
		assertEquals( complexFormat.format( c ), "1.23 + 1.43i" );
	}

	public void testSimpleWithDecimalsTrunc() {
		Complex c = new Complex(1.2323, 1.4343);
		assertEquals( complexFormat.format( c ), "1.23 + 1.43i" );
	}

	public void testNegativeReal() {
		Complex c = new Complex(-1.2323, 1.4343);
		assertEquals( complexFormat.format( c ), "-1.23 + 1.43i" );
	}

	public void testNegativeImaginary() {
		Complex c = new Complex(1.2323, -1.4343);
		assertEquals( complexFormat.format( c ), "1.23 - 1.43i" );
	}

	public void testNegativeBoth() {
		Complex c = new Complex(-1.2323, -1.4343);
		assertEquals( complexFormat.format( c ), "-1.23 - 1.43i" );
	}

	public void testZeroReal() {
		Complex c = new Complex(0.0, -1.4343);
		assertEquals( complexFormat.format( c ), "0 - 1.43i" );
	}

	public void testZeroImaginary() {
		Complex c = new Complex(30.233, 0);
		assertEquals( complexFormat.format( c ), "30.23" );
	}

	public void testDifferentImaginaryChar() {
		Complex c = new Complex(1, 1);
		assertEquals( complexFormatJ.format( c ), "1 + 1j" );
	}
	
	public void testStaticFormatComplex() {
		Complex c = new Complex(232.222, -342.33);
		assertEquals( ComplexFormat.formatComplex( c ), "232.22 - 342.33i" );
	}

}
