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
package org.jasig.portlet.proxy.service;

import javax.portlet.PortletRequest;

/**
 * IContentService represents a service for providing content.  Content might 
 * be retrieved from remote locations, the filesystem, a database, etc.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
public interface IContentService<T extends IContentRequest, S extends IContentResponse> {

	/**
	 * Get an appropriate default content request for the current portlet 
	 * request.
	 * 
	 * @param request
	 * @return
	 */
	public T getRequest(PortletRequest request);
	
	/**
	 * Retrieve content for the given content request and portlet request.
	 * 
	 * @param proxyRequest
	 * @param request
	 * @return
	 */
    public S getContent(T contentRequest, PortletRequest request);

}
