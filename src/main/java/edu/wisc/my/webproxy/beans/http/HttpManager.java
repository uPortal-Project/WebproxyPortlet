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
package edu.wisc.my.webproxy.beans.http;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.persistence.Transient;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.http.auth.Credentials;
import org.apache.http.cookie.Cookie;

import edu.wisc.my.webproxy.beans.config.ProxyComponent;

/**
 * This interface is what the portlet uses to make requests. The implementation will
 * be responsible for using the data provided in the Request interface to make the
 * remote request and construct a Response object with the results.
 * 
 * @author nramzan
 * 
 * @version
 * 
 */
public abstract class HttpManager implements ProxyComponent {
    private transient final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	/** 
     * Performs the request with the given request object.
     *
     * @param request the (@link Request) object that contains all the necessary data to make the remote request.
     * @return response the (@link Response) object that contains the result/content of the the request made.
     * 
     */
	
    public abstract Response doRequest(Request request) throws HttpTimeoutException, IOException;
    
    
    /** 
     * Creates an empty request object.
     *
     * @return request the (@link Request) object with no attributes set on it
     * 
     */
    
    public abstract Request createRequest();
    
    /**
     * Responsible for setting up the HttpManager. This component must be
     * callable from a servlet as well so the portlet request and response
     * methods are not available.
     * 
     * This method *must* be called prior to attempting to use the HttpManager.
     * 
     * @param prefs The preferences to use for configuration.
     */
    public abstract void setup(PortletRequest request);
    
    /**
     * Set the user authentication credentials.
     * 
     * @param credentials
     */
    public abstract void setCredentials(Credentials credentials);
    
    /**
     * Return the user authentication credentials.
     * 
     * @return
     */
    public abstract Credentials getCredentials();
    
    /**
     * Add a cookie to the HttpManager.
     * 
     * @param cookie
     */
    public abstract void addCookie(Cookie cookie);
    
    /**
     * Add an array of cookies to the HttpManager.
     * 
     * @param cookie
     */
    public abstract void addCookies(Cookie[] cookie);
    
    /**
     * Clear all cookies currently available in the HttpManager.
     */
    public abstract void clearCookies();
    
    /**
     * Return a list of all currently set cookies in the HttpManager.
     * 
     * @return
     */
    public abstract List<Cookie> getCookies();

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#setRenderData(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public final void setRenderData(RenderRequest request, RenderResponse response) {
        this.setup(request);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.config.ProxyComponent#setActionData(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public final void setActionData(ActionRequest request, ActionResponse response) {
        this.setup(request);
    }
    

    
    public final ReadWriteLock getCookieLock() {
        return this.lock;
    }
}

