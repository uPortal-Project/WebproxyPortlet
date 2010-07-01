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
package edu.wisc.my.webproxy.beans.http;

import java.io.InputStream;

/**
 * Represents the data retrieved from a HTTP request.
 * 
 * @author nramzan
 * 
 * @version $Id$
 * 
 */
public interface Response {
    //status constants
    public final static int SC_MOVED_TEMPORARILY = 302;
    public final static int SC_MOVED_PERMANENTLY = 301;
    public final static int SC_SEE_OTHER = 303;
    public final static int SC_TEMPORARY_REDIRECT = 307;
    
    /** 
     * Retrieves the body of the response as an inputstream.
     *
     * @return is the (@link InputStream) the whole body of the response as a InputStream.
     * 
     */
    
    InputStream getResponseBodyAsStream();
    
    /** 
     * Retrieves the type of the content of response as a String
     *
     * @return contentType the content type of the response as a String.
     * 
     */
    String getContentType();
    
    /** 
     * Retrieves all the response headers as an array of Headers
     *
     * @return headers an array of response headers.
     * 
     */
    
    IHeader[] getHeaders();
    
    /** 
     * Retrieves the status code that is returned when a request is made
     *
     * @return  statusCode a numeric number as an int representing the status code
     * 
     */
    
    int getStatusCode();
    
    String getRequestUrl();
    
    /** 
     * Releases the HTTP connection made to the remote server
     *
     * @param  none
     * 
     */

    void close();
}

