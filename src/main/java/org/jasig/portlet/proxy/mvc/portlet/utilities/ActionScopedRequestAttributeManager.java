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
 */

@Component
public class ActionScopedRequestAttributeManager {
    public static final String ACTION_SCOPED_REQUEST_PREFIX = "actionScopedRequestAttributePrefix.WebProxyPortlet";

    public void storeItem (PortletRequest request, String key, Object item) {
        request.getPortletSession().setAttribute(ACTION_SCOPED_REQUEST_PREFIX + key, item);
    }

    public Object propogateAndReturnItem (RenderRequest request, String key) {
        return propogateAndReturnItem(request, key, true);
    }

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
