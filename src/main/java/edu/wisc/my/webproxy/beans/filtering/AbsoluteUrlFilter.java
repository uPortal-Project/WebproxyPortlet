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

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.wisc.my.webproxy.beans.PortletPreferencesWrapper;
import edu.wisc.my.webproxy.beans.config.GeneralConfigImpl;
import edu.wisc.my.webproxy.portlet.WebProxyPortlet;

/**
 * <p>
 * A filter that will re-write relative URLs to absolute URLs. This is required
 * so the portlet can either proxy the URL correctly or if it is not proxied so
 * the URL works correctly.
 * </p>
 * 
 * @author dgrimwood
 * 
 * @version $Id$
 */
public class AbsoluteUrlFilter extends BaseUrlFilter {
    private static final Log LOG = LogFactory.getLog(WebProxyPortlet.class);
    
    private String urlBase = null;
    private String urlPath = null;

    /**
     * The default constructor
     */
    public AbsoluteUrlFilter() {
    }

    /**
     * Constructor that takes XMLReader object as argument
     * 
     * @param parent the (@link XMLReader) object
     */
    public AbsoluteUrlFilter(XMLReader parent) {
        super(parent);
    }

    /**
     * Rewrites a relative url to an absolute url
     * 
     * @param urlFragment the relative url as String
     * @return urlFragment the absolute url as String
     */
    public String rewriteUrl(String urlFragment) {
        final StringBuffer sb = new StringBuffer();
        
        //Ignore anchor links, ignore host based absolute URLs, ignore email links 
        if (urlFragment.startsWith("#") || urlFragment.startsWith("//") || urlFragment.startsWith("mailto:")) {
            sb.append(urlFragment);
        }
        else if (urlFragment.startsWith("/")) {
            sb.append(this.urlBase);
            sb.append(urlFragment);
        }
        else if (urlFragment.indexOf("://") == -1) {
            sb.append(this.urlBase);
            sb.append(this.urlPath);
            sb.append(urlFragment);
        }
        else {
            sb.append(urlFragment);
        }
        
        if (LOG.isTraceEnabled()) {
            LOG.trace("Rewriting '" + urlFragment + "' to '" + sb + "'");
        }
        
        return sb.toString();
    }

    public void characters(char[] ch, int start, int len) throws SAXException {
        super.characters(ch, start, len);
    }

    public String getName() {
        return "Absolute URL Filter";
    }

    public void setRenderData(RenderRequest request, RenderResponse response) {
        this.setupFilter(request);
        super.setRenderData(request, response);
    }

    public void setActionData(ActionRequest request, ActionResponse response) {
        this.setupFilter(request);
        super.setActionData(request, response);
    }
    
    

    /**
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#clearData()
     */
    public void clearData() {
        super.clearData();
        
        this.urlPath = null;
        this.urlBase = null;
    }
    
    private void setupFilter(PortletRequest request) {
        final PortletSession session = request.getPortletSession();
        
        String currentUrl = (String)session.getAttribute(GeneralConfigImpl.BASE_URL);
        if (currentUrl == null) { 
            //if nothing exists in the session use urlBase set in configuration
            PortletPreferences pp = new PortletPreferencesWrapper(request.getPreferences(), (Map)request.getAttribute(PortletRequest.USER_INFO));
            currentUrl = pp.getValue(GeneralConfigImpl.BASE_URL, null);
        }
        
        final int protocolSeperator = currentUrl.indexOf("://");
        final int baseIndex = currentUrl.indexOf('/', protocolSeperator + 3);
        
        if (baseIndex < 0) {
            this.urlBase = currentUrl;
            this.urlPath = "/";
        }
        else {
            this.urlBase = currentUrl.substring(0, baseIndex);
            
            int lastSlash = currentUrl.lastIndexOf('/');
            if (lastSlash >= 0)
                this.urlPath = currentUrl.substring(baseIndex, lastSlash + 1);
            else
                this.urlPath = "/";
        }
        
        if (LOG.isTraceEnabled()) {
            LOG.trace("Current URL ='" + currentUrl + "'");
            LOG.trace("URL Base ='" + this.urlBase + "'");
            LOG.trace("URL Path ='" + this.urlPath + "'");
        }
    }
}

