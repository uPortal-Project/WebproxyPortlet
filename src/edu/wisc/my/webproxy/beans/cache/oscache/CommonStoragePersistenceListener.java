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
 *c
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
package edu.wisc.my.webproxy.beans.cache.oscache;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import com.opensymphony.oscache.base.persistence.PersistenceListener;

import edu.wisc.my.apilayer.portlet.IScopeIdentifier;
import edu.wisc.my.storage.IDocument;
import edu.wisc.my.storage.IFolder;
import edu.wisc.my.storage.IResourceFactory;
import edu.wisc.my.storage.ResourceFactoryAccess;
import edu.wisc.my.storage.ResourceScope;
import edu.wisc.my.storage.ScopeLevel;
import edu.wisc.my.storage.StorageException;
import edu.wisc.my.webproxy.beans.WebProxyScopeIdentifier;


/**
 * OSCache PersistenceListener that uses the CommonStorage API as it's backing store.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision$
 */
public class CommonStoragePersistenceListener implements PersistenceListener {
    public static final String ALGORITHMS_PROP = "cache.persistence.commonStorage.hashing";
    
    private static final Log LOG = LogFactory.getLog(CommonStoragePersistenceListener.class);
    
    private static final IScopeIdentifier SCOPE_IDENTIFIER = new CacheScopeIdentifier();
    private static final String CACHE_FOLDER = "OsCacheContent";
    private static final String GROUPS_FOLDER = "Groups";
    private static final String KEY_PARAM = "REAL_KEY";
    
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final List DEFAULT_ALGORITHMS;
    
    static {
        final List algorithmsBuilder = new ArrayList(6);
        
        algorithmsBuilder.add("SHA-512");
        algorithmsBuilder.add("SHA-384");
        algorithmsBuilder.add("SHA-256");
        algorithmsBuilder.add("SHA-1");
        algorithmsBuilder.add("MD5");
        algorithmsBuilder.add("MD2");
        
        DEFAULT_ALGORITHMS = Collections.unmodifiableList(algorithmsBuilder);
    }
    
    //Data to tune the ByteArrayOutputStream of the cache.
    private final Average bufferSizeAverage = new Average();
    
    /** An ObjectPool is used to reduce the number of MessageDigest objects that have to be created */
    private final ObjectPool messageDigestPool;
    private List hashAlgorithms = null;
    
    public CommonStoragePersistenceListener() {
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
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isStored(java.lang.String)
     */
    public boolean isStored(String key) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("isStored(" + key + ")");
        
        try {
            final IFolder cacheFolder = this.getCacheFolder();
            final String keyHash = this.getKeyHash(key);
            final IDocument doc = cacheFolder.getDocument(keyHash);
            
            if (doc != null) {
                final String storedKey = doc.getParameter(KEY_PARAM);
            
                return key.equals(storedKey);
            }
        }
        catch (StorageException se) {
            LOG.error("Could not determine if '" + key + "' exists in storage", se);
        }
        
        return false;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isGroupStored(java.lang.String)
     */
    public boolean isGroupStored(String groupName) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("isGroupStored(" + groupName + ")");
        
        try {
            final IFolder groupsFolder = this.getGroupsFolder();
            final String groupNameHash = this.getKeyHash(groupName);
            final IDocument doc = groupsFolder.getDocument(groupNameHash);
            
            if (doc != null) {
                final String storedGroupName = doc.getParameter(KEY_PARAM);
            
                return groupName.equals(storedGroupName);
            }
        }
        catch (StorageException se) {
            LOG.error("Could not determine if '" + groupName + "' exists in storage", se);
        }
        
        return false;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#clear()
     */
    public void clear() throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("clear()");
        
        try {
            final IFolder cacheFolder = this.getCacheFolder();
            cacheFolder.delete();
        }
        catch (StorageException se) {
            LOG.error("Could not clear storage", se);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#configure(com.opensymphony.oscache.base.Config)
     */
    public PersistenceListener configure(Config config) {
        final String algorithmsList = config.getProperty(ALGORITHMS_PROP);
        if (algorithmsList != null)
            this.hashAlgorithms = CacheUtils.splitStringToList(algorithmsList, ",");
        else
            this.hashAlgorithms = DEFAULT_ALGORITHMS;
        
        if (LOG.isDebugEnabled())
            LOG.debug("Using hash algorithm list: " + this.hashAlgorithms);

        
        return this;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#remove(java.lang.String)
     */
    public void remove(String key) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("remove(" + key + ")");

        try {
            final IFolder cacheFolder = this.getCacheFolder();
            final String keyHash = this.getKeyHash(key);
            final IDocument doc = cacheFolder.getDocument(keyHash);
            
            if (doc != null) {
                final String storedKey = doc.getParameter(KEY_PARAM);
            
                if (key.equals(storedKey));
                    doc.delete();
            }
        }
        catch (StorageException se) {
            LOG.error("Could not remove '" + key + "' from storage", se);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("removeGroup(" + groupName + ")");

        try {
            final IFolder groupsFolder = this.getGroupsFolder();
            final String groupNameHash = this.getKeyHash(groupName);
            final IDocument doc = groupsFolder.getDocument(groupNameHash);
            
            if (doc != null) {
                final String storedGroupName = doc.getParameter(KEY_PARAM);
            
                if (groupName.equals(storedGroupName));
                    doc.delete();
            }
        }
        catch (StorageException se) {
            LOG.error("Could not remove '" + groupName + "' from storage", se);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieve(java.lang.String)
     */
    public Object retrieve(String key) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("retrieve(" + key + ")");
        
        try {
            final IFolder cacheFolder = this.getCacheFolder();
            final String keyHash = this.getKeyHash(key);
            final IDocument doc = cacheFolder.getDocument(keyHash);
            
            if (doc != null) {
                final String storedKey = doc.getParameter(KEY_PARAM);
                
                if (key.equals(storedKey)) {
                    final InputStream byteStream = doc.getContentAsInputStream();
                    final ObjectInputStream ois = new ObjectInputStream(byteStream);
                    try {
                        final Object obj = ois.readObject();
                        
                        return obj;
                    }
                    finally {
                        try { byteStream.close(); } catch (IOException ioe) { }
                        try { ois.close(); } catch (IOException ioe) { }
                    }
                }
            }
        }
        catch (StorageException se) {
            LOG.error("Error retrieving '" + key + "'.", se);
        }
        catch (IOException ioe) {
            LOG.error("Error retrieving '" + key + "'.", ioe);
        }
        catch (ClassNotFoundException cnfe) {
            LOG.error("Error retrieving '" + key + "'.", cnfe);
        }
        
        return null;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#store(java.lang.String, java.lang.Object)
     */
    public void store(String key, Object obj) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("store(" + key + ", " + obj + ")");

        try {
            final IFolder cacheFolder = this.getCacheFolder();
            
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(this.bufferSizeAverage.getIntAverage());
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            try {
                oos.writeObject(obj);
                oos.flush();
                baos.flush();
    
                //Get a hash of the key to ensure it is short enough and
                //doesn't contain invalid characters
                final String keyHash = this.getKeyHash(key);
                final byte[] bytes = baos.toByteArray();
                
                final IDocument doc = cacheFolder.storeDocument(keyHash, bytes);
                doc.setParameter(KEY_PARAM, key);
                doc.update();
                
                this.bufferSizeAverage.updateSize(doc.getSize());
            }
            finally {
                try { oos.close(); } catch (IOException ioe) { };
                try { baos.close(); } catch (IOException ioe) { };
            }
        }
        catch (StorageException se) {
            LOG.error("Error storing '" + key + "'.", se);
        }
        catch (IOException ioe) {
            LOG.error("Error storing '" + key + "'.", ioe);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#storeGroup(java.lang.String, java.util.Set)
     */
    public void storeGroup(String groupName, Set group) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("storeGroup(" + groupName + ", " + group + ")");

        try {
            final IFolder groupsFolder = this.getGroupsFolder();
            
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(5120);
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            try {
                oos.writeObject(group);
                oos.flush();
                baos.flush();
    
                //Get a hash of the key to ensure it is short enough and
                //doesn't contain invalid characters
                final String groupNameHash = this.getKeyHash(groupName);
                final byte[] bytes = baos.toByteArray();
                
                final IDocument doc = groupsFolder.storeDocument(groupNameHash, bytes);
                doc.setParameter(KEY_PARAM, groupName);
                doc.update();
            }
            finally {
                try { oos.close(); } catch (IOException ioe) { };
                try { baos.close(); } catch (IOException ioe) { };
            }
        }
        catch (StorageException se) {
            LOG.error("Error storing '" + groupName + "'.", se);
        }
        catch (IOException ioe) {
            LOG.error("Error storing '" + groupName + "'.", ioe);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieveGroup(java.lang.String)
     */
    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("retrieveGroup(" + groupName + ")");
        
        try {
            final IFolder groupsFolder = this.getGroupsFolder();
            final String groupNameHash = this.getKeyHash(groupName);
            final IDocument doc = groupsFolder.getDocument(groupNameHash);
            
            if (doc != null) {
                final String storedGroupName = doc.getParameter(KEY_PARAM);
                
                if (groupName.equals(storedGroupName)) {
                    final InputStream byteStream = doc.getContentAsInputStream();
                    final ObjectInputStream ois = new ObjectInputStream(byteStream);
                    try {
                        final Set groups = (Set)ois.readObject();
                        
                        return groups;
                    }
                    finally {
                        try { byteStream.close(); } catch (IOException ioe) { }
                        try { ois.close(); } catch (IOException ioe) { }
                    }
                }
            }
        }
        catch (StorageException se) {
            LOG.error("Error retrieving '" + groupName + "'.", se);
        }
        catch (IOException ioe) {
            LOG.error("Error retrieving '" + groupName + "'.", ioe);
        }
        catch (ClassNotFoundException cnfe) {
            LOG.error("Error retrieving '" + groupName + "'.", cnfe);
        }
        
        return null;
    }
    
    
    /**
     * Common operation to get the IFolder that represents the cache folder.
     * 
     * @return The IFolder to cache data in.
     * @throws StorageException If thrown while getting or creating the cache folder.
     */
    private IFolder getCacheFolder() throws StorageException {
        final ResourceScope scope = new ResourceScope(SCOPE_IDENTIFIER, ScopeLevel.SYSTEM);
        final IResourceFactory factory = ResourceFactoryAccess.getInstance().getResourceFactory(scope);
        factory.setUseCache(false);
        final IFolder rootFolder = factory.getRoot();
        
        IFolder cacheFolder = rootFolder.getFolder(CACHE_FOLDER);
        if (cacheFolder == null)
            cacheFolder = rootFolder.createFolder(CACHE_FOLDER);
        
        return cacheFolder;
    }
    
    /**
     * Common operation to get the IFolder that represents the groups folder.
     * 
     * @return The IFolder to store group Sets in.
     * @throws StorageException If thrown while getting or creating the groups folder.
     */
    private IFolder getGroupsFolder() throws StorageException {
        final IFolder cacheFolder = this.getCacheFolder();
        
        IFolder groupsFolder = cacheFolder.getFolder(GROUPS_FOLDER);
        if (groupsFolder == null)
            groupsFolder = cacheFolder.createFolder(GROUPS_FOLDER);
        
        return groupsFolder;
    }
    
    /**
     * Computes the hash of the key.
     * 
     * @param key The key to hash.
     * @return The base 16 hashed representation of the key.
     */
    private String getKeyHash(String key) {
        MessageDigest digest = null;
        try {
            try {
                digest = (MessageDigest)this.messageDigestPool.borrowObject();
            }
            catch (Exception e) {
                LOG.error("Error borrowing MessageDigest '" + digest + "' from the pool");
            }   

            //If a digest was found use it to genereate the hash of the key
            if (digest != null) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Using MessageDigest with hashCode=" + digest.hashCode());
                
                if (LOG.isDebugEnabled())
                    LOG.debug("Using '" + digest.getAlgorithm() + "' for key hashing.");
    
                final byte[] hash = digest.digest(key.getBytes());
                
                final String hashString = byteArrayToHexString(hash);
                
                if (LOG.isDebugEnabled())
                    LOG.debug("Mapped key '" + key + "' to '" + hashString + "'.");
    
                return hashString;
            }
        }
        finally {
            if (digest != null) {
                try {
                    this.messageDigestPool.returnObject(digest);
                }
                catch (Exception e) {
                    LOG.error("Error returning MessageDigest '" + digest + "' to the pool");
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
     * Simple IScopeIdentifier implementation needed for CommonStorage
     */
    private static class CacheScopeIdentifier extends WebProxyScopeIdentifier {
        /**
         * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getApplicationIdentifier()
         */
        public String getApplicationIdentifier() {
            return CommonStoragePersistenceListener.class.getName();
        }
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
                    if (LOG.isTraceEnabled())
                        LOG.trace("Cloning MessageDigest with hashCode=" + this.defaultDigest.hashCode());
                    
                    digest = (MessageDigest)this.defaultDigest.clone();
                    
                    if (digest != null && LOG.isTraceEnabled())
                        LOG.trace("Cloned MessageDigest has hashCode=" + digest.hashCode());
                }
                catch (CloneNotSupportedException cnse) {
                    LOG.error("Could not clone cached MessageDigest for algorithm '" + this.defaultDigest.getAlgorithm() + "'");
                    this.defaultDigest = null;
                }
            }
            
            //Try to use the previously used hash algorithm instead of searching
            if (digest == null && this.hashAlgorithm != null) {
                try {
                    if (LOG.isTraceEnabled())
                        LOG.trace("Creating new MessageDigest for stored algorithm '" + this.hashAlgorithm + "'");
                    
                    digest = MessageDigest.getInstance(this.hashAlgorithm);

                    if (digest != null && LOG.isTraceEnabled())
                        LOG.trace("New MessageDigest has hashCode=" + digest.hashCode());
                }
                catch (NoSuchAlgorithmException nsae) {
                    LOG.error("Could not use previously used algorithm '" + this.hashAlgorithm + "'");
                    this.hashAlgorithm = null;
                }
            }
            
            //Search for a usable hash algorithm
            for (final Iterator algItr = hashAlgorithms.iterator(); algItr.hasNext() && digest == null; ) {
                final String algorithm = (String)algItr.next();
                
                try {
                    digest = MessageDigest.getInstance(algorithm);
                    
                    //If found cache the information
                    if (digest != null) {
                        if (LOG.isTraceEnabled())
                            LOG.trace("New MessageDigest for '" + algorithm + "' has hashCode=" + digest.hashCode());

                        this.hashAlgorithm = digest.getAlgorithm();
                        
                        try {
                            this.defaultDigest = (MessageDigest)digest.clone();
                        }
                        catch (CloneNotSupportedException cnse) { }
                    }
                }
                catch (NoSuchAlgorithmException nsae) {
                    LOG.warn("The '" + algorithm + "' was not found", nsae);
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
