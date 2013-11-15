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
import org.jasig.portlet.proxy.service.web.interceptor.IPreInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("EDIT")
public class GatewayPortletEditController extends BaseGatewayPortletController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name="gatewayEntries")
    private List<GatewayEntry> gatewayEntries;

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
    private IViewSelector viewSelector;

    @RenderMapping
    public ModelAndView getView(RenderRequest request, @RequestParam(required=false) String entryName) {

        final ModelAndView mv = new ModelAndView();
        final PortletPreferences prefs = request.getPreferences();

        GatewayEntry entry = entryName != null ? getEntryByName(entryName) : null;
        if (entry == null && gatewayEntries.size() != 0) {
            // For whatever reason, we didn't match with a specified name;  choose the first
            entry = gatewayEntries.get(0);
        }
        if (entry == null) {
            // This situation is more than we can handle
            throw new RuntimeException("No GatewayEntry objects are defined");
        }
        mv.addObject("gatewayEntry", entry);

        // Look for any user-specified preference holders that are present in 
        // the chosen gatewayEntry object.  Store them so that they can be edited.
        TreeMap<String,GatewayPreference> gatewayPreferences = 
                new TreeMap<String,GatewayPreference>(getPreferencesForEntry(entry, prefs));

        // Use descendingMap so username is listed before password
        mv.addObject("gatewayPreferences", gatewayPreferences.descendingMap());

        final String view = viewSelector.isMobile(request) ? mobileViewName : viewName;
        mv.setView(view);
        return mv;
    }

    @ActionMapping(params = {"action=savePreferences"})
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

    @ActionMapping(params = {"action=clearPreferences"})
    public void clearPreferences(ActionRequest request, ActionResponse response, @RequestParam("entry") String entryName) throws Exception {
        PortletPreferences prefs = request.getPreferences();
        GatewayEntry entry = getEntryByName(entryName);
        if (entry != null) {
            Map<String,GatewayPreference> preferences = this.getPreferencesForEntry(entry, prefs);
            for (GatewayPreference p : preferences.values()) {
                prefs.reset(p.getPreferenceName());
            }
            prefs.store();
        }
        response.setPortletMode(PortletMode.VIEW);
    }

    /**
     * @return The {@link GatwayEntry} with the specified name or null
     */
    private GatewayEntry getEntryByName(final String entryName) {

        // Assertions
        if (entryName == null) {
            String msg = "Parameter 'entryName' cannot be null";
            throw new IllegalArgumentException(msg);
        }

        // Find the correct entry... (TODO load into a Map at bootstrap time)
        GatewayEntry rslt = null;
        for (GatewayEntry y: gatewayEntries) {
            if (y.getName().equalsIgnoreCase(entryName)) {
                rslt = y;
                break;
            }
        }

        return rslt;

    }

    private Map<String,GatewayPreference> getPreferencesForEntry(final GatewayEntry entry, final PortletPreferences prefs) {

        Map<String,GatewayPreference> rslt = new HashMap<String,GatewayPreference>();

        for (Map.Entry<HttpContentRequestImpl, List<IPreInterceptor>> requestEntry : entry.getContentRequests().entrySet()){
            final HttpContentRequestImpl contentRequest = requestEntry.getKey();
            Map<String, IFormField> parameters = contentRequest.getParameters();
            for (String logicalFieldName : parameters.keySet()) {
                IFormField parameter = parameters.get(logicalFieldName);
                for (String parameterValue : parameter.getValues()) {
                    if (parameterValue.matches(preferencesRegex)) {
                        String preferenceName = parameterValue;

                        // If there are multiple entries with the same preference name, just use the first one
                        if (!rslt.containsKey(preferenceName)) {
                            // retrieve the preference and stuff the value here....
                            String preferredValue = prefs.getValue(preferenceName, "");
                            if (parameter.getSecured() && StringUtils.isNotBlank(preferredValue) && stringEncryptionService != null) {
                                preferredValue = stringEncryptionService.decrypt(preferredValue);
                            }
                            rslt.put(preferenceName,
                                    new GatewayPreference(entry.getName(), logicalFieldName,
                                            preferenceName, preferredValue, parameter.getSecured()));
                        }
                    }
                }
            }
        }

        return rslt;

    }

    /**
     * getPortletPreferenceformField() returns the IFormField from gatewayEntries() where the value
     * matches the requested fieldName.
     * @param fieldName the name of the field being searched for.
     * @see IFormField
     */
    private IFormField getPortletPreferenceFormField(String fieldName) {
        IFormField formField = null;
        for (GatewayEntry entry: gatewayEntries) {
            for (Map.Entry<HttpContentRequestImpl, List<IPreInterceptor>> requestEntry : entry.getContentRequests().entrySet()){
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
