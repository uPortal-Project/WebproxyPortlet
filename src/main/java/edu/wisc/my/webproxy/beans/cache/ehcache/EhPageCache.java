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
package edu.wisc.my.webproxy.beans.cache.ehcache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import edu.wisc.my.webproxy.beans.cache.CacheEntry;
import edu.wisc.my.webproxy.beans.cache.PageCache;

/**
 * PageCache using EhCache
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class EhPageCache implements PageCache {
    private Ehcache ehcache;
    private int cacheSecondsToLiveDefault = 0;
    
    public Ehcache getEhcache() {
        return this.ehcache;
    }
    public void setEhcache(Ehcache ehcache) {
        this.ehcache = ehcache;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.cache.PageCache#cachePage(java.lang.String, edu.wisc.my.webproxy.beans.cache.CacheEntry, boolean)
     */
    public void cachePage(String key, CacheEntry entry, boolean persistent) {
        final Element element = new Element(key, entry);
        
        // don't try to cache if the expiration is null 
        if (entry.getExpirationDate()==null) return;
        
        long secondsTtl = ((entry.getExpirationDate().getTime() - System.currentTimeMillis()) > 0) ? 
                (entry.getExpirationDate().getTime() - System.currentTimeMillis())/1000 : cacheSecondsToLiveDefault; 
        element.setTimeToLive((int) Math.min(Integer.MAX_VALUE, secondsTtl)); // ensure that value can fit into an int.
        this.ehcache.put(element);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.cache.PageCache#getCachedPage(java.lang.String)
     */
    public CacheEntry getCachedPage(String key) {
        return this.getCachedPage(key, false);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.cache.PageCache#getCachedPage(java.lang.String, boolean)
     */
    public CacheEntry getCachedPage(String key, boolean useExpired) {
        final Element element = this.ehcache.get(key);
        
        if (element == null || (!useExpired && element.isExpired())) {
            return null;
        }
        
        return (CacheEntry)element.getValue();
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.cache.PageCache#getCachedPage(java.lang.String, int)
     */
    public CacheEntry getCachedPage(String key, int maxCacheAge) {
        final Element element = this.ehcache.get(key);
        
        if (element == null || (System.currentTimeMillis() - element.getCreationTime()) > maxCacheAge) {
            return null;
        }
        
        return (CacheEntry)element.getValue();
    }
    /**
     * @param cacheSecondsToLiveDefault the cacheSecondsToLiveDefault to set
     */
    public void setCacheSecondsToLiveDefault(int cacheSecondsToLiveDefault) {
        this.cacheSecondsToLiveDefault = cacheSecondsToLiveDefault;
    }

}
