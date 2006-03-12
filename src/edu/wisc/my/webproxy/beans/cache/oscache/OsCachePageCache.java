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

import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;

import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

import edu.wisc.my.webproxy.beans.cache.CacheEntry;
import edu.wisc.my.webproxy.beans.cache.PageCache;


/**
 * An implementation of the PageCache interface that wraps an OsCache
 * GeneralCacheAdministrator class.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision$
 */
public class OsCachePageCache implements PageCache, InitializingBean {
    private static final EntryRefreshPolicy REFRESH_POLICY = new CacheEntryRefreshPolicy();
    
    private GeneralCacheAdministrator cacheAdmin;
    private Properties cacheProperties;
    
    /**
     * @return Returns the cacheProperties.
     */
    public Properties getCacheProperties() {
        return this.cacheProperties;
    }
    /**
     * @param cacheProperties The cacheProperties to set.
     */
    public void setCacheProperties(Properties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }
    
    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        if (this.cacheProperties == null)
            throw new IllegalStateException("cacheProperties must be set");
        
        cacheAdmin = new GeneralCacheAdministrator(this.cacheProperties);
    }
    
    
    /**
     * @see edu.wisc.my.webproxy.beans.cache.PageCache#cachePage(java.lang.Object, java.lang.Object, boolean)
     */
    public void cachePage(String key, CacheEntry entry, boolean persistent) {
        if (persistent) {
            //TODO make persisted group name configurable
            this.cacheAdmin.putInCache(key, entry, new String[] { "PERSIST" }, REFRESH_POLICY);
        }
        else
            this.cacheAdmin.putInCache(key, entry, REFRESH_POLICY);
    }
    
    /**
     * @see edu.wisc.my.webproxy.beans.cache.PageCache#getCachedPage(java.lang.String)
     */
    public CacheEntry getCachedPage(String key) {
        return this.getCachedPage(key, false);
    }

    /**
     * @see edu.wisc.my.webproxy.beans.cache.PageCache#getCachedPage(java.lang.String, boolean)
     */
    public CacheEntry getCachedPage(String key, boolean useExpired) {
        CacheEntry entry = null;
        try {
            entry = (CacheEntry)this.cacheAdmin.getFromCache(key);
        }
        catch (NeedsRefreshException nre) {
            if (useExpired)
                entry = (CacheEntry)nre.getCacheContent();

            this.cacheAdmin.cancelUpdate(key);
        }
        
        return entry;
    }
    
    /**
     * @see edu.wisc.my.webproxy.beans.cache.PageCache#getCachedPage(java.lang.String, int)
     */
    public CacheEntry getCachedPage(String key, int maxCacheAge) {
        CacheEntry entry = null;
        try {
            entry = (CacheEntry)this.cacheAdmin.getFromCache(key, maxCacheAge);
        }
        catch (NeedsRefreshException nre) {
            this.cacheAdmin.cancelUpdate(key);
        }
        
        return entry;
    }
}
