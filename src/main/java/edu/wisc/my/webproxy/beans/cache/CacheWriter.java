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
package edu.wisc.my.webproxy.beans.cache;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;



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
public class CacheWriter extends Writer {
    private Writer out;
    private final StringWriter cacheBuffer = new StringWriter(1024);
    
    private final CacheEntry entryBase;
    private final String key;
    private final PageCache cache;
    private final boolean persistData;
    
    
    
    /**
     * Creates a new CacheWriter with the specified delegate stream and
     * caching configuration.
     * 
     * @param out The output stream to delegate cache calls to. If null no delegation is performed.
     * @param entryBase The base CacheEntry to store, the content field will be overwritten before caching.
     * @param cache The PageCache to use to store the data, may not be null.
     * @param cacheKey The key to store the data with, may not be null.
     * @param persistData If the data should be persisted by the store.
     */
    public CacheWriter(Writer out, CacheEntry entryBase, PageCache cache, String cacheKey, boolean persistData) {
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
    @Override
    public void close() throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }
        
        this.entryBase.setContent(this.cacheBuffer.toString());
        
        this.cache.cachePage(this.key, this.entryBase, this.persistData);
        
        if (this.out != null) {
            this.out.close();
        }
        
        this.out = null;
    }
    
    /**
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }

        this.out.flush();
    }


    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }
    
        this.cacheBuffer.write(cbuf, off, len);
        this.out.write(cbuf, off, len);
    }


    @Override
    public Writer append(char c) throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }
    
        this.cacheBuffer.append(c);
        this.out.append(c);
        
        return this;
    }


    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }
    
        this.cacheBuffer.append(csq, start, end);
        this.out.append(csq, start, end);
        
        return this;
    }


    @Override
    public Writer append(CharSequence csq) throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }
    
        this.cacheBuffer.append(csq);
        this.out.append(csq);
        
        return this;
    }


    @Override
    public void write(char[] cbuf) throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }
    
        this.cacheBuffer.write(cbuf);
        this.out.write(cbuf);
    }


    @Override
    public void write(int c) throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }
    
        this.cacheBuffer.write(c);
        this.out.write(c);
    }


    @Override
    public void write(String str, int off, int len) throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }
    
        this.cacheBuffer.write(str, off, len);
        this.out.write(str, off, len);
    }


    @Override
    public void write(String str) throws IOException {
        if (this.out == null) {
            throw new IllegalStateException("close() has already been called.");
        }
    
        this.cacheBuffer.write(str);
        this.out.write(str);
    }
}
