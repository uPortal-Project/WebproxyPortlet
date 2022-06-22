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
package org.jasig.portlet.proxy.mvc.portlet.utilities;

import java.util.Enumeration;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.springframework.stereotype.Component;

/**
 * Manages forwarding GatewayPreferences objects from Action to Render phases because Pluto doesn't support
 * javax.portlet.actionScopedRequestAttributes.
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 * @version $Id: $Id
 */

@Component
public class ActionScopedRequestAttributeManager {
    /** Constant <code>ACTION_SCOPED_REQUEST_PREFIX="actionScopedRequestAttributePrefix.WebP"{trunked}</code> */
    public static final String ACTION_SCOPED_REQUEST_PREFIX = "actionScopedRequestAttributePrefix.WebProxyPortlet";

    /**
     * <p>storeItem.</p>
     *
     * @param request a {@link javax.portlet.PortletRequest} object
     * @param key a {@link java.lang.String} object
     * @param item a {@link java.lang.Object} object
     */
    public void storeItem (PortletRequest request, String key, Object item) {
        request.getPortletSession().setAttribute(ACTION_SCOPED_REQUEST_PREFIX + key, item);
    }

    /**
     * <p>propogateAndReturnItem.</p>
     *
     * @param request a {@link javax.portlet.RenderRequest} object
     * @param key a {@link java.lang.String} object
     * @return a {@link java.lang.Object} object
     */
    public Object propogateAndReturnItem (RenderRequest request, String key) {
        return propogateAndReturnItem(request, key, true);
    }

    /**
     * <p>propogateAndReturnItem.</p>
     *
     * @param request a {@link javax.portlet.RenderRequest} object
     * @param key a {@link java.lang.String} object
     * @param clearItem a boolean
     * @return a {@link java.lang.Object} object
     */
    public Object propogateAndReturnItem (RenderRequest request, String key, boolean clearItem) {
        Object item = request.getPortletSession().getAttribute(ACTION_SCOPED_REQUEST_PREFIX + key);
        // If an item was present, store the item in the request and clear the session key to reduce session storage
        if (item != null) {
            request.setAttribute(key, item);
            if (clearItem) {
                request.getPortletSession().removeAttribute(ACTION_SCOPED_REQUEST_PREFIX + key);
            }
        }
        return item;
    }

    /**
     * <p>clearAttributes.</p>
     *
     * @param request a {@link javax.portlet.PortletRequest} object
     */
    public void clearAttributes(PortletRequest request) {
        Enumeration<String> attributeNames = request.getPortletSession().getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            if (attributeName.startsWith(ACTION_SCOPED_REQUEST_PREFIX)) {
                request.getPortletSession().removeAttribute(attributeName);
            }
        }
    }
}
