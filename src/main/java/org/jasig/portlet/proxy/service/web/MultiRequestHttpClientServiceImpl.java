package org.jasig.portlet.proxy.service.web;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

public class MultiRequestHttpClientServiceImpl implements IHttpClientService {
	
	protected static final String CLIENT_SESSION_KEY = "httpClient";
	protected static final String SHARED_SESSION_KEY = "sharedSessionKey";

	@Override
	public DefaultHttpClient getHttpClient(PortletRequest request) {
		final PortletSession session = request.getPortletSession();
		final PortletPreferences preferences = request.getPreferences();
		
		// determine whether this portlet should share its HttpClient with
		// other portlets
		final String sharedSessionKey = preferences.getValue(SHARED_SESSION_KEY, null);
		final int scope = sharedSessionKey != null ? PortletSession.APPLICATION_SCOPE : PortletSession.PORTLET_SCOPE;
		final String clientSessionKey = sharedSessionKey != null ? sharedSessionKey : CLIENT_SESSION_KEY;
		
		// TODO: synchronize on session access when updating the client

		final DefaultHttpClient client;
		if (session.getAttribute(clientSessionKey, scope) != null) {
			client = (DefaultHttpClient) session.getAttribute(clientSessionKey, scope);
		} else {
			client = createHttpClient(request);
			session.setAttribute(clientSessionKey, client, scope);
		}
		
		// TODO: allow session to be persisted to database
		
		return client;
	}
	
	public DefaultHttpClient createHttpClient(PortletRequest request) {
		
		// TODO: increase configurability
		
        final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(10);
        
		final DefaultHttpClient client = new DefaultHttpClient(connectionManager);
		client.addResponseInterceptor(new RedirectTrackingResponseInterceptor());
		return client;
	}

}
