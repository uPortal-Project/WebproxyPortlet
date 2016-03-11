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
package edu.wisc.my.webproxy.beans.interceptors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.wisc.my.webproxy.beans.http.Request;

public abstract class UserInfoParameterizingPreInterceptor implements PreInterceptor {

    protected enum Strategy {

        REPLACE {
            @Override
            public String execute(final String paramName, final String value) throws UnsupportedEncodingException {
                return URLEncoder.encode(value, "UTF-8");
            }
        },

        EXPAND {
            @Override
            public String execute(final String paramName, final String value) throws UnsupportedEncodingException {
                return URLEncoder.encode(paramName, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
            }
        };

        public abstract String execute(String paramName, String value) throws UnsupportedEncodingException;

    }

    protected final Log log = LogFactory.getLog(getClass());

    public void intercept(HttpServletRequest req, HttpServletResponse res, Request httpReq) {
        log.warn("Invoking intercept() with HttpServletRequest/HttpServletResponse;  " +
                "Tokens cannot be rewritten since there is no access to the " +
                "PortletRequest.USER_INFO map.");
        /*
         * Nothing we can do here...
         */
    }

    protected String resolveTokens(final PortletRequest req, final String input, final Strategy strategy) {

        String rslt = input;

        @SuppressWarnings("unchecked")
        Map<String,String> userInfo = (Map<String,String>) req.getAttribute(PortletRequest.USER_INFO);

        // Inject requestUri params
        for (Map.Entry<String,String> y : userInfo.entrySet()) {

            final String key = y.getKey();
            final String value = y.getValue();
            final String token = "{" + key + "}";

            String inject;
            try {
                inject = strategy.execute(key, value);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Failed to resolve tokens", e);
            }

            // Don't use String.replaceAll b/c token looks like an illegal regex
            while(rslt.contains(token)) {
                rslt = rslt.replace(token, inject);
            }

        }

        return rslt;

    }

}
