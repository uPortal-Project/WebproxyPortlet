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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
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
import org.apache.http.message.BasicNameValuePair;
import org.jasig.portlet.proxy.service.GenericContentResponseImpl;
import org.jasig.portlet.proxy.service.IContentService;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.web.interceptor.IPreInterceptor;
import org.jasig.portlet.spring.IExpressionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * <p>HttpContentServiceImpl class.</p>
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
@Service("httpContentService")
@Slf4j
public class HttpContentServiceImpl implements IContentService<HttpContentRequestImpl, GenericContentResponseImpl> {

    /** Constant <code>PROXY_PORTLET_PARAM_PREFIX="proxy."</code> */
    public final static String PROXY_PORTLET_PARAM_PREFIX = "proxy.";
    /** Constant <code>URL_PARAM="PROXY_PORTLET_PARAM_PREFIX.concat(url)"</code> */
    public final static String URL_PARAM = PROXY_PORTLET_PARAM_PREFIX.concat("url");
    /** Constant <code>IS_FORM_PARAM="PROXY_PORTLET_PARAM_PREFIX.concat(isFor"{trunked}</code> */
    public final static String IS_FORM_PARAM = PROXY_PORTLET_PARAM_PREFIX.concat("isForm");
    /** Constant <code>FORM_METHOD_PARAM="PROXY_PORTLET_PARAM_PREFIX.concat(formM"{trunked}</code> */
    public final static String FORM_METHOD_PARAM = PROXY_PORTLET_PARAM_PREFIX.concat("formMethod");

    /** Constant <code>PREINTERCEPTOR_LIST_KEY="preInterceptors"</code> */
    public static final String PREINTERCEPTOR_LIST_KEY = "preInterceptors";

    private ApplicationContext applicationContext;

    /**
     * <p>Setter for the field <code>applicationContext</code>.</p>
     *
     * @param applicationContext a {@link org.springframework.context.ApplicationContext} object
     */
    @Autowired(required = true)
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private IHttpClientService httpClientService;

    /**
     * <p>Setter for the field <code>httpClientService</code>.</p>
     *
     * @param httpClientService a {@link org.jasig.portlet.proxy.service.web.IHttpClientService} object
     */
    @Autowired(required = true)
    public void setHttpClientService(IHttpClientService httpClientService) {
        this.httpClientService = httpClientService;
    }

    private List<String> replayedRequestHeaders;

    /**
     * <p>Setter for the field <code>replayedRequestHeaders</code>.</p>
     *
     * @param replayedRequestHeaders a {@link java.util.List} object
     */
    @Required
    @Resource(name = "replayedRequestHeaders")
    public void setReplayedRequestHeaders(List<String> replayedRequestHeaders) {
        this.replayedRequestHeaders = replayedRequestHeaders;
    }

    @Autowired(required = true)
    private IExpressionProcessor expressionParser;

    /** {@inheritDoc} */
    @Override
    public HttpContentRequestImpl getRequest(PortletRequest request) {
        final HttpContentRequestImpl contentRequest = new HttpContentRequestImpl(request, expressionParser);

        for (String headerName : replayedRequestHeaders) {
            final String headerValue = request.getProperty(headerName);
            if (headerValue != null) {
                contentRequest.getHeaders().put(headerName, headerValue);
            }
        }

        return contentRequest;
    }

    /** {@inheritDoc} */
    @Override
    public void beforeGetContent(HttpContentRequestImpl proxyRequest, PortletRequest request) {
        // locate all pre-processing filters configured for this portlet

        final PortletPreferences preferences = request.getPreferences();
        final String[] interceptorKeys = preferences.getValues(PREINTERCEPTOR_LIST_KEY, new String[] {});
        for (final String key : interceptorKeys) {
            final IPreInterceptor preinterceptor = applicationContext.getBean(key, IPreInterceptor.class);
            preinterceptor.intercept(proxyRequest, request);
        }
    }

    /** {@inheritDoc} */
    @Override
    public GenericContentResponseImpl getContent(HttpContentRequestImpl proxyRequest, PortletRequest request) {
        beforeGetContent(proxyRequest, request);
        return getContent(proxyRequest, request, true);
    }

    /**
     * <p>getContent.</p>
     *
     * @param proxyRequest a {@link org.jasig.portlet.proxy.service.web.HttpContentRequestImpl} object
     * @param request a {@link javax.portlet.PortletRequest} object
     * @param runWrapperMethods a boolean
     * @return a {@link org.jasig.portlet.proxy.service.GenericContentResponseImpl} object
     */
    public GenericContentResponseImpl getContent(HttpContentRequestImpl proxyRequest, PortletRequest request, boolean runWrapperMethods) {
        try {

            // get an HttpClient appropriate for this user and portlet instance
            // and set any basic auth credentials, if applicable
            final HttpClient httpclient = httpClientService.getHttpClient(request);

            // create the request
            final HttpUriRequest httpRequest = getHttpRequest(proxyRequest, request);
            if (log.isTraceEnabled()) {
                log.trace("Proxying " + httpRequest.getURI() + " via " + httpRequest.getMethod());
            }

            // execute the request
            final HttpResponse response = httpclient.execute(httpRequest, proxyRequest.getHttpContext());
            final HttpEntity entity = response.getEntity();

            // create the response object and set the content stream
            final HttpContentResponseImpl proxyResponse = new HttpContentResponseImpl(entity);
            proxyResponse.setContent(entity.getContent());

            // add each response header to our response object
            for (Header header : response.getAllHeaders()) {
                proxyResponse.getHeaders().put(header.getName(), header.getValue());
            }

            // set the final URL of the response in case it was redirected
            String finalUrl = (String) proxyRequest.getHttpContext().getAttribute(RedirectTrackingResponseInterceptor.FINAL_URL_KEY);
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

    /** {@inheritDoc} */
    @Override
    public void afterGetContent(HttpContentRequestImpl proxyRequest, PortletRequest request, GenericContentResponseImpl proxyResponse) {
    }

    /**
     * <p>getHttpRequest.</p>
     *
     * @param proxyRequest a {@link org.jasig.portlet.proxy.service.web.HttpContentRequestImpl} object
     * @param request a {@link javax.portlet.PortletRequest} object
     * @return a {@link org.apache.http.client.methods.HttpUriRequest} object
     */
    protected HttpUriRequest getHttpRequest(HttpContentRequestImpl proxyRequest, PortletRequest request) {
        final HttpUriRequest httpRequest;

        // if this is a form request, we may need to use a POST or add form parameters
        if (proxyRequest.isForm()) {

            // handle POST form request
            final Map<String, IFormField> params = proxyRequest.getParameters();
            if ("POST".equalsIgnoreCase(proxyRequest.getMethod())) {

                final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                for (Map.Entry<String, IFormField> param : params.entrySet()) {
                    for (String value : param.getValue().getValues()) {
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
                    for (Map.Entry<String, IFormField> param : params.entrySet()) {
                        for (String value : param.getValue().getValues()) {
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
            log.debug("Submitting a GET request to proxied location [{}]", proxyRequest.getProxiedLocation());
            httpRequest = new HttpGet(proxyRequest.getProxiedLocation());
        }

        // set any configured request headers
        for (Map.Entry<String, String> header : proxyRequest.getHeaders().entrySet()) {
            httpRequest.setHeader(header.getKey(), header.getValue());
        }

        return httpRequest;
    }
}
