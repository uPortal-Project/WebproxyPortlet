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
package org.jasig.portlet.proxy.service.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jasig.portlet.proxy.service.GenericContentResponseImpl;
import org.jasig.portlet.proxy.service.IContentService;
import org.jasig.portlet.proxy.service.web.interceptor.IPreInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Service("httpContentService")
public class HttpContentServiceImpl implements IContentService<HttpContentRequestImpl, GenericContentResponseImpl> {

    private final Log log = LogFactory.getLog(getClass());

    public final static String PROXY_PORTLET_PARAM_PREFIX = "proxy.";
    public final static String URL_PARAM = PROXY_PORTLET_PARAM_PREFIX.concat("url");
    public final static String IS_FORM_PARAM = PROXY_PORTLET_PARAM_PREFIX.concat("isForm");
    public final static String FORM_METHOD_PARAM = PROXY_PORTLET_PARAM_PREFIX.concat("formMethod");

    protected static final String PREINTERCEPTOR_LIST_KEY = "preInterceptors";

    private ApplicationContext applicationContext;
    
    @Autowired(required = true)
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public HttpContentRequestImpl getRequest(PortletRequest request) {
        return new HttpContentRequestImpl(request);
    }
    
    private IHttpClientService httpClientService = new MultiRequestHttpClientServiceImpl();
    
    public GenericContentResponseImpl getContent(HttpContentRequestImpl proxyRequest, PortletRequest request) {

        // locate all pre-processing filters configured for this portlet
        final PortletPreferences preferences = request.getPreferences();
        final String[] interceptorKeys = preferences.getValues(PREINTERCEPTOR_LIST_KEY, new String[]{});
        for (final String key : interceptorKeys) {
            final IPreInterceptor preinterceptor = applicationContext.getBean(key, IPreInterceptor.class);
            preinterceptor.intercept(proxyRequest, request);
        }

        try {

            final DefaultHttpClient httpclient = httpClientService.getHttpClient(request);
            if (proxyRequest.getCredentialsProvider() != null) {
            	httpclient.setCredentialsProvider(proxyRequest.getCredentialsProvider());
            }
            
            final HttpUriRequest httpRequest = getHttpRequest(proxyRequest, request);
            
        	if (log.isTraceEnabled()) {
        		log.trace("Proxying " + httpRequest.getURI() + " via " + httpRequest.getMethod());
        	}
        	
        	final HttpContext context = new BasicHttpContext(); 
            final HttpResponse response = httpclient.execute(httpRequest, context);            
            final HttpEntity entity = response.getEntity();
            
            final GenericContentResponseImpl proxyResponse = new GenericContentResponseImpl();
            proxyResponse.setContent(entity.getContent());
            
            String finalUrl = (String) context.getAttribute(RedirectTrackingResponseInterceptor.FINAL_URL_KEY);
            if (finalUrl == null) {
            	finalUrl = proxyRequest.getProxiedLocation();
            }
            proxyResponse.setProxiedLocation(finalUrl);

            return proxyResponse;
            
        } catch (ClientProtocolException e) {
            log.error("Exception retrieving remote content", e);
        } catch (IOException e) {
            log.error("Exception retrieving remote content", e);
        }

        return null;
    }
    
    protected HttpUriRequest getHttpRequest(HttpContentRequestImpl proxyRequest, PortletRequest request) {
        final HttpUriRequest httpRequest;

        // if this is a form request, we may need to use a POST or add form parameters
        if (proxyRequest.isForm()) {
        	
            // handle POST form request
            final Map<String, String[]> params = proxyRequest.getParameters()   ;         
            if ("POST".equalsIgnoreCase(proxyRequest.getMethod())) {

                final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String[]> param : params.entrySet()) {
        			for (String value : param.getValue()) {
        				if (value != null) {
							pairs.add(new BasicNameValuePair(param.getKey(), value));
        				}
        			}
                }

                // construct a new POST request and set the form data
                try {
                    httpRequest = new HttpPost(proxyRequest.getProxiedLocation());
                    if (pairs.size() > 0) {
                    	((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
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
                    final URIBuilder builder = new URIBuilder(proxyRequest.getProxiedLocation());
                    for (Map.Entry<String, String[]> param : params.entrySet()) {
            			for (String value : param.getValue()) {
                    		builder.addParameter(param.getKey(), value);
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
            httpRequest = new HttpGet(proxyRequest.getProxiedLocation());
        }
        
        return httpRequest;
    }

}
