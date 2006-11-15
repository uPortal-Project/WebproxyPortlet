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

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;


/**
 * A PersistenceListener proxy that filters calls to the underlying
 * PersistenceListener using the groups the item is in. This implementation assumes
 * that the Object passed in by the {@link #store(String, Object)} call is a
 * {@link com.opensymphony.oscache.base.CacheEntry}. If it is not it will not
 * be persisted no matter what the group. 
 * <br>
 * The set of groups to be persisted os configured using the property
 * {@link #PERSISTED_GROUPS_PROP}. The group names are comma delimited and case
 * sensitive.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class FilteringPersistenceListener extends DelegatingPersistenceListener {
    public static final String DELEGATE_CLASS_PROP = "cache.persistence.filter.delegateClass";
    public static final String PERSISTED_GROUPS_PROP = "cache.persistence.filter.groups";
    
    private Set persistedGroups;
    
    
    /**
     * @return Returns the persistedGroups.
     */
    public Set getPersistedGroups() {
        return this.persistedGroups;
    }

    /**
     * @param persistedGroups The persistedGroups to set.
     */
    public void setPersistedGroups(Set persistedGroups) {
        this.persistedGroups = persistedGroups;
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
        final String groups = config.getProperty(PERSISTED_GROUPS_PROP);
        
        if (groups == null) {
            if (this.persistedGroups == null) {
                throw new IllegalArgumentException("'" + PERSISTED_GROUPS_PROP + "' is not set but required or setPersistedGroups must be called.");
            }
            else {
                return;
            }
        }
        
        if (this.persistedGroups != null) {
            this.logger.warn("PersistedGroups is already set to '" + this.persistedGroups + "', they will be overriden by the groups defined by '" + PERSISTED_GROUPS_PROP + "'");
        }
        
        this.persistedGroups = CacheUtils.splitStringToSet(groups, ",");
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Persisting groups matching: " + this.persistedGroups);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isGroupStored(java.lang.String)
     */
    public boolean isGroupStored(String groupName) throws CachePersistenceException {
        return this.isGroupCachable(groupName) && super.isGroupStored(groupName);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws CachePersistenceException {
        if (this.isGroupCachable(groupName)) {
            super.removeGroup(groupName);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#store(java.lang.String, java.lang.Object)
     */
    public void store(String key, Object obj) throws CachePersistenceException {
        if (this.isObjectCachable(obj)) {
            super.store(key, obj);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#storeGroup(java.lang.String, java.util.Set)
     */
    public void storeGroup(String groupName, Set group) throws CachePersistenceException {
        if (this.isGroupCachable(groupName)) {
            super.storeGroup(groupName, group);
        }
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieveGroup(java.lang.String)
     */
    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        if (this.isGroupCachable(groupName)) {
            return super.retrieveGroup(groupName);
        }
        else {
            return null;
        }
    }
    
    
    
    /**
     * Determines if an object to be cached is cachable.
     */
    private boolean isObjectCachable(Object obj) {
        if (obj instanceof CacheEntry) {
            final CacheEntry entry = (CacheEntry)obj;
            final Set entryGroups = entry.getGroups();
            
            if (entryGroups != null) {
                for (final Iterator groupItr = entryGroups.iterator(); groupItr.hasNext(); ) {
                    final String groupName = (String)groupItr.next();
                    
                    if (this.persistedGroups.contains(groupName)) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Object='" + obj + "' is cachable because it is in group='" + groupName + "'");
                        }
                        
                        return true;
                    }
                }
            }

            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Object='" + obj + "' is not in any cachable groups");
            }
            
            return false;
        }
        else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Object='" + obj + "' is no of type='" + CacheEntry.class + "' so it cannot be filtered and is not cachable.");
            }
            
            return false;
        }
    }
    
    /**
     * Determines if a group name is cachable
     */
    private boolean isGroupCachable(String groupName) {
        return this.persistedGroups.contains(groupName);
    }
}
