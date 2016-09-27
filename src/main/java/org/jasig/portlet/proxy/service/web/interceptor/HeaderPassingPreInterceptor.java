
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

import javax.portlet.PortletRequest;
import javax.portlet.PortletPreferences;

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * HeaderPassingPreInterceptor allows content requests to include
 * pass along header attributes to proxied content
 */
@Service("headerPassingPreInterceptor")
public class HeaderPassingPreInterceptor implements IPreInterceptor {

  protected final Logger logger = LoggerFactory.getLogger(this.getClass());
  
    public static final String HEADER_PREFERENCE = "portletPreferencesBasicAuthenticationPreInterceptor";

    @SuppressWarnings("unchecked")
    @Override
    public void intercept(HttpContentRequestImpl proxyRequest, PortletRequest portletRequest) {
        PortletPreferences prefs = portletRequest.getPreferences();
        final String[] headerNames = prefs.getValues(HEADER_PREFERENCE, new String[0]);
        Map<String, String> userInfo = (Map<String, String>) portletRequest.getAttribute(PortletRequest.USER_INFO);
        Map<String, String> headerMap = proxyRequest.getHeaders();
        for(int i = 0; i<headerNames.length; i++){
            String headerName = headerNames[i];
            String headerValue = userInfo.get(headerName);
            headerMap.put(headerName, headerValue);
        }
        proxyRequest.setHeaders(headerMap);
    }

    @Override
    public boolean validate(HttpContentRequestImpl proxyRequest,
            PortletRequest portletRequest) {
        return true;
    }

}
