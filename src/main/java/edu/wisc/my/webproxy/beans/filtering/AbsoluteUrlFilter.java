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
    @Override
    public String rewriteUrl(String urlFragment, boolean passThrough) {
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

    @Override
    public void characters(char[] ch, int start, int len) throws SAXException {
        super.characters(ch, start, len);
    }

    public String getName() {
        return "Absolute URL Filter";
    }

    @Override
    public void setRenderData(RenderRequest request, RenderResponse response) {
        this.setupFilter(request);
        super.setRenderData(request, response);
    }

    @Override
    public void setActionData(ActionRequest request, ActionResponse response) {
        this.setupFilter(request);
        super.setActionData(request, response);
    }
    
    

    /**
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#clearData()
     */
    @Override
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

