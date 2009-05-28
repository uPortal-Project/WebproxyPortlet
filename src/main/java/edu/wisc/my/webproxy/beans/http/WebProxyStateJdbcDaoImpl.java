package edu.wisc.my.webproxy.beans.http;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

/**
 * WebProxyStateJdbcDaoImpl represents the default JPA implementation of 
 * IWebProxyStateDao.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
public class WebProxyStateJdbcDaoImpl implements IWebProxyStateDao {
	
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
	public IWebProxyState getState(String id) {
		return entityManager.find(WebProxyStateImpl.class, id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.IWebProxyStateDao#saveState(edu.wisc.my.webproxy.beans.http.IWebProxyState)
	 */
	@Transactional
	public IWebProxyState saveState(IWebProxyState state) {
		return entityManager.merge(state);
	}

}
