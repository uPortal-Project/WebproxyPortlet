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
 * @author dgrimwood
 * @version $Id$
 */

public class ClippingConfigImpl extends JspConfigPage {
    private static final String CLIPPING_PREF_PREFIX = "webproxy.clipping.";
    
    public static final String DISABLE = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CLIPPING_PREF_PREFIX).append("sClippingDisable").toString();
    public static final String XPATH = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CLIPPING_PREF_PREFIX).append("sXPath").toString();
    public static final String COMMENT = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CLIPPING_PREF_PREFIX).append("sComment").toString();
    public static final String ELEMENT = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(CLIPPING_PREF_PREFIX).append("sElement").toString();
    
    
    public String getName(){
        return "Clipping Configuration";
    }

    public void process(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        final PortletPreferences prefs = request.getPreferences();

        final Boolean useCache = new Boolean(request.getParameter(DISABLE));
        prefs.setValue(DISABLE, useCache.toString());
        
        final String[] paths = ConfigUtils.checkNullStringArray(request.getParameterValues(XPATH), new String[0]);
        final String[] trimmedPaths = this.parseArray(paths);
        prefs.setValues(XPATH, trimmedPaths);
        
        final String[] comments = ConfigUtils.checkNullStringArray(request.getParameterValues(COMMENT), new String[0]);
        final String[] trimmedComments = this.parseArray(comments);
        prefs.setValues(COMMENT, trimmedComments);
        
        final String[] elements = ConfigUtils.checkNullStringArray(request.getParameterValues(ELEMENT), new String[0]);
        final String[] trimmedElements = this.parseArray(elements);
        prefs.setValues(ELEMENT, trimmedElements);
        
        prefs.store();
    }

    private String[] parseArray(final String[] data) {
        final List dataList = new ArrayList(data.length);
        
        for (int index = 0; index < data.length; index++) {
            final String d = ConfigUtils.checkEmptyNullString(data[index], null);
            
            if (d != null)
                dataList.add(ConfigUtils.checkEmptyNullString(d, ""));
        }
        
        return (String[])dataList.toArray(new String[dataList.size()]);
    }
}
