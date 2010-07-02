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

package edu.wisc.my.webproxy.beans.config;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * @author dgrimwood
 * @version $Id$
 */
public class StaticHtmlConfigImpl extends JspConfigPage {
    private static final String STATICHTML_PREF_PREFIX = "webproxy.statichtml.";

    public static final String STATIC_HEADER = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(STATICHTML_PREF_PREFIX).append("sStaticHeader").toString();
    public static final String STATIC_FOOTER = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(STATICHTML_PREF_PREFIX).append("sStaticFooter").toString();

    public String getName() {
        return "Static HTML Configuration";
    }

    public void process(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        final PortletPreferences prefs = request.getPreferences();
        
        final String staticHeader = ConfigUtils.checkEmptyNullString(request.getParameter(STATIC_HEADER), "");
        prefs.setValue(STATIC_HEADER, staticHeader);
        
        final String staticFooter = ConfigUtils.checkEmptyNullString(request.getParameter(STATIC_FOOTER), "");
        prefs.setValue(STATIC_FOOTER, staticFooter);
        
        prefs.store();
    }
}