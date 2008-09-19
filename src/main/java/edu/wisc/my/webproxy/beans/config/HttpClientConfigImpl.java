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
 *c
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * @author dgrimwood
 * @version $Id$
 */
public class HttpClientConfigImpl extends JspConfigPage {
    private static final String HTTPCLIENT_PREF_PREFIX = "webproxy.httpclient.";
    
    public static final String HTTP_TIMEOUT     = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("httpTimeout").toString(); 
    public static final String MAX_REDIRECTS    = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("redirects").toString();
    public static final String CIRCULAR_REDIRECTS    = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("circularRedirects").toString();
    public static final String AUTH_TYPE        = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sAuthType").toString();
    public static final String AUTH_URL         = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sAuthenticationUrl").toString();
    
    
    public static final String AUTH_ENABLE      = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("authEnable").toString();
    
    public static final String USER_NAME         = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("userName").toString();
    public static final String PROMPT_USER_NAME  = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("promptUserName").toString();
    public static final String PERSIST_USER_NAME = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("persistUserName").toString();
    
    public static final String PASSWORD         = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("password").toString();
    public static final String PROMPT_PASSWORD  = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("promptPassword").toString();
    public static final String PERSIST_PASSWORD = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("persistPassword").toString();
  
    public static final String SHARED_SESSION_KEY           = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sessionKey").toString();
    public static final String SESSION_PERSISTENCE_ENABLE   = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sessionPersistenceEnable").toString();
    public static final String DOMAIN           = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("domain").toString();
    public static final String PROMPT_DOMAIN    = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("promptDomain").toString();
    public static final String PERSIST_DOMAIN   = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("persistDomain").toString();
    
    public static final String SESSION_TIMEOUT  = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sessionTimeout").toString();
    public static final String DYNAMIC_PARAM_NAMES     = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sDynamicParameterNames").toString();
    public static final String DYNAMIC_PARAM_VALUES    = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sDynamicParameterValues").toString();
    public static final String DYNAMIC_PARAM_PERSIST   = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sDynamicParameterPersist").toString();
    public static final String DYNAMIC_PARAM_SENSITIVE = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sDynamicParameterSensitive").toString();
    public static final String STATIC_PARAM_NAMES      = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sStaticParameterNames").toString();
    public static final String STATIC_PARAM_VALUES     = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPCLIENT_PREF_PREFIX).append("sStaticParameterValues").toString();
    
    
    public static final String AUTH_TYPE_BASIC = "BASIC";
    public static final String AUTH_TYPE_NTLM  = "NTLM";
    public static final String AUTH_TYPE_FORM  = "FORM";
    
    
    public String getName() {
        return "Http Configuration";
    }

    public void process(ActionRequest request, ActionResponse response) throws PortletException, IOException, ConfigurationException {
        final StringBuffer errorMessages = new StringBuffer();
        final PortletPreferences prefs = request.getPreferences();
        
        
        final Boolean authEnable = new Boolean(request.getParameter(AUTH_ENABLE));
        prefs.setValue(AUTH_ENABLE, authEnable.toString());
        
        final Boolean sessionPersistenceEnable = new Boolean(request.getParameter(SESSION_PERSISTENCE_ENABLE));
        prefs.setValue(SESSION_PERSISTENCE_ENABLE, sessionPersistenceEnable.toString());
        
        
        String maxRedirectsStr = null;
        try {
            maxRedirectsStr = ConfigUtils.checkEmptyNullString(request.getParameter(MAX_REDIRECTS), null);
            if (maxRedirectsStr != null && maxRedirectsStr.length() > 0) {
                final Integer maxRedirects = new Integer(maxRedirectsStr);
                prefs.setValue(MAX_REDIRECTS, maxRedirects.toString());
            }
            else {
                prefs.setValue(MAX_REDIRECTS, "");
            }
        }
        catch (NumberFormatException nfe) {
            errorMessages.append("Invalid max redirects specified '").append(maxRedirectsStr).append("'\n");
        }
        
        final Boolean circularRedirects = Boolean.valueOf(request.getParameter(CIRCULAR_REDIRECTS));
        prefs.setValue(CIRCULAR_REDIRECTS, circularRedirects.toString());
        
        String httpTimeoutStr = null;
        try {
            httpTimeoutStr = ConfigUtils.checkEmptyNullString(request.getParameter(HTTP_TIMEOUT), null);
            if (httpTimeoutStr != null && httpTimeoutStr.length() > 0) {
                final Long httpTimeout = new Long(httpTimeoutStr);
                prefs.setValue(HTTP_TIMEOUT, httpTimeout.toString());
            }
            else {
                prefs.setValue(HTTP_TIMEOUT, "");
            }
        }
        catch (NumberFormatException nfe) {
            errorMessages.append("Invalid authorization timeout specified '").append(httpTimeoutStr).append("'\n");
        }

        String sessionTimeoutStr = null;
        try {
            sessionTimeoutStr = ConfigUtils.checkEmptyNullString(request.getParameter(SESSION_TIMEOUT), null);
            if (sessionTimeoutStr != null && sessionTimeoutStr.length() > 0) {
                final Long sessionTimeout = new Long(sessionTimeoutStr);
                prefs.setValue(SESSION_TIMEOUT, sessionTimeout.toString());
            }
            else {
                prefs.setValue(SESSION_TIMEOUT, "");
            }
        }
        catch (NumberFormatException nfe) {
            errorMessages.append("Invalid authorization timeout specified '").append(sessionTimeoutStr).append("'\n");
        }
        
        final String sessionKey = request.getParameter(SHARED_SESSION_KEY);
        if (sessionKey != null) {
            prefs.setValue(SHARED_SESSION_KEY, sessionKey);
        }
        
        final String authType = request.getParameter(AUTH_TYPE);
        if (AUTH_TYPE_BASIC.equals(authType))
            prefs.setValue(AUTH_TYPE, AUTH_TYPE_BASIC);
        else if (AUTH_TYPE_NTLM.equals(authType))
            prefs.setValue(AUTH_TYPE, AUTH_TYPE_NTLM);
        else if (AUTH_TYPE_FORM.equals(authType))
            prefs.setValue(AUTH_TYPE, AUTH_TYPE_FORM);
        else
            errorMessages.append("Invalid authorization type specified '").append(authType).append("'\n");

        
        final String authUrl = ConfigUtils.checkEmptyNullString(request.getParameter(AUTH_URL), "");
        prefs.setValue(AUTH_URL, authUrl);
        

        final String userName = ConfigUtils.checkEmptyNullString(request.getParameter(USER_NAME), "");
        prefs.setValue(USER_NAME, userName);
        final Boolean promptUserName = new Boolean(request.getParameter(PROMPT_USER_NAME));
        prefs.setValue(PROMPT_USER_NAME, promptUserName.toString());
        final Boolean persistUserName = new Boolean(request.getParameter(PERSIST_USER_NAME));
        prefs.setValue(PERSIST_USER_NAME, persistUserName.toString());
        
        
        final String password = ConfigUtils.checkEmptyNullString(request.getParameter(PASSWORD), "");
        prefs.setValue(PASSWORD, password);
        final Boolean promptPassword = new Boolean(request.getParameter(PROMPT_PASSWORD));
        prefs.setValue(PROMPT_PASSWORD, promptPassword.toString());
        final Boolean persistPassword = new Boolean(request.getParameter(PERSIST_PASSWORD));
        prefs.setValue(PERSIST_PASSWORD, persistPassword.toString());

        
        
        final String[] dynamicParamNames = ConfigUtils.checkNullStringArray(request.getParameterValues(DYNAMIC_PARAM_NAMES), new String[0]);
        final String[] dynamicParamPersist = ConfigUtils.checkNullStringArray(request.getParameterValues(DYNAMIC_PARAM_PERSIST), new String[0]);
        final String[] dynamicParamSensitive = ConfigUtils.checkNullStringArray(request.getParameterValues(DYNAMIC_PARAM_SENSITIVE), new String[0]);

        final List<String> dynamicParamNamesList = new ArrayList<String>(dynamicParamNames.length);
        final Set<String> dynamicParamPersistSet = new HashSet<String>(Arrays.asList(dynamicParamPersist)); 
        final List<String> dynamicParamPersistList = new ArrayList<String>(dynamicParamPersistSet.size());
        final Set<String> dynamicParamSensitiveSet = new HashSet<String>(Arrays.asList(dynamicParamSensitive)); 
        final List<String> dynamicParamSensitiveList = new ArrayList<String>(dynamicParamSensitiveSet.size());

        for (int index = 0; index < dynamicParamNames.length; index++) {
            final String paramName = ConfigUtils.checkEmptyNullString(dynamicParamNames[index], null);
            if (paramName != null) {
                dynamicParamNamesList.add(paramName);
                
                final String indexStr = Integer.toString(index);
                if (dynamicParamPersistSet.contains(indexStr)) {
                    dynamicParamPersistList.add(indexStr);
                }
                
                if (dynamicParamSensitiveSet.contains(indexStr)) {
                    dynamicParamSensitiveList.add(indexStr);
                }
            }
        }

        prefs.setValues(DYNAMIC_PARAM_NAMES, (String[])dynamicParamNamesList.toArray(new String[dynamicParamNamesList.size()]));
        prefs.setValues(DYNAMIC_PARAM_PERSIST, (String[])dynamicParamPersistList.toArray(new String[dynamicParamPersistList.size()]));
        prefs.setValues(DYNAMIC_PARAM_SENSITIVE, (String[])dynamicParamSensitiveList.toArray(new String[dynamicParamSensitiveList.size()]));



        final String[] staticParamNames = ConfigUtils.checkNullStringArray(request.getParameterValues(STATIC_PARAM_NAMES), new String[0]);
        final String[] staticParamValues = ConfigUtils.checkNullStringArray(request.getParameterValues(STATIC_PARAM_VALUES), new String[0]);
        
        if (staticParamNames.length == staticParamValues.length) {
            final List<String> staticParamNamesList = new ArrayList<String>(staticParamNames.length);
            final List<String> staticParamValuesList = new ArrayList<String>(staticParamValues.length);
    
            for (int index = 0; index < staticParamNames.length; index++) {
                final String paramName = ConfigUtils.checkEmptyNullString(staticParamNames[index], null);

                if (paramName != null) {
                    final String paramValue = ConfigUtils.checkEmptyNullString(staticParamValues[index], null);
                    
                    staticParamNamesList.add(ConfigUtils.checkEmptyNullString(paramName, ""));
                    staticParamValuesList.add(ConfigUtils.checkEmptyNullString(paramValue, ""));
                }
            }
        
            prefs.setValues(STATIC_PARAM_NAMES, (String[])staticParamNamesList.toArray(new String[staticParamNamesList.size()]));
            prefs.setValues(STATIC_PARAM_VALUES, (String[])staticParamValuesList.toArray(new String[staticParamValuesList.size()]));
        }
        else {
            errorMessages.append("Static parameter and value parameter lists have inconsistent lengths.\n");
        }

        
        if (errorMessages.length() > 0) {
            throw new ConfigurationException(errorMessages.toString());
        }
        else {
            prefs.store();
        }
    }

}

