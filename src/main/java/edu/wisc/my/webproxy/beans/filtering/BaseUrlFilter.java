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

import java.util.Map;
import java.util.Set;

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
    
	private Map elements = null;
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
    
    public void setElements(Map springElements){
        this.elements = springElements;
    }
    

    /**
     * Filter the Namespace URI for start-element events.
     * 
     * @param uri the uri as a String
     * @param localName the local name as a String
     * @param qName the query name as a String
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        String sTempAtt = null;
        AttributesImpl newAtts = new AttributesImpl(atts);
        boolean getMethod = false;
        if(qName.equalsIgnoreCase("FORM")){
            boolean foundAction = false;
            int methodIndex = -1;
            //check to see if Form has Action attribute
            int index;
            for (index = 0; index < newAtts.getLength(); index++) {
                if (newAtts.getQName(index).toUpperCase().equals("ACTION")){
                    foundAction = true;
                }
                if (newAtts.getQName(index).toUpperCase().equals("METHOD")){
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
            //if using exclude list decrament index to avoid arrayoutofbounds exception
//            if (!newAtts.getType(index-1).equalsIgnoreCase("POST"))
//                newAtts.setType(index-1, "POST");
            //if form has no action attribute create one.
            if(!foundAction){
                newAtts.addAttribute(uri, "ACTION", "ACTION", "CDATA", "/");
            }
        }
        for (int index = 0; index < newAtts.getLength(); index++) {
            String newAttsQName = newAtts.getQName(index).toUpperCase();
            //check to see if qName is in element Map
            if(elements.containsKey(qName.toUpperCase())){
                Set tempSet = (Set) elements.get(qName.toUpperCase());
                //check to see if newAttsQName is in the Element Set
                if(tempSet.contains(newAttsQName)){
                    //Do not handle JavaScript
                    String sTemp = newAtts.getValue(index);
                    if (sTemp!=null && !sTemp.toUpperCase().startsWith("JAVASCRIPT")) {
                        sTempAtt = rewriteUrl(sTemp);
                        newAtts.setValue(index, sTempAtt);
                    }
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
    
    public abstract String rewriteUrl(String sTempAtt);

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
    }

    public void characters(char[] ch, int start, int len) throws SAXException {
        super.characters(ch, start, len);
    }

    public void clearData() {
        //clear data of parent
        ChainingSaxFilter parent = (ChainingSaxFilter) super.getParent();
        parent.clearData();
        setParent(null);

    }

}