/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.wisc.my.webproxy.beans.http;

import java.util.List;

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
	 * Retrieve a list of cookies associated with this state.
	 * 
	 * @return
	 */
	public List<ICookie> getCookies();
	
	/**
	 * Set the list of cookies associated with this state.
	 * 
	 * @param cookies
	 */
	public void setCookies(List<ICookie> cookies);

}
