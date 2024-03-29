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
package org.jasig.portlet.proxy.service.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jasig.portlet.proxy.service.GenericContentResponseImpl;

/**
 * <p>HttpContentResponseImpl class.</p>
 *
 * @author bjagg
 * @version $Id: $Id
 */
public class HttpContentResponseImpl extends GenericContentResponseImpl implements Serializable {

    private static final long serialVersionUID = 1L;  // To get rid of FindBugs complaints

	private Map<String, String> headers = new HashMap<String, String>();

	private transient HttpEntity entity;
	
	/**
	 * <p>Constructor for HttpContentResponseImpl.</p>
	 *
	 * @param entity a {@link org.apache.http.HttpEntity} object
	 */
	public HttpContentResponseImpl(HttpEntity entity) {
		this.entity = entity;
		this.headers = new HashMap<String, String>();
	}

	/**
	 * <p>Getter for the field <code>headers</code>.</p>
	 *
	 * @return a {@link java.util.Map} object
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * <p>Setter for the field <code>headers</code>.</p>
	 *
	 * @param headers a {@link java.util.Map} object
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		EntityUtils.consumeQuietly(entity);
	}


}
