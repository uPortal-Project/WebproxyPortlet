/*******************************************************************************
* Copyright 2004, The Board of Regents of the University of Wisconsin System.
* All rights reserved.
*
* A non-exclusive worldwide royalty-free license is granted for this Software.
* Permission to use, copy, modify, and distribute this Software and its
* documentation, with or without modification, for any purpose is granted
* provided that such redistribution and use in source and binary forms, with or
* without modification meets the following conditions:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* 3. Redistributions of any form whatsoever must retain the following
* acknowledgement:
*
* "This product includes software developed by The Board of Regents of
* the University of Wisconsin System."
*
*THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
*WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
*BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
*PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS OF
*THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
*INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
*PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
*LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
*OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
*ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/
package edu.wisc.my.webproxy.beans.config;


/**
 * A set of utility methods for working with common configuration
 * objects.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public final class ConfigUtils {
    private ConfigUtils() {}
    
    /**
     * Takes the val String and returns the trim() version if it isn't null. If
     * null the nulVal String is returned.
     * 
     * @param val String to test.
     * @param nullVal Default value if val String is null.
     * @return The trim() version of val or nullVal if val is null.
     */
    public static String checkEmptyNullString(String val, String nullVal) {
        if (val != null && val.trim().length() > 0)
            return val.trim();
        else
            return nullVal;
    }
    
    
    public static String[] checkNullStringArray(String[] vals, String[] nullVal) {
        if (vals != null)
            return vals;
        else
            return nullVal;
    }
    
    public static String[] checkArrayForNulls(String[] vals, String nullVal) {
        for (int index = 0; index < vals.length; index++) {
            vals[index] = checkEmptyNullString(vals[index], nullVal);
        }

        return vals;
    }
    
    public static boolean[] toBooleanArray(String[] vals, boolean[] nullVal) {
        if (vals == null)
            return nullVal;

        final boolean[] retVals = new boolean[vals.length];
        
        for (int index = 0; index < vals.length; index++) {
            retVals[index] = new Boolean(vals[index]).booleanValue();
        }
        
        return retVals;
    }
    
    public static int parseInt(String num, int defaultVal) {
        return parseInteger(num, new Integer(defaultVal)).intValue();
    }
    
    public static Integer parseInteger(String num, Integer defaultVal) {
        if (num == null || num.length() == 0)
            return defaultVal;
        
        try {
            return new Integer(num);
        }
        catch (NumberFormatException nfe) {
            return defaultVal;
        }
    }
}
