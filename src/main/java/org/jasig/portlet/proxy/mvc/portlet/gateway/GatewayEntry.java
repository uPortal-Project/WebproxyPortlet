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

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.jasig.portlet.proxy.service.web.IAuthenticationFormModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
    private String javascriptFile;
    private List<String> roleWhitelist = new ArrayList<String>();
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
    @Required
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

    public String getJavascriptFile() {
        return javascriptFile;
    }

    public void setJavascriptFile(String javascriptFile) {
        this.javascriptFile = javascriptFile;
    }

    public List<String> getRoleWhitelist() {
        return roleWhitelist;
    }

    public void setRoleWhitelist(List<String> roleWhitelist) {
        this.roleWhitelist = roleWhitelist;
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

    public List<IAuthenticationFormModifier> getAuthenticationFormModifier() {
        return this.authenticationFormModifier;
    }

    public void setAuthenticationFormModifier(List<IAuthenticationFormModifier> authenticationFormModifier) {
        this.authenticationFormModifier = authenticationFormModifier;
    }

    public boolean isRequireSecure() {
        return requireSecure;
    }

    public void setRequireSecure(boolean requireSecure) {
        this.requireSecure = requireSecure;
    }

    /**
     * Returns true if the entry is accessible to the user.  An entry is accessible if there are no whitelist roles
     * defined, or if the user has at least one of the roles in the whitelist.
     * @param request PortletRequest
     * @return true if the entry is accessible to the user.
     */
    public boolean entryIsAccessible(PortletRequest request) {
        if (getRoleWhitelist().size() == 0) {
            return true;
        }
        for (String roleName : getRoleWhitelist()) {
            if (request.isUserInRole(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a hashcode of the name.
     * @return Hashcode value.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Entries are equal if the names are equal
     * @param obj 2nd object to test
     * @return true if entries are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof GatewayEntry) {
                String othername = ((GatewayEntry) obj).getName();
            return name.equals(othername);
        }
        return false;
    }
}
