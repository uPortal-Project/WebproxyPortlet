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
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * @author dgrimwood
 * @version $Id$
 */
public class HtmlParserConfigImpl extends JspConfigPage {
    private static final String HTMLPARSER_PREF_PREFIX = "webproxy.htmlparser.";
    
    public static final String SCRIPTSTRIPCOMMENT   = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTMLPARSER_PREF_PREFIX).append("sScriptStripComment").toString();
    public static final String INSERTDOCTYPE        = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTMLPARSER_PREF_PREFIX).append("sInsertDocType").toString();
    public static final String BALANCETAGS          = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTMLPARSER_PREF_PREFIX).append("sBalanceTags").toString();
    public static final String STRIPCOMMENTS        = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTMLPARSER_PREF_PREFIX).append("sStripComments").toString();
    public static final String REPORTERRORS         = new StringBuffer(WebproxyConstants.UNIQUE_CONSTANT).append(HTMLPARSER_PREF_PREFIX).append("sReportErrors").toString();

    public String getName() {
        return "HTML Parser Configuration";
    }

    public void process(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        final PortletPreferences prefs = request.getPreferences();

        final Boolean stripScriptComments = new Boolean(request.getParameter(SCRIPTSTRIPCOMMENT));
        prefs.setValue(SCRIPTSTRIPCOMMENT, stripScriptComments.toString());
        
        final Boolean insertDocType = new Boolean(request.getParameter(INSERTDOCTYPE));
        prefs.setValue(INSERTDOCTYPE, insertDocType.toString());
        
        final Boolean stripComments = new Boolean(request.getParameter(STRIPCOMMENTS));
        prefs.setValue(STRIPCOMMENTS, stripComments.toString());
        
        final Boolean balanceTags = new Boolean(request.getParameter(BALANCETAGS));
        prefs.setValue(BALANCETAGS, balanceTags.toString());
        
        final Boolean reportErrors = new Boolean(request.getParameter(REPORTERRORS));
        prefs.setValue(REPORTERRORS, reportErrors.toString());
        
        prefs.store();
    }
}