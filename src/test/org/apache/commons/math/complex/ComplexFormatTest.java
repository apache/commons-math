/*
 * Copyright 2004,2004 The Apache Software Foundation.
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
