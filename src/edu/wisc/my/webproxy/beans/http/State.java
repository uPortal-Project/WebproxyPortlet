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

import org.apache.commons.httpclient.NTCredentials;

/**
 * Represents the state of a HTTP session, this includes cookies and
 * credentials.
 * 
 * @author nramzan
 * 
 * @version $Id$
 */
public interface State extends Serializable {
	
	/**
     * Retrives all the cookies contained in this state object.
     * 
     * @return cookies an array of Cookies that were set on this object.
     */   
    
    Cookie[] getCookies();
    
    /**
     * Retrives the UserCredential object that this state object contains.
     * 
     * @return credentials the (@link Credentials) that this state object contains
     */   
    
    Credentials getUserCredentials();
    
    /**
     * Retrives the NTCredentials object that this state object contains.
     * 
     * @return proxyCredentials the (@link Credentials) that this state object contains
     */   

    Credentials getNTCredentials();
    
    /**
     * Adds a single cookie to the list of cookies contained by this state object.
     * 
     * @param c the (@link Cookie) object that needs to be set on this object
     */   

    void addCookie(Cookie c);
    
    /**
     * Adds multiple cookie to the list of cookies contained by this state object.
     * 
     * @param c an array of cookie objects that need to be set on this object
     */   

    
    void addCookies(Cookie[] c);
    
    /**
     * Removes all the cookies from this state object.
     * 
     * @param none
     */
    
    void clearCookies();
    
    /**
     * Sets the userCredentials(username, password) on this atate object.
     * 
     * @param c the (@link Credentials) objects that need to be set to keep track of sessions
     */   

    
    void setUserCredentials(Credentials c);
    
    /**
     * Sets the NTCredentials(username, password, host, domain) on this atate object.
     * 
     * @param c the (@link Credentials) objects that need to be set to keep track of sessions
     */   

    void setNTCredentials(Credentials c);
    
    /**
     * Creates an empty (@link Cookie) object
     * 
     * @return cookie the (@link Cookie) with no data set on it
     */   
    
    Cookie createCookie();
    
    /**
     * Creates an (@link Credentials) object with the given username and password
     * 
     * @param username the username as String
     * @param password the password as String
     * @return credentials the (@link Credentials) object
     */   
    
    Credentials createUserCredentials(String userName, String password);
    
    /**
     * Creates an (@link NTCredentials) object with the given username, password, host and domain
     * 
     * @param username the username as String
     * @param password the password as String
     * @param host the name of the host as String
     * @param domain the domain name as String
     * @return credentials the (@link NTCredentials) object
     */   
    
    
    NTCredentials createNTCredentials(String userName, String password, String host, String domain);
}

