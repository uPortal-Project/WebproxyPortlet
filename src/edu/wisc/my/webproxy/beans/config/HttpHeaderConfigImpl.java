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
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * @author nramzan
 * @version $Id$
 */
public class HttpHeaderConfigImpl extends JspConfigPage {
    private static final String HTTPHEADER_PREF_PREFIX = "webproxy.httpheader.";
    
    public static final String HEADER_NAME  = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPHEADER_PREF_PREFIX).append("sHeaderName").toString();
    public static final String HEADER_VALUE = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTTPHEADER_PREF_PREFIX).append("sHeaderValue").toString();
    
	/* (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.config.ConfigPage#getName()
	 */
	public String getName() {
		return "HTTP Header Configuration";
	}

	/* (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.config.ConfigPage#process(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
	 */
	public void process(ActionRequest request, ActionResponse response) throws PortletException, IOException, ConfigurationException {
        final StringBuffer errorMessages = new StringBuffer();
        final PortletPreferences prefs = request.getPreferences();
        
        final String[] headerNames = ConfigUtils.checkNullStringArray(request.getParameterValues(HEADER_NAME),  new String[] {"", "", ""});
        final String[] headerValues = ConfigUtils.checkNullStringArray(request.getParameterValues(HEADER_VALUE),  new String[] {"", "", ""});
        
        if (headerNames.length == headerValues.length) {
            final List headerNamesList = new ArrayList(headerNames.length);
            final List headerValuesList = new ArrayList(headerValues.length);
    
            for (int index = 0; index < headerNames.length; index++) {
                final String name = ConfigUtils.checkEmptyNullString(headerNames[index], null);

                if (name != null) {
                    final String value = ConfigUtils.checkEmptyNullString(headerValues[index], null);
                    
                    headerNamesList.add(ConfigUtils.checkEmptyNullString(name, ""));
                    headerValuesList.add(ConfigUtils.checkEmptyNullString(value, ""));
                }
            }
        
            prefs.setValues(HEADER_NAME, (String[])headerNamesList.toArray(new String[headerNamesList.size()]));
            prefs.setValues(HEADER_VALUE, (String[])headerValuesList.toArray(new String[headerValuesList.size()]));
        }
        else {
            errorMessages.append("Header name and value lists have inconsistent lengths.\n");
        }
        
        //Check for logged errors and store the prefs
        if (errorMessages.length() > 0) {
            throw new ConfigurationException(errorMessages.toString());
        }
        else {
            prefs.store();
        }
    }
}
