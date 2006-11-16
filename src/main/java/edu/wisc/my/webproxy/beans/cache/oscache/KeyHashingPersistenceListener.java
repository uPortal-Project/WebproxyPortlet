/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.webproxy.beans.cache.oscache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import com.opensymphony.oscache.base.persistence.PersistenceListener;

/**
 * PersistenceListener that provides a way to hash the keys being used, ensures a maximum key length
 * for the delegate PersistenceListener.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class KeyHashingPersistenceListener extends DelegatingPersistenceListener {
    public static final String ALGORITHMS_PROP      = "cache.persistence.hashing.algorithms";
    public static final String DELEGATE_CLASS_PROP  = "cache.persistence.hashing.delegateClass";
    
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final List<String> DEFAULT_ALGORITHMS;
    
    static {
        final List<String> algorithmsBuilder = new ArrayList<String>(6);
        
        algorithmsBuilder.add("SHA-512");
        algorithmsBuilder.add("SHA-384");
        algorithmsBuilder.add("SHA-256");
        algorithmsBuilder.add("SHA-1");
        algorithmsBuilder.add("MD5");
        algorithmsBuilder.add("MD2");
        
        DEFAULT_ALGORITHMS = Collections.unmodifiableList(algorithmsBuilder);
    }
    
    /** An ObjectPool is used to reduce the number of MessageDigest objects that have to be created */
    private final ObjectPool messageDigestPool;
    private List<String> hashAlgorithms = null;
    

    public KeyHashingPersistenceListener() {
        this(null);
    }

    public KeyHashingPersistenceListener(PersistenceListener delegate) {
        super(delegate);

        //TODO this should be configured as a spring bean
        final GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = -1; //No limit on the number of active MessageDigesters
        config.maxIdle = 32; //TODO this limit should be configurable
        config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW; //Never not return a MessageDigest
        config.testOnBorrow = false; //MessageDigests don't need validation
        config.testOnReturn = false;
        config.timeBetweenEvictionRunsMillis = 60000; //Check idle objects once per minute
        config.minEvictableIdleTimeMillis = -1; //Only evict due to too many objects
        config.testWhileIdle = false;
        
        this.messageDigestPool = new GenericObjectPool(new MessageDigestorPoolableObjectFactory());
    }



    /**
     * @return Returns the hashAlgorithms.
     */
    public List<String> getHashAlgorithms() {
        return this.hashAlgorithms;
    }
    /**
     * @param hashAlgorithms The hashAlgorithms to set.
     */
    public void setHashAlgorithms(List<String> hashAlgorithms) {
        this.hashAlgorithms = hashAlgorithms;
    }


    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#getDelegateClassPropertyName()
     */
    protected String getDelegateClassPropertyName() {
        return DELEGATE_CLASS_PROP;
    }

    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#configureInternal(com.opensymphony.oscache.base.Config)
     */
    protected void configureInternal(Config config) {
        final String algorithmsList = config.getProperty(ALGORITHMS_PROP);
        if (algorithmsList != null)
            this.hashAlgorithms = CacheUtils.splitStringToList(algorithmsList, ",");
        else
            this.hashAlgorithms = DEFAULT_ALGORITHMS;
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Using hash algorithm list: " + this.hashAlgorithms);
        }
    }
    
    
    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#isGroupStored(java.lang.String)
     */
    public boolean isGroupStored(String groupName) throws CachePersistenceException {
        final String groupNameHash = this.getKeyHash(groupName);
        return super.isGroupStored(groupNameHash);
    }

    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#isStored(java.lang.String)
     */
    public boolean isStored(String key) throws CachePersistenceException {
        final String keyHash = this.getKeyHash(key);
        return super.isStored(keyHash);
    }

    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#remove(java.lang.String)
     */
    public void remove(String key) throws CachePersistenceException {
        final String keyHash = this.getKeyHash(key);
        super.remove(keyHash);
    }

    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws CachePersistenceException {
        final String groupNameHash = this.getKeyHash(groupName);
        super.removeGroup(groupNameHash);
    }

    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#retrieve(java.lang.String)
     */
    public Object retrieve(String key) throws CachePersistenceException {
        final String keyHash = this.getKeyHash(key);
        return super.retrieve(keyHash);
    }

    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#retrieveGroup(java.lang.String)
     */
    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        final String groupNameHash = this.getKeyHash(groupName);
        return super.retrieveGroup(groupNameHash);
    }

    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#store(java.lang.String, java.lang.Object)
     */
    public void store(String key, Object obj) throws CachePersistenceException {
        final String keyHash = this.getKeyHash(key);
        super.store(keyHash, obj);
    }

    /**
     * @see edu.wisc.my.webproxy.beans.cache.oscache.DelegatingPersistenceListener#storeGroup(java.lang.String, java.util.Set)
     */
    public void storeGroup(String groupName, Set group) throws CachePersistenceException {
        final String groupNameHash = this.getKeyHash(groupName);
        super.storeGroup(groupNameHash, group);
    }


    /**
     * Computes the hash of the key.
     * 
     * @param key The key to hash.
     * @return The base 16 hashed representation of the key.
     */
    private String getKeyHash(String key) {
        //TODO depending on how expensive this is perhaps a largish LFU cache of keys->hashes would be good?
        
        MessageDigest digest = null;
        try {
            try {
                digest = (MessageDigest)this.messageDigestPool.borrowObject();
            }
            catch (Exception e) {
                logger.error("Error borrowing MessageDigest '" + digest + "' from the pool");
            }   
    
            //If a digest was found use it to genereate the hash of the key
            if (digest != null) {
                if (logger.isTraceEnabled())
                    logger.trace("Using MessageDigest with hashCode=" + digest.hashCode());
                
                if (logger.isDebugEnabled())
                    logger.debug("Using '" + digest.getAlgorithm() + "' for key hashing.");
    
                final byte[] hash = digest.digest(key.getBytes());
                
                final String hashString = byteArrayToHexString(hash);
                
                if (logger.isDebugEnabled())
                    logger.debug("Mapped key '" + key + "' to '" + hashString + "'.");
    
                return hashString;
            }
        }
        finally {
            if (digest != null) {
                try {
                    this.messageDigestPool.returnObject(digest);
                }
                catch (Exception e) {
                    logger.error("Error returning MessageDigest '" + digest + "' to the pool");
                }
            }
        }
        
        //Failover to using the internal java hash of the String
        //if no MessgeDigest algorithms are configured or found.
        return Integer.toString(key.hashCode());
    }

    
    /**
     * Hex String conversion based on code from OSCache.
     * 
     * @param in the byte array to convert
     * @return a String based version of the byte array
     */
    private static String byteArrayToHexString(byte[] in) {
        if ((in == null) || (in.length <= 0)) {
            return null;
        }
    
        final StringBuffer out = new StringBuffer(in.length * 2);
    
        for (int byteIndex = 0; byteIndex < in.length; byteIndex++) {
            byte charIndex = (byte)(in[byteIndex] & 0xF0); // Strip off high nibble
            charIndex = (byte)(charIndex >>> 4);
    
            // shift the bits down
            charIndex = (byte)(charIndex & 0x0F);
            //must do this is high order bit is on!
            out.append(HEX_CHARS[charIndex]); // convert the nibble to a String Character
            
            charIndex = (byte)(in[byteIndex] & 0x0F); // Strip off low nibble 
            out.append(HEX_CHARS[charIndex]); // convert the nibble to a String Character
        }
    
        return out.toString();
    }


    
    
    
    /**
     * Factory for the ObjectPool to use to create MessageDigest objects
     * if needed.
     */
    private class MessageDigestorPoolableObjectFactory implements PoolableObjectFactory {
        private String hashAlgorithm;
        private MessageDigest defaultDigest;
        
        /**
         * @see org.apache.commons.pool.PoolableObjectFactory#activateObject(java.lang.Object)
         */
        public void activateObject(Object obj) throws Exception {
            ((MessageDigest)obj).reset();
        }
        
        /**
         * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
         */
        public void destroyObject(Object obj) throws Exception {
            ((MessageDigest)obj).reset();
        }
        
        /**
         * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
         */
        public Object makeObject() throws Exception {
            MessageDigest digest = null;
            
            //Try to clone the cached digest object instead of searching
            if (this.defaultDigest != null) {
                try {
                    if (logger.isTraceEnabled())
                        logger.trace("Cloning MessageDigest with hashCode=" + this.defaultDigest.hashCode());
                    
                    digest = (MessageDigest)this.defaultDigest.clone();
                    
                    if (digest != null && logger.isTraceEnabled())
                        logger.trace("Cloned MessageDigest has hashCode=" + digest.hashCode());
                }
                catch (CloneNotSupportedException cnse) {
                    logger.error("Could not clone cached MessageDigest for algorithm '" + this.defaultDigest.getAlgorithm() + "'");
                    this.defaultDigest = null;
                }
            }
            
            //Try to use the previously used hash algorithm instead of searching
            if (digest == null && this.hashAlgorithm != null) {
                try {
                    if (logger.isTraceEnabled())
                        logger.trace("Creating new MessageDigest for stored algorithm '" + this.hashAlgorithm + "'");
                    
                    digest = MessageDigest.getInstance(this.hashAlgorithm);

                    if (digest != null && logger.isTraceEnabled())
                        logger.trace("New MessageDigest has hashCode=" + digest.hashCode());
                }
                catch (NoSuchAlgorithmException nsae) {
                    logger.error("Could not use previously used algorithm '" + this.hashAlgorithm + "'");
                    this.hashAlgorithm = null;
                }
            }
            
            //Search for a usable hash algorithm
            for (final String algorithm : KeyHashingPersistenceListener.this.hashAlgorithms) {
                if (digest == null) {
                    break;
                }
                
                try {
                    digest = MessageDigest.getInstance(algorithm);
                    
                    //If found cache the information
                    if (digest != null) {
                        if (logger.isTraceEnabled())
                            logger.trace("New MessageDigest for '" + algorithm + "' has hashCode=" + digest.hashCode());

                        this.hashAlgorithm = digest.getAlgorithm();
                        
                        try {
                            this.defaultDigest = (MessageDigest)digest.clone();
                        }
                        catch (CloneNotSupportedException cnse) { }
                    }
                }
                catch (NoSuchAlgorithmException nsae) {
                    logger.warn("The '" + algorithm + "' was not found", nsae);
                }
            }
            
            return digest;
        }
        
        /**
         * @see org.apache.commons.pool.PoolableObjectFactory#passivateObject(java.lang.Object)
         */
        public void passivateObject(Object obj) throws Exception {
            ((MessageDigest)obj).reset();
        }
        
        /**
         * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
         */
        public boolean validateObject(Object obj) {
            ((MessageDigest)obj).reset();
            return true;
        }
    }
}
