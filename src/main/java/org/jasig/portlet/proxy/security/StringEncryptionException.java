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
package org.jasig.portlet.proxy.security;


/**
 * A custom Exception for the Web Proxy portlet that may be thrown as the result
 * of a failure in encrypting/decrypting a string.
 *
 * @author Misagh Moayyed
 * @see IStringEncryptionService
 * @version $Id: $Id
 */
public class StringEncryptionException extends RuntimeException {

    private static final long serialVersionUID = 1l;
    
    /**
     * <p>Constructor for StringEncryptionException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object
     */
    public StringEncryptionException(Throwable cause) {
        super(cause);
    }

    /**
     * <p>Constructor for StringEncryptionException.</p>
     *
     * @param message a {@link java.lang.String} object
     * @param cause a {@link java.lang.Throwable} object
     */
    public StringEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for StringEncryptionException.</p>
     *
     * @param message a {@link java.lang.String} object
     */
    public StringEncryptionException(String message) {
        super(message);
    }

}
