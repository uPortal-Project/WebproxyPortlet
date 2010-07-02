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
package edu.wisc.my.webproxy.beans.http;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a cookie that is passed to/from the remote application.
 * 
 * @author nramzan
 * 
 * @version $Id$ 
 * 
 */
public interface ICookie extends Serializable {
	
	/**
     * Returns the comment describing the purpose of this cookie, or
     * <tt>null</tt> if no such comment has been defined.
     * 
     * @return comment 
     *
     * @see #setComment(String)
     */
	
    String getComment();
    
    /**
    * Returns domain attribute of the cookie.
    * 
    * @return the value of the domain attribute
    *
    * @see #setDomain(java.lang.String)
    */
    
    String getDomain();
    
    /**
     * Returns the expiration {@link Date} of the cookie, or <tt>null</tt>
     * if none exists.
     * @return Expiration {@link Date}, or <tt>null</tt>.
     *
     * @see #setExpiryDate(java.util.Date)
     *
     */
    
    Date getExpiryDate();
    
    /**
     * Return the name.
     *
     * @return String name The name
     * @see #setName(String)
     */
    
    String getName();
    
    /**
     * Returns the path attribute of the cookie
     * 
     * @return The value of the path attribute.
     * 
     * @see #setPath(java.lang.String)
     */
    
    String getPath();
    
    /**
     * @return <code>true</code> if this cookie should only be sent over secure connections.
     * @see #setSecure(boolean)
     */
    
    boolean isSecure();
    
    /**
     * Returns true if this cookie has expired.
     * 
     * @return <tt>true</tt> if the cookie has expired.
     */
    
    boolean isExpired();
    String getValue();
    
    /**
     * Returns the version of the cookie specification to which this
     * cookie conforms.
     *
     * @return the version of the cookie.
     * 
     * @see #setVersion(int)
     *
     */
    
    int getVersion();
    
    /**
     * Sets the comment used to describe the cookie's purpose.
     * 
     * @param comment
     *  
     * @see #getComment()
     */
    
    void setComment(String purpose);
    
    /**
     * Sets expiration date.
     * @param expiryDate the {@link Date} after which this cookie is no longer valid.
     *
     * @see #getExpiryDate
     *
     */
    
    void setExpiryDate(Date expiry);
    
    /**
     * Sets the path attribute.
     *
     * @param path The value of the path attribute
     *
     * @see #getPath
     *
     */
    
    void setPath(String uri);
    
    /**
     * Sets the secure attribute of the cookie.
     * <p>
     * When <tt>true</tt> the cookie should only be sent
     * using a secure protocol (https).  This should only be set when
     * the cookie's originating server used a secure protocol to set the
     * cookie's value.
     *
     * @param secure The value of the secure attribute
     * 
     * @see #getSecure()
     */
    
    void setSecure(boolean flag);
    
    /**
     * Set the value.
     *
     * @param value The new value.
     */
    
    void setValue(String newValue);
    
    /**
     * Sets the version of the cookie specification to which this
     * cookie conforms. 
     *
     * @param version the version of the cookie.
     * 
     * @see #getVersion
     */
    
    void setVersion(int v);

    /**
     * @return When this cookie was originally created
     */
    public Date getCreated();

    /**
     * @return The last time this cookie was updated
     */
    public Date getUpdated();
    
}

