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
package org.jasig.portlet.proxy.mvc.portlet.gateway;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.PortletRequest;

import org.springframework.beans.factory.annotation.Required;

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.jasig.portlet.proxy.service.web.IAuthenticationFormModifier;
import org.jasig.portlet.proxy.service.web.interceptor.IPreInterceptor;
import org.jasig.portlet.proxy.service.web.interceptor.UserPreferencesPreInterceptor;

/**
 * GatewayEntry represents a user-facing link in the Gateway SSO portlet.
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
public class GatewayEntry {

    private String name;
    private String iconUrl;
    private LinkedHashMap<HttpContentRequestImpl, List<IPreInterceptor>> contentRequests = new LinkedHashMap<HttpContentRequestImpl, List<IPreInterceptor>>();
    private List<IAuthenticationFormModifier> authenticationFormModifier = new ArrayList<IAuthenticationFormModifier>();
    private String javascriptFile;
    private List<String> roleWhitelist = new ArrayList<String>();
    private boolean requireSecure = true;
    private GatewayEntryOperations operations = new GatewayEntryOperations();

    /**
     * Get the display text for this link (user-friendly system name)
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * Set the display text (user-friendly system name) for this link.
     *
     * @param name a {@link java.lang.String} object
     */
    @Required
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the icon for this link.
     *
     * @return a {@link java.lang.String} object
     */
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * Set an icon for this link.
     *
     * @param iconUrl a {@link java.lang.String} object
     */
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    /**
     * <p>Getter for the field <code>javascriptFile</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getJavascriptFile() {
        return javascriptFile;
    }

    /**
     * <p>Setter for the field <code>javascriptFile</code>.</p>
     *
     * @param javascriptFile a {@link java.lang.String} object
     */
    public void setJavascriptFile(String javascriptFile) {
        this.javascriptFile = javascriptFile;
    }

    /**
     * <p>Getter for the field <code>roleWhitelist</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getRoleWhitelist() {
        return roleWhitelist;
    }

    /**
     * <p>Setter for the field <code>roleWhitelist</code>.</p>
     *
     * @param roleWhitelist a {@link java.util.List} object
     */
    public void setRoleWhitelist(List<String> roleWhitelist) {
        this.roleWhitelist = roleWhitelist;
    }

    /**
     * Get a map of content requests to be executed for this link.
     *
     * @return a {@link java.util.LinkedHashMap} object
     */
    public LinkedHashMap<HttpContentRequestImpl, List<IPreInterceptor>> getContentRequests() {
        return contentRequests;
    }

    /**
     * Set the map of content requests to be executed for this link.  Each
     * entry should have a key consisting of the desired configured content
     * request pointing to a list of interceptor keys.  Each request will be
     * executed in the given order after being processed by any interceptors.
     *
     * @param contentRequests a {@link java.util.LinkedHashMap} object
     */
    public void setContentRequests(LinkedHashMap<HttpContentRequestImpl, List<IPreInterceptor>> contentRequests) {
        this.contentRequests = contentRequests;
    }

    /**
     * <p>Getter for the field <code>authenticationFormModifier</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<IAuthenticationFormModifier> getAuthenticationFormModifier() {
        return this.authenticationFormModifier;
    }

    /**
     * <p>Setter for the field <code>authenticationFormModifier</code>.</p>
     *
     * @param authenticationFormModifier a {@link java.util.List} object
     */
    public void setAuthenticationFormModifier(List<IAuthenticationFormModifier> authenticationFormModifier) {
        this.authenticationFormModifier = authenticationFormModifier;
    }

    /**
     * <p>isRequireSecure.</p>
     *
     * @return a boolean
     */
    public boolean isRequireSecure() {
        return requireSecure;
    }

    /**
     * <p>Setter for the field <code>requireSecure</code>.</p>
     *
     * @param requireSecure a boolean
     */
    public void setRequireSecure(boolean requireSecure) {
        this.requireSecure = requireSecure;
    }

    /**
     * <p>Getter for the field <code>operations</code>.</p>
     *
     * @return a {@link org.jasig.portlet.proxy.mvc.portlet.gateway.GatewayEntry.GatewayEntryOperations} object
     */
    public GatewayEntryOperations getOperations() {
        return operations;
    }

    /**
     * Returns true if the entry is accessible to the user.  An entry is accessible if there are no whitelist roles
     * defined, or if the user has at least one of the roles in the whitelist.
     *
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
     * {@inheritDoc}
     *
     * Return a hashcode of the name.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * Entries are equal if the names are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof GatewayEntry) {
                String othername = ((GatewayEntry) obj).getName();
            return name.equals(othername);
        }
        return false;
    }
    
    /*
     * Nested Types
     */

    /**
     * Represents the set of config or administrative actions that may be performed 
     * by a user on a {@link GatewayEntry}.  It has nothing to do with invoking or 
     * following the entry itself.  Evaluating these questions is a bit inelegant 
     * with the current setup-- the whole area could use some refactoring.  This 
     * inner class exists so that these transgressions can be grouped together 
     * and replaced easily in the future.
     */
    public /* non-static */ class GatewayEntryOperations {

        public boolean getEnterCredentialsAllowed() {
            // The way Gateway SSO supports user-provided credentials is by 
            // piggy-backing on the UserPreferencesPreInterceptor, so the way 
            // we'll answer this question (for now... until we learn why this 
            // approach doesn't suit all circumstances) is by checking whether 
            // the GatewayEntry uses it at all.
            boolean allowed = false;  // default
            for (Map.Entry<HttpContentRequestImpl, List<IPreInterceptor>> y : contentRequests.entrySet()) {
                for (IPreInterceptor interceptor : y.getValue()) {
                    if (interceptor instanceof UserPreferencesPreInterceptor) {
                        allowed = true;
                        break;
                    }
                }
                if (allowed) {
                    break;
                }
            }
            return allowed;
        }

        public boolean getClearCredentialsAllowed() {
            // For the present, we've placed this function within EDIT mode
            // (where you may only be if you can enter creds) so we will always
            // allow it
            return true;
        }

    }

}
