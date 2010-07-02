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
