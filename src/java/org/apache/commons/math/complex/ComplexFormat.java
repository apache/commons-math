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

import java.text.NumberFormat;

// @TODO Maybe, eventually at least, this should work with NumberFormat
//       but in the mean time.  This scratches an itch
public class ComplexFormat {

	private static final ComplexFormat DEFAULT = new ComplexFormat();

	// @TODO This class only allows for max fraction digits, we might want to allow other parameters

    private String imaginaryCharacter = "i";
    private int fractionDigits = 2;

    public ComplexFormat() {}

    public ComplexFormat(String imaginaryCharacter) {
        this.imaginaryCharacter = imaginaryCharacter;
    }

    public ComplexFormat(String imaginaryCharacter, int fractionDigits) {
        this.imaginaryCharacter = imaginaryCharacter;
        this.fractionDigits = fractionDigits;
    }

	// @TODO Javadoc for this format method
    public String format(Complex c) {

		// @TODO What happens when either a real or imaginary is NaN, INIFINITY, etc?

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits( fractionDigits );

        StringBuffer buffer = new StringBuffer();

        buffer.append( format.format( c.getReal() ) );

        if( c.getImaginary() < 0 ) {
            buffer.append( " - " );
            buffer.append( format.format( Math.abs(c.getImaginary()) ) );
            buffer.append( imaginaryCharacter );
        } else if( c.getImaginary() > 0 ) {
            buffer.append( " + " );
            buffer.append( format.format( c.getImaginary() ) );
            buffer.append( imaginaryCharacter );
        }            
        
        return( buffer.toString() );

    }
    
    public static String formatComplex( Complex c ) {
    	return DEFAULT.format( c );
    }
}


    
