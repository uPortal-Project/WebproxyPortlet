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

import java.io.InputStream;

/**
 * IContentResponse represents the response from a content service.
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
public interface IContentResponse {
	
	/**
	 * Get the proxied location.  This string should reflect the final location
	 * and may differ from that originally provided to the content service.
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getProxiedLocation();
	
	/**
	 * Get a stream of retrieved content.
	 *
	 * @return a {@link java.io.InputStream} object
	 */
	public InputStream getContent();
	
	/**
	 * Close the content response.  This method *must* be called by the client
	 * after all processing is complete.
	 */
	public void close();

}
