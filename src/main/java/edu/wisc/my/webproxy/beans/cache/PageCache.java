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
