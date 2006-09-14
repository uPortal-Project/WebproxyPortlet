/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.webproxy.beans.cache.oscache;

import java.util.MissingResourceException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import com.opensymphony.oscache.base.persistence.PersistenceListener;

/**
 * PersistenceListener that implements the logic for loading a delegate class from the
 * cache configuration.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class DelegatingPersistenceListener implements PersistenceListener {
    public static final String DELEGATE_CLASS_PROP = "cache.persistence.delegateClass";
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    private PersistenceListener delegateListener;
    
    public DelegatingPersistenceListener() {
        this(null);
    }
    
    public DelegatingPersistenceListener(PersistenceListener delegate) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("DelegatingPersistenceListener(" + delegate + ")");
        }
        
        this.delegateListener = delegate;
    }
    
    
    /**
     * @return Returns the delegateListener.
     */
    public PersistenceListener getDelegateListener() {
        return this.delegateListener;
    }
    /**
     * @param delegateListener The delegateListener to set.
     */
    public void setDelegateListener(PersistenceListener delegateListener) {
        this.delegateListener = delegateListener;
    }

    /**
     * @return The name of the property to use for loading the delegate class. Called before {@link #configureInternal(Config)} is called.
     */
    protected String getDelegateClassPropertyName() {
        return DELEGATE_CLASS_PROP;
    }
    
    
    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#configure(com.opensymphony.oscache.base.Config)
     */
    public final PersistenceListener configure(Config config) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("configure(" + config.getProperties() + ")");
        }
        
        this.loadDelegatePersistenceListener(config);
        
        this.configureInternal(config);
        
        this.delegateListener = this.delegateListener.configure(config);

        return this;
    }
    
    /**
     * Configure method to be implemented by subclasses.
     */
    protected void configureInternal(Config config) {
    }

    /**
     * Using a property in the cache config loads a delegate Persistence listener.
     * 
     * @param config The cache Config to load the delegate class name from. 
     */
    private void loadDelegatePersistenceListener(Config config) {
        final String delegateClassProperty = this.getDelegateClassPropertyName();
        
        final String delegateClassName = config.getProperty(delegateClassProperty);
        if (delegateClassName == null) {
            if (this.delegateListener == null) {
                throw new IllegalArgumentException("'" + delegateClassProperty + "' is not set but required");
            }
            else {
                return;
            }
        }

        if (this.delegateListener != null) {
            this.logger.warn("A delegate PersistenceListener is already configured, it will be overriden. delegate='" + this.delegateListener.getClass() + "'");
        }
        
        //Get the delegate PersistenceListener Class
        final Class delegateClass;
        try {
            delegateClass = Class.forName(delegateClassName);
        }
        catch (ClassNotFoundException cnfe) {
            final MissingResourceException mre = new MissingResourceException("Can not find delegate PersistenceListener class='" + delegateClassName + "'", this.getClass().getName(), delegateClassName);
            mre.initCause(cnfe);
            throw mre;
        }
        
        //Create the delegate PersistenceListener
        try {
            this.delegateListener = (PersistenceListener)delegateClass.newInstance();
        }
        catch (InstantiationException ie) {
            final MissingResourceException mre = new MissingResourceException("Could not instantiate delegate PersistenceListener class='" + delegateClassName + "'", this.getClass().getName(), delegateClassName);
            mre.initCause(ie);
            throw mre;
        }
        catch (IllegalAccessException iae) {
            final MissingResourceException mre = new MissingResourceException("Could not instantiate delegate PersistenceListener class='" + delegateClassName + "'", this.getClass().getName(), delegateClassName);
            mre.initCause(iae);
            throw mre;
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Using persistence class: " + this.delegateListener.getClass());
        }
    }

    


    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isStored(java.lang.String)
     */
    public boolean isStored(String key) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("isStored(" + key + ")");
        }
        
        return this.delegateListener.isStored(key);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isGroupStored(java.lang.String)
     */
    public boolean isGroupStored(String groupName) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("isGroupStored(" + groupName + ")");
        }
        
        return this.delegateListener.isGroupStored(groupName);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#clear()
     */
    public void clear() throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("clear()");
        }
        
        this.delegateListener.clear();
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#remove(java.lang.String)
     */
    public void remove(String key) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("remove(" + key + ")");
        }

        this.delegateListener.remove(key);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("removeGroup(" + groupName + ")");
        }

        this.delegateListener.removeGroup(groupName);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieve(java.lang.String)
     */
    public Object retrieve(String key) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("retrieve(" + key + ")");
        }

        return this.delegateListener.retrieve(key);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#store(java.lang.String, java.lang.Object)
     */
    public void store(String key, Object obj) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("store(" + key + ", " + obj + ")");
        }

        this.delegateListener.store(key, obj);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#storeGroup(java.lang.String, java.util.Set)
     */
    public void storeGroup(String groupName, Set group) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("storeGroup(" + groupName + ", " + group + ")");
        }

        this.delegateListener.storeGroup(groupName, group);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieveGroup(java.lang.String)
     */
    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("retrieveGroup(" + groupName + ")");
        }

        return this.delegateListener.retrieveGroup(groupName);
    }
}
