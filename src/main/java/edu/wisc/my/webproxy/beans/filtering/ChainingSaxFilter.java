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
    @Override
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
    @Override
    public void setParent(XMLReader parent) {
        if (parent instanceof ChainingSaxFilter)
            this.parent = (ChainingSaxFilter)parent;

        super.setParent(parent);
    }
    
    /**
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    @Override
    public void parse(InputSource input) throws SAXException, IOException {
        this.setupParse();
        super.parse(input);
    }
    
    /**
     * @see org.xml.sax.XMLReader#parse(java.lang.String)
     */
    @Override
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