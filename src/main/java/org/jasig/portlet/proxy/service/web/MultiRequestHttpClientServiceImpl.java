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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.portlet.util.PortletUtils;

/**
 * @author Jen Bourey
 */
@Service
public class MultiRequestHttpClientServiceImpl implements IHttpClientService {
	
	protected static final String CLIENT_SESSION_KEY = "httpClient";
	protected static final String SHARED_SESSION_KEY = "sharedSessionKey";

	private PoolingClientConnectionManager connectionManager;
	
	@Autowired(required=true)
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
		
		return client;
	}
	
	protected AbstractHttpClient createHttpClient(PortletRequest request) {
		
		// TODO: increase configurability
        
		final AbstractHttpClient client = new DefaultHttpClient(this.connectionManager);
		client.addResponseInterceptor(new RedirectTrackingResponseInterceptor());
		return client;
	}
	
}
