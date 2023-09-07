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

import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class with common methods used by all Gateway Controllers.  Currently holds security methods that can be
 * replaced with Spring Security SpEL annotations when time permits.
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 * @version $Id: $Id
 */
@Deprecated // No known usage of this portlet type. Remove when we jump to Java 17.
public class BaseGatewayPortletController {
    /**
     * <p>removeInaccessibleEntries.</p>
     *
     * @param entries a {@link java.util.List} object
     * @param request a {@link javax.portlet.PortletRequest} object
     * @return a {@link java.util.List} object
     */
    protected List<GatewayEntry> removeInaccessibleEntries(List<GatewayEntry> entries, PortletRequest request) {
        List<GatewayEntry> accessibleEntries = new ArrayList<GatewayEntry>();
        for (GatewayEntry entry : entries) {
            if (entry.entryIsAccessible(request)) {
                accessibleEntries.add(entry);
            }
        }
        return accessibleEntries;
    }

    /**
     * <p>getAccessibleEntry.</p>
     *
     * @param entries a {@link java.util.List} object
     * @param request a {@link javax.portlet.PortletRequest} object
     * @param beanName a {@link java.lang.String} object
     * @return a {@link org.jasig.portlet.proxy.mvc.portlet.gateway.GatewayEntry} object
     */
    protected GatewayEntry getAccessibleEntry(List<GatewayEntry> entries, PortletRequest request, String beanName) {
        for (GatewayEntry entry : entries) {
            if (entry.getName().equals(beanName)) {
                if (entry.entryIsAccessible(request)) {
                    return entry;
                }
                return null;
            }
        }
        return null;
    }
}
