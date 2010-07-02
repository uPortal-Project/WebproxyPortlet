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
package edu.wisc.my.webproxy.beans.filtering;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.XMLReader;

import edu.wisc.my.webproxy.beans.config.GeneralConfigImpl;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;


/**
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class FuncNameUrlFilter extends InclExclUrlFilter {
    private static final Log log = LogFactory.getLog(FuncNameUrlFilter.class);
    
    private static final String IDEMPOTENT_UP_FILE  = "tag.idempotent.render.userLayoutRootNode.uP";
    private static final String PORTLET_ACTION      = "?uP_portlet_action=true";
    private static final String FNAME_PREFIX        = "&uP_fname=";
    private static final String WINDOW_STATE_PREFIX = "&uP_window_state=";
    private static final String SEPERATOR_AND       = "&";
    private static final String SEPERATOR_EQL       = "=";
    
    private String[] urlStateList = null;
    private String encodedFuncName = null;

    public FuncNameUrlFilter() {
    }

    public FuncNameUrlFilter(XMLReader parent) {
        super(parent);
    }

    protected String doUrlRewite(String orignialUrl, int matchIndex) {
        if (this.encodedFuncName == null) {
            return orignialUrl;
        }
        
        final StringBuffer buff = new StringBuffer(512);
        
        buff.append(IDEMPOTENT_UP_FILE);
        buff.append(PORTLET_ACTION);
        buff.append(FNAME_PREFIX);
        buff.append(this.encodedFuncName);

        if (matchIndex >= 0 && this.urlStateList[matchIndex] != null && this.urlStateList[matchIndex].trim().length() > 0) {
            buff.append(WINDOW_STATE_PREFIX);
            buff.append(this.urlStateList[matchIndex]);
        }

        buff.append(SEPERATOR_AND);
        buff.append(WebproxyConstants.BASE_URL);
        buff.append(SEPERATOR_EQL);
        buff.append(this.encodeString(orignialUrl));
        
        final String fNameUrl = orignialUrl.toString();
        
        if (log.isDebugEnabled()) {
            log.debug("Re-Wrote '" + orignialUrl + "' to '" + fNameUrl + "'");
        }
        
        return buff.toString();
    }
    
    private String encodeString(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            log.error("Error URL encoding string '" + text + "' to 'UTF-8'", e);
            throw new RuntimeException(e);
        }
    }

    public void setRenderData(RenderRequest request, RenderResponse response) {
        final PortletPreferences pp = request.getPreferences();
        
        final String[] urlRegExList = pp.getValues(GeneralConfigImpl.FNAME_URL_REWRITE_MASKS, null);
        this.setUrlRegExList(urlRegExList);
        
        final String listType = pp.getValue(GeneralConfigImpl.FNAME_URL_LIST_TYPE, null);
        this.setListType(listType);
        
        this.urlStateList = pp.getValues(GeneralConfigImpl.FNAME_URL_REWRITE_STATES, null);
        this.encodedFuncName = pp.getValue(GeneralConfigImpl.FNAME_TARGET, null);
        if (this.encodedFuncName != null) {
            this.encodedFuncName = this.encodeString(this.encodedFuncName);
        }
        
        super.setRenderData(request, response);
    }

    public void clearData() {
        this.urlStateList = null;
        this.encodedFuncName = null;

        this.setListType(null);
        this.setListType(null);
        
        //clear data of parent
        super.clearData();
    }

    public String getName() {
        return "Functional Name URL Filter";
    }

    public void setActionData(ActionRequest request, ActionResponse response) {
        throw new IllegalStateException("FuncNameUrlFilter is invalid to use during an action");
    }
}
