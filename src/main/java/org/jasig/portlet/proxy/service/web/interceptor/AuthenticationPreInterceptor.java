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
package org.jasig.portlet.proxy.service.web.interceptor;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.portlet.util.PortletUtils;

/**
 * AuthenticationPreInterceptor provides a base class for implementing authentication
 * to remote web content.  This interceptor determines if a current session is
 * already available for the remote site using a configured timeout.  If one
 * does not exist, the interceptor performs any implemented authentication logic.
 *
 * @author Jen Bourey
 * @version $Id: $Id
 */
public abstract class AuthenticationPreInterceptor implements IPreInterceptor {

  protected final Logger log = LoggerFactory.getLogger(this.getClass());
    
	/** Constant <code>AUTHENTICATION_TIMEOUT_KEY="authenticationTimeout"</code> */
	public static final String AUTHENTICATION_TIMEOUT_KEY = "authenticationTimeout";
	/** Constant <code>AUTHENTICATION_TIMESTAMP_KEY="authenticationTimestamp"</code> */
	public static final String AUTHENTICATION_TIMESTAMP_KEY = "authenticationTimestamp";
	
	/** {@inheritDoc} */
	@Override
	public void intercept(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {

		// if the user isn't already authenticated, perform any required 
		// authentication pre-processing
		final PortletSession session = portletRequest.getPortletSession();
        synchronized (PortletUtils.getSessionMutex(session)) {
			if (!isAlreadyAuthenticated(portletRequest)) {
				prepareAuthentication(proxyRequest, portletRequest);
			}
			session.setAttribute(AUTHENTICATION_TIMEOUT_KEY, System.currentTimeMillis());
        }
		
	}

	/**
	 * Use the configured timeout to determine if the user is already
	 * authenticated to the remote site.
	 *
	 * @param request a {@link javax.portlet.PortletRequest} object
	 * @return a boolean
	 */
	protected boolean isAlreadyAuthenticated(PortletRequest request) {
		
		// determine if the last session access was within the timeout window
		final PortletPreferences preferences = request.getPreferences();
		final PortletSession session = request.getPortletSession();
		final Long timestamp = (Long) session.getAttribute(AUTHENTICATION_TIMESTAMP_KEY);
		if (timestamp == null) {
			return false;
		} else {
			final Long timeout = Long.valueOf(preferences.getValue(AUTHENTICATION_TIMESTAMP_KEY, String.valueOf(30*60*1000)));
			return (timestamp < System.currentTimeMillis() - timeout);
		}
	}
	
	/**
	 * Perform authentication or prepare the content request to include
	 * authentication parameters.
	 *
	 * @param contentRequest a {@link org.jasig.portlet.proxy.service.web.HttpContentRequestImpl} object
	 * @param portletRequest a {@link javax.portlet.PortletRequest} object
	 */
	protected abstract void prepareAuthentication(HttpContentRequestImpl contentRequest, PortletRequest portletRequest);

}
