package org.jasig.portlet.proxy.service.web.interceptor;

import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.proxy.security.IStringEncryptionService;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("UserPreferencesPreInterceptor")
public class UserPreferencesPreInterceptor implements IPreInterceptor {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String preferencesRegex;
    
    @Value("${login.preferences.regex}")
    public void setPreferencesRegex(String preferencesRegex) {
        this.preferencesRegex = preferencesRegex;
    }
    
    @Autowired(required=false)
    private IStringEncryptionService stringEncryptionService;
    

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
						logger.debug("decrypting preferredValue '" + preferredValue + "' for parameterKey: '" + parameterKey);
						preferredValue = stringEncryptionService.decrypt(preferredValue);
					}
					parameterValues[i] = preferredValue;
				}
		    }
		}
	}


	/**
	 * validate() checks portlet preferences and confirms that all of the needed
	 * preferences have been set.  The preferences could be set incorrectly, which 
	 * will not be detected until the gateway entry is tried.  This simply validates 
	 * that the preferences have been created and saved by the user.
	 * @param proxyRequest 
	 * @param portletRequest 
	 * @return true if all portlet preferences have been set (are not blank), false if all have not been set.
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
