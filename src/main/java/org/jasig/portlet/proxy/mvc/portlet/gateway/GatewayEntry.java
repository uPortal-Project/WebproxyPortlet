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
package org.jasig.portlet.proxy.mvc.portlet.gateway;

import java.util.LinkedHashMap;
import java.util.List;

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;

public class GatewayEntry {

	private String name;
	private String iconUrl;
	private LinkedHashMap<HttpContentRequestImpl, List<String>> contentRequests = new LinkedHashMap<HttpContentRequestImpl, List<String>>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public LinkedHashMap<HttpContentRequestImpl, List<String>> getContentRequests() {
		return contentRequests;
	}

	public void setContentRequests(LinkedHashMap<HttpContentRequestImpl, List<String>> contentRequests) {
		this.contentRequests = contentRequests;
	}

}
