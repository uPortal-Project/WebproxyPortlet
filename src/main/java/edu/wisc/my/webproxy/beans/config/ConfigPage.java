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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Represents a configuration page to be displayed during the portlet's
 * configuration. The render method is called to render the page's markup
 * and process is called when the configuration page is submitted. The
 * implementation is expected to use the PortletPreferences object to persist
 * its configuration. Preference names should be created using the standard
 * java package naming scheme to avoid collisions.
 * 
 * @author dgrimwood
 * 
 * @version $Id$
 * 
 */
public interface ConfigPage {

    /**
     * Returns a displayable title for the configuration page. This title is
     * used when the portlet renders the workflow portion of the configuration
     * UI.
     */
    public String getName();

    /**
     * Called by the portlet when the page should render its configuration UI.
     * 
     * @param context
     * @param request
     * @param response
     * @throws IOException
     * @throws PortletException
     */
    public void render(PortletContext context, RenderRequest request, RenderResponse response) throws IOException, PortletException;

    /**
     * Called by the portlet when the submitted configuration page was for this
     * object. This is where the page should persist its settings using the
     * PortletPreferences API.
     *
     * This may throw a ConfigurationException if the submitted data is incorrect
     *  
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     * @throws ConfigurationException
     */
    public void process(ActionRequest request, ActionResponse response) throws PortletException, IOException, ConfigurationException;
}

