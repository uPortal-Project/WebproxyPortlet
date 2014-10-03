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

/*
 * Created on Mar 1, 2005
 *
 */
package edu.wisc.my.webproxy.beans.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.web.portlet.util.PortletUtils;

import edu.wisc.my.webproxy.beans.PortletPreferencesWrapper;
import edu.wisc.my.webproxy.beans.config.ConfigUtils;
import edu.wisc.my.webproxy.beans.config.HttpClientConfigImpl;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.HttpManager) interface
 * 
 * @author nramzan
 * @version $Id$
 */
public class HttpManagerImpl extends HttpManager {
    private static final String HTTP_CLIENT_ATTR = HttpManagerImpl.class.getName() + ".HTTP_CLIENT";
    
	protected final Log logger = LogFactory.getLog(this.getClass());
	private ClientConnectionManager clientConnectionManager;
	private DefaultHttpClient client;
	private SchemeRegistry schemeRegistry;
	
    /**
     * Default constructor
     */
    public HttpManagerImpl() {
    }
    
    
    /**
     * The {@link SchemeRegistry} to use for the connections, required
     */
    public void setSchemeRegistry(SchemeRegistry schemeRegistry) {
        this.schemeRegistry = schemeRegistry;
    }
    
    /**
     * (Optional) The ClientConnectionManager to use for creating the {@link HttpClient} instance. If not specified one will be created for each {@link HttpClient} created.
     */
    public void setClientConnectionManager(ClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }


    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#doRequest(edu.wisc.my.webproxy.beans.http.Request)
     */
    public Response doRequest(Request request) throws HttpTimeoutException, IOException {

    	// get the request type
    	String requestType = request.getType();
        int indx = requestType.indexOf('#');
        if (indx != -1)
        {
        	requestType = requestType.substring(0, indx);    
        }
        
        // construct the HttpUriRequest
        final HttpUriRequest method;
        if (WebproxyConstants.GET_REQUEST.equals(requestType)) {
            method = new HttpGet(request.getUrl());
        }
        else if (WebproxyConstants.POST_REQUEST.equals(requestType)) {
            method = new HttpPost(request.getUrl());
            
            // append any parameters to the post request
            final ParameterPair[] postParameters = request.getParameters();
            if (postParameters != null) {
                List<NameValuePair> realParameters = new ArrayList<NameValuePair>();
                for (int index = 0; index < postParameters.length; index++) {
                    realParameters.add(new BasicNameValuePair(postParameters[index].getName(), postParameters[index].getValue()));
                }
    
                ((HttpPost) method).setEntity(new UrlEncodedFormEntity(realParameters));
            }
        }
        else if (WebproxyConstants.HEAD_REQUEST.equals(requestType)) {
            method = new HttpHead(request.getUrl());
        }
        else {
            throw new IllegalArgumentException("Unknown request type '" + requestType + "'");
        }
        
        // set any headers on the request method
        if (request.getHeaders() != null) {
        	IHeader[] headers = request.getHeaders();
            for (int index = 0; index < headers.length; index++) {
                method.setHeader(headers[index].getName(), headers[index].getValue());
            }
        }
        // WPP-84 Insure connection closes and doesn't leave socket in CLOSE_WAIT.  Tried other approaches but
        // they weren't reliable.  Unfortunately this will use a 2nd socket for authentication scenarios but it
        // will insure sockets aren't left hanging.
        method.setHeader("Connection", "close");

        return new ResponseImpl(method, client);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#createRequest()
     */
    @Override
    public Request createRequest() {
        return new RequestImpl();
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#getName()
     */
    public String getName() {
        return "HTTP_Manager";
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#clearData()
     */
    public void clearData() {
    }
    
    /*
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#setup(javax.portlet.PortletPreferences)
     */
    @Override
    public void setup(PortletRequest request) {

    	// get a new HttpClient instance
    	client = getHttpClient(request);
    	
    	PortletPreferences prefs = new PortletPreferencesWrapper(request.getPreferences(), (Map)request.getAttribute(PortletRequest.USER_INFO));
        HttpParams params = client.getParams();
        
        // configure circular redirects
        final String circularRedirectsStr = prefs.getValue(HttpClientConfigImpl.CIRCULAR_REDIRECTS, null);
        if (circularRedirectsStr != null) {
        	params.setBooleanParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, Boolean.valueOf(circularRedirectsStr));
        }
    }


	protected DefaultHttpClient setHttpClientTimeouts(PortletRequest request, DefaultHttpClient client) {
		PortletPreferences prefs = new PortletPreferencesWrapper(request.getPreferences(), (Map)request.getAttribute(PortletRequest.USER_INFO));
		HttpParams params = client.getParams();
        final String httpConnectionTimeout = prefs.getValue(HttpClientConfigImpl.HTTP_CONNECTION_TIMEOUT, "");
        final String httpSocketTimeout = prefs.getValue(HttpClientConfigImpl.HTTP_SOCKET_TIMEOUT, "");
        try {
            /*
             * The connection is attempted 5 times prior to stopping
             * so the actual time before failure will be 5 times this setting.
             * Suggested way of testing Connection Timeout is by hitting a
             * domain with a port that is firewalled:
             * ie. http://www.google.com:81
             */
            final int httpTimeout = Integer.parseInt(httpConnectionTimeout);
            HttpConnectionParams.setConnectionTimeout(params, httpTimeout * 1000);
            
            /*
             * Suggested way of testing Socket Timeout is by using a tool locally to connect
             * but not respond.  Example tool: bane
             * http://blog.danielwellman.com/2010/09/introducing-bane-a-test-harness-for-server-connections.html
             * usage: $bane 10010 NeverRespond
             * ie. http://localhost:10010
             */
            final int httpClientSocketTimeout = Integer.parseInt(httpSocketTimeout);
            HttpConnectionParams.setSoTimeout(params, httpClientSocketTimeout * 1000);
        }
        catch (NumberFormatException nfe) {
            
        }
		return client;
	}

    /*
     * (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.HttpManager#addCookie(org.apache.http.cookie.Cookie)
     */
	@Override
	public void addCookie(Cookie cookie) {
		client.getCookieStore().addCookie(cookie);
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#addCookies(org.apache.http.cookie.Cookie[])
	 */
	@Override
	public void addCookies(Cookie[] cookies) {
		for (Cookie cookie : cookies) { 
			client.getCookieStore().addCookie(cookie);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#clearCookies()
	 */
	@Override
	public void clearCookies() {
		client.getCookieStore().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#getCookies()
	 */
	@Override
	public List<Cookie> getCookies() {
		return client.getCookieStore().getCookies();
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#getCredentials()
	 */
	@Override
	public Credentials getCredentials() {
		return client.getCredentialsProvider().getCredentials(AuthScope.ANY);
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.HttpManager#setCredentials(org.apache.http.auth.Credentials)
	 */
	@Override
	public void setCredentials(Credentials credentials) {

        // if no username/password on the state object, then get it from the request object
        if (credentials.getUserPrincipal() == null && credentials.getPassword() == null) {
            throw new IllegalArgumentException("Both username and password are null for BASIC authentication");
        }

        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);

	}
	
	protected final DefaultHttpClient getHttpClient(PortletRequest request) {
	    final PortletSession portletSession = request.getPortletSession();
	    
	    synchronized (PortletUtils.getSessionMutex(portletSession)) {
            DefaultHttpClient client = (DefaultHttpClient)portletSession.getAttribute(HTTP_CLIENT_ATTR);
	        if (client == null) {
	            client = this.createHttpClient(request);
	            
	            final HttpParams params = client.getParams();
	            params.setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET, "UTF-8");
	            
	            portletSession.setAttribute(HTTP_CLIENT_ATTR, client);
	        }
	        client = setHttpClientTimeouts(request, client);
	        return client;
        }
	}

	/**
     * Create a new THREAD SAFE HttpClient instance using the available portlet preferences.
     * This method may be used by subclasses to provide an alternate instance
     * of DefaultHttpClient.  The returned client should be sure to  use a 
     * thread-safe client connection manager.
     * 
     * @param prefs
     * @return new DefaultHttpClient instance
     */
    protected DefaultHttpClient createHttpClient(PortletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating new DefaultHttpClient for " + request.getRemoteUser());
        }
        
        final HttpParams params = new BasicHttpParams();
        final ClientConnectionManager clientConnectionManager = this.createClientConnectionManager(request, params);
        client = new DefaultHttpClient (clientConnectionManager, params);
        return client;
    }
    
    /**
     * Creates a new ClientConnectionManager to be used by the {@link HttpClient}. Configures the {@link SchemeRegistry}
     * as well as setting up connection related {@link HttpParams}
     */
    protected ClientConnectionManager createClientConnectionManager(PortletRequest request, HttpParams params) {
        if (this.clientConnectionManager != null) {
            return this.clientConnectionManager;
        }
        
        final int maxConnections = ConfigUtils.parseInt(request.getPreferences().getValue(HttpClientConfigImpl.MAX_CONNECTIONS, "50"), 50);
        final int maxConnectionsPerRoute = ConfigUtils.parseInt(request.getPreferences().getValue(HttpClientConfigImpl.MAX_CONNECTIONS_PER_ROUTE, "10"), 10);
        
        final ThreadSafeClientConnManager threadSafeClientConnManager = new ThreadSafeClientConnManager(this.schemeRegistry, 300, TimeUnit.SECONDS);
        threadSafeClientConnManager.setMaxTotal(maxConnections);
        threadSafeClientConnManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
        return threadSafeClientConnManager;
    }
	
}