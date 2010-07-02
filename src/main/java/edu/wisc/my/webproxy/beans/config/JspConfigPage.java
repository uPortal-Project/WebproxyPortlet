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

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Base ConfigPage that provides means for configuring a JSP page name
 * and takes care of the render method by dispatching to the configured JSP.
 * 
 * @author dgrimwood
 * @version $Id$
 */
abstract public class JspConfigPage implements ConfigPage {
    private String jsp = null;

    /**
     * @return Returns the jsp.
     */
    public String getJsp() {
        return this.jsp;
    }
    /**
     * @param jsp The jsp to set.
     */
    public void setJsp(String jsp) {
        this.jsp = jsp;
    }
    
    /**
     * Dispatches the request to the configured JSP.
     * 
     * @see edu.wisc.my.webproxy.beans.config.ConfigPage#render(javax.portlet.PortletContext, javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void render(PortletContext context, RenderRequest request, RenderResponse response) throws IOException, PortletException {
        final PortletRequestDispatcher rd = context.getRequestDispatcher(this.getJsp());
        rd.include(request, response);
    }
}
