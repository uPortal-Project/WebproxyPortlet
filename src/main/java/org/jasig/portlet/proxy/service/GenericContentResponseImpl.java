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

import java.io.InputStream;


public class GenericContentResponseImpl implements IContentResponse {

	private String proxiedLocation;
	private InputStream content;
	
	public GenericContentResponseImpl() { }
	
	public GenericContentResponseImpl(String proxiedLocation, InputStream content) {
		this.proxiedLocation = proxiedLocation;
		this.content = content;
	}

	@Override
	public void close() {
		// no-op
	}

	@Override
	public String getProxiedLocation() {
		return proxiedLocation;
	}

	public void setProxiedLocation(String proxiedLocation) {
		this.proxiedLocation = proxiedLocation;
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}
	
}
