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

    public String getName() {
        return "Output Filter";
    }

    
    /**
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
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
    public void endEntity(String name) throws SAXException {
        this.currentEntity = null;
    }

    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
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
