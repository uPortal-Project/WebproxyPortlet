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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.wisc.my.webproxy.beans.config.ClippingConfigImpl;

/**
 * The clipping filter uses an XPath expression to clip a subset of the Document
 * 
 * @author dgirmwood
 *
 * @version $Id$
 */
public class ClippingFilter extends ChainingSaxFilter {

    List<List<String>> xPath = null;

    List<String> currentPath = new LinkedList<String>();

    private Set notAcceptable = null;

    private String[] sElement = null;

    private Map<String, String> comments = new HashMap<String, String>();

    private boolean commentMatch = false;

    private boolean xPathMatch = false;

    private boolean elementMatch = false;

    private boolean disable = true;

    /**
     * The default constructor 
     */

    public ClippingFilter() {
    }

    /**
     * Constructor that takes XMLReader object as argument
     * @param parent the (@link XMLReader) object
     * 
     */

    public ClippingFilter(XMLReader parent) {
        super(parent);
    }

    /**
     * Set xPath Lists for clipping
     * 
     * @param path array of Strings containing paths
     */
    public void setXPath(String[] path) {
        xPath = new ArrayList<List<String>>(path.length);
        
        for (int pathIndex = 0; pathIndex < path.length; pathIndex++) {
            final LinkedList<String> pathPartList = new LinkedList<String>();
            xPath.add(pathPartList);
            
            StringTokenizer st = new StringTokenizer(path[pathIndex], "/");
            while (st.hasMoreTokens()) {
                pathPartList.add(st.nextToken());
            }
        }
    }

    /**
     * Set elemets Lists for clipping
     * 
     * @param newElement array of Strings containing elements
     */

    public void setElement(String[] newElement) {
        this.sElement = newElement;
    }

    /**
     * Set the comment for this clipping filter
     * 
     * @param newComments array of Strings containing comments
     */

    public void setComments(String[] newComments) {
        for (int index = 0; index < newComments.length; index++) {
            comments.put(newComments[index], "off");
        }
    }

    /**
     * Retrives the Xpath set on this clipping filter
     * 
     * @return xPath an array of LinkedList containing xPaths 
     */

    public List<List<String>> getXPath() {
        return this.xPath;
    }

    /**
     * Filters the Namespace URI for start-element events.
     * 
     * @param uri The element's Namespace URI, or the empty string.
     * @param localName The element's local name, or the empty string.
     * @param qName The element's qualified (prefixed) name, or the empty string.
     * 
     */
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (!disable) {
            this.currentPath.add(qName);
            
            //loop thru each xPath to see if clipping is set to true.
            if (xPath != null) {
                for (final List<String> pathPartList : this.xPath) {
                    //check current path to see if equals defined clipping
                    // xPath
                    for (int myPathIndex = 0; myPathIndex < pathPartList.size() && myPathIndex < this.currentPath.size(); myPathIndex++) {
                        final String currentPathPart = currentPath.get(myPathIndex);
                        final String testPathPart = pathPartList.get(myPathIndex);
                        
                        if (myPathIndex < currentPath.size() && currentPathPart != null && currentPathPart.equalsIgnoreCase(testPathPart)) {
                            xPathMatch = true;
                            break;
                        }
                        else {
                            xPathMatch = false;
                        }
                    }
                }
            }
            
            if (sElement != null)
                for (final String testElement : this.sElement) {
                    if (currentPath.contains(testElement)) {
                        elementMatch = true;
                        break;
                    }
                    else {
                        elementMatch = false;
                    }
                }

            if (xPathMatch || elementMatch || commentMatch) {
                super.startElement(uri, localName, qName, atts);
            }
        }
        else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    /**
     * Filters the Namespace URI for end-element events.
     * 
     * @param uri The element's Namespace URI, or the empty string.
     * @param localName The element's local name, or the empty string.
     * @param qName The element's qualified (prefixed) name, or the empty string.
     * 
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!disable) {
            boolean removeFromXPath = true;
            int index = this.currentPath.size();
            //remove endElements from currentPath
            while (index > 0 && removeFromXPath) {
                String tempName = (String)currentPath.get(index - 1);
                if (notAcceptable(tempName))
                    currentPath.remove(index - 1);
                else if (tempName.equalsIgnoreCase(qName)) {
                    currentPath.remove(index - 1);
                    removeFromXPath = false;
                }
                index--;
            }
            //loop thru each xPath to see if clipping is set to true.
            if (xPath != null) {
                for (final List<String> pathPartList : xPath) {
                    //check current path to see if equals defined clipping
                    // xPath
                    for (int myPathIndex = 0; myPathIndex < pathPartList.size(); myPathIndex++) {
                        final String currentPathPart = currentPath.get(myPathIndex);
                        final String testPathPart = pathPartList.get(myPathIndex);
                        
                        if (myPathIndex < currentPath.size() && currentPathPart != null && currentPathPart.equalsIgnoreCase(testPathPart)) {
                            xPathMatch = true;
                            break;
                        }
                        else {
                            xPathMatch = false;
                        }
                    }
                }
            }
            if (sElement != null)
                for (final String testElement : this.sElement) {
                    if (currentPath.contains(testElement)) {
                        elementMatch = true;
                        break;
                    }
                    else {
                        elementMatch = false;
                    }
                }

            if (xPathMatch || elementMatch || commentMatch) {
                super.endElement(uri, localName, qName);
            }
        }
        else {
            super.endElement(uri, localName, qName);
        }
    }

    public void characters(char[] ch, int start, int len) throws SAXException {
        if (!disable) {
            if (xPathMatch || elementMatch || commentMatch)
                super.characters(ch, start, len);
        }
        else
            super.characters(ch, start, len);

    }

    /*
     * These QNames do not require to be closed so can be removed from the path
     * w/o close tags
     *  
     */
    public void setAcceptableQNames(Set notAcceptable) {
        this.notAcceptable = notAcceptable;

    }

    /**
     * Checks to see if element is in notAcceptable array
     * 
     * @param qName the qName as String
     */
    public boolean notAcceptable(String qName) {
        boolean isAcceptable = false;
        if (notAcceptable.contains(qName))
            isAcceptable = true;
        return isAcceptable;

    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        String currentComment = new String(ch, start, length);
        if (comments.containsKey(currentComment)) {
            String commentStatus = (String)comments.get(currentComment);
            if (commentStatus.equals("off"))
                comments.put(commentStatus.toString(), "on");
            else
                comments.put(commentStatus.toString(), "off");
            if (comments.containsValue("on"))
                commentMatch = true;
            else
                commentMatch = false;
        }
        
        if (!disable) {
            if (xPathMatch || elementMatch || commentMatch)
                super.comment(ch, start, length);
        }
        else
            super.comment(ch, start, length);
    }

    public String getName() {
        return "Clipping Filter";
    }

    public void setRenderData(RenderRequest request, RenderResponse response) {
        this.setupFilter(request);
        super.setRenderData(request, response);
    }

    public void setActionData(ActionRequest request, ActionResponse response) {
        this.setupFilter(request);
        super.setActionData(request, response);
    }
    
    private void setupFilter(PortletRequest request) {
        PortletPreferences pp = request.getPreferences();
        String[] sTemp = null;
        sTemp = pp.getValues(ClippingConfigImpl.XPATH, null);
        if (sTemp != null)
            this.setXPath(sTemp);
        sTemp = pp.getValues(ClippingConfigImpl.ELEMENT, null);
        if (sTemp != null)
            this.setElement(sTemp);
        sTemp = pp.getValues(ClippingConfigImpl.COMMENT, null);
        if (sTemp != null)
            this.setComments(sTemp);
        this.disable = !new Boolean(pp.getValue(ClippingConfigImpl.DISABLE, null)).booleanValue();
    }

}