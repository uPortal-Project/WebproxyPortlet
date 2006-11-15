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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Provides a utility to track a running average with minimal overhead. This
 * class is thread safe.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class Average {
    private static final Log LOG = LogFactory.getLog(Average.class);
    
    private final long minSize;
    private final long initialSize;
    private final long maxSize;
    
    private final Object AVERAGE_LOCK = new Object();
    private double currentAverage;
    private long count = 1;
    
    public Average() {
        this(1024, 10240, 524288);
    }
    
    public Average (long min, long initial, long max) {
        if (min > initial || initial > max)
            throw new IllegalArgumentException("Constraing 'min <= initial <= max' violated min='" + min + "', initial='" + initial + "', max='" + max + "'");
        
        this.minSize = min;
        this.initialSize = initial;
        this.maxSize = max;
        
        this.currentAverage = this.initialSize;
    }
    
    public void updateSize(double size) {
        synchronized (AVERAGE_LOCK) {
            this.count++;
            final double ratio = ((double)(this.count - 1))/((double)this.count);
            this.currentAverage = (this.currentAverage * ratio) + size/this.count;
            

            if (this.currentAverage < this.minSize)
                this.currentAverage = this.minSize;
            else if (this.currentAverage > this.maxSize)
                this.currentAverage = this.maxSize;
        }
        
        if (LOG.isTraceEnabled())
            LOG.trace("Updated average to '" + this.currentAverage + "'");
    }
    
    public void updateSize(long size) {
        this.updateSize((double)size);
    }
    
    public void updateSize(int size) {
        this.updateSize((double)size);
    }
    
    public double getDoubleAverage() {
        return this.currentAverage;
    }
    
    public long getLongAverage() {
        return (long)this.currentAverage;
    }
    
    public int getIntAverage() {
        return (int)this.currentAverage;
    }
}
