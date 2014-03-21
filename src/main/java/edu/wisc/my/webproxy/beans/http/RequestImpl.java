/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Created on Mar 3, 2005
 *
 */
package edu.wisc.my.webproxy.beans.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.apache.http.cookie.Cookie;

/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.Request) interface
 * 
 * @author nramzan
 *
 * @version $Id$
 */
public class RequestImpl implements Request {
    private ParameterPair[] postAttributes;
    private IHeader[] headers;
    private List<Cookie> extraCookies = null;
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
    public RequestImpl(ParameterPair[] postAtributes, IHeader[] headers, String url, String type, String authType) {
        this.setParameters(postAtributes);
        this.setHeaders(headers);
        this.setUrl(url);
        this.setType(type);
        this.setAuthType(authType);
    }

    /**
     * Add an additional cookie to be sent as part of the request.
     */
    public void addCookie(final Cookie cookie) {
        if (null == this.extraCookies) {
            this.extraCookies = new ArrayList<Cookie>();
        }
        this.extraCookies.add(cookie);
    }

    /**
     * Get extra cookies to be sent with the request.
     */
    public Collection<Cookie> getExtraCookies() {
        return this.extraCookies;
    }

    /**
     * @see edu.wisc.my.webproxy.beans.http.Request#addHeaders(edu.wisc.my.webproxy.beans.http.IHeader[])
     */
    public void setHeaders(IHeader[] h) {
        headers = h;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Request#getHeaders()
     */
    public IHeader[] getHeaders() {
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
    public IHeader createHeader(String name, String value) {
        return new HeaderImpl(name, value);
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
