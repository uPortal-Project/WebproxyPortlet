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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletPreferences;
import java.io.IOException;
import java.net.URL;

public class ExampleCustomFormModifier implements IAuthenticationFormModifier {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String fieldName;

	public ExampleCustomFormModifier() {
	}

    /**
     * Example of modifications to a login form that invokes the system to gateway to in order to obtain a token
     * from that system that must be submitted with the login form and inserts the token into the url to submit.
     * Also demonstrates that you can custom-calculate the url to submit to by having the fieldname of proxiedLocation
     * (per the configuration in gateway-sso-portlet.xml).
     * @param preferences PortletPreferences that may be useful when calculating the return result
     * @return value of the form field
     * @throws IOException
     */
	@Override
	public String getResult(PortletPreferences preferences) throws IOException {

	      String urlSource ="https://prod.sdbor.edu/WebAdvisor/webadvisor?&TYPE=M&PID=CORE-WBMAIN&TOKENIDX="; //must be secure
	      URL xmlURLToOpen = new URL(urlSource);

	      String headerInfo = xmlURLToOpen.openConnection().getHeaderFields().toString();
	      int headerInfoBegin = headerInfo.indexOf("LASTTOKEN=");
	      String tokenID = headerInfo.substring(headerInfoBegin+10,headerInfoBegin+20);
	      tokenID = tokenID.replaceAll("=","");
	      tokenID = tokenID.replaceAll(",","");
	      logger.debug("urlSource: " + urlSource);
	      String formAction = urlSource+tokenID+"&SS=LGRQ&URL=https%3A%2F%2Fprod.sdbor.edu%2FWebAdvisor%2Fwebadvisor%3F%26TYPE%3DM%26PID%3DCORE-WBMAIN%26TOKENIDX%3D"+tokenID;
	      logger.debug("formAction: " + formAction);
		return formAction;
	}
	
	@Override
	public String getFieldName() {
		return this.fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
