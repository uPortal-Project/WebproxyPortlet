/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.webproxy.beans.cache.oscache;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.CachePersistenceException;
import com.opensymphony.oscache.base.persistence.PersistenceListener;

import edu.wisc.my.webproxy.portlet.ApplicationContextLocator;

/**
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public class SpringLocatingPersistenceListener implements PersistenceListener {
    public static final String BEAN_NAME_PROP = "cache.persistence.spring.beanName";
    
    private static final Set initializedBeans = new HashSet();
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String beanName;
    private Config config;
    
    public SpringLocatingPersistenceListener() {
        this(null);
    }
    
    public SpringLocatingPersistenceListener(String beanName) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("SpringLocatingPersistenceListener(" + beanName + ")");
        }
        
        this.beanName = beanName;
    }
    
    
    /**
     * @return The name of the property to use for loading the delegate class. Called before {@link #configureInternal(Config)} is called.
     */
    protected String getBeanNamePropertyName() {
        return BEAN_NAME_PROP;
    }
    
    
    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#configure(com.opensymphony.oscache.base.Config)
     */
    public final PersistenceListener configure(Config config) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("configure(" + config.getProperties() + ")");
        }
        
        this.loadDelegateBeanName(config);
        
        this.configureInternal(config);
        
        this.config = config;
        
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
    private void loadDelegateBeanName(Config config) {
        final String delegateClassProperty = this.getBeanNamePropertyName();
        
        this.beanName = config.getProperty(delegateClassProperty);
    }

    


    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isStored(java.lang.String)
     */
    public boolean isStored(String key) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("isStored(" + key + ")");
        }
        
        return this.getSpringConfiguredListener().isStored(key);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#isGroupStored(java.lang.String)
     */
    public boolean isGroupStored(String groupName) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("isGroupStored(" + groupName + ")");
        }
        
        return this.getSpringConfiguredListener().isGroupStored(groupName);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#clear()
     */
    public void clear() throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("clear()");
        }
        
        this.getSpringConfiguredListener().clear();
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#remove(java.lang.String)
     */
    public void remove(String key) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("remove(" + key + ")");
        }

        this.getSpringConfiguredListener().remove(key);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#removeGroup(java.lang.String)
     */
    public void removeGroup(String groupName) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("removeGroup(" + groupName + ")");
        }

        this.getSpringConfiguredListener().removeGroup(groupName);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieve(java.lang.String)
     */
    public Object retrieve(String key) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("retrieve(" + key + ")");
        }

        return this.getSpringConfiguredListener().retrieve(key);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#store(java.lang.String, java.lang.Object)
     */
    public void store(String key, Object obj) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("store(" + key + ", " + obj + ")");
        }

        this.getSpringConfiguredListener().store(key, obj);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#storeGroup(java.lang.String, java.util.Set)
     */
    public void storeGroup(String groupName, Set group) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("storeGroup(" + groupName + ", " + group + ")");
        }

        this.getSpringConfiguredListener().storeGroup(groupName, group);
    }

    /**
     * @see com.opensymphony.oscache.base.persistence.PersistenceListener#retrieveGroup(java.lang.String)
     */
    public Set retrieveGroup(String groupName) throws CachePersistenceException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("retrieveGroup(" + groupName + ")");
        }

        return this.getSpringConfiguredListener().retrieveGroup(groupName);
    }
    
    
    
    
    
    private PersistenceListener getSpringConfiguredListener() {
        final ApplicationContext context = ApplicationContextLocator.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Cannot retrieve ApplicationContext from ApplicationContextLocator. The SpringLocatingPersistenceListener cannot work without access to the ApplicationContext");
        }
        
        final PersistenceListener listener = (PersistenceListener)context.getBean(this.beanName, PersistenceListener.class);
        if (listener == null) {
            throw new NoSuchBeanDefinitionException(this.beanName, "Cannot retrieve a PersistenceListener with bean name '" + this.beanName + "' from the ApplicationContext. The SpringLocatingPersistenceListener cannot work without a PersistenceListener");
        }
        
        synchronized (initializedBeans) {
            if (!initializedBeans.contains(this.beanName)) {
                listener.configure(this.config);
                initializedBeans.add(this.beanName);
            }
        }
        
        return listener;
    }
}
