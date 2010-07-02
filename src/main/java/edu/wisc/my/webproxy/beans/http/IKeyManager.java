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

package edu.wisc.my.webproxy.beans.http;

import javax.portlet.PortletRequest;

/**
 * Generates portlet instance specific persistent keys
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface IKeyManager {
    public static final String PORTLET_INSTANCE_KEY = IKeyManager.class.getName() + ".PORTLET_INSTANCE_KEY";

    public String generateCacheKey(String pageUrl, PortletRequest request);

    public String generateCacheKey(String pageUrl, String instanceKey);

    public String generateStateKey(String key, PortletRequest request);

    /**
     * Generates a unique key for this portlet instance that will persist across portal restarts
     */
    public String getInstanceKey(PortletRequest request);

}