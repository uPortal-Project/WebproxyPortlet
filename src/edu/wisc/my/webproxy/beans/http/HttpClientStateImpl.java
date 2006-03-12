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
 * Created on Mar 16, 2005
 *
 */
package edu.wisc.my.webproxy.beans.http;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.State) interface
 * 
 * @author nramzan
 *
 * @version $Id$
 *
 */
public class HttpClientStateImpl implements State, Serializable {
    private transient HttpState httpClientState;
 
    /**
     * The default constructor for State Object.
     */
    
    public HttpClientStateImpl() {
        this.httpClientState = new HttpState();
    } 
    
    /**
     * Constructor that takes HTTPClient's state object as a argument
     */
    
    public HttpClientStateImpl(HttpState state) {
        this.httpClientState = state;
    }
    
    
 
    public Credentials getUserCredentials() {
        final org.apache.commons.httpclient.Credentials creds = this.httpClientState.getCredentials(AuthScope.ANY);
        
        if (creds instanceof UsernamePasswordCredentials) {
            final UsernamePasswordCredentials upCreds = (UsernamePasswordCredentials)creds;
            return new Credentials(upCreds.getUserName(), upCreds.getPassword());
        }
        else if (creds instanceof org.apache.commons.httpclient.NTCredentials) {
            final org.apache.commons.httpclient.NTCredentials ntCreds = (org.apache.commons.httpclient.NTCredentials)creds;
            return new NtCredentials(ntCreds.getUserName(), ntCreds.getPassword(), ntCreds.getHost(), ntCreds.getDomain());
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.State#getProxyCredentials()
     */
    public Credentials getNTCredentials() {
        final org.apache.commons.httpclient.Credentials creds = this.httpClientState.getProxyCredentials(AuthScope.ANY);
        
        if (creds instanceof UsernamePasswordCredentials) {
            final UsernamePasswordCredentials upCreds = (UsernamePasswordCredentials)creds;
            return new Credentials(upCreds.getUserName(), upCreds.getPassword());
        }
        else if (creds instanceof org.apache.commons.httpclient.NTCredentials) {
            final org.apache.commons.httpclient.NTCredentials ntCreds = (org.apache.commons.httpclient.NTCredentials)creds;
            return new NtCredentials(ntCreds.getUserName(), ntCreds.getPassword(), ntCreds.getHost(), ntCreds.getDomain());
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.State#setCookies(edu.wisc.my.webproxy.beans.http.Cookie[])
     */
    public void addCookies(Cookie[] cookies) {

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                this.addCookie(cookies[i]);
            }
        }

    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.State#setCredentials(edu.wisc.my.webproxy.beans.http.Credentials)
     */
    public void setUserCredentials(Credentials c) {
        if (c instanceof NtCredentials) {
            final NtCredentials myCreds = (NtCredentials)c;
            final org.apache.commons.httpclient.NTCredentials ntCreds = new org.apache.commons.httpclient.NTCredentials(myCreds.getUserName(), myCreds.getPassword(), myCreds.getHost(), myCreds.getDomain());
            
            this.httpClientState.setCredentials(AuthScope.ANY, ntCreds);
        }
        else {
            final UsernamePasswordCredentials creds = new UsernamePasswordCredentials(c.getUserName(), c.getPassword());
            
            this.httpClientState.setCredentials(AuthScope.ANY, creds);
        }
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.State#setProxyCredentials(edu.wisc.my.webproxy.beans.http.Credentials)
     */
    public void setNTCredentials(Credentials c) {
        if (c instanceof NtCredentials) {
            final NtCredentials myCreds = (NtCredentials)c;
            final org.apache.commons.httpclient.NTCredentials ntCreds = new org.apache.commons.httpclient.NTCredentials(myCreds.getUserName(), myCreds.getPassword(), myCreds.getHost(), myCreds.getDomain());
            
            this.httpClientState.setProxyCredentials(AuthScope.ANY, ntCreds);
        }
        else {
            final UsernamePasswordCredentials creds = new UsernamePasswordCredentials(c.getUserName(), c.getPassword());
            
            this.httpClientState.setProxyCredentials(AuthScope.ANY, creds);
        }
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.State#createCookie()
     */
    public Cookie createCookie() {
        Cookie cookie = new HttpClientCookieImpl();
        return cookie;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.State#createCredentials()
     */
    public Credentials createUserCredentials(String userName, String password) {
        Credentials credentials = new Credentials(userName, password);
        return credentials;
    }
    
    public NTCredentials createNTCredentials(String userName, String password, String domain, String host) {
        NTCredentials credentails = new NTCredentials(userName, password, host, domain);
        return credentails;
    }

    public void addCookie(Cookie cookie) {
        if (cookie instanceof HttpClientCookieImpl) {
            this.httpClientState.addCookie(((HttpClientCookieImpl)cookie).getWrappedCookie());
        }
        else {
            final org.apache.commons.httpclient.Cookie c = new org.apache.commons.httpclient.Cookie();
            
            c.setComment(cookie.getComment());
            c.setDomain(cookie.getDomain());
            c.setExpiryDate(cookie.getExpiryDate());
            c.setName(cookie.getName());
            c.setPath(cookie.getPath());
            c.setSecure(cookie.getSecure());
            c.setValue(cookie.getValue());
            c.setVersion(cookie.getVersion());
            
            this.httpClientState.addCookie(c);
        }
    }

    public Cookie[] getCookies() {
        final org.apache.commons.httpclient.Cookie[] cookies = this.httpClientState.getCookies();
        final Cookie[] myCookies = new Cookie[cookies.length];
        
        for (int index = 0; index < myCookies.length; index++) {
            myCookies[index] = new HttpClientCookieImpl(cookies[index]);
        }
        
        return myCookies;
    }

    public void clearCookies() {
        this.httpClientState.clear();
    }

    protected HttpState getWrappedState() {
        return this.httpClientState;
    }
    
    
    /**
     * @param out
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        final org.apache.commons.httpclient.Cookie[] cookies = this.httpClientState.getCookies();
        out.writeObject(cookies);
        
        final org.apache.commons.httpclient.Credentials creds = this.httpClientState.getCredentials(AuthScope.ANY);
        this.writeCredentials(out, creds);
        
        final org.apache.commons.httpclient.Credentials proxyCreds = this.httpClientState.getProxyCredentials(AuthScope.ANY);
        this.writeCredentials(out, proxyCreds);
    }

    /**
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.httpClientState = new HttpState();
        
        final org.apache.commons.httpclient.Cookie[] cookies = (org.apache.commons.httpclient.Cookie[])in.readObject();
        httpClientState.addCookies(cookies);
        
        httpClientState.setCredentials(AuthScope.ANY, this.readCredentials(in));
        httpClientState.setProxyCredentials(AuthScope.ANY, this.readCredentials(in));
    }
    

    /**
     * @param out
     * @param creds
     * @throws IOException
     */
    private void writeCredentials(ObjectOutputStream out, final org.apache.commons.httpclient.Credentials creds) throws IOException {
        Credentials myCreds = new Credentials();
        
        if (creds instanceof org.apache.commons.httpclient.NTCredentials) {
            final org.apache.commons.httpclient.NTCredentials ntCreds = (org.apache.commons.httpclient.NTCredentials)creds;
            
            myCreds = new NtCredentials();
            myCreds.setUserName(ntCreds.getUserName());
            myCreds.setPassword(ntCreds.getPassword());
            ((NtCredentials)myCreds).setDomain(ntCreds.getDomain());
            ((NtCredentials)myCreds).setHost(ntCreds.getHost());
        }
        else if (creds instanceof UsernamePasswordCredentials) {
            final UsernamePasswordCredentials upCreds = (UsernamePasswordCredentials)creds;

            myCreds.setUserName(upCreds.getUserName());
            myCreds.setPassword(upCreds.getPassword());
        }
        
        out.writeObject(myCreds);
    }
    
    /**
     * @param in
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private org.apache.commons.httpclient.Credentials readCredentials(ObjectInputStream in) throws IOException, ClassNotFoundException {
        final Credentials creds = (Credentials)in.readObject();
        
        if (creds instanceof NtCredentials) {
            final NtCredentials ntCreds = (NtCredentials)creds;
            return new org.apache.commons.httpclient.NTCredentials(ntCreds.getUserName(), ntCreds.getPassword(), ntCreds.getHost(), ntCreds.getDomain());
        }
        else {
            if (creds.getUserName() != null && creds.getPassword() != null)
                return new UsernamePasswordCredentials(creds.getUserName(), creds.getPassword());
        }
        
        return null;
    }
}