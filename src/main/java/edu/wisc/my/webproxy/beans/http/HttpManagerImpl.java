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
 * Created on Mar 1, 2005
 *
 */
package edu.wisc.my.webproxy.beans.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import edu.wisc.my.webproxy.beans.PortletPreferencesWrapper;
import edu.wisc.my.webproxy.beans.config.HttpClientConfigImpl;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.HttpManager) interface
 * 
 * @author nramzan
 * @version $Id$
 */
public class HttpManagerImpl extends HttpManager {

	private static final Log log = LogFactory.getLog(HttpManagerImpl.class);

	private DefaultHttpClient client;
    
    /**
     * Default constructor
     */
    public HttpManagerImpl() {
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#doRequest(edu.wisc.my.webproxy.beans.http.Request)
     */
    public Response doRequest(Request request) throws HttpTimeoutException, IOException {

    	// get the request type
    	String requestType = request.getType();
        int indx = requestType.indexOf('#');
        if (indx != -1)
        {
        	requestType = requestType.substring(0, indx);    
        }
        
        // construct the HttpUriRequest
        final HttpUriRequest method;
        if (WebproxyConstants.GET_REQUEST.equals(requestType)) {
            method = new HttpGet(request.getUrl());
        }
        else if (WebproxyConstants.POST_REQUEST.equals(requestType)) {
            method = new HttpPost(request.getUrl());
            
            // append any parameters to the post request
            final ParameterPair[] postParameters = request.getParameters();
            if (postParameters != null) {
                List<NameValuePair> realParameters = new ArrayList<NameValuePair>();
                for (int index = 0; index < postParameters.length; index++) {
                    realParameters.add(new BasicNameValuePair(postParameters[index].getName(), postParameters[index].getValue()));
                }
    
                ((HttpPost) method).setEntity(new UrlEncodedFormEntity(realParameters));
            }
        }
        else if (WebproxyConstants.HEAD_REQUEST.equals(requestType)) {
            method = new HttpHead(request.getUrl());
        }
        else {
            throw new IllegalArgumentException("Unknown request type '" + requestType + "'");
        }
        
        // set any headers on the request method
        if (request.getHeaders() != null) {
        	IHeader[] headers = request.getHeaders();
            for (int index = 0; index < headers.length; index++) {
                method.setHeader(headers[index].getName(), headers[index].getValue());
            }
        }
        
        return new ResponseImpl(method, client);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#createRequest()
     */
    @Override
    public Request createRequest() {
        return new RequestImpl();
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#getName()
     */
    public String getName() {
        return "HTTP_Manager";
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#clearData()
     */
    public void clearData() {
    }
    
    /*
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#setup(javax.portlet.PortletPreferences)
     */
    @Override
    public void setup(PortletRequest request) {

    	// get a new HttpClient instance
    	client = createHttpClient(request);
    	
    	PortletPreferences prefs = new PortletPreferencesWrapper(request.getPreferences(), (Map)request.getAttribute(PortletRequest.USER_INFO));

        //Configure connection timeout
    	HttpParams params = client.getParams();
        final String httpTimeoutStr = prefs.getValue(HttpClientConfigImpl.HTTP_TIMEOUT, "");
        try {
            final int httpTimeout = Integer.parseInt(httpTimeoutStr);
            HttpConnectionParams.setConnectionTimeout(params, httpTimeout * 1000);
        }
        catch (NumberFormatException nfe) {
            
        }
        
        // configure circular redirects
        final String circularRedirectsStr = prefs.getValue(HttpClientConfigImpl.CIRCULAR_REDIRECTS, null);
        if (circularRedirectsStr != null) {
        	params.setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, Boolean.valueOf(circularRedirectsStr));
        }
    }

    /*
     * (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#addCookie(org.apache.http.cookie.Cookie)
     */
	@Override
	public void addCookie(Cookie cookie) {
		client.getCookieStore().addCookie(cookie);
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#addCookies(org.apache.http.cookie.Cookie[])
	 */
	@Override
	public void addCookies(Cookie[] cookies) {
		for (Cookie cookie : cookies) { 
			client.getCookieStore().addCookie(cookie);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#clearCookies()
	 */
	@Override
	public void clearCookies() {
		client.getCookieStore().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#getCookies()
	 */
	@Override
	public List<Cookie> getCookies() {
		return client.getCookieStore().getCookies();
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#getCredentials()
	 */
	@Override
	public Credentials getCredentials() {
		return client.getCredentialsProvider().getCredentials(AuthScope.ANY);
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#setCredentials(org.apache.http.auth.Credentials)
	 */
	@Override
	public void setCredentials(Credentials credentials) {

        // if no username/password on the state object, then get it from the request object
        if (credentials.getUserPrincipal() == null && credentials.getPassword() == null) {
            throw new IllegalArgumentException("Both username and password are null for BASIC authentication");
        }

        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);

	}
	
	/**
	 * Create a new HttpClient instance using the available portlet preferences.
	 * This method may be used by subclasses to provide an alternate instance
	 * of DefaultHttpClient.  The returned client should be sure to  use a 
	 * thread-safe client connection manager.
	 * 
	 * @param prefs
	 * @return new DefaultHttpClient instance
	 */
	protected DefaultHttpClient createHttpClient(PortletRequest request) {
		// construct a new DefaultHttpClient backed by a ThreadSafeClientConnManager
	    DefaultHttpClient client = new DefaultHttpClient ();
	    SchemeRegistry registry = client.getConnectionManager().getSchemeRegistry();
	    HttpParams params = new BasicHttpParams();
	    log.debug("Returning new DefaultHttpClient");
	    return new DefaultHttpClient (new ThreadSafeClientConnManager(params, registry), params); 
	}
	
}