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
package edu.wisc.my.webproxy.servlet;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.collections.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Holds a Map of HttpSessions for sharing between the portlet and servlet.
 * The Map uses weak references to the HttpSession objects to ensure this
 * class does not cause any memmory leaks if a session is not removed from
 * the map at the appropriate time.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class SessionMappingListener implements HttpSessionListener {
    private static final Log LOG = LogFactory.getLog(SessionMappingListener.class);
    private static final Map sessionMap = Collections.synchronizedMap(new ReferenceMap(ReferenceMap.HARD, ReferenceMap.WEAK));
    
    /** 
     * Gets the session with the specified ID.
     * 
     * @param sid The ID of the session to retrieve.
     * @return The session, null if one is not found for the specified ID.
     */
    public static HttpSession getSession(final String sid) {
        return (HttpSession)sessionMap.get(sid);
    }
    
    /**
     * Stores a session.
     * 
     * @param session The session to store.
     */
    public static void setSession(final HttpSession session) {
        final String sid = session.getId();
        
        if (LOG.isDebugEnabled())
            LOG.debug("Storing session with ID=" + sid);
            
        sessionMap.put(sid, session);
        
        if (LOG.isDebugEnabled())
            LOG.debug(sessionMap.size() + " session stored");
    }
    
    /**
     * Removes a session with the specified ID.
     * 
     * @param sid The ID of the session to remove.
     * @return The removed session, null if one was not found.
     */
    public static HttpSession removeSession(final String sid) {
        if (LOG.isDebugEnabled())
            LOG.debug("Removing session with ID=" + sid);
            
        final HttpSession removed = (HttpSession)sessionMap.remove(sid);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug(sessionMap.size() + " session stored");

            if (removed == null)
                LOG.debug("No HttpSession removed for ID=" + sid);
            else
                LOG.debug("HttpSession removed for ID=" + sid);
        }
        
        return removed;
    }

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent event) {
        final HttpSession session = event.getSession();
        setSession(session);
    }

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        final HttpSession session = event.getSession();
        final String sid = session.getId();
        removeSession(sid);
    }
}
