package edu.wisc.my.webproxy.beans.http;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.context.ApplicationContext;
import org.springframework.web.portlet.util.PortletUtils;

import edu.wisc.my.webproxy.beans.PortletPreferencesWrapper;
import edu.wisc.my.webproxy.beans.config.ConfigUtils;
import edu.wisc.my.webproxy.beans.config.HttpClientConfigImpl;
import edu.wisc.my.webproxy.portlet.ApplicationContextLocator;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * HttpManagerFindingService is responsible for retrieving and saving 
 * HttpManager instances for a particular user and request.  These HttpManagers 
 * may be retrieved from the user session and potentially configured from 
 * state information persisted to a back-end store.  This class also contains
 * methods for persisting a current HttpManager, either to the session or the
 * store. 
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
public class HttpManagerService {
    private final static String COOKIES = HttpManagerService.class.getName() + ".COOKIES"; 
    
    protected final Log logger = LogFactory.getLog(this.getClass());
	private IWebProxyStateDao webProxyStateDao;
	private IKeyManager keyManager;
	
	public void setWebProxyStateDao(IWebProxyStateDao webProxyStateDao) {
		this.webProxyStateDao = webProxyStateDao;
	}
	public void setKeyManager(IKeyManager keyManager) {
        this.keyManager = keyManager;
    }


    /**
	 * Find an HttpManager for the current portlet request.
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public HttpManager findManager(PortletRequest request) {
        ApplicationContext context = ApplicationContextLocator.getApplicationContext();
        final PortletSession session = request.getPortletSession();
        final PortletPreferences prefs = new PortletPreferencesWrapper(request.getPreferences(), (Map<?, ?>)request.getAttribute(PortletRequest.USER_INFO));
        final String sharedStateKey = ConfigUtils.checkEmptyNullString(prefs.getValue(HttpClientConfigImpl.SHARED_SESSION_KEY, null), null);

        
        synchronized (PortletUtils.getSessionMutex(session)) {
            // attempt to find the manager in the current user session
            HttpManager httpManager;
            if (sharedStateKey != null) {
                httpManager = (HttpManager)session.getAttribute(sharedStateKey, PortletSession.APPLICATION_SCOPE);
            }
            else {
                httpManager = (HttpManager)session.getAttribute(WebproxyConstants.CURRENT_STATE);
            }
    
            // if no manager could be found in the session, create a new one
            if (httpManager == null) {
                httpManager = (HttpManager)context.getBean("HttpManagerBean", HttpManager.class);
                if (sharedStateKey != null) {
                    session.setAttribute(sharedStateKey, httpManager, PortletSession.APPLICATION_SCOPE);
                }
                else {
                    session.setAttribute(WebproxyConstants.CURRENT_STATE, httpManager);
                }
                
                httpManager.setup(request);
            
                // if session persistence is enabled, attempt to get any persisted cookies
                // from the store
                final boolean sessionPersistenceEnabled = new Boolean(prefs.getValue(HttpClientConfigImpl.SESSION_PERSISTENCE_ENABLE, null)).booleanValue();
                if (sessionPersistenceEnabled) {
                	// get state key
                	final String stateKey;
                	if (sharedStateKey != null)
                	    stateKey = this.keyManager.generateStateKey(sharedStateKey, request);
                	else
                	    stateKey = this.keyManager.generateStateKey(WebproxyConstants.CURRENT_STATE, request);
        
                	final IWebProxyState state = webProxyStateDao.getState(stateKey);
                	if (state != null) {
                	    final ReadWriteLock cookieLock = httpManager.getCookieLock();
                        final Lock readLock = cookieLock.readLock();
                        readLock.lock();
                	    try {
                    		for (ICookie cookie : state.getCookies()) {
                    		    if (cookie == null) {
                    		        continue;
                    		    }
                    		    
                    		    if (this.logger.isDebugEnabled()) {
                                    this.logger.debug("Loaded persistent coookie: " + cookie);
                                }
                    		    
                        		final BasicClientCookie c = new BasicClientCookie(cookie.getName(), cookie.getValue());
                        		c.setComment(cookie.getComment());
                        		c.setDomain(cookie.getDomain());
                        		c.setExpiryDate(cookie.getExpiryDate());
                        		c.setPath(cookie.getPath());
                        		c.setSecure(cookie.isSecure());
                        		c.setVersion(cookie.getVersion());
                        		
                    			httpManager.addCookie(c);
                    		}
                	    }
                	    finally {
                	        readLock.unlock();
                	    }
                	}
                }
            }

            // return the manager
        	return httpManager;
        }
	}
	
	/**
	 * Save an HttpManager state.
	 * 
	 * @param request
	 * @param httpManager
	 */
    public void saveHttpManager(PortletRequest request, HttpManager httpManager) {
        final PortletPreferences prefs = new PortletPreferencesWrapper(request.getPreferences(), (Map<?, ?>)request.getAttribute(PortletRequest.USER_INFO));
        
        // if session persistence is enabled, save any cookies currently in
        // the manager to the store
        final boolean sessionPersistenceEnabled = new Boolean(prefs.getValue(HttpClientConfigImpl.SESSION_PERSISTENCE_ENABLE, null)).booleanValue();
        if (sessionPersistenceEnabled) {
            // save the current http manager to the user's session
            final String sharedStateKey = ConfigUtils.checkEmptyNullString(prefs.getValue(HttpClientConfigImpl.SHARED_SESSION_KEY, null), null);
            
        	final String stateKey;
        	if (sharedStateKey != null) {
        	    stateKey = this.keyManager.generateStateKey(sharedStateKey, request);
        	}
        	else {
        	    stateKey = this.keyManager.generateStateKey(WebproxyConstants.CURRENT_STATE, request);
        	}
        	
        	final ReadWriteLock cookieLock = httpManager.getCookieLock();
            final Lock writeLock = cookieLock.writeLock();
            writeLock.lock();
        	
        	final IWebProxyState state = this.getOrCreateState(request, stateKey);
            try {
            	final List<ICookie> cookies = state.getCookies();
            	
            	final int originalCookieCount = cookies.size();
            	
            	final Map<CookieKey, ICookie> existingCookies = new LinkedHashMap<CookieKey, ICookie>();
            	for (final Iterator<ICookie> cookieItr = cookies.iterator(); cookieItr.hasNext();) {
            	    final ICookie cookie = cookieItr.next();
                    if (cookie == null) {
                        cookieItr.remove();
                        continue;
                    }
                    
            	    existingCookies.put(new CookieKey(cookie.getName(), cookie.getDomain()), cookie);
            	}
            	
            	final Date now = new Date();
            	for (final Cookie cookie : httpManager.getCookies()) {
            		if (!cookie.isExpired(now) && cookie.isPersistent()) {
            		    final String name = cookie.getName();
                        final String domain = cookie.getDomain();
                        final CookieKey key = new CookieKey(name, domain);
            		    
            		    final ICookie existingCookie = existingCookies.remove(key);
                        if (existingCookie != null) {
                            existingCookie.setComment(cookie.getComment());
                            existingCookie.setExpiryDate(cookie.getExpiryDate());
                            existingCookie.setPath(cookie.getPath());
                            existingCookie.setSecure(cookie.isSecure());
                            existingCookie.setValue(cookie.getValue());
                            existingCookie.setVersion(cookie.getVersion());
                            
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("Updated existing persistent cookie: " + existingCookie);
                            }
                        }
                        else {
                            final ICookie c = new PersistedCookieImpl(name, domain);
                            c.setComment(cookie.getComment());
                            c.setExpiryDate(cookie.getExpiryDate());
                            c.setPath(cookie.getPath());
                            c.setSecure(cookie.isSecure());
                            c.setValue(cookie.getValue());
                            c.setVersion(cookie.getVersion());
                            
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("Created new persistent cookie: " + c);
                            }
                            
                            cookies.add(c);
                        }
            		}
            	}

            	//remove cookies that weren't listed in the HttpManager
            	cookies.removeAll(existingCookies.values());
            	
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Storing cookie changes old cookie count: " + originalCookieCount + ", current cookie count: " + cookies.size());
                }

                state.setCookies(cookies);
                webProxyStateDao.saveState(state);
        	}
            finally {
                writeLock.unlock();
            }
        }
    }

    protected IWebProxyState getOrCreateState(final PortletRequest request, final String stateKey) {
        final PortletSession portletSession = request.getPortletSession();
        synchronized (PortletUtils.getSessionMutex(portletSession)) {
        	IWebProxyState state = webProxyStateDao.getState(stateKey);
        	if (state == null) {
        	    state = new WebProxyStateImpl(stateKey);
                webProxyStateDao.saveState(state);
        	}
        	
        	return state;
        }
    }
}
