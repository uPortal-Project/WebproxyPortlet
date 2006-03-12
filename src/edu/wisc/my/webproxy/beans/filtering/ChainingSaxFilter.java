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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import edu.wisc.my.webproxy.beans.config.ProxyComponent;

/**
 * This abstract class can be implemented by filters that can use SAX events to perform
 * their action.
 * 
 * @author dgrimwood
 * 
 * @version $Id$
 */
public abstract class ChainingSaxFilter extends XMLFilterImpl implements ProxyComponent, LexicalHandler {
    private LexicalHandler lexHandler = null;
    private ChainingSaxFilter parent = null;
    
    /**
     * @see org.xml.sax.helpers.XMLFilterImpl#characters(char[], int, int)
     */
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        super.characters(arg0, arg1, arg2);
    }

    /**
     * The default constructor 
     */
    public ChainingSaxFilter() {
    }

    /**
     * Constructor that takes XMLReader object as argument
     * 
     * @param parent the (@link XMLReader) object
     */
    public ChainingSaxFilter(XMLReader parent) {
        this.setParent(parent);
    }
    

    /**
     * @return Returns the lexHandler.
     */
    public LexicalHandler getLexicalHandler() {
        return this.lexHandler;
    }
    /**
     * @param lexHandler The lexHandler to set.
     */
    public void setLexicalHandler(LexicalHandler lexHandler) {
        this.lexHandler = lexHandler;
    }

    /*
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#setActionData(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void setActionData(ActionRequest request, ActionResponse response) {
        if (this.parent != null)
            this.parent.setActionData(request, response);
    }
    /*
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#setRenderData(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void setRenderData(RenderRequest request, RenderResponse response) {
        if (this.parent != null)
            this.parent.setRenderData(request, response);
    }
    /*
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#clearData()
     */
    public void clearData() {
        if (this.parent != null)
            this.parent.clearData();
    }

    
    
    /**
     * @see org.xml.sax.XMLFilter#setParent(org.xml.sax.XMLReader)
     */
    public void setParent(XMLReader parent) {
        if (parent instanceof ChainingSaxFilter)
            this.parent = (ChainingSaxFilter)parent;

        super.setParent(parent);
    }
    
    /**
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    public void parse(InputSource input) throws SAXException, IOException {
        this.setupParse();
        super.parse(input);
    }
    
    /**
     * @see org.xml.sax.XMLReader#parse(java.lang.String)
     */
    public void parse(String systemId) throws SAXException, IOException {
        this.setupParse();
        super.parse(systemId);
    }
    
    /**
     * Sets up the Lexical Handler chain before a parse starts
     */
    private void setupParse() {
        if (this.parent != null)
            this.parent.setLexicalHandler(this);
    }
    
    
    //***** Lexical Handler Chaining Methods *****//
    
    /**
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.lexHandler != null)
            this.lexHandler.comment(ch, start, length);
    }

    /**
     * @see org.xml.sax.ext.LexicalHandler#endCDATA()
     */
    public void endCDATA() throws SAXException {
        if (this.lexHandler != null)
            this.lexHandler.endCDATA();
    }

    /**
     * @see org.xml.sax.ext.LexicalHandler#endDTD()
     */
    public void endDTD() throws SAXException {
        if (this.lexHandler != null)
            this.lexHandler.endDTD();
    }

    /**
     * @see org.xml.sax.ext.LexicalHandler#startCDATA()
     */
    public void startCDATA() throws SAXException {
        if (this.lexHandler != null)
            this.lexHandler.startCDATA();
    }

    /**
     * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
     */
    public void endEntity(String name) throws SAXException {
        if (this.lexHandler != null)
            this.lexHandler.endEntity(name);
    }

    /**
     * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
     */
    public void startEntity(String name) throws SAXException {
        if (this.lexHandler != null)
            this.lexHandler.startEntity(name);
    }

    /**
     * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        if (this.lexHandler != null)
            this.lexHandler.startDTD(name, publicId, systemId);
    }
}