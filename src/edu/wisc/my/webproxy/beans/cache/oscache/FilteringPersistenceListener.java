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
package edu.wisc.my.webproxy.beans.cache.oscache;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import com.opensymphony.oscache.base.persistence.PersistenceListener;


/**
 * A PersistenceListener proxy that filters calls to the underlying
 * PersistenceListener using the groups the item is in. This implementation assumes
 * that the Object passed in by the {@link #store(String, Object)} call is a
 * {@link com.opensymphony.oscache.base.CacheEntry}. If it is not it will not
 * be persisted no matter what the group. 
 * <br>
 * The delegate {@link com.opensymphony.oscache.base.persistence.PersistenceListener} is
 * configured using the property defined by {@link #DELEGATE_CLASS_PROP}.
 * <br>
 * The set of groups to be persisted os configured using the property
 * {@link #PERSISTED_GROUPS_PROP}. The group names are comma delimited and case
 * sensitive.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class FilteringPersistenceListener implements PersistenceListener {
    private static final Log LOG = LogFactory.getLog(FilteringPersistenceListener.class);
    
    public static final String DELEGATE_CLASS_PROP = "cache.persistence.filter.delegateClass";
    public static final String PERSISTED_GROUPS_PROP = "cache.persistence.filter.groups";
    
    private PersistenceListener delegate;
    private Set persistedGroups;

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isStored(java.lang.String)
     */
    public boolean isStored(String key) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("isStored(" + key + ")");
        
        return this.delegate.isStored(key);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isGroupStored(java.lang.String)
     */
    public boolean isGroupStored(String groupName) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("isGroupStored(" + groupName + ")");
        
        return this.delegate.isGroupStored(groupName);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#clear()
     */
    public void clear() throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("clear()");
        
        this.delegate.clear();
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#configure(com.opensymphony.oscache.base.Config)
     */
    public PersistenceListener configure(Config config) {
        if (LOG.isTraceEnabled())
            LOG.trace("configure(" + config.getProperties() + ")");
        
        final String delegateClassName = config.getProperty(DELEGATE_CLASS_PROP);
        if (delegateClassName == null)
            throw new IllegalArgumentException(DELEGATE_CLASS_PROP + " is not set but required");

        final String groups = config.getProperty(PERSISTED_GROUPS_PROP);
        if (groups == null)
            throw new IllegalArgumentException(PERSISTED_GROUPS_PROP + " is not set but required");

        //Get the delegate PersistenceListener
        try {
            final Class delegateClass = Class.forName(delegateClassName);
            this.delegate = (PersistenceListener)delegateClass.newInstance();
        }
        catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }
        catch (InstantiationException ie) {
            throw new RuntimeException(ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        }

        this.persistedGroups = CacheUtils.splitStringToSet(groups, ",");
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Using persistence class: " + this.delegate.getClass().getName());
            LOG.debug("Persisting groups matching: " + this.persistedGroups);
        }
        
        this.delegate = this.delegate.configure(config);
        return this;
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#remove(java.lang.String)
     */
    public void remove(String key) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("remove(" + key + ")");

        this.delegate.remove(key);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("removeGroup(" + groupName + ")");

        this.delegate.removeGroup(groupName);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieve(java.lang.String)
     */
    public Object retrieve(String key) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("retrieve(" + key + ")");

        return this.delegate.retrieve(key);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#store(java.lang.String, java.lang.Object)
     */
    public void store(String key, Object obj) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("store(" + key + ", " + obj + ")");

        if (obj instanceof CacheEntry) {
            final CacheEntry entry = (CacheEntry)obj;
            final Set entryGroups = entry.getGroups();
            
            if (entryGroups != null) {
                for (final Iterator groupItr = entryGroups.iterator(); groupItr.hasNext(); ) {
                    final String groupName = (String)groupItr.next();
                    
                    if (this.persistedGroups.contains(groupName)) {
                        if (LOG.isDebugEnabled())
                            LOG.debug("Storing '" + key + "' because of a match on '" + groupName + "'");
                        
                        this.delegate.store(key, obj);
                        return;
                    }
                }
            }

            if (LOG.isDebugEnabled())
                LOG.debug("No group match for '" + key + "' with group set '" + entryGroups + "'");
        }
        
        if (LOG.isDebugEnabled())
            LOG.debug("Object for '" + key + "' is not a CacheEntry. It will not be stored.");
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#storeGroup(java.lang.String, java.util.Set)
     */
    public void storeGroup(String groupName, Set group) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("storeGroup(" + groupName + ", " + group + ")");

        if (this.persistedGroups.contains(groupName)) {
            if (LOG.isDebugEnabled())
                LOG.debug("Storing group '" + groupName + "'");

            this.delegate.storeGroup(groupName, group);
        }
        else {
            if (LOG.isDebugEnabled())
                LOG.debug("Not storing group '" + groupName + "'");
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieveGroup(java.lang.String)
     */
    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        if (LOG.isTraceEnabled())
            LOG.trace("retrieveGroup(" + groupName + ")");

        return this.delegate.retrieveGroup(groupName);
    }
}
