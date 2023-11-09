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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.portlet.PortletRequest;


import lombok.extern.slf4j.Slf4j;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.springframework.stereotype.Service;

/**
 * UserInfoUrlParameterizingPreInterceptor allows content requests to include
 * dynamic parameters of the form {attributeName} in content requests, either
 * in the URL string or in parameters associated with the request.  Each
 * dynamic string will be replaced with the value of the attribute in the UserInfo
 * map, if one exists.
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
@Service("userInfoUrlParameterizingPreInterceptor")
@Slf4j
public class UserInfoUrlParameterizingPreInterceptor implements IPreInterceptor {

  /** {@inheritDoc} */
  @Override
	public void intercept(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {

        try {
			String url = proxyRequest.getProxiedLocation();

			// iterate through each value in the UserInfo map
			@SuppressWarnings("unchecked")
			final Map<String, String> userInfo = (Map<String, String>) portletRequest.getAttribute(PortletRequest.USER_INFO);
			for (final String key : userInfo.keySet()) {
				if (log.isDebugEnabled()) {
					log.debug("Checking user attribute {} with value {}", key, "password".equals(key) ?
							"length is " + userInfo.get(key) : userInfo.get(key));
				}
				final String token = "{".concat(key).concat("}");

				// if the URL contains {attributeName}, replace that string
				// with the attribute's value
				if (url.contains(token)) {
					url = url.replaceAll("\\{".concat(key).concat("\\}"), URLEncoder.encode(userInfo.get(key), "UTF-8"));
					log.debug("Token {} found in url and replaced", key);
				}
				
				// if any parameter values associated with the request contain
				// {attributeName}, replace that string in the parameter with
				// the attribute's value
				for (Map.Entry<String, IFormField> param : proxyRequest.getParameters().entrySet()) {
					final int length = param.getValue().getValues().length;
					for (int i = 0; i < length; i++) {
						String value = param.getValue().getValues()[i];
						if (value.contains(token)) {
							param.getValue().getValues()[i] = value.replaceAll("\\{".concat(key).concat("\\}"), userInfo.get(key));
							log.debug("Token {} found in parameter and replaced", key);
						}
					}
					
				}
			}

			// curly braces are illegal URL characters
			url = removeRemainingUrlTokens(url);

			// update the URL in the content request
			proxyRequest.setProxiedLocation(url);

		} catch (UnsupportedEncodingException e) {
			log.error("Exception while encoding URL parameters", e);
		}

	}

	private String removeRemainingUrlTokens(String url) {
	  String s = url.replaceAll("/\\{[^\\}]*\\}/", "/");
	  return s.replaceAll("\\{[^\\}]*\\}", "");
	}

	/** {@inheritDoc} */
	@Override
	public boolean validate(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {
		return true;
	}

}
