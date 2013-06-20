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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.jasig.portlet.proxy.service.GenericContentRequestImpl;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.proxy.document.URLRewritingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpContentRequestImpl extends GenericContentRequestImpl {

    private static final Logger log = LoggerFactory.getLogger(HttpContentRequestImpl.class);

    private Map<String, IFormField> parameters = new HashMap<String, IFormField>();
    private Map<String, String> headers = new HashMap<String, String>();
    private String method;
    private boolean isForm;

    public HttpContentRequestImpl() { 
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
            final ConcurrentMap<String,String> rewrittenUrls = (ConcurrentMap<String,String>) session.getAttribute(URLRewritingFilter.REWRITTEN_URLS_KEY);
            if (!rewrittenUrls.containsKey(urlParam)) {
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
        		IFormField formField = new FormFieldImpl(param.getKey(), param.getValue());
				this.parameters.put(param.getKey(), formField);
        	}
        }
        
        this.isForm = Boolean.valueOf(request.getParameter(HttpContentServiceImpl.IS_FORM_PARAM));
        this.method = request.getParameter(HttpContentServiceImpl.FORM_METHOD_PARAM);

    }

    /**
     * add a new parameter to the list of available parameters.
     * @param fieldName
     * @param value
     */
    public void addParameter(String fieldName, String value) {
    	IFormField field = new FormFieldImpl();
    	field.setName(fieldName);
    	field.setValue(value);
    	parameters.put(fieldName, field);
    }
    /**
     * Returns a map of parameters for the gateway login form.  Map Keys are user-friendly logical names for the parameter.
     * @return Map of parameters for the gateway login form.
     */
	public Map<String, IFormField> getParameters() {
		return parameters;
	}

    /**
     * Sets a map of parameters for the gateway login form.  The Map's keys are user-friendly logical names for
     * each parameter.
     * @param parameters
     */
	public void setParameters(Map<String, IFormField> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
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

    /**
	 * duplicate() creates a duplicate of the HttpContentRequest without
	 * using clone().  All objects are unique, but the data contained within
	 * the objects is the same
	 * @return a unique HttpContentRequestImpl object with the same data
	 */
	public HttpContentRequestImpl duplicate() {
		HttpContentRequestImpl copy = new HttpContentRequestImpl();
		copy.setMethod(this.getMethod());
		copy.setForm(this.isForm());
		copy.setProxiedLocation(this.getProxiedLocation());
		
		Map<String, String> copyHeaders = new LinkedHashMap<String, String>();
		copyHeaders.putAll(this.headers);
		copy.setHeaders(copyHeaders);
		
		// String[] needs to be copied manually, otherwise, you end up with the
		// same object in the new HttpContentRequestImpl
		Map<String, IFormField> copyParameters = new LinkedHashMap<String, IFormField>();
		for (Entry<String, IFormField> requestEntry : this.getParameters().entrySet()){
			String key = requestEntry.getKey();
			IFormField values = requestEntry.getValue();
			copyParameters.put(key, values.duplicate());
		}
		copy.setParameters(copyParameters);
		return copy;
	}
}
