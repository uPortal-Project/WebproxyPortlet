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

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.exceptions.EncryptionInitializationException;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * JasyptPBEStringEncryptionServiceImpl is an implementation of
 * IStringEncryptionService that uses a configurable Jasypt PBEStringEncryptor
 * to perform string encryption and decryption.
 *
 * @author Jen Bourey
 * @version $Id: $Id
 */
public class JasyptPBEStringEncryptionServiceImpl implements IStringEncryptionService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PBEStringEncryptor encryptor = null;
    
    /**
     * Set the PBEStringEncryptor to be used
     *
     * @param encryptor a {@link org.jasypt.encryption.pbe.PBEStringEncryptor} object
     */
    @Required
    public void setStringEncryptor(final PBEStringEncryptor encryptor) {
        this.encryptor = encryptor;
    }
    
    /** {@inheritDoc} */
    public String encrypt(final String plaintext) {
        try {
            return this.encryptor.encrypt(plaintext);
        } catch (EncryptionInitializationException e) {
            logger.warn(e.getMessage(), e);
            throw new StringEncryptionException("Encryption error. Verify an encryption password"
                    + " is configured in configuration.properties", e);
        }
    }
    
    private String decrypt(final String cryptotext, boolean logError) {
        try {
            return this.encryptor.decrypt(cryptotext);
        } catch (EncryptionInitializationException e) {
            if (logError) {
                logger.warn("Decryption failed.  Error message: {}", e.getMessage());
            }
            throw new StringEncryptionException("Decryption error. Was encryption password"
                        + " changed in the configuration.properties?", e);
        } catch (EncryptionOperationNotPossibleException e) {
            if (logError) {
                logger.warn("Decryption failed.  This suggests the salt key in "
                        + "configuration.properties has been changed.");
            }
            throw new StringEncryptionException("Decryption error. Was encryption password"
                        + " changed in the configuration.properties?", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String decrypt(final String cryptotext) {
        return decrypt(cryptotext, true);
    }

    /**
     * {@inheritDoc}
     *
     * @throws java.lang.Exception if any.
     */
    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        final String enc = this.encrypt(this.getClass().getName());
        Assert.notNull(enc, "String encryption service is not properly configured.");

        final String dec = this.decrypt(enc);
        Assert.notNull(dec, "String decryption service is not properly configured.");
        Assert.isTrue(dec.equals(this.getClass().getName()), "String decryption failed to decode the encrypted text " + enc);

        if (usingDefaultEncryptionKey()) {
            logger.error("Encryption key at default value.  Change it in configuration.properties for improved security!");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean usingDefaultEncryptionKey() {
        try {
            String result = this.decrypt("pD2vrJ0CiAbnW4k4lF84S8yXN6gSl6VUjISd8NN6AFnDGuei5rGyuw==", false);
            return "EncryptionKeyStillchangeMe".equals(result);
        } catch (StringEncryptionException e) {
            // If encryption is being used, a decryption error is good because it means the encryption key was changed.
            return false;
        }
    }
}
