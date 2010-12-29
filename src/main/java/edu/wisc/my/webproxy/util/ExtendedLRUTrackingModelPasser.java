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

package edu.wisc.my.webproxy.util;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

import org.jasig.web.util.LRUTrackingModelPasser;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class ExtendedLRUTrackingModelPasser extends LRUTrackingModelPasser implements ExtendedModelPasser {
    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.util.ExtendedModelPasser#getModelFromPortlet(javax.portlet.PortletRequest, javax.portlet.PortletResponse, java.lang.String)
     */
    public Map<String, ?> getModelFromPortlet(PortletRequest request, PortletResponse response, String key) {
        final PortletSession portletSession = request.getPortletSession();
        if (portletSession == null) {
            this.logger.debug("No session available, returning null for key '" + key + "'");
            return null;
        }
        
        final Map<String, Map<String, ?>> modelCache = this.getModelCache(portletSession);
        final Map<String, ?> model = modelCache.get(key);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found model '" + model + "' in PortletSession for key '" + key + "'");
        }

        return model;
    }
}
