
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

import lombok.extern.slf4j.Slf4j;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.springframework.stereotype.Service;

/**
 * HeaderPassingPreInterceptor allows content requests to include
 * pass along header attributes to proxied content
 *
 * @author bjagg
 * @version $Id: $Id
 */
@Service("headerPassingPreInterceptor")
@Slf4j
public class HeaderPassingPreInterceptor implements IPreInterceptor {

    /** Constant <code>HEADER_PREFERENCE_NAMES="headerNames"</code> */
    public static final String HEADER_PREFERENCE_NAMES = "headerNames";
    /** Constant <code>HEADER_PREFERENCE_VALUES="headerValues"</code> */
    public static final String HEADER_PREFERENCE_VALUES = "headerValues";

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void intercept(HttpContentRequestImpl proxyRequest, PortletRequest portletRequest) {
        PortletPreferences prefs = portletRequest.getPreferences();
        final String[] headerNames = prefs.getValues(HEADER_PREFERENCE_NAMES, new String[0]);
        final String[] headerValues = prefs.getValues(HEADER_PREFERENCE_VALUES, new String[0]);
        if (headerNames.length == headerValues.length) {
          Map<String, String> userInfo = (Map<String, String>) portletRequest.getAttribute(PortletRequest.USER_INFO);
          Map<String, String> headerMap = proxyRequest.getHeaders();
          for(int i = 0; i<headerNames.length; i++){
              String headerName = headerNames[i];
              String headerValue = userInfo.get(headerValues[i]);
              headerMap.put(headerName, headerValue);
          }
          proxyRequest.setHeaders(headerMap);
        }else{
          log.warn("Invalid data in preferences. Header name array length does not equal header value array length");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean validate(HttpContentRequestImpl proxyRequest,
            PortletRequest portletRequest) {
        return true;
    }

}
