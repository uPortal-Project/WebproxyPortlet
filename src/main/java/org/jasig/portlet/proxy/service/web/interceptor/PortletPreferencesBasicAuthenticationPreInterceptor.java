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

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.stereotype.Service;

/**
 * Provides support for BASIC AuthN using credentials specified in portlet
 * preferences.
 *
 * @author drewwills
 */
@Service(PortletPreferencesBasicAuthenticationPreInterceptor.BEAN_ID)
public class PortletPreferencesBasicAuthenticationPreInterceptor extends AbstractBasicAuthenticationPreInterceptor {

    public static final String BEAN_ID = "portletPreferencesBasicAuthenticationPreInterceptor";

    public static final String USERNAME_PREFERENCE = PortletPreferencesBasicAuthenticationPreInterceptor.class.getName() + ".username";
    public static final String PASSWORD_PREFERENCE = PortletPreferencesBasicAuthenticationPreInterceptor.class.getName() + ".password";

    @Override
    protected UsernamePasswordCredentials getCredentials(PortletRequest portletRequest) {

        // get the username and password attribute names configured for this
        // portlet instance
        final PortletPreferences preferences = portletRequest.getPreferences();
        final String username = preferences.getValue(USERNAME_PREFERENCE, null);
        final String password = preferences.getValue(PASSWORD_PREFERENCE, null);

        // Sanity check...
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            final String msg = "Both username and password are required for PortletPreferencesBasicAuthenticationPreInterceptor";
            throw new IllegalStateException(msg);
        }

        // construct a credentials object using hte attributes
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        return credentials;
    }

}
