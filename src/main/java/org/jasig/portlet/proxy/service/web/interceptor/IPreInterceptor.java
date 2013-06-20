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
package org.jasig.portlet.proxy.service.web.interceptor;

import javax.portlet.PortletRequest;

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;

/**
 * IPreInterceptor provides an interface for modifying HTTP content requests before
 * the request is actually made.  Interceptors have access to request information
 * such as the target URL, headers, etc. and may modify these resources to perform
 * authentication, add request headers from the original request, or perform other
 * desired pre-processing tasks.
 * 
 * @author Bill Smith (wsmith@unicon.net)
 */
public interface IPreInterceptor {

	/**
	 * Intercept a content request before it is executed.
	 * 
	 * @param proxyRequest
	 * @param portletRequest
	 */
    public void intercept(HttpContentRequestImpl proxyRequest, PortletRequest portletRequest);
    
    /**
     * Confirm that the proxyRequest has all of the information required to execute.  Refer to the
     * specific implementation for details.
     * 
     * @param proxyRequest
     * @param portletRequest
     * @return true if the proxyRequest has all of the necessary information to be successfully run,
     * otherwise false
     */
    public boolean validate(HttpContentRequestImpl proxyRequest, PortletRequest portletRequest);
    
}
