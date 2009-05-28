package edu.wisc.my.webproxy.beans.http;

import java.util.Collection;

/**
 * IWebProxyState represents an interface representing the state of a user's
 * web proxy browsing.  This state may potentially be shared between two portlets
 * but should be specific to a particular user.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
public interface IWebProxyState {
	
	/**
	 * Get the unique key associated with this state.
	 * 
	 * @return
	 */
	public String getStateKey();
	
	/**
	 * Set the unique key associated with this state.
	 * 
	 * @param stateKey
	 */
	public void setStateKey(String stateKey);
	
	/**
	 * Retrieve a list of cookies associated with this state.
	 * 
	 * @return
	 */
	public Collection<ICookie> getCookies();
	
	/**
	 * Set the list of cookies associated with this state.
	 * 
	 * @param cookies
	 */
	public void setCookies(Collection<ICookie> cookies);

}
