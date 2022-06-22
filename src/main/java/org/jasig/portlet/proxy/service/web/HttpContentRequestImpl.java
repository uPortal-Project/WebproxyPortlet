/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
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
import javax.portlet.WindowState;

import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.jasig.portlet.proxy.service.GenericContentRequestImpl;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.proxy.document.URLRewritingFilter;
import org.jasig.portlet.spring.IExpressionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>HttpContentRequestImpl class.</p>
 *
 * @author bjagg
 * @version $Id: $Id
 */
public class HttpContentRequestImpl extends GenericContentRequestImpl {

    private static final Logger log = LoggerFactory.getLogger(HttpContentRequestImpl.class);

    private Map<String, IFormField> parameters = new HashMap<String, IFormField>();
    private Map<String, String> headers = new HashMap<String, String>();
    private String method;
    private boolean isForm;
    private HttpContext httpContext;

    /**
     * <p>Constructor for HttpContentRequestImpl.</p>
     */
    public HttpContentRequestImpl() {
    }

    /**
     * <p>Constructor for HttpContentRequestImpl.</p>
     *
     * @param request a {@link javax.portlet.PortletRequest} object
     * @param expressionProcessor a {@link org.jasig.portlet.spring.IExpressionProcessor} object
     */
    public HttpContentRequestImpl(PortletRequest request, IExpressionProcessor expressionProcessor) {
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

            setProxiedLocation(expressionProcessor.process(urlParam, request));
        }

        // otherwise use the default starting URL for this proxy portlet
        else {
            final PortletPreferences preferences = request.getPreferences();
            String location = null;
            if (WindowState.MAXIMIZED.equals(request.getWindowState())) {
                location = preferences.getValue(CONTENT_LOCATION_MAXIMIZED_PREFERENCE, null);
            }
            if (location == null || location.length() == 0) {
                location = preferences.getValue(CONTENT_LOCATION_PREFERENCE, null);
            }
            String url = expressionProcessor.process(location, request);

            setProxiedLocation( url);
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

        this.httpContext = HttpClientContext.create();

    }

    /**
     * add a new parameter to the list of available parameters.
     *
     * @param fieldName a {@link java.lang.String} object
     * @param value a {@link java.lang.String} object
     */
    public void addParameter(String fieldName, String value) {
        IFormField field = new FormFieldImpl();
        field.setName(fieldName);
        field.setValue(value);
        parameters.put(fieldName, field);
    }
    /**
     * Returns a map of parameters for the gateway login form.  Map Keys are user-friendly logical names for the parameter.
     *
     * @return Map of parameters for the gateway login form.
     */
    public Map<String, IFormField> getParameters() {
        return parameters;
    }

    /**
     * Sets a map of parameters for the gateway login form.  The Map's keys are user-friendly logical names for
     * each parameter.
     *
     * @param parameters a {@link java.util.Map} object
     */
    public void setParameters(Map<String, IFormField> parameters) {
        this.parameters = parameters;
    }

    /**
     * <p>Getter for the field <code>headers</code>.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * <p>Setter for the field <code>headers</code>.</p>
     *
     * @param headers a {@link java.util.Map} object
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * <p>Getter for the field <code>method</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getMethod() {
        return method;
    }

    /**
     * <p>Setter for the field <code>method</code>.</p>
     *
     * @param method a {@link java.lang.String} object
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * <p>isForm.</p>
     *
     * @return a boolean
     */
    public boolean isForm() {
        return isForm;
    }

    /**
     * <p>setForm.</p>
     *
     * @param isForm a boolean
     */
    public void setForm(boolean isForm) {
        this.isForm = isForm;
    }

    /**
     * <p>Getter for the field <code>httpContext</code>.</p>
     *
     * @return a {@link org.apache.http.protocol.HttpContext} object
     */
    public HttpContext getHttpContext() {
        return httpContext;
    }

    /**
     * <p>Setter for the field <code>httpContext</code>.</p>
     *
     * @param httpContext a {@link org.apache.http.protocol.HttpContext} object
     */
    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    /**
     * duplicate() creates a duplicate of the HttpContentRequest without
     * using clone().  All objects are unique, but the data contained within
     * the objects is the same
     *
     * @return a unique HttpContentRequestImpl object with the same data
     */
    public HttpContentRequestImpl duplicate() {
        HttpContentRequestImpl copy = new HttpContentRequestImpl();
        copy.setMethod(this.getMethod());
        copy.setForm(this.isForm());
        copy.setProxiedLocation(this.getProxiedLocation());
        copy.setHttpContext(this.getHttpContext());

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
