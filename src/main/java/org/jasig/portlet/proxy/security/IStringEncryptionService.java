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
 * IStringEncryptionService is a small interface for string encryption/decryption.
 * It is expected that this service will generally consist of a small wrapper
 * for some other encryption API.
 *
 * @author bourey
 * @version $Id: $Id
 */
public interface IStringEncryptionService {
    
    /**
     * Encrypt a string
     *
     * @param plaintext a {@link java.lang.String} object
     * @return encrypted version of the plaintext
     * @throws org.jasig.portlet.proxy.security.StringEncryptionException
     */
    public String encrypt(String plaintext);
    
    /**
     * Decrypt a string
     *
     * @param cryptotext a {@link java.lang.String} object
     * @return decrypted version of the cryptotext
     * @throws org.jasig.portlet.proxy.security.StringEncryptionException
     */
    public String decrypt(String cryptotext);

    /**
     * Returns true if the encryptor is configured to use the default encryption key.
     *
     * @return true if encryption key is default value
     */
    public boolean usingDefaultEncryptionKey();

}
