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
package edu.wisc.my.webproxy.beans.cache;

import java.io.Serializable;
import java.util.Date;


/**
 * Represents the rendered content of a page. The content type of the request
 * and a String of the content are stored which allow the content to be
 * replayed to the user.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision$
 */
public class CacheEntry implements Serializable {
    private String contentType;
    private String content;
    private Date expirationDate;
    
    
    /**
     * @return Returns the content.
     */
    public String getContent() {
        return this.content;
    }
    /**
     * @return Returns the contentType.
     */
    public String getContentType() {
        return this.contentType;
    }
    /**
     * @return Returns the expirationDate
     */
    public Date getExpirationDate() {
        return this.expirationDate;
    }
    /**
     * @param content The content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * @param contentType The contentType to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    /**
     * @param expirationDate The expirationDate to set.
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
