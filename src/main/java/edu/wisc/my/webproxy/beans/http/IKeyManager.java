/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
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