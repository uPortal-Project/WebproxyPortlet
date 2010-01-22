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
 * "This product includes software developed by The Board of Regents of the
 * University of Wisconsin System."
 * 
 * THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
 * WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS
 * OF THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package edu.wisc.my.webproxy.beans.filtering;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.XMLReader;

import edu.wisc.my.webproxy.beans.config.GeneralConfigImpl;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * A filter that re-writes all URLs to portlet URLs
 * 
 * @author dgrimwood
 * 
 * @version $Id$
 * 
 */

public final class PortletUrlFilter extends InclExclUrlFilter {
    private static final Log log = LogFactory.getLog(PortletUrlFilter.class);
    
    private String[] urlStateList = null;
    private RenderResponse renderResponse = null;

    public PortletUrlFilter() {
    }

    public PortletUrlFilter(XMLReader parent) {
        super(parent);
    }

    protected String doUrlRewite(String orignialUrl, int matchIndex) {
        final PortletURL newUrl = this.renderResponse.createActionURL();
        newUrl.setParameter(WebproxyConstants.BASE_URL, orignialUrl);

        if (matchIndex >= 0 && this.urlStateList != null && this.urlStateList[matchIndex] != null && this.urlStateList[matchIndex].trim().length() > 0) {
            try {
                newUrl.setWindowState(new WindowState(this.urlStateList[matchIndex]));
            }
            catch (WindowStateException e) {
                log.error("Unable to set WindowState='" + this.urlStateList[matchIndex] + "'", e);
            }
        }

        final String portletUrl = newUrl.toString();
        
        if (log.isDebugEnabled()) {
            log.debug("Re-Wrote '" + orignialUrl + "' to '" + portletUrl + "'");
        }
        
        return portletUrl;
    }

    public void setRenderData(RenderRequest request, RenderResponse response) {
        final PortletPreferences pp = request.getPreferences();
        
        final String[] urlRegExList = pp.getValues(GeneralConfigImpl.PORTLET_URL_REWRITE_MASKS, null);
        this.setUrlRegExList(urlRegExList);
        
        final String listType = pp.getValue(GeneralConfigImpl.PORTLET_URL_LIST_TYPE, null);
        this.setListType(listType);
        
        this.urlStateList = pp.getValues(GeneralConfigImpl.PORTLET_URL_REWRITE_STATES, urlStateList);
        this.renderResponse = response;
        
        super.setRenderData(request, response);
    }

    public void clearData() {
        this.setListType(null);
        this.setListType(null);
        
        this.renderResponse = null;
        
        //clear data of parent
        super.clearData();
    }

    public String getName() {
        return "Portlet URL Filter";
    }

    public void setActionData(ActionRequest request, ActionResponse response) {
        throw new IllegalStateException("PortletUrlFilter is invalid to use during an action");
    }
}

