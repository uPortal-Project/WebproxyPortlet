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
            defaultParser.setProperty("http://cyberneko.org/html/properties/default-encoding", "UTF-8");
            defaultParser.setProperty("http://cyberneko.org/html/properties/names/elems", "match");
            defaultParser.setProperty("http://cyberneko.org/html/properties/names/attrs", "no-change");
            
            defaultParser.setFeature("http://cyberneko.org/html/features/report-errors", reportErrors);
            defaultParser.setFeature("http://cyberneko.org/html/features/insert-doctype", insertDoctype);
            defaultParser.setFeature("http://cyberneko.org/html/features/balance-tags", balanceTags);
            defaultParser.setFeature("http://cyberneko.org/html/features/scanner/script/strip-comment-delims", scriptStripComment);
            defaultParser.setFeature("http://cyberneko.org/html/features/scanner/style/strip-comment-delims", stripComments);

            // AW (2008/09/03):  The 'strip-cdata-delims' feature removes CDATA 
            // delimiters ('<![CDATA[' and ']]>') from <script> elements found 
            // within the source DOM.  These delimiters are sometimes placed 
            // there b/c JavaScript commonly contains characters that would need 
            // escaping in XML.  The delimiters, conversely, break JavaScript in 
            // HTML.  Since there is currently no forseeable scenario where these 
            // delimiters should *not* be removed, we just set this option to 
            // 'true.'  
            defaultParser.setFeature("http://cyberneko.org/html/features/scanner/script/strip-cdata-delims", true);            
            
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

