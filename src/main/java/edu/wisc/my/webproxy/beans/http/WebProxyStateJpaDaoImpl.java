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

package edu.wisc.my.webproxy.beans.http;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * WebProxyStateJpaDaoImpl represents the default JPA implementation of 
 * IWebProxyStateDao.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
public class WebProxyStateJpaDaoImpl implements IWebProxyStateDao {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private static final String FIND_STATE_BY_KEY = 
        "from WebProxyStateImpl state where state.stateKey = :stateKey";
    
    private static final String DELETE_EXPIRED_COOKIES =
        "DELETE FROM PersistedCookieImpl cookie WHERE cookie.expiryDate < :now";
	
    private EntityManager entityManager;
    
    /**
     * @param entityManager the entityManager to set
     */
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    
    /*
     * (non-Javadoc)
     * @see edu.wisc.my.webproxy.beans.http.IWebProxyStateDao#getState(java.lang.Long)
     */
	@SuppressWarnings("unchecked")
    public IWebProxyState getState(String stateKey) {
        final Query query = this.entityManager.createQuery(FIND_STATE_BY_KEY);
        query.setParameter("stateKey", stateKey);
        query.setHint("org.hibernate.cacheable", true);
        query.setHint("org.hibernate.cacheRegion", WebProxyStateImpl.class.getName() + ".query.FIND_STATE_BY_KEY");
        query.setMaxResults(1);
        
        final List<IWebProxyState> states = query.getResultList();
        IWebProxyState state = (IWebProxyState) DataAccessUtils.uniqueResult(states);
        return state;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.IWebProxyStateDao#saveState(edu.wisc.my.webproxy.beans.http.IWebProxyState)
	 */
	@Transactional
	public IWebProxyState saveState(IWebProxyState state) {
		entityManager.persist(state);
		
		return state;
	}

    @Transactional
	public void purgeExpiredCookies() {
        final Query query = this.entityManager.createQuery(DELETE_EXPIRED_COOKIES);
        query.setParameter("now", new Date());
        final int results = query.executeUpdate();
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Deleted " + results + " expired cookies");
        }
	}
}
