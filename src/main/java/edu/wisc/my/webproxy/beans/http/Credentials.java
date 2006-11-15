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

/**
 * Represents a authentication credentials that is passed to remote
 * application. This only applies to the Basic, Digest and NTLM forms of
 * authentication.
 * 
 * @author nramzan
 * 
 * @version $Id$
 * 
 */
public class Credentials implements Serializable {
    
    private String userName = null;
    private String password = null;
    
    /**
     * Default constructor.
     * 
     */
    
    public Credentials() {
        
    }
    
    /**
     * The constructor with the username and password arguments.
     *
     * @param userName the user name
     * @param password the password
     */
    
    public Credentials(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Returns the password attribute.
     *
     * @return the password
     * @see #setPassword(String)
     */
    
    public String getPassword() {
        return password;
        
    /**
     * Password property setter. Password may not be null
     *
     * @param password
     * @see #getPassword()
     * 
     */    
        
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Returns the username attribute.
     *
     * @return the userName
     * @see #setUserName(String)
     */
    
    public String getUserName() {
        return userName;
    }
    
    /**
     * User name property setter. Username may not be null.
     *
     * @param userName
     * @see #getUserName()
     * 
     */
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
}

