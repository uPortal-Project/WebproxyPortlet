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

package edu.wisc.my.webproxy.beans.interceptors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.wisc.my.webproxy.beans.http.Request;

public class UserInfoUrlParameterizingPreInterceptor implements PreInterceptor {

    private final Log log = LogFactory.getLog(getClass());

    public void intercept(RenderRequest req, RenderResponse res, Request httpReq) {
        updateUri(req, httpReq);
    }

    public void intercept(ActionRequest req, ActionResponse res, Request httpReq) {
        updateUri(req, httpReq);
    }

    public void intercept(HttpServletRequest req, HttpServletResponse res, Request httpReq) {
        log.warn("Invoking intercept() with HttpServletRequest/HttpServletResponse;  " +
        		"URL parameters cannot be rewritten since there is no access to the " +
        		"PortletRequest.USER_INFO map.");
        /*
         * Nothing we can do here...
         */
    }
    
    /*
     * Implementation
     */
    
    private void updateUri(PortletRequest req, Request httpResp) {
        @SuppressWarnings("unchecked")
        Map<String,String> userInfo = (Map<String,String>) req.getAttribute(PortletRequest.USER_INFO);
        String uri = httpResp.getUrl();
        log.debug("Supplied URL:  " + uri);
        try {
            String newUri = injectUriParameters(uri, userInfo);
            log.debug("URL after processing parameters:  " + newUri);
            httpResp.setUrl(newUri);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String injectUriParameters(String uri, Map<String, String> params) throws UnsupportedEncodingException {

        String requestUri = null;
        String queryString = "";  // default
        
        final int queryStringBegin = uri.indexOf("?");
        if (queryStringBegin == -1) {
            // Simple case -- no querystring
            requestUri = uri;
        } else {
            // Complex case -- uri+querystring
            requestUri = uri.substring(0, queryStringBegin);
            queryString = uri.substring(queryStringBegin);  // will already contain the '?' character
        }

        // Inject requestUri params
        for (Map.Entry<String,String> y : params.entrySet()) {
            final String token = "{" + y.getKey() + "}";
            final String value = y.getValue();
            final String inject = URLEncoder.encode(value, "UTF-8");
            while(requestUri.contains(token)) {
                // Don't use String.replaceAll b/c token looks like an illegal regex
                requestUri = requestUri.replace(token, inject);
            }
        }
        
        // Inject queryString params
        for (Map.Entry<String,String> y : params.entrySet()) {
            final String paramName = y.getKey();
            final String token = "{" + paramName + "}";
            if (queryString.contains(token)) {
                final String inject = URLEncoder.encode(paramName, "UTF-8") + 
                        "=" + URLEncoder.encode(y.getValue(), "UTF-8");
                queryString = queryString.replace(token, inject);
            }
        }
        
        return requestUri + queryString;
        
    }
    
}
