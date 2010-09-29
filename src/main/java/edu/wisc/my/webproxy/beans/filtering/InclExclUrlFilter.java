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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.XMLReader;


/**
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public abstract class InclExclUrlFilter extends BaseUrlFilter {
    private static final String EXCLUDE = "EXCLUDE";

    private String[] urlRegExList = null;
    private String listType = null;

    public InclExclUrlFilter() {
    }

    public InclExclUrlFilter(XMLReader parent) {
        super(parent);
    }

    @Override
    public final String rewriteUrl(String originalUrl, boolean passThrough) {
        if (this.urlRegExList == null) {
            return originalUrl;
        }
        
        for (int index = 0; index < this.urlRegExList.length; index++) {
            if (this.urlRegExList[index] == null || this.urlRegExList[index].trim().length() == 0) {
                continue;
            }
            
            final Pattern urlPattern = Pattern.compile(this.urlRegExList[index]);
            final Matcher urlMatcher = urlPattern.matcher(originalUrl);
            
            if (urlMatcher.find()) {
                if (EXCLUDE.equalsIgnoreCase(this.listType)) {
                    return originalUrl;
                }

                return this.doUrlRewite(originalUrl, index, passThrough);
            }
        }
        
        if (EXCLUDE.equalsIgnoreCase(this.listType)) {
            return this.doUrlRewite(originalUrl, -1, passThrough);
        }

        return originalUrl;
    }

    
    /**
     * @return Returns the listType.
     */
    protected String getListType() {
        return this.listType;
    }
    /**
     * @param listType The listType to set.
     */
    protected void setListType(String listType) {
        this.listType = listType;
    }
    
    /**
     * @return Returns the urlRegExList.
     */
    protected String[] getUrlRegExList() {
        return this.urlRegExList;
    }
    /**
     * @param urlRegExList The urlRegExList to set.
     */
    protected void setUrlRegExList(String[] urlRegExList) {
        this.urlRegExList = urlRegExList;
    }

    
    /**
     * Rewrite URLs that match the RegEx and INCLUDE/EXCLUDE
     * criteria.
     * 
     * @param orignialUrl The URL to re-write
     * @return The re-written URL.
     */
    protected abstract String doUrlRewite(String orignialUrl, int matchIndex, boolean passThrough);
}
