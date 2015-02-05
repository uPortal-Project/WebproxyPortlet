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

import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.springframework.stereotype.Service;

/**
 * UserInfoBasicAuthencitationPreInterceptor applies BASIC authentication using
 * username and password tokens available in the UserInfo map.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Service("userInfoBasicAuthenticationPreInterceptor")
public class UserInfoBasicAuthenticationPreInterceptor extends AbstractBasicAuthenticationPreInterceptor {

	final public static String USERNAME_KEY = "usernameKey";
	final public static String PASSWORD_KEY = "passwordKey";
	
	@Override
	protected UsernamePasswordCredentials getCredentials(PortletRequest portletRequest) {

		// get the username and password attribute names configured for this
		// portlet instance
		final PortletPreferences preferences = portletRequest.getPreferences();
		final String usernameKey = preferences.getValue(USERNAME_KEY, "user.login.id");
		final String passwordKey = preferences.getValue(PASSWORD_KEY, "password");
		
		// request the associated username and password attributes from the
		// UserInfo map
		@SuppressWarnings("unchecked")
		final Map<String, String> userInfo = (Map<String, String>) portletRequest.getAttribute(PortletRequest.USER_INFO);
		final String username = userInfo.get(usernameKey);
		final String password = userInfo.get(passwordKey);

		// construct a credentials object using hte attributes
		final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		return credentials;
	}

	@Override
	public boolean validate(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {
		return true;
	}
	
}
