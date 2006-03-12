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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.wisc.my.webproxy.portlet.WebProxyPortlet;


/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.Response) interface
 * 
 * @author nramzan
 *
 * @version $Id$
 */
public class ResponseImpl implements Response{
    
    private static final Log LOG = LogFactory.getLog(WebProxyPortlet.class);
    
    HttpMethod method;
    HttpClient client;
    
    /**
	 * Creates a response object
	 * @param method the HttpMethod used to make the remote request
	 * @param client the HttpClient client instance that made the request
	 * 
	 */

    public ResponseImpl(HttpMethod method, HttpClient client) throws HttpTimeoutException, IOException {
       //constructor for test impl
    	this.client = client;
    	this.method = method;
    	try {
		int statusCode = client.executeMethod(method);
			
			if (statusCode != HttpStatus.SC_OK) {  
                LOG.info(("Method failed: " + method.getStatusLine()));
		      }

		} catch (ConnectTimeoutException cte) {
			throw new HttpTimeoutException(cte);
		}
    }
    
    
    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Response#getResponseBodyAsStream()
     */
    
    public InputStream getResponseBodyAsStream() {
    	InputStream is = null;
        try {
			is = method.getResponseBodyAsStream();
		} catch (IOException e) {
            LOG.error("Caught an IOException when retrieving the response body: ", e);
		}
    
        return is;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Response#getContentType()
     */
    public String getContentType() {
    	String contentType = null;
    
    	Header header = method.getResponseHeader("Content-Type");
        if (header != null) {
        	contentType = header.getValue();
        }

        return contentType;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Response#getState()
     */
    public State getState() {
    	org.apache.commons.httpclient.HttpState httpState = client.getState();
        State state = new HttpClientStateImpl(httpState);    	 
        return state;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Response#getHeaders()
     */
    public edu.wisc.my.webproxy.beans.http.Header[] getHeaders() {
        
    	org.apache.commons.httpclient.Header[] httpClientHeaders = method.getResponseHeaders();
    	edu.wisc.my.webproxy.beans.http.Header[] myHeaders =  new edu.wisc.my.webproxy.beans.http.Header[httpClientHeaders.length];
    	for (int i=0; i < httpClientHeaders.length; i++)
    	{
    	myHeaders[i] = new HeaderImpl(httpClientHeaders[i].getName(), httpClientHeaders[i].getValue());	
    	}
    	
        return myHeaders;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Response#getStatusCode()
     */
    public int getStatusCode() {
        return method.getStatusCode();
    }
    
    public String getRequestUrl() {
        try {
            return method.getURI().toString();
        }
        catch (URIException ue) {
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Response#close()
     */
    public void close() {
        method.releaseConnection();
        
    }

}
