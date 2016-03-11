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

    @Override
    protected String doUrlRewite(String orignialUrl, int matchIndex, boolean passThrough) {
        final PortletURL newUrl = this.renderResponse.createActionURL();
        newUrl.setParameter(WebproxyConstants.BASE_URL, orignialUrl);
        if (passThrough) {
            newUrl.setParameter(WebproxyConstants.PASS_THROUGH, Boolean.TRUE.toString());
        }

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

    @Override
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

    @Override
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

    @Override
    public void setActionData(ActionRequest request, ActionResponse response) {
        throw new IllegalStateException("PortletUrlFilter is invalid to use during an action");
    }
}

