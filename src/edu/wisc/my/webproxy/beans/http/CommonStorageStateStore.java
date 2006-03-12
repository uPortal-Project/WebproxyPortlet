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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * StateStore implementation that uses CommonStorage for persistence.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class CommonStorageStateStore implements StateStore {
    private static final Log LOG = LogFactory.getLog(CommonStorageStateStore.class);
    private static final IScopeIdentifier SCOPE_IDENTIFIER = new StateStoreScopeIdentifier();
    private static final String STATE_FOLDER = "StateStore";
    
    /**
     * @see edu.wisc.my.webproxy.beans.http.StateStore#storeState(edu.wisc.my.apilayer.portlet.IScopeIdentifier, java.lang.String, edu.wisc.my.webproxy.beans.http.State)
     */
    public void storeState(String key, State state) {
        if (LOG.isTraceEnabled())
            LOG.trace("storeState(" + key + ", " + state + ")");
        
        try {
            final IFolder stateFolder = this.getStateFolder();
            
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(5120);
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            try {
                oos.writeObject(state);
                oos.flush();
                baos.flush();
    
                final byte[] bytes = baos.toByteArray();
                
                stateFolder.storeDocument(key, bytes);
            }
            finally {
                try { oos.close(); } catch (IOException ioe) { };
                try { baos.close(); } catch (IOException ioe) { };
            }
        }
        catch (StorageException se) {
            LOG.error("Could not persist state for key='" + key + "'", se);
        }
        catch (IOException ioe) {
            LOG.error("Could not persist state for key='" + key + "'", ioe);
        }
    }

    /**
     * @see edu.wisc.my.webproxy.beans.http.StateStore#getState(edu.wisc.my.apilayer.portlet.IScopeIdentifier, java.lang.String)
     */
    public State getState(String key) {
        if (LOG.isTraceEnabled())
            LOG.trace("getState(" + key + ")");
        
        try {
            final IFolder stateFolder = this.getStateFolder();
            final IDocument doc = stateFolder.getDocument(key);
            
            if (doc != null) {
                final InputStream byteStream = doc.getContentAsInputStream();
                final ObjectInputStream ois = new ObjectInputStream(byteStream);

                try {
                    final State state = (State)ois.readObject();
                    
                    return state;
                }
                finally {
                    try { byteStream.close(); } catch (IOException ioe) { }
                    try { ois.close(); } catch (IOException ioe) { }
                }
            }
        }
        catch (StorageException se) {
            LOG.error("Could not retrieve state for key='" + key + "'", se);
        }
        catch (IOException ioe) {
            LOG.error("Could not retrieve state for key='" + key + "'", ioe);
        }
        catch (ClassNotFoundException cnfe) {
            LOG.error("Could not retrieve state for key='" + key + "'", cnfe);
        }
        
        return null;
    }

    /**
     * @see edu.wisc.my.webproxy.beans.http.StateStore#deleteState(edu.wisc.my.apilayer.portlet.IScopeIdentifier, java.lang.String)
     */
    public void deleteState(String key) {
        if (LOG.isTraceEnabled())
            LOG.trace("deleteState(" + key + ")");

        try {
            final IFolder stateFolder = this.getStateFolder();
            final IDocument doc = stateFolder.getDocument(key);
            
            if (doc != null) {
                doc.delete();
            }
        }
        catch (StorageException se) {
            LOG.error("Could not delete state for key='" + key + "'", se);
        }
    }

    /**
     * @see edu.wisc.my.webproxy.beans.http.StateStore#clearAll(edu.wisc.my.apilayer.portlet.IScopeIdentifier)
     */
    public void clearAll() {
        if (LOG.isTraceEnabled())
            LOG.trace("clearAll()");
        
        try {
            final IFolder stateFolder = this.getStateFolder();
            stateFolder.delete();
        }
        catch (StorageException se) {
            LOG.error("Could not clear all states", se);
        }
    }

    /**
     * Common operation to get the IFolder that represents the cache folder.
     * 
     * @return The IFolder to cache data in.
     * @throws StorageException If thrown while getting or creating the cache folder.
     */
    private IFolder getStateFolder() throws StorageException {
        final ResourceScope resScope = new ResourceScope(SCOPE_IDENTIFIER, ScopeLevel.SYSTEM);
        final IResourceFactory factory = ResourceFactoryAccess.getInstance().getResourceFactory(resScope);
        factory.setUseCache(true);
        final IFolder rootFolder = factory.getRoot();
        
        IFolder stateFolder = rootFolder.getFolder(STATE_FOLDER);
        if (stateFolder == null)
            stateFolder = rootFolder.createFolder(STATE_FOLDER);
        
        return stateFolder;
    }
    
    /**
     * Simple IScopeIdentifier implementation needed for CommonStorage
     */
    private static class StateStoreScopeIdentifier extends WebProxyScopeIdentifier {
        /**
         * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getApplicationIdentifier()
         */
        public String getApplicationIdentifier() {
            return CommonStorageStateStore.class.getName();
        }
    }
}
