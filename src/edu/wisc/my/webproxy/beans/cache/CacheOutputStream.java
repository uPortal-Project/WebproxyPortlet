/**
 * Copyright (c) 2001 The JA-SIG Collaborative.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the JA-SIG Collaborative
 *    (http://www.jasig.org/)."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JA-SIG COLLABORATIVE "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JA-SIG COLLABORATIVE OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package edu.wisc.my.webproxy.beans.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;



/**
 * Caching output stream. Takes a PageCache and the cache parameters along with
 * an output stream to delegate the calls to.
 * 
 * When close() is called on this class the bufferend content is written
 * out to the PageCache.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision$
 */
public class CacheOutputStream extends OutputStream {
    private final OutputStream out;
    private final CacheEntry entryBase;
    private final String key;
    private final PageCache cache;
    private final boolean persistData;
    
    private ByteArrayOutputStream cacheBuffer = new ByteArrayOutputStream(1024);;
    
    /**
     * Creates a new CacheOutputStream with the specified delegate stream and
     * caching configuration.
     * 
     * @param out The output stream to delegate cache calls to. If null no delegation is performed.
     * @param entryBase The base CacheEntry to store, the content field will be overwritten before caching.
     * @param cache The PageCache to use to store the data, may not be null.
     * @param cacheKey The key to store the data with, may not be null.
     * @param persistData If the data should be persisted by the store.
     */
    public CacheOutputStream(OutputStream out, CacheEntry entryBase, PageCache cache, String cacheKey, boolean persistData) {
        if (cache == null)
            throw new IllegalArgumentException("cache cannot be null");
        if (cacheKey == null)
            throw new IllegalArgumentException("cacheKey cannot be null");
        
        this.out = out;
        this.entryBase = entryBase;
        this.key = cacheKey;
        this.cache = cache;
        this.persistData = persistData;
    }
    

    /**
     * Creates and stores a {@link CacheEntry} in the {@link PageCache}.
     * 
     * @see java.io.OutputStream#close()
     */
    public void close() throws IOException {
        if (this.cacheBuffer == null)
            throw new IllegalStateException("close() has already been called.");
        
        this.entryBase.setContent(this.cacheBuffer.toByteArray());
        
        this.cache.cachePage(this.key, this.entryBase, this.persistData);
        
        this.cacheBuffer = null;
        
        if (this.out != null)
            this.out.close();
    }
    
    /**
     * @see java.io.OutputStream#flush()
     */
    public void flush() throws IOException {
        if (this.cacheBuffer == null)
            throw new IllegalStateException("close() has already been called.");

        if (this.out != null)
            this.out.flush();
    }
    
    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {
        if (this.cacheBuffer == null)
            throw new IllegalStateException("close() has already been called.");

        this.cacheBuffer.write(b, off, len);
        
        if (this.out != null)
            this.out.write(b, off, len);
    }
    
    /**
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        if (this.cacheBuffer == null)
            throw new IllegalStateException("close() has already been called.");

        this.cacheBuffer.write(b);
        
        if (this.out != null)
            this.out.write(b);
    }
    
    /**
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        if (this.cacheBuffer == null)
            throw new IllegalStateException("close() has already been called.");

        this.cacheBuffer.write(b);
        
        if (this.out != null)
            this.out.write(b);
    }
}
