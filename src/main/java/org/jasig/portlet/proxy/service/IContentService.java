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
package org.jasig.portlet.proxy.service;

import javax.portlet.PortletRequest;

/**
 * IContentService represents a service for providing content.  Content might
 * be retrieved from remote locations, the filesystem, a database, etc.
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
public interface IContentService<T extends IContentRequest, S extends IContentResponse> {

	/**
	 * Get an appropriate default content request for the current portlet
	 * request.
	 *
	 * @param request a {@link javax.portlet.PortletRequest} object
	 * @return a T object
	 */
	public T getRequest(PortletRequest request);

    /**
     * Execute any necessary logic prior to retrieving the content
     *
     * @param contentRequest a T object
     * @param request a {@link javax.portlet.PortletRequest} object
     */
    public void beforeGetContent(T contentRequest, PortletRequest request);

    /**
     * Retrieve content for the given content request and portlet request.
     *
     * @param request a {@link javax.portlet.PortletRequest} object
     * @param contentRequest a T object
     * @return a S object
     */
    public S getContent(T contentRequest, PortletRequest request);

    /**
     * Execute any necessary logic after retrieving the content
     *
     * @param contentRequest a T object
     * @param request a {@link javax.portlet.PortletRequest} object
     * @param proxyResponse a S object
     */
    public void afterGetContent(T contentRequest, PortletRequest request, S proxyResponse);
}
