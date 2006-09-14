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
import java.util.Set;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private static final IScopeIdentifier SCOPE_IDENTIFIER = new CacheScopeIdentifier();
    private static final String CACHE_FOLDER = "OsCacheContent";
    private static final String GROUPS_FOLDER = "Groups";
    private static final String KEY_PARAM = "REAL_KEY";
    
    //Data to tune the ByteArrayOutputStream of the cache.
    private final Average bufferSizeAverage = new Average();
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#configure(com.opensymphony.oscache.base.Config)
     */
    public PersistenceListener configure(Config arg0) {
        return this;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isStored(java.lang.String)
     */
    public boolean isStored(String key) throws CachePersistenceException {
        if (logger.isTraceEnabled())
            logger.trace("isStored(" + key + ")");
        
        try {
            final IFolder cacheFolder = this.getCacheFolder();
            final IDocument doc = cacheFolder.getDocument(key);
            
            if (doc != null) {
                final String storedKey = doc.getParameter(KEY_PARAM);
            
                return key.equals(storedKey);
            }
        }
        catch (StorageException se) {
            logger.error("Could not determine if '" + key + "' exists in storage", se);
        }
        
        return false;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isGroupStored(java.lang.String)
     */
    public boolean isGroupStored(String groupName) throws CachePersistenceException {
        if (logger.isTraceEnabled())
            logger.trace("isGroupStored(" + groupName + ")");
        
        try {
            final IFolder groupsFolder = this.getGroupsFolder();
            final IDocument doc = groupsFolder.getDocument(groupName);
            
            if (doc != null) {
                final String storedGroupName = doc.getParameter(KEY_PARAM);
            
                return groupName.equals(storedGroupName);
            }
        }
        catch (StorageException se) {
            logger.error("Could not determine if '" + groupName + "' exists in storage", se);
        }
        
        return false;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#clear()
     */
    public void clear() throws CachePersistenceException {
        if (logger.isTraceEnabled())
            logger.trace("clear()");
        
        try {
            final IFolder cacheFolder = this.getCacheFolder();
            cacheFolder.delete();
        }
        catch (StorageException se) {
            logger.error("Could not clear storage", se);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#remove(java.lang.String)
     */
    public void remove(String key) throws CachePersistenceException {
        if (logger.isTraceEnabled())
            logger.trace("remove(" + key + ")");

        try {
            final IFolder cacheFolder = this.getCacheFolder();
            final IDocument doc = cacheFolder.getDocument(key);
            
            if (doc != null) {
                final String storedKey = doc.getParameter(KEY_PARAM);
            
                if (key.equals(storedKey));
                    doc.delete();
            }
        }
        catch (StorageException se) {
            logger.error("Could not remove '" + key + "' from storage", se);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws CachePersistenceException {
        if (logger.isTraceEnabled())
            logger.trace("removeGroup(" + groupName + ")");

        try {
            final IFolder groupsFolder = this.getGroupsFolder();
            final IDocument doc = groupsFolder.getDocument(groupName);
            
            if (doc != null) {
                final String storedGroupName = doc.getParameter(KEY_PARAM);
            
                if (groupName.equals(storedGroupName));
                    doc.delete();
            }
        }
        catch (StorageException se) {
            logger.error("Could not remove '" + groupName + "' from storage", se);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieve(java.lang.String)
     */
    public Object retrieve(String key) throws CachePersistenceException {
        if (logger.isTraceEnabled())
            logger.trace("retrieve(" + key + ")");
        
        try {
            final IFolder cacheFolder = this.getCacheFolder();
            final IDocument doc = cacheFolder.getDocument(key);
            
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
            logger.error("Error retrieving '" + key + "'.", se);
        }
        catch (IOException ioe) {
            logger.error("Error retrieving '" + key + "'.", ioe);
        }
        catch (ClassNotFoundException cnfe) {
            logger.error("Error retrieving '" + key + "'.", cnfe);
        }
        
        return null;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#store(java.lang.String, java.lang.Object)
     */
    public void store(String key, Object obj) throws CachePersistenceException {
        if (logger.isTraceEnabled())
            logger.trace("store(" + key + ", " + obj + ")");

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
                final byte[] bytes = baos.toByteArray();
                
                final IDocument doc = cacheFolder.storeDocument(key, bytes);
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
            logger.error("Error storing '" + key + "'.", se);
        }
        catch (IOException ioe) {
            logger.error("Error storing '" + key + "'.", ioe);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#storeGroup(java.lang.String, java.util.Set)
     */
    public void storeGroup(String groupName, Set group) throws CachePersistenceException {
        if (logger.isTraceEnabled())
            logger.trace("storeGroup(" + groupName + ", " + group + ")");

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
                final byte[] bytes = baos.toByteArray();
                
                final IDocument doc = groupsFolder.storeDocument(groupName, bytes);
                doc.setParameter(KEY_PARAM, groupName);
                doc.update();
            }
            finally {
                try { oos.close(); } catch (IOException ioe) { };
                try { baos.close(); } catch (IOException ioe) { };
            }
        }
        catch (StorageException se) {
            logger.error("Error storing '" + groupName + "'.", se);
        }
        catch (IOException ioe) {
            logger.error("Error storing '" + groupName + "'.", ioe);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieveGroup(java.lang.String)
     */
    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        if (logger.isTraceEnabled())
            logger.trace("retrieveGroup(" + groupName + ")");
        
        try {
            final IFolder groupsFolder = this.getGroupsFolder();
            final IDocument doc = groupsFolder.getDocument(groupName);
            
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
            logger.error("Error retrieving '" + groupName + "'.", se);
        }
        catch (IOException ioe) {
            logger.error("Error retrieving '" + groupName + "'.", ioe);
        }
        catch (ClassNotFoundException cnfe) {
            logger.error("Error retrieving '" + groupName + "'.", cnfe);
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
}
