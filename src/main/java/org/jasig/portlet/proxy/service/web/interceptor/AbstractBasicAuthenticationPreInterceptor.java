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

import javax.portlet.PortletRequest;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.jasig.portlet.proxy.service.web.IHttpClientService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AbstractBasicAuthenticationPreInterceptor provides common logic for any 
 * authentication implementations using BASIC auth.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
public abstract class AbstractBasicAuthenticationPreInterceptor extends AuthenticationPreInterceptor {

	private IHttpClientService httpClientService;
	
	@Autowired(required = true)
	public void setHttpClientService(IHttpClientService httpClientService) {
		this.httpClientService = httpClientService;
	}

    @Override
    public boolean validate(HttpContentRequestImpl proxyRequest,
            PortletRequest portletRequest) {
        return true;
    }

	/**
	 * Add BASIC authentication credentials to the user's HttpClientService.
	 */
	@Override
	protected void prepareAuthentication(HttpContentRequestImpl contentRequest,
			PortletRequest portletRequest) {
		
		// create a new basic auth type credentials provider
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		final Credentials credentials = getCredentials(portletRequest);
		credentialsProvider.setCredentials(AuthScope.ANY,credentials);
		
		// Set the credentials provider on the HTTP client.  The HTTP client is
		// not limited to the session of the target website, so these credentials
		// may be applied more than once.  We expect these periodic updates to 
		// be unnecessary but do not expect them to cause any problems.
		final HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credentialsProvider);

		contentRequest.setHttpContext(context);
	}

	/**
	 * Provide credentials for the current user for this target service.
	 * 
	 * @param portletRequest
	 * @return
	 */
	protected abstract Credentials getCredentials(PortletRequest portletRequest);

}
