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

/*
 * Created on Mar 21, 2005
 *
 */
package edu.wisc.my.webproxy.beans.http;

import org.apache.http.message.BasicHeader;


/**
 * This class is the implementation of the methods defined in the (@link edu.wisc.my.webproxy.beans.http.Header) interface
 * 
 * @author nramzan
 *
 * @version $Id$
 */
public class HeaderImpl implements edu.wisc.my.webproxy.beans.http.IHeader {
    private BasicHeader httpClientHeader;

    /**
     * The constructor with the name and value arguments.
     * 
     * @param name
     * @param value
     */
    public HeaderImpl(String name, String value) {
        BasicHeader header = new BasicHeader(name, value);
        this.httpClientHeader = header;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Header#getName()
     */
    public String getName() {
        return this.httpClientHeader.getName();
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.Header#getValue()
     */
    public String getValue() {
        return this.httpClientHeader.getValue();
    }

}