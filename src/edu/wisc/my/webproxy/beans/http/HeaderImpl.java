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
/*
 * Created on Mar 21, 2005
 *
 */
package edu.wisc.my.webproxy.beans.http;

import org.apache.commons.httpclient.Header;

/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.Header) interface
 * 
 * @author nramzan
 *
 * @version $Id$
 */
public class HeaderImpl implements edu.wisc.my.webproxy.beans.http.Header {
    private Header httpClientHeader;

    //Default Constructor
    public HeaderImpl() {
        Header header = new Header();
        this.httpClientHeader = header;
    }

    /**
     * The constructor with the name and value arguments.
     * 
     * @param name
     * @param value
     */
    public HeaderImpl(String name, String value) {
        Header header = new Header(name, value);
        this.httpClientHeader = header;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Header#getName()
     */
    public String getName() {
        return this.httpClientHeader.getName();
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Header#getValue()
     */
    public String getValue() {
        return this.httpClientHeader.getValue();
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Header#setName(java.lang.String)
     */
    public void setName(String name) {
        this.httpClientHeader.setName(name);

    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Header#setValue(java.lang.String)
     */
    public void setValue(String value) {
        this.httpClientHeader.setValue(value);
    }

}