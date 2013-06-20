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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jasig.portlet.proxy.service.GenericContentResponseImpl;

public class HttpContentResponseImpl extends GenericContentResponseImpl implements Serializable {

    private static final long serialVersionUID = 1L;  // To get rid of FindBugs complaints

	private Map<String, String> headers = new HashMap<String, String>();

	private transient HttpEntity entity;
	
	public HttpContentResponseImpl(HttpEntity entity) {
		this.entity = entity;
		this.headers = new HashMap<String, String>();
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public void close() {
		EntityUtils.consumeQuietly(entity);
	}


}
