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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * The base URL filter is an abstract filter that locates URLs in the proxied content 
 * and call an abstract method to re-write the URL.
 * 
 * @author dgrimwood
 * 
 * @version $Id$
 * 
 */
 
public abstract class BaseUrlFilter extends ChainingSaxFilter{
    
	/**
     * 
     */
    private static final String JAVASCRIPT_PREFIX = "JAVASCRIPT:";
    private Map<String, Set<String>> elements = Collections.emptyMap();
	private Map<String, Set<String>> passThroughElements = Collections.emptyMap();
	
    /**
	 * Constructor that takes XMLReader object as argument
	 * @param parent the (@link XMLReader) object
	 * 
	 */
	
    public BaseUrlFilter(XMLReader parent) {
        super(parent);
    }
    
    /**
	 * The default constructor 
	 */
    
    public BaseUrlFilter() {
    }
    
    public void setElements(Map<String, Set<String>> elements) {
        this.elements = this.makeCaseInsensitive(elements);
    }
    
    public void setPassThroughElements(Map<String, Set<String>> passThroughElements) {
        this.passThroughElements = this.makeCaseInsensitive(passThroughElements);
    }
    
    protected Map<String, Set<String>> makeCaseInsensitive(Map<String, Set<String>> elements) {
        final Map<String, Set<String>> ciElements = new TreeMap<String, Set<String>>(String.CASE_INSENSITIVE_ORDER);
        
        for (final Map.Entry<String, Set<String>> entry : elements.entrySet()) {
            final String element = entry.getKey();
            final Set<String> attributes = entry.getValue();
            
            final Set<String> ciAttributes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            ciAttributes.addAll(attributes);
            
            ciElements.put(element, ciAttributes);
        }
        
        return ciElements;
    }

    /**
     * Filter the Namespace URI for start-element events.
     * 
     * @param uri the uri as a String
     * @param localName the local name as a String
     * @param qName the query name as a String
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        String sTempAtt = null;
        AttributesImpl newAtts = new AttributesImpl(atts);
        boolean getMethod = false;
        if("FORM".equalsIgnoreCase(qName)){
            boolean foundAction = false;
            int methodIndex = -1;
            //check to see if Form has Action attribute
            int index;
            for (index = 0; index < newAtts.getLength(); index++) {
                if ("ACTION".equalsIgnoreCase(newAtts.getQName(index))){
                    foundAction = true;
                }
                if ("METHOD".equalsIgnoreCase(newAtts.getQName(index))){
                    methodIndex = index;
                }
            }
            if(methodIndex!=-1){
                if("GET".equalsIgnoreCase(newAtts.getValue(methodIndex))){
                    getMethod=true;
                }
                newAtts.setValue(methodIndex, "POST");
            }
            else{
                newAtts.addAttribute(uri, "method", "method", "CDATA", "POST");
                getMethod=true;
            }
            //if form has no action attribute create one.
            if(!foundAction){
                newAtts.addAttribute(uri, "ACTION", "ACTION", "CDATA", "/");
            }
        }
        
        boolean passThrough = false;
        Set<String> attributes = elements.get(qName);
        if (attributes == null) {
            attributes = passThroughElements.get(qName);
            passThrough = true;
        }
        
        if (attributes != null) {
            for (int index = 0; index < newAtts.getLength(); index++) {
                final String attrName = newAtts.getQName(index);
                
                //check to see if newAttsQName is in the Element Set
                if(attributes.contains(attrName)) {

                    final String attrValue = newAtts.getValue(index);
                    if (attrValue == null || (
                            attrValue.length() >= JAVASCRIPT_PREFIX.length() && 
                            JAVASCRIPT_PREFIX.equalsIgnoreCase(attrValue.substring(0, JAVASCRIPT_PREFIX.length())))) {
                        //Skip attributes with null values or JavaScript prefixes
                        continue;
                    }
                    
                    sTempAtt = this.rewriteUrl(attrValue, passThrough);
                    newAtts.setValue(index, sTempAtt);
                }                    
            }
        }

        super.startElement(uri, localName, qName, newAtts);
        if(getMethod){
            AttributesImpl methodAtt = new AttributesImpl();
            methodAtt.addAttribute(uri, "type", "type", "CDATA", "HIDDEN");
            methodAtt.addAttribute(uri, "name", "name", "CDATA", WebproxyConstants.UNIQUE_CONSTANT + ".getMethod");
            methodAtt.addAttribute(uri, "value", "value", "CDATA", "GET");
            super.startElement(uri, "input", "input", methodAtt);
            super.endElement(uri, "input", "input");
        }
    }

    /**
     * Abstract method for re-writng a given url
     * 
     * @param sTempAtt the url as a String before it is re-written to some other url
     * @return sTempAtt the url as a String after it is re-written to some other url
     */
    
    public abstract String rewriteUrl(String sTempAtt, boolean passThrough);

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int len) throws SAXException {
        super.characters(ch, start, len);
    }

    @Override
    public void clearData() {
        //clear data of parent
        ChainingSaxFilter parent = (ChainingSaxFilter) super.getParent();
        parent.clearData();
        setParent(null);

    }

}