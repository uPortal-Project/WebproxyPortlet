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
package edu.wisc.my.webproxy.beans.http;


/**
 * {@link ICredentials} for use with the NTLM authentication scheme which requires additional
 * information
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * 
 * @version $Revision$
 */
public class NtCredentials extends ICredentials {
    private String domain;
    private String host;
    
    /**
     * Default constructor.
     * 
     */
    
    public NtCredentials() {
        
    }

    /**
     * Constructor.
     * @param userName The user name.  This should not include the domain to authenticate with.
     * For example: "user" is correct whereas "DOMAIN\\user" is not.
     * @param password The password.
     * @param host The host the authentication request is originating from.  Essentially, the
     * computer name for this machine.
     * @param domain The domain to authenticate within.
     */
    
    public NtCredentials(String userName, String password, String host, String domain) {
        super(userName, password);
        this.host = host;
        this.domain = domain;
    }
    
    
    /**
     * Retrieves the domain name to authenticate with.
     *
     * @return String the domain these credentials are intended to authenticate with.
     * 
     * @see #setDomain(String)
     * 
     */
    
    public String getDomain() {
        return this.domain;
    }
    
    /**
     * Retrieves the host name of the computer originating the request.
     *
     * @return String the host the user is logged into.
     */
    
    public String getHost() {
        return this.host;
    }
    
    /**
     * Sets the domain to authenticate with. The domain may not be null.
     *
     * @param domain the NT domain to authenticate in.
     * 
     * @see #getDomain()
     * 
     */
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    /** 
     * Sets the host name of the computer originating the request. The host name may
     * not be null.
     *
     * @param host the Host the user is logged into.
     * 
     */
    
    public void setHost(String host) {
        this.host = host;
    }
}
