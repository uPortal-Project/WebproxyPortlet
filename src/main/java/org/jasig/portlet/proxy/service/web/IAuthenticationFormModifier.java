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

package org.jasig.portlet.proxy.service.web;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.portlet.PortletPreferences;

/**
 * IAuthenticationFormModifier is the interface for a bean that encapsulates logic that must be executed in order
 * to populate additional fields, which will be returned by the GatewayPortletController.
 * The GatewayPortletController actionRequest processor returns a list of values that will
 * be included in a form to submit to an external system.  If those values require complex
 * business logic, that business logic can be developed externally and called during the
 * controller execution.  Any class that implements this interface will be able to define any
 * needed logic to build that field to be passed to that form.
 *
 * Special form field names:
 * - proxiedLocation: if present the form should be posted to this URL
 *
 * @author mgillian
 * @see org.jasig.portlet.proxy.mvc.portlet.gateway.GatewayPortletController#showTarget(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse, int)
 *
 */
public interface IAuthenticationFormModifier {

    public static final String PROXY_LOCATION_OVERRIDE = "proxiedLocation";
	
	/**
	 * getResult() returns the value for the field name
	 * @param preferences PortletPreferences that may be useful when calculating the return result
	 * @return the value of the field being returned
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String getResult(PortletPreferences preferences) throws MalformedURLException, IOException;
	
	/**
	 * Returns the form field name.  The form field "proxiedLocation" is a special field name that modifies the
     * form's action URL.
	 * @return the name of the field being calculated and returned.
	 */
	public String getFieldName();

}
