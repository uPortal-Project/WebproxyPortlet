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

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;
import com.liferay.portletmvc4spring.util.PortletUtils;

/**
 * MultiRequestHttpClientServiceImpl associates a single HTTP client with each
 * user's portlet session.  This client may optionally be shared among multiple
 * portlets to provide cross-portlet session state sharing.
 *
 * @author Jen Bourey
 * @version $Id: $Id
 */
@Service
public class MultiRequestHttpClientServiceImpl implements IHttpClientService {
    /** Constant <code>HTTP_CLIENT_CONNECTION_TIMEOUT="httpClientConnectionTimeout"</code> */
    public static final String HTTP_CLIENT_CONNECTION_TIMEOUT = "httpClientConnectionTimeout";
    /** Constant <code>HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT="httpClientConnectionRequestTimeout"</code> */
    public static final String HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT = "httpClientConnectionRequestTimeout";
    /** Constant <code>HTTP_CLIENT_SOCKET_TIMEOUT="httpClientSocketTimeout"</code> */
    public static final String HTTP_CLIENT_SOCKET_TIMEOUT = "httpClientSocketTimeout";
    /** Constant <code>DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT=10000</code> */
    public static final int DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT = 10000;
    /** Constant <code>DEFAULT_HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT=5000</code> */
    public static final int DEFAULT_HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT = 5000;
    /** Constant <code>DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT=10000</code> */
    public static final int DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT = 10000;
    /** Constant <code>CLIENT_SESSION_KEY="httpClient"</code> */
    public static final String CLIENT_SESSION_KEY = "httpClient";
    /** Constant <code>SHARED_SESSION_KEY="sharedSessionKey"</code> */
    public static final String SHARED_SESSION_KEY = "sharedSessionKey";

    /** {@inheritDoc} */
    @Override
    public HttpClient getHttpClient(PortletRequest request) {
        final PortletSession session = request.getPortletSession();
        final PortletPreferences preferences = request.getPreferences();
        // determine whether this portlet should share its HttpClient with
        // other portlets 
        final String sharedSessionKey = preferences.getValue(SHARED_SESSION_KEY, null);
        final int scope = sharedSessionKey != null ? PortletSession.APPLICATION_SCOPE : PortletSession.PORTLET_SCOPE;
        final String clientSessionKey = sharedSessionKey != null ? sharedSessionKey : CLIENT_SESSION_KEY;

        // get the client currently in the user session, or if none exists, 
        // create a new one
        HttpClient client;
        synchronized (PortletUtils.getSessionMutex(session)) {
            client = (HttpClient) session.getAttribute(clientSessionKey, scope);
            if (client == null) {
                client = createHttpClient(request);
                session.setAttribute(clientSessionKey, client, scope);
            }
        }

        // TODO: allow session to be persisted to database

        // Don't know why this was needed as the httpClient configuration shouldn't change
        //client = setHttpClientTimeouts(request, client);
        return client;
    }

    /**
     * Create a new HTTP Client for the provided portlet request.
     *
     * @param request a {@link javax.portlet.PortletRequest} object
     * @return a {@link org.apache.http.client.HttpClient} object
     */
    protected HttpClient createHttpClient(PortletRequest request) {
        PortletPreferences prefs = request.getPreferences();
        int httpClientConnectionTimeout = Integer.parseInt(prefs.getValue(HTTP_CLIENT_CONNECTION_TIMEOUT, String.valueOf(DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT)));
        int httpClientConnectionRequestTimeout = Integer.parseInt(prefs.getValue(HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT, String.valueOf(DEFAULT_HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT)));
        int httpClientSocketTimeout = Integer.parseInt(prefs.getValue(HTTP_CLIENT_SOCKET_TIMEOUT, String.valueOf(DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT)));
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(httpClientConnectionTimeout)
                .setConnectionRequestTimeout(httpClientConnectionRequestTimeout)
                .setSocketTimeout(httpClientSocketTimeout).build();
        final HttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .addInterceptorFirst(new RedirectTrackingResponseInterceptor())
                .build();
        return client;
    }
}
