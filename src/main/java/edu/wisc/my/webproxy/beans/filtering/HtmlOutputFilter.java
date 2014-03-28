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

import java.io.IOException;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A filter that will always be at the end of the filter chain. 
 * It takes the SAX events and create an XML document into an OutputStream. 
 * This filter uses the portlet's OutputStream to write the data to.
 * 
 * @author dgrimwood
 *
 * @version $Id$
 */
public final class HtmlOutputFilter extends ChainingSaxFilter {
    private static final String ENTITY_START    = "&";
    private static final String ENTITY_END      = ";";
    private static final String TAG_OPEN_START  = "<";
    private static final String TAG_CLOSE_START = "</";
    private static final String TAG_END         = ">";
    private static final String COMMENT_START   = "<!--";
    private static final String COMMENT_END     = "-->";
    private static final String QUOTE           = "\"";
    private static final String EQUAL           = "=";
    private static final String SPACE           = " ";
    
    private final Writer out;
    private String currentEntity = null; //The current entity that is being rendered
    
    public HtmlOutputFilter(Writer out) {
        if (out == null)
            throw new IllegalArgumentException("OutputStream cannot be null");
        
        this.out = out;
    }

    @Override
    public String getName() {
        return "Output Filter";
    }

    
    /**
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        try {
            out.write(COMMENT_START);
            out.write(ch, start, length);
            out.write(COMMENT_END);
        }
        catch (IOException ioe) {
            throw new SAXException("Error writing data to output stream", ioe);
        }
    }
    
    /**
     * @see edu.wisc.my.webproxy.beans.filtering.ChainingSaxFilter#startEntity(java.lang.String)
     */
    @Override
    public void startEntity(String name) throws SAXException {
        this.currentEntity = name;
        
        try {
            out.write(ENTITY_START);
            out.write(this.currentEntity);
            out.write(ENTITY_END);
        }
        catch (IOException ioe) {
            throw new SAXException("Error writing data to output stream", ioe);
        }
    }

    /**
     * @see edu.wisc.my.webproxy.beans.filtering.ChainingSaxFilter#endEntity(java.lang.String)
     */
    @Override
    public void endEntity(String name) throws SAXException {
        this.currentEntity = null;
    }

    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            if (this.currentEntity == null) {
                final String chars = new String(ch, start, length);
                out.write(chars);
            }
        }
        catch (IOException ioe) {
            throw new SAXException("Error writing data to output stream", ioe);
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        try {
            out.flush();
        }
        catch (IOException ioe) {
            throw new SAXException("Error flushing output stream", ioe);
        }
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            out.write(TAG_CLOSE_START);
            out.write(qName);
            out.write(TAG_END);
        }
        catch (IOException ioe) {
            throw new SAXException("Error writing data to output stream", ioe);
        }
    }
    
    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        try {
            out.write(TAG_OPEN_START);
            out.write(qName);
            
            for (int index = 0; index < atts.getLength(); index++) {
                final String name = atts.getQName(index);
                final String value = atts.getValue(index);
                
                out.write(SPACE);
                out.write(name);
                
                if (value != null) {
                    out.write(EQUAL);
                    out.write(QUOTE);
                    out.write(value);
                    out.write(QUOTE);
                }
            }
            
            out.write(TAG_END);
        }
        catch (IOException ioe) {
            throw new SAXException("Error writing data to output stream", ioe);
        }
    }
}
