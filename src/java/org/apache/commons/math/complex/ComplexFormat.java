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


    
