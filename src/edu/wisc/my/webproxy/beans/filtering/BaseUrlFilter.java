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