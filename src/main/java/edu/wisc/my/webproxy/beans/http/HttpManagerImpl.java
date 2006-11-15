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

import javax.portlet.PortletPreferences;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import edu.wisc.my.webproxy.beans.config.HttpClientConfigImpl;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.HttpManager) interface
 * 
 * @author nramzan
 * @version $Id$
 */
public final class HttpManagerImpl extends HttpManager {
    private final HttpClient client = new HttpClient();

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#doRequest(edu.wisc.my.webproxy.beans.http.Request)
     */
    public Response doRequest(Request request) throws HttpTimeoutException, IOException {
        final HttpMethod method;
        String requestType = request.getType();
        int indx = requestType.indexOf('#');
        if (indx != -1)
        {
        requestType = requestType.substring(0, indx);    
        }
        if (WebproxyConstants.GET_REQUEST.equals(requestType)) {
            method = new GetMethod(request.getUrl());
        }
        else if (WebproxyConstants.POST_REQUEST.equals(requestType)) {
            method = new PostMethod(request.getUrl());
            
            final ParameterPair[] postParameters = request.getParameters();
            if (postParameters != null) {
                final NameValuePair[] realParameters = new NameValuePair[postParameters.length];
                
                for (int index = 0; index < postParameters.length; index++) {
                    realParameters[index] = new NameValuePair(postParameters[index].getName(), postParameters[index].getValue());
                }
    
                ((PostMethod)method).addParameters(realParameters);
            }
        }
        else if (WebproxyConstants.HEAD_REQUEST.equals(requestType)) {
            method = new HeadMethod(request.getUrl());
        }
        else {
            throw new IllegalArgumentException("Unknown request type '" + requestType + "'");
        }
        
        if (HttpClientConfigImpl.AUTH_TYPE_BASIC.equalsIgnoreCase(request.getAuthType())) {
            final String username = request.getState().getUserCredentials().getUserName();
            final String password = request.getState().getUserCredentials().getPassword();
            
            // if no username/password on the state object, then get it from the request object
            if (username == null && password == null) {
                throw new IllegalArgumentException("Both username and password are null for BASIC authentication");
            }

            final Credentials creds = new UsernamePasswordCredentials(username, password);
            client.getState().setCredentials(AuthScope.ANY, creds);
            method.setDoAuthentication(true);
        }

        this.setStaticHeaders(request.getHeaders(), method);
        
        final State state = request.getState();
        if (state != null) {
            org.apache.commons.httpclient.HttpState httpState = ((HttpClientStateImpl)state).getWrappedState();
            client.setState(httpState);
        }
        
        return new ResponseImpl(method, client);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#createRequest()
     */
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
    public void setup(PortletPreferences prefs) {
        //Configure connection timeout
        final String httpTimeoutStr = prefs.getValue(HttpClientConfigImpl.HTTP_TIMEOUT, "");
        try {
            final int httpTimeout = Integer.parseInt(httpTimeoutStr);
            this.client.getHttpConnectionManager().getParams().setConnectionTimeout(httpTimeout * 1000);
        }
        catch (NumberFormatException nfe) {
            
        }
        
        //TODO remove proxy config before prod
        //this.client.getHostConfiguration().setProxy("localhost", 7999);
    }

    private void setStaticHeaders(Header[] headers, HttpMethod method) {
        if (headers != null) {
            for (int index = 0; index < headers.length; index++) {
                method.setRequestHeader(headers[index].getName(), headers[index].getValue());
            }
        }
    }
}