package edu.wisc.my.webproxy.beans.http;


/**
 * IWebProxyStateDao provides an interface for persisting and retrieving
 * web proxy state information.  This state may include information such as
 * any cookies.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
public interface IWebProxyStateDao {
	
	/**
	 * Retrieve an IWebProxyState from the store by its unique String key.  If
	 * no state currently exists for the given key, the method will return
	 * <code>null</code>
	 * 
	 * @param id
	 * @return
	 */
	public IWebProxyState getState(String id);
	
	/**
	 * Save an IWebProxyState to the store.
	 * 
	 * @param state
	 * @return
	 */
	public IWebProxyState saveState(IWebProxyState state);

    /**
     * Purge expired cookies
     */
    public void purgeExpiredCookies();
}
