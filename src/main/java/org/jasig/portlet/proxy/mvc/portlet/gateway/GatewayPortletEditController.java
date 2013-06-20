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
package org.jasig.portlet.proxy.mvc.portlet.gateway;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.proxy.mvc.IViewSelector;
import org.jasig.portlet.proxy.security.IStringEncryptionService;
import org.jasig.portlet.proxy.security.StringEncryptionException;
import org.jasig.portlet.proxy.service.IFormField;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("EDIT")
public class GatewayPortletEditController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String preferencesRegex;
    
    @Value("${login.preferences.regex}")
    public void setPreferencesRegex(String preferencesRegex) {
    	this.preferencesRegex = preferencesRegex;
    }
    
    private IStringEncryptionService stringEncryptionService;
    
    @Autowired(required=false)
    public void setStringEncryptionService(IStringEncryptionService stringEncryptionService) {
    	this.stringEncryptionService = stringEncryptionService;
    }
    
    @Autowired(required=false)
    private String viewName = "gatewayEdit";
    
    @Autowired(required=false)
    private String mobileViewName = "mobileGatewayEdit";
    
    @Autowired(required=true)
    private ApplicationContext applicationContext;
    
    @Autowired(required=true)
    private IViewSelector viewSelector;

    @RequestMapping
    public ModelAndView getView(RenderRequest request){
    	
        PortletPreferences prefs = request.getPreferences();

        final ModelAndView mv = new ModelAndView();
        final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
        
        // Look for any user-specified preference holders that are present in any of the gatewayEntry objects.
        // Store them in a list so that they can be edited.
        TreeMap<String,GatewayPreference> gatewayPreferences = new TreeMap<String,GatewayPreference>();
        for (GatewayEntry entry: entries) {
            for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
                final HttpContentRequestImpl contentRequest = requestEntry.getKey();
                Map<String, IFormField> parameters = contentRequest.getParameters();
                for (String logicalFieldName : parameters.keySet()) {
                	IFormField parameter = parameters.get(logicalFieldName);
                    for (String parameterValue : parameter.getValues()) {
                        if (parameterValue.matches(preferencesRegex)) {
                            String preferenceName = parameterValue;

                            // If there are multiple entries with the same preference name, just use the first one
                            if (gatewayPreferences.get(preferenceName) == null) {
                                // retrieve the preference and stuff the value here....
                                String preferredValue = prefs.getValue(preferenceName, "");
                                if (parameter.getSecured() && StringUtils.isNotBlank(preferredValue) && stringEncryptionService != null) {
                                    preferredValue = stringEncryptionService.decrypt(preferredValue);
                                }
                                gatewayPreferences.put(preferenceName,
                                        new GatewayPreference(entry.getName(), logicalFieldName,
                                                preferenceName, preferredValue, parameter.getSecured()));
                            }
                        }
                    }
                }
            }
        }

        // Use descendingMap so username is listed before password
        mv.addObject("gatewayPreferences", gatewayPreferences.descendingMap());

        final String view = viewSelector.isMobile(request) ? mobileViewName : viewName;
        mv.setView(view);
        return mv;
    }
    
    @RequestMapping(params = {"action=savePreferences"})
    public void savePreferences(ActionRequest request, ActionResponse response) throws Exception {
        PortletPreferences prefs = request.getPreferences();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.matches(preferencesRegex)) {
                String parameterValue = request.getParameter(parameterName);
                IFormField parameter = getPortletPreferenceFormField(parameterName);
                if (parameter != null && parameter.getSecured() && StringUtils.isNotBlank(parameterValue)) {
                    // If stringEncryptionService is not specified, throw an exception.  Do NOT allow storing
                    // sensitive information in an unencrypted format.
                    if (stringEncryptionService == null) {
                        throw new StringEncryptionException("String encryption service must be configured!");
                    }
                    parameterValue = stringEncryptionService.encrypt(parameterValue);
                }
                prefs.setValue(parameterName, parameterValue);
            }
        }
        prefs.store();
        response.setPortletMode(PortletMode.VIEW);
    }
    
    /**
     * getPortletPreferenceformField() returns the IFormField from gatewayEntries() where the value
     * matches the requested fieldName.  The fieldName will match the property format
     * specified by login.preferences.regex
     * @param fieldName the name of the field being searched for.
     * @see IFormField
     */
    private IFormField getPortletPreferenceFormField(String fieldName) {
    	IFormField formField = null;
    	final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
        for (GatewayEntry entry: entries) {
            for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
                final HttpContentRequestImpl contentRequest = requestEntry.getKey();
                Map<String, IFormField> parameters = contentRequest.getParameters();
                for (String parameterNames: parameters.keySet()) {
                	IFormField parameter = parameters.get(parameterNames);
                	if (parameter.getValue().equals(fieldName)) {
                		formField = parameter;
                    	break;
                	}
                }
            }
            if (formField != null) {
            	break;
            }
        }
        return formField;
    }
}
