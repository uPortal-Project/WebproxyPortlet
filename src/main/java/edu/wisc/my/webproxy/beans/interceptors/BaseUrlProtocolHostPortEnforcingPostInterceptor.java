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

package edu.wisc.my.webproxy.beans.interceptors;

import java.net.MalformedURLException;
import java.net.URL;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.wisc.my.webproxy.beans.config.ConfigUtils;
import edu.wisc.my.webproxy.beans.config.GeneralConfigImpl;
import edu.wisc.my.webproxy.beans.http.Response;

/**
 * Requires any proxied URL to match the portlet's baseUrl in hostname, port, 
 * and context (first path element).  Written as a PostInterceptor because you 
 * can only configure 1 Pre- and 1-PostInterceptor.
 * 
 * @author awills
 */
public class BaseUrlProtocolHostPortEnforcingPostInterceptor implements PostInterceptor {

    protected final Log log = LogFactory.getLog(getClass());

    @Override
    public void intercept(RenderRequest request, RenderResponse response, Response httpResponse) {
        enforce(request, httpResponse);
    }

    @Override
    public void intercept(ActionRequest request, ActionResponse response, Response httpResponse) {
        enforce(request, httpResponse);
    }

    @Override
    public void intercept(HttpServletRequest request, HttpServletResponse response, Response httpResponse) {
        log.warn("Invoking intercept() with HttpServletRequest/HttpServletResponse;  " +
                "Tokens cannot be rewritten since there is no access to the " +
                "PortletRequest.USER_INFO map.");
        /*
         * Nothing we can do here...
         */
    }

    private void enforce(final PortletRequest req, Response httpResponse) {

        final PortletPreferences prefs = req.getPreferences();

        final String baseUrlString = ConfigUtils.checkEmptyNullString(prefs.getValue(GeneralConfigImpl.BASE_URL, null), null);
        if (baseUrlString == null) {
            throw new IllegalStateException("Preference '" + GeneralConfigImpl.BASE_URL + "' not set");
        }

        try {

            final URL baseUrl = new URL(baseUrlString);
            final URL requestUrl =  new URL(httpResponse.getRequestUrl());
            if (requestUrl.getProtocol().equals(baseUrl.getProtocol())  // Protocols match
                    && requestUrl.getHost().equals(baseUrl.getHost())   // Hosts match
                    && requestUrl.getPort() == baseUrl.getPort()) {     // Ports match
                // The URL meets the requirements
                return;
            }

            // If we reach this point, that's a Bad Thing
            log.warn("Attempt to proxy '" + httpResponse.getRequestUrl() + 
                    "' from within portlet of baseUrl '" + baseUrlString + 
                    "' by user account '" + req.getRemoteUser() + "'");
            throw new SecurityException();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

}
