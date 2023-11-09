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
package org.jasig.portlet.proxy.service.web.interceptor;

import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.proxy.security.IStringEncryptionService;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>UserPreferencesPreInterceptor class.</p>
 *
 * @author bjagg
 * @version $Id: $Id
 */
@Service("UserPreferencesPreInterceptor")
@Slf4j
public class UserPreferencesPreInterceptor implements IPreInterceptor {

    private String preferencesRegex;
    
    /**
     * <p>Setter for the field <code>preferencesRegex</code>.</p>
     *
     * @param preferencesRegex a {@link java.lang.String} object
     */
    @Value("${login.preferences.regex}")
    public void setPreferencesRegex(String preferencesRegex) {
        this.preferencesRegex = preferencesRegex;
    }
    
    @Autowired(required=false)
    private IStringEncryptionService stringEncryptionService;
    

	/** {@inheritDoc} */
	@Override
	public void intercept(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {
		
		// replace the portlet preference fields with user specific entries
		PortletPreferences prefs = portletRequest.getPreferences();
		
		Map<String, IFormField> parameters = proxyRequest.getParameters();
		for (String parameterKey: parameters.keySet()) {
			IFormField parameter = parameters.get(parameterKey);
			String[] parameterValues = parameter.getValues();
			for (int i = 0; i < parameterValues.length; i++) {
				String parameterValue = parameterValues[i];
				if (parameterValue.matches(preferencesRegex)) {
					String preferredValue = prefs.getValue(parameterValue, parameterValue);
					if (parameter.getSecured() && StringUtils.isNotBlank(preferredValue) && stringEncryptionService != null) {
						log.debug("decrypting preferredValue '" + preferredValue + "' for parameterKey: '" + parameterKey);
						preferredValue = stringEncryptionService.decrypt(preferredValue);
					}
					parameterValues[i] = preferredValue;
				}
		    }
		}
	}


	/**
	 * {@inheritDoc}
	 *
	 * validate() checks portlet preferences and confirms that all of the needed
	 * preferences have been set.  The preferences could be set incorrectly, which
	 * will not be detected until the gateway entry is tried.  This simply validates
	 * that the preferences have been created and saved by the user.
	 */
	@Override
	public boolean validate(HttpContentRequestImpl proxyRequest, PortletRequest portletRequest) {
		boolean allPreferencesSet = true;
		PortletPreferences prefs = portletRequest.getPreferences();

		for (Map.Entry<String, IFormField> entry : proxyRequest.getParameters().entrySet()) {
			IFormField parameter = entry.getValue();
			String[] parameterValues = parameter.getValues();
			for (String parameterValue : parameterValues) {
				if (parameterValue.matches(preferencesRegex)) {
					
					// look for the value for all portletPreferences fields
					// if it doesn't find a preference for that field, value has not been set.
					String preferredValue = prefs.getValue(parameterValue, null);
					if (StringUtils.isBlank(preferredValue)) {
						allPreferencesSet = false;
                        break;
					}
				}
		    }
		}
		return allPreferencesSet;
	}
}
