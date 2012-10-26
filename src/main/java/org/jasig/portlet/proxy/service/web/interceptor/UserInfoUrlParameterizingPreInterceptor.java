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
package org.jasig.portlet.proxy.service.web.preprocessor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.springframework.stereotype.Service;

@Service("userInfoUrlParameterizingPreInterceptor")
public class UserInfoUrlParameterizingPreInterceptor implements IPreInterceptor {
	
	protected final Log log = LogFactory.getLog(getClass());

	@Override
	public void intercept(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {

        try {
			String url = proxyRequest.getProxiedLocation();

			@SuppressWarnings("unchecked")
			final Map<String, String> userInfo = (Map<String, String>) portletRequest.getAttribute(PortletRequest.USER_INFO);
			for (final String key : userInfo.keySet()) {
				final String token = "{".concat(key).concat("}");
				
				if (url.contains(token)) {
					url = url.replaceAll("\\{".concat(key).concat("\\}"), URLEncoder.encode(userInfo.get(key), "UTF-8"));
				}
				
				for (Map.Entry<String, String[]> param : proxyRequest.getParameters().entrySet()) {
					final int length = param.getValue().length;
					for (int i = 0; i < length; i++) {
						String value = param.getValue()[i];
						if (value.contains(token)) {
							param.getValue()[i] = value.replaceAll("\\{".concat(key).concat("\\}"), userInfo.get(key));
						}
					}
					
				}
			}
			
			proxyRequest.setProxiedLocation(url);

		} catch (UnsupportedEncodingException e) {
			log.error("Exception while encoding URL parameters", e);
		}

	}

}
