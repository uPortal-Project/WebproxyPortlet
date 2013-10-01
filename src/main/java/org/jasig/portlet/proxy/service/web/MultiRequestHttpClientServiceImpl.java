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

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.portlet.util.PortletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MultiRequestHttpClientServiceImpl associates a single HTTP client with each
 * user's portlet session.  This client may optionally be shared among multiple
 * portlets to provide cross-portlet session state sharing. 
 * 
 * @author Jen Bourey
 */
@Service
public class MultiRequestHttpClientServiceImpl implements IHttpClientService {
    private static final Logger LOG = LoggerFactory.getLogger(MultiRequestHttpClientServiceImpl.class);
    private static final String HTTP_CLIENT_CONNECTION_TIMEOUT = "httpClientConnectionTimeout";
    private static final String HTTP_CLIENT_SOCKET_TIMEOUT = "httpClientSocketTimeout";
    private static final int DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT = 10000;
    private static final int DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT = 10000;
    protected static final String          CLIENT_SESSION_KEY = "httpClient";
    protected static final String          SHARED_SESSION_KEY = "sharedSessionKey";

    private PoolingClientConnectionManager connectionManager;

    @Autowired(required = true)
    public void setPoolingClientConnectionManager(PoolingClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public AbstractHttpClient getHttpClient(PortletRequest request) {
        final PortletSession session = request.getPortletSession();
        final PortletPreferences preferences = request.getPreferences();
        // determine whether this portlet should share its HttpClient with
        // other portlets 
        final String sharedSessionKey = preferences.getValue(SHARED_SESSION_KEY, null);
        final int scope = sharedSessionKey != null ? PortletSession.APPLICATION_SCOPE : PortletSession.PORTLET_SCOPE;
        final String clientSessionKey = sharedSessionKey != null ? sharedSessionKey : CLIENT_SESSION_KEY;

        // get the client currently in the user session, or if none exists, 
        // create a new one
        AbstractHttpClient client;
        synchronized (PortletUtils.getSessionMutex(session)) {
            client = (AbstractHttpClient) session.getAttribute(clientSessionKey, scope);
            if (client == null) {
                client = createHttpClient(request);
                session.setAttribute(clientSessionKey, client, scope);
            }
        }

        // TODO: allow session to be persisted to database
        client = setHttpClientTimeouts(request, client);
        return client;
    }

    /**
     * Create a new HTTP Client for the provided portlet request.
     * 
     * @param request
     * @return
     */
    protected AbstractHttpClient createHttpClient(PortletRequest request) {
        final AbstractHttpClient client = new DefaultHttpClient(this.connectionManager);
        client.addResponseInterceptor(new RedirectTrackingResponseInterceptor());
        return client;
    }

    private AbstractHttpClient setHttpClientTimeouts(PortletRequest request, AbstractHttpClient client) {
        PortletPreferences prefs = request.getPreferences();
        HttpParams params = client.getParams();
        if (params == null) {
            params = new BasicHttpParams();
            client.setParams(params);
        }
        // The connection is attempted 5 times prior to stopping
        // so the actual time before failure will be 5 times this setting.
        // Suggested way of testing Connection Timeout is by hitting a
        // domain with a port that is firewalled:
        // ie. http://www.google.com:81
        int httpClientConnectionTimeout = Integer.parseInt(prefs.getValue(HTTP_CLIENT_CONNECTION_TIMEOUT, String.valueOf(DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT)));
        // Suggested way of testing Socket Timeout is by using a tool locally to connect
        // but not respond.  Example tool: bane
        // http://blog.danielwellman.com/2010/09/introducing-bane-a-test-harness-for-server-connections.html
        // usage: $bane 10010 NeverRespond
        // ie http://localhost:10010
        int httpClientSocketTimeout = Integer.parseInt(prefs.getValue(HTTP_CLIENT_SOCKET_TIMEOUT, String.valueOf(DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT)));
        HttpConnectionParams.setConnectionTimeout(params, httpClientConnectionTimeout);
        HttpConnectionParams.setSoTimeout(params, httpClientSocketTimeout);
        return client;
    }
}
