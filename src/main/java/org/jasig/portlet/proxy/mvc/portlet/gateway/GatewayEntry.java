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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.jasig.portlet.proxy.service.web.IAuthenticationFormModifier;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GatewayEntry represents a user-facing link in the Gateway SSO portlet.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
public class GatewayEntry {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String name;
	private String iconUrl;
	private LinkedHashMap<HttpContentRequestImpl, List<String>> contentRequests = new LinkedHashMap<HttpContentRequestImpl, List<String>>();
    private List<IAuthenticationFormModifier> authenticationFormModifier = new ArrayList<IAuthenticationFormModifier>();
    private boolean requireSecure = true;

	/**
	 * Get the display text for this link (user-friendly system name)
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the display text (user-friendly system name) for this link.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the icon for this link.
	 * 
	 * @return
	 */
	public String getIconUrl() {
		return iconUrl;
	}

	/**
	 * Set an icon for this link.
	 * 
	 * @param iconUrl
	 */
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	/**
	 * Get a map of content requests to be executed for this link.
	 * 
	 * @return
	 */
	public LinkedHashMap<HttpContentRequestImpl, List<String>> getContentRequests() {
		return contentRequests;
	}

	/**
	 * Set the map of content requests to be executed for this link.  Each
	 * entry should have a key consisting of the desired configured content
	 * request pointing to a list of interceptor keys.  Each request will be
	 * executed in the given order after being processed by any interceptors.
	 * 
	 * @param contentRequests
	 */
	public void setContentRequests(LinkedHashMap<HttpContentRequestImpl, List<String>> contentRequests) {
		this.contentRequests = contentRequests;
	}
	
    public List getAuthenticationFormModifier() {
    	return this.authenticationFormModifier;
    }
    
    public void setAuthenticationFormModifier(List authenticationFormModifier) {
    	this.authenticationFormModifier = authenticationFormModifier;
    }

    public boolean isRequireSecure() {
        return requireSecure;
    }

    public void setRequireSecure(boolean requireSecure) {
        this.requireSecure = requireSecure;
    }

}
