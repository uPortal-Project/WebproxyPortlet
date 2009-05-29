package edu.wisc.my.webproxy.beans.http;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.springframework.context.ApplicationContext;

import edu.wisc.my.webproxy.beans.PortletPreferencesWrapper;
import edu.wisc.my.webproxy.beans.config.ConfigUtils;
import edu.wisc.my.webproxy.beans.config.HttpClientConfigImpl;
import edu.wisc.my.webproxy.portlet.ApplicationContextLocator;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;
import edu.wisc.my.webproxy.servlet.ProxyServlet;

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
	
	private Log log = LogFactory.getLog(HttpManagerService.class);

	private IWebProxyStateDao webProxyStateDao;
	
	public void setWebProxyStateDao(IWebProxyStateDao webProxyStateDao) {
		this.webProxyStateDao = webProxyStateDao;
	}

	/**
	 * Find an HttpManager for the current portlet request.
	 * 
	 * @param request
	 * @return
	 */
	public HttpManager findManager(PortletRequest request) {
        ApplicationContext context = ApplicationContextLocator.getApplicationContext();
        final PortletSession session = request.getPortletSession();
        final PortletPreferences prefs = new PortletPreferencesWrapper(request.getPreferences(), (Map)request.getAttribute(PortletRequest.USER_INFO));
        final String sharedStateKey = ConfigUtils.checkEmptyNullString(prefs.getValue(HttpClientConfigImpl.SHARED_SESSION_KEY, null), null);

        // attempt to find the manager in the current user session
        HttpManager httpManager;
        if (sharedStateKey != null)
            httpManager = (HttpManager)session.getAttribute(sharedStateKey, PortletSession.APPLICATION_SCOPE);
        else
            httpManager = (HttpManager)session.getAttribute(WebproxyConstants.CURRENT_STATE);

        // if no manager could be found in the session, create a new one
        if (httpManager == null) {
            httpManager = (HttpManager)context.getBean("HttpManagerBean", HttpManager.class);
            httpManager.setup(request);
        }
        
        // if session persistence is enabled, attempt to get any persisted cookies
        // from the store
        final boolean sessionPersistenceEnabled = new Boolean(prefs.getValue(HttpClientConfigImpl.SESSION_PERSISTENCE_ENABLE, null)).booleanValue();
        if (sessionPersistenceEnabled) {
        	// get state key
        	final String namespace = (String)session.getAttribute(WebproxyConstants.NAMESPACE);
        	final String stateKey;
        	if (sharedStateKey != null)
        	    stateKey = generateStateKey(sharedStateKey, namespace);
        	else
        	stateKey = generateStateKey(WebproxyConstants.CURRENT_STATE, namespace);

        	IWebProxyState state = webProxyStateDao.getState(stateKey);
        	if (state != null) {
        		for (ICookie cookie : state.getCookies()) {
            		BasicClientCookie c = new BasicClientCookie(cookie.getName(), cookie.getValue());
            		c.setComment(cookie.getComment());
            		c.setDomain(cookie.getDomain());
            		c.setExpiryDate(cookie.getExpiryDate());
            		c.setPath(cookie.getPath());
            		c.setSecure(cookie.isSecure());
            		c.setVersion(cookie.getVersion());
        			httpManager.addCookie(c);
        		}
        	}
        }

        // return the manager
    	return httpManager;
	}
	
	/**
	 * Save an HttpManager state.
	 * 
	 * @param request
	 * @param httpManager
	 */
    public void saveHttpManager(PortletRequest request, HttpManager httpManager) {
        final PortletSession session = request.getPortletSession();
        final PortletPreferences prefs = new PortletPreferencesWrapper(request.getPreferences(), (Map)request.getAttribute(PortletRequest.USER_INFO));

        // save the current http manager to the user's session
        final String sharedStateKey = ConfigUtils.checkEmptyNullString(prefs.getValue(HttpClientConfigImpl.SHARED_SESSION_KEY, null), null);
        if (sharedStateKey != null)
            session.setAttribute(sharedStateKey, httpManager, PortletSession.APPLICATION_SCOPE);
        else
            session.setAttribute(WebproxyConstants.CURRENT_STATE, httpManager);

        // if session persistence is enabled, save any cookies currently in
        // the manager to the store
        final boolean sessionPersistenceEnabled = new Boolean(prefs.getValue(HttpClientConfigImpl.SESSION_PERSISTENCE_ENABLE, null)).booleanValue();
        if (sessionPersistenceEnabled) {
        	final String namespace = (String)session.getAttribute(WebproxyConstants.NAMESPACE);
        	final String stateKey;
        	if (sharedStateKey != null)
        	    stateKey = generateStateKey(sharedStateKey, namespace);
        	else
        	stateKey = generateStateKey(WebproxyConstants.CURRENT_STATE, namespace);
        	
        	IWebProxyState state = new WebProxyStateImpl(stateKey);
        	Set<ICookie> cookies = new HashSet<ICookie>();
        	Date now = new Date();
        	for (Cookie cookie : httpManager.getCookies()) {
        		if (!cookie.isExpired(now)) {
            		PersistedCookieImpl c = new PersistedCookieImpl();
            		c.setComment(cookie.getComment());
            		c.setDomain(cookie.getDomain());
            		c.setExpiryDate(cookie.getExpiryDate());
            		c.setName(cookie.getName());
            		c.setPath(cookie.getPath());
            		c.setSecure(cookie.isSecure());
            		c.setValue(cookie.getValue());
            		c.setVersion(cookie.getVersion());
            		cookies.add(c);
        		}
        	}
        	state.setCookies(cookies);
            webProxyStateDao.saveState(state);
        }
    }
	
    /**
     * Find an HttpManager for a servlet request.
     * 
     * @param request
     * @param prefs
     * @param session
     * @return
     */
	public HttpManager findManager(HttpServletRequest request, PortletPreferences prefs, HttpSession session) {
        final String sharedStateKey = ConfigUtils.checkEmptyNullString(prefs.getValue(HttpClientConfigImpl.SHARED_SESSION_KEY, null), null);
        final String prefix = request.getParameter(ProxyServlet.NAMESPACE_PREFIX_PARAM);
        final String sufix = request.getParameter(ProxyServlet.NAMESPACE_SUFIX_PARAM);

        // attempt to find the manager in the current user session
        HttpManager httpManager;
        if (sharedStateKey != null)
            httpManager = (HttpManager)session.getAttribute(sharedStateKey);
        else
        	httpManager = (HttpManager)session.getAttribute(prefix + WebproxyConstants.CURRENT_STATE + sufix);

        // if no manager could be found in the session, throw an error
        if (httpManager == null) {
            IllegalStateException ise = new IllegalStateException("No HttpManager found in the current session");
            log.error(ise, ise);
            throw ise;
        }
        
    	return httpManager;
	}

	/**
	 * Generate a state key, given a key and a namespace.
	 * 
	 * @param key
	 * @param namespace
	 * @return
	 */
    protected static String generateStateKey(String key, String namespace) {
        final StringBuffer cacheKeyBuf = new StringBuffer();
        
        cacheKeyBuf.append(namespace);
        cacheKeyBuf.append(".");
        cacheKeyBuf.append(key);

        return cacheKeyBuf.toString();
    }

}
