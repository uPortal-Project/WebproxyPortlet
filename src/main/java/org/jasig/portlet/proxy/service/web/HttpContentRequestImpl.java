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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.http.client.CredentialsProvider;
import org.jasig.portlet.proxy.service.GenericContentRequestImpl;

public class HttpContentRequestImpl extends GenericContentRequestImpl {

    private Map<String, String[]> parameters;
    private Map<String, String[]> headers;
    private String method;
    private boolean isForm;
    private CredentialsProvider credentialsProvider;
    
    // TODO: add support for basic auth credentials
    
    public HttpContentRequestImpl() { 
    	this.parameters = new HashMap<String, String[]>();
    	this.headers = new HashMap<String, String[]>();
    }
    
    public HttpContentRequestImpl(PortletRequest request) {
    	this();
    	
        // If a URL parameter has been specified, check to make sure that it's 
        // one that the portlet rewrote (we want to prevent this portlet from
        // acting as an open proxy).  If we did rewrite this URL, set the URL
        // to be proxied to the requested one
        final String urlParam = request.getParameter(HttpContentServiceImpl.URL_PARAM);
        if (urlParam != null) {
            final PortletSession session = request.getPortletSession();
            @SuppressWarnings("unchecked")
            final List<String> rewrittenUrls = (List<String>) session.getAttribute("rewrittenUrls");
            if (!rewrittenUrls.contains(urlParam)) {
            	throw new RuntimeException("Illegal URL " + urlParam);
            }
            setProxiedLocation(urlParam);
        } 
        
        // otherwise use the default starting URL for this proxy portlet
        else {
            final PortletPreferences preferences = request.getPreferences();
        	setProxiedLocation(preferences.getValue(CONTENT_LOCATION_KEY, null));
        }
        
        final Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
        	if (!param.getKey().startsWith(HttpContentServiceImpl.PROXY_PORTLET_PARAM_PREFIX)) {
				this.parameters.put(param.getKey(), param.getValue());
        	}
        }
        
        this.isForm = Boolean.valueOf(request.getParameter(HttpContentServiceImpl.IS_FORM_PARAM));
        this.method = request.getParameter(HttpContentServiceImpl.FORM_METHOD_PARAM);

    }

	public Map<String, String[]> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String[]> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String[]> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String[]> headers) {
		this.headers = headers;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isForm() {
		return isForm;
	}

	public void setForm(boolean isForm) {
		this.isForm = isForm;
	}

	public CredentialsProvider getCredentialsProvider() {
		return credentialsProvider;
	}

	public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
		this.credentialsProvider = credentialsProvider;
	}

}
