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

package org.jasig.portlet.cas;

import javax.portlet.PortletRequest;

import org.jasig.cas.client.validation.Assertion;

/**
 * IProxyTicketService provides an interface for procuring proxy tickets.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
public interface ICASProxyTicketService {

	/**
	 * Retrieve a CAS receipt for the specified portlet request.
	 * 
	 * @param request
	 * @param ticket
	 * @return
	 */
	public Assertion getProxyTicket(PortletRequest request);
	
	
	/**
	 * Return a proxy ticket for a CAS receipt and URL target.
	 * 
	 * @param receipt CAS receipt for the current user
	 * @param target URL of the service to be proxied
	 * @return
	 */
	public String getCasServiceToken(Assertion assertion, String target);

}
