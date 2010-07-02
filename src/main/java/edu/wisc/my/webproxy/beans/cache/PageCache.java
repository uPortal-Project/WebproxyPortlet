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

package edu.wisc.my.webproxy.beans.cache;


/**
 * Represents a generic cache of WebProxy CacheEntry objects.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision$
 */
public interface PageCache {
    
    /**
     * Caches a page with the specified key and persistence flag.
     * 
     * @param key The key to cache the data for.
     * @param entry The cache entry to store.
     * @param persistent If true the entry will be stored in the persistent cache and be avaialble between restarts.
     */
    public void cachePage(String key, CacheEntry entry, boolean persistent);
    
    /**
     * Retrieves the cache entry for the specified key. If the entry is expired
     * null is returned. The expirationDate field of the CacheEntry object is used
     * for determining expiration.
     * 
     * @param key The key to retrieve the entry for.
     * @return Then entry for the key, null if no entry exists or it has expired.
     */
    public CacheEntry getCachedPage(String key);

    /**
     * Retrieves the cache entry for the specified key. If the entry is expired
     * but still exists in the cache it is stil returned.
     * 
     * @param key The key to retrieve the entry for.
     * @param useExpired Flag to use the entry if it expired or not.
     * @return The entry for the key, null if no entry exists for the key.
     */
    public CacheEntry getCachedPage(String key, boolean useExpired);
    
    /**
     * Retrieves the cache entry for the specified key. If the entry is older
     * than the number of seconds specified by maxCacheAge null is returned.
     * 
     * @param key The key to retrieve the entry for.
     * @param maxCacheAge The maximum age of the entry in seconds.
     * @return The entry for the key, null if no entry exists or if the entry is older than the specified max age.
     */
    public CacheEntry getCachedPage(String key, int maxCacheAge);
}
