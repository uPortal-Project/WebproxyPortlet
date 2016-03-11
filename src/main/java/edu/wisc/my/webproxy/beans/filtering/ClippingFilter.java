/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final Log LOG = LogFactory.getLog(ClippingFilter.class);
    
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
                            if (myPathIndex == pathPartList.size()-1) {
                                break;
                            }
                        }
                        else {
                            xPathMatch = false;
                            break;
                        }
                    }
                    if (xPathMatch == true && currentPath.size() >= pathPartList.size()) {
                        break;
                    } else {
                            xPathMatch = false;
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
                            if (myPathIndex == pathPartList.size()-1) {
                                break;
                            }
                        }
                        else {
                            xPathMatch = false;
                            break;
                        }
                    }
                    if (xPathMatch == true && currentPath.size() >= pathPartList.size()) {
                        break;
                    } else {
                            xPathMatch = false;
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
        if (notAcceptable.contains(qName) || notAcceptable.contains(qName.toLowerCase()))
            isAcceptable = true;
        return isAcceptable;

    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        String currentComment = new String(ch, start, length);
        if (comments.containsKey(currentComment)) {
            String commentStatus = (String)comments.get(currentComment);
            if (commentStatus.equals("off"))
                comments.put(currentComment, "on");
            else
                comments.put(currentComment, "off");
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
