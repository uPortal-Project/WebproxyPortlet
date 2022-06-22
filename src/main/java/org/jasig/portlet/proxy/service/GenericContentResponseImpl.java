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
 * GenericContentResponseImpl provides a basic content response.
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
public class GenericContentResponseImpl implements IContentResponse {

	private String proxiedLocation;
	private InputStream content;
	
	/**
	 * <p>Constructor for GenericContentResponseImpl.</p>
	 */
	public GenericContentResponseImpl() { }

	/**
	 * Construct a content response using the provided location and content stream
	 *
	 * @param proxiedLocation a {@link java.lang.String} object
	 * @param content a {@link java.io.InputStream} object
	 */
	public GenericContentResponseImpl(String proxiedLocation, InputStream content) {
		this.proxiedLocation = proxiedLocation;
		this.content = content;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// no-op
	}

	/** {@inheritDoc} */
	@Override
	public String getProxiedLocation() {
		return proxiedLocation;
	}

	/**
	 * Set the proxied location.
	 *
	 * @param proxiedLocation a {@link java.lang.String} object
	 */
	public void setProxiedLocation(String proxiedLocation) {
		this.proxiedLocation = proxiedLocation;
	}

	/**
	 * <p>Getter for the field <code>content</code>.</p>
	 *
	 * @return a {@link java.io.InputStream} object
	 */
	public InputStream getContent() {
		return content;
	}

	/**
	 * Set the stream of retrieved content.
	 *
	 * @param content a {@link java.io.InputStream} object
	 */
	public void setContent(InputStream content) {
		this.content = content;
	}
	
}
