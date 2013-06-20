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

package org.jasig.portlet.proxy.security;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.exceptions.EncryptionInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * JasyptPBEStringEncryptionServiceImpl is an implementation of 
 * IStringEncryptionService that uses a configurable Jasypt PBEStringEncryptor
 * to perform string encryption and decryption.
 * 
 * @author Jen Bourey
 */
public class JasyptPBEStringEncryptionServiceImpl implements IStringEncryptionService, InitializingBean {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private PBEStringEncryptor encryptor = null;
	
	/**
	 * Set the PBEStringEncryptor to be used
	 * 
	 * @param encryptor
	 */
	public void setStringEncryptor(final PBEStringEncryptor encryptor) {
		this.encryptor = encryptor;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String encrypt(final String plaintext) {
		if (this.encryptor == null) {
			logger.error("encryptor not set");
            return null;
		}
        try {
            return this.encryptor.encrypt(plaintext);
        } catch (EncryptionInitializationException e) {
            logger.warn(e.getMessage(), e);
            throw new StringEncryptionException("Encryption error. Verify an encryption password"
                    + " is configured in the email preview portlet's"
                    + " stringEncryptionService bean in applicationContent.xml", e);
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String decrypt(final String cryptotet) {
		if (this.encryptor == null) {
			logger.error("encryptor not set");
            return null;
		}
      try {
	        return this.encryptor.decrypt(cryptotet);
      } catch (EncryptionInitializationException e) {
    	  logger.warn(e.getMessage(), e);
          throw new StringEncryptionException("Decryption error. Was encryption password"
                  + " changed in the email preview portlet's"
                  + " stringEncryptionService bean in applicationContent.xml?", e);
      }
    }

    @Override
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

    @Override
    public boolean usingDefaultEncryptionKey() {
        String result = this.decrypt("pD2vrJ0CiAbnW4k4lF84S8yXN6gSl6VUjISd8NN6AFnDGuei5rGyuw==");
        return "EncryptionKeyStillchangeMe".equals(result);
    }
}
