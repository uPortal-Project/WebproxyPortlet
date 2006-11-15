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
 * Created on Mar 17, 2005
 *
 */
package edu.wisc.my.webproxy.beans.http;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.httpclient.Cookie;

/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.Cookie) interface
 * 
 * @author nramzan
 * 
 * @version $Id$
 *
 */
public class HttpClientCookieImpl implements
		edu.wisc.my.webproxy.beans.http.Cookie, Serializable {

    final Cookie httpClientCookie;

    protected HttpClientCookieImpl(Cookie wrappedCookie) {
        this.httpClientCookie = wrappedCookie;
    }
	
    /**
     * Returns the wrapped HTTPClient's Cookie object 
     */
    
    protected Cookie getWrappedCookie() {
        return this.httpClientCookie;
    }
	
    /**
     * Default constructor. Creates a blank cookie 
     */
    
    public HttpClientCookieImpl()
	{
	
		Cookie cookie = new Cookie();
		this.httpClientCookie = cookie;
	}
	
	/**
     * Creates a cookie with the given name, value and domain attribute.
     *
     * @param name    the cookie name
     * @param value   the cookie value
     * @param domain  the domain this cookie can be sent to
     */
	
	public HttpClientCookieImpl(String domain, String name, String value)
	{
		
		Cookie cookie = new Cookie(domain, name, value);
		
		this.httpClientCookie = cookie;
	}
	
	/**
     * Creates a cookie with the given name, value, domain attribute,
     * path attribute, expiration attribute, and secure attribute 
     *
     * @param name    the cookie name
     * @param value   the cookie value
     * @param domain  the domain this cookie can be sent to
     * @param path    the path prefix for which this cookie can be sent
     * @param expires the {@link Date} at which this cookie expires,
     *                or <tt>null</tt> if the cookie expires at the end
     *                of the session
     * @param secure if true this cookie can only be sent over secure
     * connections
     *   
     */
	
	
	public HttpClientCookieImpl(String domain, String name, String value, String path, Date expires, boolean secure)
	{
		Cookie cookie = new Cookie(domain, name, value, path, expires, secure);
		
		this.httpClientCookie = cookie;
	}
	
	/**
     * Creates a cookie with the given name, value, domain attribute,
     * path attribute, maximum age attribute, and secure attribute 
     *
     * @param name   the cookie name
     * @param value  the cookie value
     * @param domain the domain this cookie can be sent to
     * @param path   the path prefix for which this cookie can be sent
     * @param maxAge the number of seconds for which this cookie is valid.
     *               maxAge is expected to be a non-negative number. 
     *               <tt>-1</tt> signifies that the cookie should never expire.
     * @param secure if <tt>true</tt> this cookie can only be sent over secure
     * connections
     */
	
	public HttpClientCookieImpl(String domain, String name, String value, String path, int maxAge, boolean secure)
	{
		
		Cookie cookie = new Cookie(domain, name, value, path, maxAge, secure);
		
		this.httpClientCookie = cookie;
	}
	

	public Date getExpiryDate() {
		return this.httpClientCookie.getExpiryDate();
	}

	public void setExpiryDate(Date expiry) {
		this.httpClientCookie.setExpiryDate(expiry);

	}
	
	public int getVersion()
	{
		return this.httpClientCookie.getVersion();
	}

	public String getComment() {
		
		return this.httpClientCookie.getComment();
	}
	
	public String getDomain() {
		
		return this.httpClientCookie.getDomain();
	}
	
	public String getName() {
		
		return this.httpClientCookie.getName();
	}
	
	public String getPath() {

		return this.httpClientCookie.getPath();
	}
	
	public boolean getSecure() {
		
		return this.httpClientCookie.getSecure();
	}
	
	public String getValue() {

		return this.httpClientCookie.getValue();
	}
	
	public boolean isExpired() {
		
		return this.httpClientCookie.isExpired();
	}
    
    
    public void setName(String name) {
        this.httpClientCookie.setName(name);
    }
    
	public void setComment(String purpose) {
	
		this.httpClientCookie.setComment(purpose);

	}
	
	public void setDomain(String pattern) {
		
		this.httpClientCookie.setDomain(pattern);

	}
	
	public void setPath(String uri) {
		
		this.httpClientCookie.setPath(uri);

	}
	
	public void setSecure(boolean flag) {
		
		this.httpClientCookie.setSecure(flag);

	}
	
	public void setValue(String newValue) {
	
		this.httpClientCookie.setValue(newValue);

	}
	
	public void setVersion(int v) {
	
		this.httpClientCookie.setVersion(v);

	}

}
