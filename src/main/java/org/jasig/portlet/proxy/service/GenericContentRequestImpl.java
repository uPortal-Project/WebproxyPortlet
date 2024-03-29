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

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;

/**
 * GenericContentRequestImpl represents a basic content request consisting
 * simply of a target location.
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
public class GenericContentRequestImpl implements IContentRequest {

    private String proxiedLocation;

    /**
     * Standard key for storing a content location in the portlet preferences.
     */
    public static final String CONTENT_LOCATION_PREFERENCE = "location";
    /** Constant <code>CONTENT_LOCATION_MAXIMIZED_PREFERENCE="location.MAXIMIZED"</code> */
    public static final String CONTENT_LOCATION_MAXIMIZED_PREFERENCE = "location.MAXIMIZED";

    /**
     * <p>Constructor for GenericContentRequestImpl.</p>
     */
    public GenericContentRequestImpl() { }

    /**
     * Construct a new content request, populating the location from the
     * portlet preferences.
     *
     * @param portletRequest a {@link javax.portlet.PortletRequest} object
     */
    public GenericContentRequestImpl(final PortletRequest portletRequest) {
        final PortletPreferences preferences = portletRequest.getPreferences();
        if (WindowState.MAXIMIZED.equals(portletRequest.getWindowState())) {
            this.proxiedLocation = preferences.getValue(CONTENT_LOCATION_MAXIMIZED_PREFERENCE, null);
        }
        if (this.proxiedLocation == null || this.proxiedLocation.length() == 0) {
            this.proxiedLocation = preferences.getValue(CONTENT_LOCATION_PREFERENCE, null);
        }
    }

    /**
     * <p>Getter for the field <code>proxiedLocation</code>.</p>
     *
     * @return Get the target location.
     */
    public String getProxiedLocation() {
        return proxiedLocation;
    }

    /**
     * Set the target location.
     *
     * @param proxiedLocation a {@link java.lang.String} object
     */
    public void setProxiedLocation(String proxiedLocation) {
        this.proxiedLocation = proxiedLocation;
    }

}
