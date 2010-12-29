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
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import edu.wisc.my.webproxy.beans.interceptors.PostInterceptor;
import edu.wisc.my.webproxy.beans.interceptors.PreInterceptor;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * @author dgrimwood
 * @version $Id$
 */
public class GeneralConfigImpl extends JspConfigPage {
    private static final String GENERAL_PREF_PREFIX = "webproxy.general.config.";
    
    public static final String BASE_URL                     = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("sBaseUrl").toString();
    public static final String BASE_URL_KEY                 = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("sBaseUrlKey").toString();
    public static final String POST_PARAM_KEY                 = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("sPostParamKey").toString();
    public static final String EDIT_URL                     = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("sEditUrl").toString();
    public static final String PORTLET_URL_REWRITE_MASKS    = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("sPortletUrl").toString();
    public static final String PORTLET_URL_REWRITE_STATES   = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("sPortletState").toString();
    public static final String PORTLET_URL_LIST_TYPE        = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("sListType").toString();
    public static final String FNAME_URL_REWRITE_MASKS      = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("funcNameUrlRegEx").toString();
    public static final String FNAME_URL_REWRITE_STATES     = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("funcNameUrlStates").toString();
    public static final String FNAME_URL_LIST_TYPE          = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("funcNameListType").toString();
    public static final String FNAME_TARGET                 = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("funcNameTarget").toString();
    public static final String PRE_INTERCEPTOR_CLASS        = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("sPreInterceptor").toString();
    public static final String POST_INTERCEPTOR_CLASS       = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(GENERAL_PREF_PREFIX).append("sPostInterceptor").toString();
    
    public static final String URL_LIST_TYPE_INCLUDE = "INCLUDE";
    public static final String URL_LIST_TYPE_EXCLUDE = "EXCLUDE";
    
    public String getName() {
        return "General Configuration";
    }

    public void process(ActionRequest request, ActionResponse response) throws PortletException, IOException, ConfigurationException {
        final StringBuffer errorMessages = new StringBuffer();
        final PortletPreferences prefs = request.getPreferences();
        
        //Store base URL
        final String baseUrl = ConfigUtils.checkEmptyNullString(request.getParameter(BASE_URL), null);
        if (baseUrl == null)
            errorMessages.append("Base URL cannot be null\n");
        else
            prefs.setValue(BASE_URL, baseUrl);
        
        //Store edit URL
        final String editUrl = ConfigUtils.checkEmptyNullString(request.getParameter(EDIT_URL), null);
        if (editUrl != null)
            prefs.setValue(EDIT_URL, editUrl);
        else
            prefs.setValue(EDIT_URL, "");
        
        //Store rewrite masks
        final String[] portletUrlMasks = ConfigUtils.checkNullStringArray(request.getParameterValues(PORTLET_URL_REWRITE_MASKS), new String[0]);
        final String[] portletUrlStates = ConfigUtils.checkNullStringArray(request.getParameterValues(PORTLET_URL_REWRITE_STATES), new String[0]);
        
        if (portletUrlMasks.length == portletUrlStates.length) {
            final List<String> portletUrlMaskList = new ArrayList<String>(portletUrlMasks.length);
            final List<String> urlWindowStateList = new ArrayList<String>(portletUrlStates.length);
    
            for (int index = 0; index < portletUrlMasks.length; index++) {
                final String urlMask = ConfigUtils.checkEmptyNullString(portletUrlMasks[index], null);

                if (urlMask != null) {
                    final String windowState = ConfigUtils.checkEmptyNullString(portletUrlStates[index], null);
                    
                    portletUrlMaskList.add(ConfigUtils.checkEmptyNullString(urlMask, ""));
                    urlWindowStateList.add(ConfigUtils.checkEmptyNullString(windowState, ""));
                }
            }
        
            prefs.setValues(PORTLET_URL_REWRITE_MASKS, portletUrlMaskList.toArray(new String[portletUrlMaskList.size()]));
            prefs.setValues(PORTLET_URL_REWRITE_STATES, urlWindowStateList.toArray(new String[urlWindowStateList.size()]));
        }
        else {
            errorMessages.append("Portlet URL rewrite mask and window state lists have inconsistent lengths.\n");
        }

        //Store portlet URL rewrite list type
        final String portletUrlRewriteListType = request.getParameter(PORTLET_URL_LIST_TYPE);
        if (URL_LIST_TYPE_INCLUDE.equals(portletUrlRewriteListType))
            prefs.setValue(PORTLET_URL_LIST_TYPE, URL_LIST_TYPE_INCLUDE);
        else if (URL_LIST_TYPE_EXCLUDE.equals(portletUrlRewriteListType))
            prefs.setValue(PORTLET_URL_LIST_TYPE, URL_LIST_TYPE_EXCLUDE);
        else
            errorMessages.append("Invalid portlet URL rewrite list type specified '").append(portletUrlRewriteListType).append("'\n");


        //Store rewrite masks
        final String[] fNameUrlMasks = ConfigUtils.checkNullStringArray(request.getParameterValues(FNAME_URL_REWRITE_MASKS), new String[0]);
        final String[] fNameUrlStates = ConfigUtils.checkNullStringArray(request.getParameterValues(FNAME_URL_REWRITE_STATES), new String[0]);
        
        if (fNameUrlMasks.length == fNameUrlStates.length) {
            final List<String> fNameUrlMaskList = new ArrayList<String>(fNameUrlMasks.length);
            final List<String> urlWindowStateList = new ArrayList<String>(fNameUrlStates.length);
    
            for (int index = 0; index < fNameUrlMasks.length; index++) {
                final String urlMask = ConfigUtils.checkEmptyNullString(fNameUrlMasks[index], null);

                if (urlMask != null) {
                    final String windowState = ConfigUtils.checkEmptyNullString(fNameUrlStates[index], null);
                    
                    fNameUrlMaskList.add(ConfigUtils.checkEmptyNullString(urlMask, ""));
                    urlWindowStateList.add(ConfigUtils.checkEmptyNullString(windowState, ""));
                }
            }
        
            prefs.setValues(FNAME_URL_REWRITE_MASKS, fNameUrlMaskList.toArray(new String[fNameUrlMaskList.size()]));
            prefs.setValues(FNAME_URL_REWRITE_STATES, urlWindowStateList.toArray(new String[urlWindowStateList.size()]));
        }
        else {
            errorMessages.append("fName URL rewrite mask and window state lists have inconsistent lengths.\n");
        }

        //Store fName URL rewrite list type
        final String fNameUrlRewriteListType = request.getParameter(FNAME_URL_LIST_TYPE);
        if (URL_LIST_TYPE_INCLUDE.equals(fNameUrlRewriteListType))
            prefs.setValue(FNAME_URL_LIST_TYPE, URL_LIST_TYPE_INCLUDE);
        else if (URL_LIST_TYPE_EXCLUDE.equals(fNameUrlRewriteListType))
            prefs.setValue(FNAME_URL_LIST_TYPE, URL_LIST_TYPE_EXCLUDE);
        else
            errorMessages.append("Invalid fName URL rewrite list type specified '").append(fNameUrlRewriteListType).append("'\n");

        final String fNameTarget = ConfigUtils.checkEmptyNullString(request.getParameter(FNAME_TARGET), null);
        if (fNameTarget != null)
            prefs.setValue(FNAME_TARGET, fNameTarget);
        else
            prefs.setValue(FNAME_TARGET, "");
        
        
        //Validate the pre-interceptor class
        final String preInterceptorClassName = ConfigUtils.checkEmptyNullString(request.getParameter(PRE_INTERCEPTOR_CLASS), null);
        if (preInterceptorClassName != null) {
            String className = "";
            try {
                final Class preInterceptorClass = Class.forName(preInterceptorClassName);
                final PreInterceptor preInterceptor = (PreInterceptor)preInterceptorClass.newInstance();
                className = preInterceptor.getClass().getName();
            }
            catch (ClassNotFoundException cnfe) {
                errorMessages.append("Could not find specified pre-interceptor class '").append(preInterceptorClassName).append("'");
            }
            catch (InstantiationException ie) {
                errorMessages.append("Could not find specified pre-interceptor class '").append(preInterceptorClassName).append("'");
            }
            catch (IllegalAccessException iae) {
                errorMessages.append("Could not find specified pre-interceptor class '").append(preInterceptorClassName).append("'");
            }
            catch (ClassCastException cce) {
                errorMessages.append("Could not cast '").append(preInterceptorClassName).append("' to 'edu.wisc.my.webproxy.beans.interceptors.PreInterceptor'");
            }
            
            prefs.setValue(PRE_INTERCEPTOR_CLASS, className);
        }
        else {
            prefs.setValue(PRE_INTERCEPTOR_CLASS, "");
        }
        
        //Validate the post-interceptor class
        final String postInterceptorClassName = ConfigUtils.checkEmptyNullString(request.getParameter(POST_INTERCEPTOR_CLASS), null);
        if (postInterceptorClassName != null) {
            String className = "";
            try {
                final Class postInterceptorClass = Class.forName(postInterceptorClassName);
                final PostInterceptor postInterceptor = (PostInterceptor)postInterceptorClass.newInstance();
                className = postInterceptor.getClass().getName();
            }
            catch (ClassNotFoundException cnfe) {
                errorMessages.append("Could not find specified post-interceptor class '").append(postInterceptorClassName).append("'");
            }
            catch (InstantiationException ie) {
                errorMessages.append("Could not find specified post-interceptor class '").append(postInterceptorClassName).append("'");
            }
            catch (IllegalAccessException iae) {
                errorMessages.append("Could not find specified post-interceptor class '").append(postInterceptorClassName).append("'");
            }
            catch (ClassCastException cce) {
                errorMessages.append("Could not cast '").append(postInterceptorClassName).append("' to 'edu.wisc.my.webproxy.beans.interceptors.PostInterceptor'");
            }
            
            prefs.setValue(POST_INTERCEPTOR_CLASS, className);
        }
        else {
            prefs.setValue(POST_INTERCEPTOR_CLASS, "");
        }
        
        //Check for logged errors and store the prefs
        if (errorMessages.length() > 0) {
            throw new ConfigurationException(errorMessages.toString());
        }
        else {
            prefs.store();
        }
    }
}
