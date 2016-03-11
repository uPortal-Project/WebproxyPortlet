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
/*******************************************************************************
* Copyright 2004, The Board of Regents of the University of Wisconsin System.
* All rights reserved.
*
* A non-exclusive worldwide royalty-free license is granted for this Software.
* Permission to use, copy, modify, and distribute this Software and its
* documentation, with or without modification, for any purpose is granted
* provided that such redistribution and use in source and binary forms, with or
* without modification meets the following conditions:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* 3. Redistributions of any form whatsoever must retain the following
* acknowledgement:
*
* "This product includes software developed by The Board of Regents of
* the University of Wisconsin System."
*
*THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
*WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
*BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
*PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS OF
*THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
*INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
*PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
*LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
*OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
*ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/
package edu.wisc.my.webproxy.beans.config;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import edu.wisc.my.webproxy.portlet.WebproxyConstants;


/**
 * Provides the configuration page for the caching features of the
 * WebProxy portlet.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class CacheConfigImpl extends JspConfigPage {
    private static final String CACHE_PREF_PREFIX = "webproxy.cache.";
    
    //PortletPreferences keys for the different cache options
    public static final String USE_CACHE        = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CACHE_PREF_PREFIX).append("useCache").toString();
    public static final String CACHE_TIMEOUT    = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CACHE_PREF_PREFIX).append("cacheTimeOut").toString();
    public static final String USE_EXPIRED      = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CACHE_PREF_PREFIX).append("useExpired").toString();
    public static final String RETRY_DELAY      = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CACHE_PREF_PREFIX).append("retryDelay").toString();
    public static final String PERSIST_CACHE    = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CACHE_PREF_PREFIX).append("persistCache").toString();
    public static final String CACHE_SCOPE      = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CACHE_PREF_PREFIX).append("cacheScope").toString();
    public static final String CACHE_SCOPE_USER      = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CACHE_PREF_PREFIX).append("user").toString();
    public static final String CACHE_SCOPE_APP      = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CACHE_PREF_PREFIX).append("application").toString();
    
    
    /**
     * @see edu.wisc.my.webproxy.beans.config.ConfigPage#getName()
     */
    public String getName() {
        return "Cache Configuration";
    }
    
    /**
     * @see edu.wisc.my.webproxy.beans.config.ConfigPage#process(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void process(ActionRequest request, ActionResponse response) throws PortletException, IOException, ConfigurationException {
        final StringBuffer errorMessages = new StringBuffer();
        final PortletPreferences prefs = request.getPreferences();
        
        
        final Boolean useCache = new Boolean(request.getParameter(USE_CACHE));
        prefs.setValue(USE_CACHE, useCache.toString());
        
        /*
         * Cache Scope is either "user" or "application"
         * user scope confines the cache to a portlet session while 
         * application scope is global to all instances of web proxy portlet
         */
        String cacheScope = null;
        cacheScope = ConfigUtils.checkEmptyNullString(request.getParameter(CACHE_SCOPE), CACHE_SCOPE_USER);
        prefs.setValue(CACHE_SCOPE, cacheScope);
                
        String cacheTimeoutStr = null;
        Integer cacheTimeout = null;
        try {
            cacheTimeoutStr = ConfigUtils.checkEmptyNullString(request.getParameter(CACHE_TIMEOUT), null);
            if (cacheTimeoutStr != null) {
                cacheTimeout = new Integer(cacheTimeoutStr);
                prefs.setValue(CACHE_TIMEOUT, cacheTimeout.toString());
            }
            else {
                prefs.setValue(CACHE_TIMEOUT, "");
            }
        }
        catch (NumberFormatException nfe) {
            errorMessages.append("Invalid cache timeout specified '").append(cacheTimeoutStr).append("'\n");
        }
        
        
        final Boolean useExpired = new Boolean(request.getParameter(USE_EXPIRED));
        prefs.setValue(USE_EXPIRED, useExpired.toString());
        
        
        String retryDelayStr = null;
        Integer retryDelay = null;
        try {
            retryDelayStr = ConfigUtils.checkEmptyNullString(request.getParameter(RETRY_DELAY), null);
            if (retryDelayStr != null) {
                retryDelay = new Integer(retryDelayStr);
                prefs.setValue(RETRY_DELAY, retryDelay.toString());
            }
            else {
                prefs.setValue(RETRY_DELAY, "");
            }
        }
        catch (NumberFormatException nfe) {
            errorMessages.append("Invalid retry delay specified '").append(retryDelayStr).append("'\n");
        }
        
        
        final Boolean persistCache = new Boolean(request.getParameter(PERSIST_CACHE));
        prefs.setValue(PERSIST_CACHE, persistCache.toString());

        
        if (errorMessages.length() > 0) {
            throw new ConfigurationException(errorMessages.toString());
        }
        else {
            prefs.store();
        }
    }
}
