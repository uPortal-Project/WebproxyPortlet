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
package org.jasig.portlet.proxy.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jasig.portlet.proxy.mvc.portlet.proxy.ProxyPortletController;
import org.springframework.stereotype.Service;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Service("httpContentService")
public class HttpContentService implements IContentService {

    private final Log log = LogFactory.getLog(getClass());
    
    public InputStream getContent(String url, PortletRequest request) {


        // TODO: authentication, parameterizable URLs

        try {
        	
            final HttpClient httpclient = getHttpClient();
            final HttpUriRequest httpRequest = getHttpRequest(url, request);
            
        	if (log.isTraceEnabled()) {
        		log.trace("Proxying " + httpRequest.getURI() + " via " + httpRequest.getMethod());
        	}
        	
            final HttpResponse response = httpclient.execute(httpRequest);
            final HttpEntity entity = response.getEntity();
            return entity.getContent();
            
        } catch (ClientProtocolException e) {
            log.error("Exception retrieving remote content", e);
        } catch (IOException e) {
            log.error("Exception retrieving remote content", e);
        }

        return null;
    }
    
    protected HttpClient getHttpClient() {
    	return new DefaultHttpClient();
    }
    
    protected HttpUriRequest getHttpRequest(String url, PortletRequest request) {
        final HttpUriRequest httpRequest;

        // if this is a form request, we may need to use a POST or add form parameters
        final String isForm = request.getParameter(ProxyPortletController.IS_FORM_PARAM);
        if (isForm != null && Boolean.parseBoolean(isForm)) {
            final String method = request.getParameter(ProxyPortletController.FORM_METHOD_PARAM);
        	
            // handle POST form request
            final Map<String, String[]> params = request.getParameterMap();            
            if ("POST".equalsIgnoreCase(method)) {

                final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String[]> param : params.entrySet()) {
                	if (!param.getKey().startsWith(ProxyPortletController.PROXY_PORTLET_PARAM_PREFIX)) {
            			for (String value : param.getValue()) {
                    		pairs.add(new BasicNameValuePair(param.getKey(), value));
            			}
                	}
                }

                // construct a new POST request and set the form data
                try {
                    httpRequest = new HttpPost(url);
                    if (pairs.size() > 0) {
                    	((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(pairs));
                    }
				} catch (UnsupportedEncodingException e) {
					log.error("Failed to encode form parameters", e);
					throw new RuntimeException(e);
				}

            } 

            // handle GET form requests
            else {
                
                try {
                	
                	// build a URL including any passed form parameters
                    final URIBuilder builder = new URIBuilder(url);
                    for (Map.Entry<String, String[]> param : params.entrySet()) {
                    	if (!param.getKey().startsWith(ProxyPortletController.PROXY_PORTLET_PARAM_PREFIX)) {
                			for (String value : param.getValue()) {
                        		builder.addParameter(param.getKey(), value);
                			}
                    	}
                    }
					final URI uri = builder.build();
	                httpRequest = new HttpGet(uri);
	                
				} catch (URISyntaxException e) {
					log.error("Failed to build URI for proxying", e);
					throw new RuntimeException(e);
				}
                
            }

        } 
        
        // not a form, simply a normal get request
        else {
            httpRequest = new HttpGet(url);
        }
        
        return httpRequest;
    }

}
