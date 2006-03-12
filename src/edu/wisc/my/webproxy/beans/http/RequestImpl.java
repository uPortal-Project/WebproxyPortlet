/*******************************************************************************
 * Copyright 2004, The Board of Regents of the University of Wisconsin System.
 * All rights reserved.
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
 * Created on Mar 3, 2005
 *
 */
package edu.wisc.my.webproxy.beans.http;


/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.Request) interface
 * 
 * @author nramzan
 *
 * @version $Id$
 */
public class RequestImpl implements Request {
    private State state;
    private ParameterPair[] postAttributes;
    private Header[] headers;
    private String url;
    private String type;
    private String authType;

    public RequestImpl() {

    }

    /**
     * @param state
     * @param postAtributes
     * @param headers
     * @param url
     * @param type
     * @param authType
     */
    public RequestImpl(State state, ParameterPair[] postAtributes, Header[] headers, String url, String type, String authType) {
        this.setState(state);
        this.setParameters(postAtributes);
        this.setHeaders(headers);
        this.setUrl(url);
        this.setType(type);
        this.setAuthType(authType);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#setState(edu.wisc.my.webproxy.beans.http.State)
     */
    public void setState(State s) {
        this.state = s;

    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#getState()
     */
    public State getState() {
        if (state == null)
            state = new HttpClientStateImpl();

        return state;
    }

    /**
     * @see edu.wisc.my.webproxy.beans.http.Request#addHeaders(edu.wisc.my.webproxy.beans.http.Header[])
     */
    public void setHeaders(Header[] h) {
        headers = h;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#getHeaders()
     */
    public Header[] getHeaders() {
        return this.headers;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#setUrl(java.lang.String)
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#getUrl()
     */
    public String getUrl() {
        return url;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#setType(java.lang.String)
     */
    public void setType(String type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#getType()
     */
    public String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#createHeader()
     */
    public Header createHeader() {
        return new HeaderImpl();
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#setAttributes(java.lang.String[])
     */
    public void setParameters(ParameterPair[] sPostAttributes) {
        this.postAttributes = sPostAttributes;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#getAttributes()
     */
    public ParameterPair[] getParameters() {
        return this.postAttributes;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#setAuthType(java.lang.String)
     */
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#getAuthType()
     */
    public String getAuthType() {
        return this.authType;
    }
}