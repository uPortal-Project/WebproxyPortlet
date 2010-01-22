/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.webproxy.beans.http;

import java.io.IOException;
import java.security.SecureRandom;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.portlet.util.PortletUtils;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class KeyManager implements IKeyManager {
    //Hoping that a 1032 bit random key won't cause collisions between portlets (extra 8 bits so it fills out the base 64 string)
    private static final int KEY_SIZE = 129;
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private final SecureRandom random = new SecureRandom();
    
    public String generateCacheKey(String pageUrl, PortletRequest request) {
        final String instanceKey = this.getInstanceKey(request);
        
        return this.generateCacheKey(pageUrl, instanceKey);
    }
    
    public String generateCacheKey(String pageUrl, String instanceKey) {
        final StringBuffer cacheKeyBuf = new StringBuffer();
        cacheKeyBuf.append(instanceKey);
        cacheKeyBuf.append(".");
        cacheKeyBuf.append(pageUrl);
        return cacheKeyBuf.toString();
    }

    public String generateStateKey(String key, PortletRequest request) {
        final String instanceKey = this.getInstanceKey(request);

        final StringBuilder cacheKeyBuf = new StringBuilder();
        cacheKeyBuf.append(instanceKey);
        cacheKeyBuf.append(".");
        cacheKeyBuf.append(key);
        return cacheKeyBuf.toString();
    }

    /**
     * Generates a unique key for this portlet instance that will persist across portal restarts
     */
    public String getInstanceKey(PortletRequest request) {
        final PortletSession portletSession = request.getPortletSession();
        
        synchronized (PortletUtils.getSessionMutex(portletSession)) {
            final PortletPreferences preferences = request.getPreferences();
            String instanceKey = preferences.getValue(PORTLET_INSTANCE_KEY, null);
            if (instanceKey == null) {
                instanceKey = (String)portletSession.getAttribute(PORTLET_INSTANCE_KEY);
                
                if (instanceKey == null) {
                    final byte[] keyBytes = new byte[KEY_SIZE];
                    this.random.nextBytes(keyBytes);
                    instanceKey = new String(Base64.encodeBase64(keyBytes));
                    
                    portletSession.setAttribute(PORTLET_INSTANCE_KEY, instanceKey);
                    
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Created new portlet instance key: " + instanceKey);
                    }
                }
    
                //Only try updating the preferences if it is an action request
                if (request instanceof ActionRequest) {
                    try {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Stored portlet instance key in portlet preferences: " + instanceKey);
                        }
                        
                        preferences.setValue(PORTLET_INSTANCE_KEY, instanceKey);
                        preferences.store();
                    }
                    catch (ReadOnlyException e) {
                        throw new RuntimeException("The portlet preference " + PORTLET_INSTANCE_KEY + " must not be set read-only", e);
                    }
                    catch (ValidatorException e) {
                        throw new RuntimeException("The portlet preference " + PORTLET_INSTANCE_KEY + " failed storage validation", e);
                    }
                    catch (IOException e) {
                        throw new RuntimeException("Storing the portlet preference " + PORTLET_INSTANCE_KEY + " failed", e);
                    }
                }
            }
            return instanceKey;
        }
    }
}
