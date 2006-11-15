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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

import edu.wisc.my.webproxy.beans.config.ProxyComponent;

/**
 * This class implements the HtmlParser Interface by using NekoHtml
 * 
 * @author dgrimwood
 * 
 * @version $Id$
 *  
 */
public class NekoHtmlParser implements HtmlParser, ProxyComponent {

    private static final Log log = LogFactory.getLog(NekoHtmlParser.class);

    private boolean insertDoctype = true;

    private boolean balanceTags = false;

    private boolean scriptStripComment = true;

    private boolean stripComments = true;

    private boolean reportErrors = false;

    public NekoHtmlParser() {

    }

    public XMLReader getReader(LexicalHandler myHandler) {

        SAXParser defaultParser = new SAXParser();

        try {
            defaultParser.setProperty("http://xml.org/sax/properties/lexical-handler", myHandler);
            defaultParser.setProperty("http://cyberneko.org/html/properties/default-encoding", "ASCII");
            defaultParser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
            defaultParser.setProperty("http://cyberneko.org/html/properties/names/attrs", "no-change");
            
            defaultParser.setFeature("http://cyberneko.org/html/features/report-errors", reportErrors);
            defaultParser.setFeature("http://cyberneko.org/html/features/insert-doctype", insertDoctype);
            defaultParser.setFeature("http://cyberneko.org/html/features/balance-tags", balanceTags);
            defaultParser.setFeature("http://cyberneko.org/html/features/scanner/script/strip-comment-delims", scriptStripComment);
            defaultParser.setFeature("http://cyberneko.org/html/features/scanner/style/strip-comment-delims", stripComments);

            defaultParser.setFeature("http://cyberneko.org/html/features/scanner/notify-builtin-refs", true);
            defaultParser.setFeature("http://apache.org/xml/features/scanner/notify-char-refs", true);
        }
        catch (SAXNotRecognizedException e) {
            log.debug("SaxParser not recognized:  ", e);
        }
        catch (SAXNotSupportedException e) {
            log.debug("SaxParser not supported:  ", e);
        }
        return defaultParser;
    }

    public String getName() {
        return "NekoHtml Filter";
    }

    public void setRenderData(RenderRequest request, RenderResponse response) {
        PortletPreferences pp = request.getPreferences();
        this.reportErrors = new Boolean(pp.getValue("reportErrors", null)).booleanValue();
        this.balanceTags = new Boolean(pp.getValue("balanceTags", null)).booleanValue();
        this.insertDoctype = new Boolean(pp.getValue("insertDoctype", null)).booleanValue();
        this.scriptStripComment = new Boolean(pp.getValue("scriptStripComment", null)).booleanValue();
        this.stripComments = new Boolean(pp.getValue("stripComments", null)).booleanValue();
    }

    public void setActionData(ActionRequest request, ActionResponse response) {

    }

    public void clearData() {

    }
}

