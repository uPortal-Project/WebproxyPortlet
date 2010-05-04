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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;

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
    
    HttpUriRequest method;
    HttpClient client;
    HttpResponse response;
    
    /**
	 * Creates a response object
	 * @param method the HttpUriRequest used to make the remote request
	 * @param client the HttpClient client instance that made the request
	 * 
	 */

    public ResponseImpl(HttpUriRequest method, HttpClient client) throws HttpTimeoutException, IOException {
    	this.client = client;
    	this.method = method;
    	
    	try {
			this.response = this.client.execute(this.method);
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != HttpStatus.SC_OK) {  
                LOG.info(("Method failed: " + response.getStatusLine()));
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
			is = response.getEntity().getContent();
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
    
    	Header[] headers = response.getHeaders("Content-Type");
        if (headers.length > 0) {
        	contentType = headers[0].getValue();
        }

        return contentType;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Response#getHeaders()
     */
    public edu.wisc.my.webproxy.beans.http.IHeader[] getHeaders() {
        
    	Header[] httpClientHeaders = response.getAllHeaders();
    	IHeader[] myHeaders =  new edu.wisc.my.webproxy.beans.http.IHeader[httpClientHeaders.length];
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
        return response.getStatusLine().getStatusCode();
    }
    
    public String getRequestUrl() {
        return method.getURI().toString();
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Response#close()
     */
    public void close() {
        if (response == null) {
            return;
        }
        
        final HttpEntity entity = response.getEntity();
        if (entity == null) {
            return;
        }
        
        try {
            entity.consumeContent();
        }
        catch (IOException e) {
            LOG.warn("Exception while closing connection", e);
        }
    }

}
