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
package org.jasig.portlet.proxy.service.web;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

/**
 * RedirectTrackingResponseInterceptor tracks any redirects that occur during
 * retrieval of the target content.  The final URL is set as a context attribute
 * on the HttpResponse for later retrieval.  This interceptor ensures that 
 * any relative URL calculations performed while processing the response are made
 * absolute using the correct final URL.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
public class RedirectTrackingResponseInterceptor implements
		HttpResponseInterceptor {

	public static final String FINAL_URL_KEY = "finalUrl";
	
	@Override
	public void process(HttpResponse response, HttpContext context)
			throws HttpException, IOException {
        if (response.containsHeader("Location")) {
            Header[] locations = response.getHeaders("Location");
            if (locations.length > 0) {
            	context.setAttribute(FINAL_URL_KEY, locations[0].getValue());
            }
        }
	}
	
}
