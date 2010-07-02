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
