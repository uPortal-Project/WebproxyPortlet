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
package org.jasig.portlet.proxy.mvc.portlet.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.proxy.search.ISearchService;
import org.jasig.portlet.proxy.service.GenericContentRequestImpl;
import org.jasig.portlet.proxy.service.proxy.document.ContentClippingFilter;
import org.jasig.portlet.proxy.service.proxy.document.HeaderFooterFilter;
import org.jasig.portlet.proxy.service.proxy.document.URLRewritingFilter;
import org.jasig.portlet.proxy.service.web.HttpContentServiceImpl;
import org.jasig.portlet.proxy.service.web.interceptor.PortletPreferencesBasicAuthenticationPreInterceptor;
import org.jasig.portlet.proxy.service.web.interceptor.ProxyCASAuthenticationPreInterceptor;
import org.jasig.portlet.proxy.service.web.interceptor.UserInfoBasicAuthenticationPreInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * ProxyConfigController
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Controller
@RequestMapping("CONFIG")
public class EditProxyController {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<String,String> pageCharacterEncodings;

    @Resource(name = "pageCharacterEncodings")
    public void setPageCharacterEncodings(Map<String, String> pageCharacterEncodings) {
        this.pageCharacterEncodings = pageCharacterEncodings;
    }

    @ModelAttribute("pageCharacterEncodings")
    public Map<String,String> getPageCharacterEncodings() {
        return pageCharacterEncodings;
    }

    private ISearchService searchService;
    @Required
    @Resource(name="contentSearchProvider")
    public void setSearchService(ISearchService searchService) {
        this.searchService = searchService;
    }

    @RenderMapping
    public String getEditView(PortletRequest request, Model model) {
        model.addAttribute("form", getForm(request));
        model.addAttribute("strategyNames", searchService.getStrategyNames());
        return "editProxyPortlet";
    }

    @ActionMapping
    public void updatePortlet(@ModelAttribute("form") ProxyPortletForm form, ActionRequest request,
                              ActionResponse response, @RequestParam(value="Save", required=false) String save)
                              throws PortletModeException {
        if (StringUtils.isNotBlank(save)) {
            try {

                final PortletPreferences preferences = request.getPreferences();

                preferences.setValue(GenericContentRequestImpl.CONTENT_LOCATION_PREFERENCE, form.getLocation());
                preferences.setValue(GenericContentRequestImpl.CONTENT_LOCATION_MAXIMIZED_PREFERENCE, form.getMaxLocation());
                preferences.setValue(ProxyPortletController.CONTENT_SERVICE_KEY, form.getContentService());
                preferences.setValue(URLRewritingFilter.WHITELIST_REGEXES_KEY, form.getWhitelistRegexes());
                preferences.setValue(ProxyPortletController.PREF_CHARACTER_ENCODING, form.getPageCharacterEncodingFormat());

                final List<String> filters = new ArrayList<String>();

                // if a clipping selector has been specified, add the content clipping filter
                preferences.setValue(ContentClippingFilter.SELECTOR_KEY, form.getClippingSelector());
                if (StringUtils.isNotBlank(form.getClippingSelector())) {
                    filters.add("contentClippingFilter");
                }

                preferences.setValue(HeaderFooterFilter.HEADER_KEY, form.getHeader());
                preferences.setValue(HeaderFooterFilter.FOOTER_KEY, form.getFooter());
                if (StringUtils.isNotBlank(form.getHeader()) || StringUtils.isNotBlank(form.getFooter())) {
                    filters.add("headerFooterFilter");
                }

                // if the HTTP content service is in use, we need to rewrite the
                // proxied URLs
                if ("httpContentService".equals(form.getContentService())) {
                    filters.add("urlRewritingFilter");
                }

                preferences.setValues(ProxyPortletController.FILTER_LIST_KEY, filters.toArray(new String[]{}));

                final List<String> preInterceptors = new ArrayList<String>();
                preInterceptors.add("userInfoUrlParameterizingPreInterceptor");

                // Authentication
                ProxyPortletForm.ProxyAuthType authType = form.getAuthType();
                switch (authType) {
                    case NONE:
                        // Fair enough;  nothing to do...
                        break;
                    case CAS:
                        preInterceptors.add(ProxyCASAuthenticationPreInterceptor.BEAN_ID);
                        break;
                    case BASIC:
                        preInterceptors.add(UserInfoBasicAuthenticationPreInterceptor.BEAN_ID);
                        break;
                    case BASIC_PORTLET_PREFERENCES:
                        preInterceptors.add(PortletPreferencesBasicAuthenticationPreInterceptor.BEAN_ID);
                        preferences.setValue(PortletPreferencesBasicAuthenticationPreInterceptor.USERNAME_PREFERENCE, form.getBasicAuthPreferencesUsername());
                        preferences.setValue(PortletPreferencesBasicAuthenticationPreInterceptor.PASSWORD_PREFERENCE, form.getBasicAuthPreferencesPassword());
                        break;
                    default:
                        final String msg = "Unrecognized authentication type:  " + authType;
                        throw new IllegalArgumentException(msg);
                }
                if (!ProxyPortletForm.ProxyAuthType.BASIC_PORTLET_PREFERENCES.equals(authType)) {
                    // Clear out any previous data...
                    preferences.reset(PortletPreferencesBasicAuthenticationPreInterceptor.USERNAME_PREFERENCE);
                    preferences.reset(PortletPreferencesBasicAuthenticationPreInterceptor.PASSWORD_PREFERENCE);
                }

                preferences.setValue(ProxyPortletForm.AUTHENTICATION_TYPE, form.getAuthType().toString());
                preferences.setValues(HttpContentServiceImpl.PREINTERCEPTOR_LIST_KEY, preInterceptors.toArray(new String[]{}));

                preferences.setValue("gsaHost", form.getGsaHost());
                preferences.setValue("gsaCollection", form.getGsaCollection());
                preferences.setValue("gsaFrontend", form.getGsaFrontend());
                preferences.setValue("gsaWhitelistRegex", form.getGsaWhitelistRegex());
                preferences.setValue("anchorWhitelistRegex", form.getAnchorWhitelistRegex());

                preferences.setValues("searchStrategies", form.getSearchStrategies());
                preferences.store();

            } catch (Exception e) {
                log.error("Unable to update web proxy portlet configuration", e);
            }
        }
        response.setPortletMode(PortletMode.VIEW);
    }

    // Do not annotate as model attribute.  When viewing the portlet if you access Configure menu, make changes, click
    // Cancel, then access Configure menu again for some reason this method is not invoked and the previous, modified
    // form is displayed which is very confusing because it looks like the form data was saved to portlet preferences
    // but it wasn't.
    public ProxyPortletForm getForm(PortletRequest request) {
        final PortletPreferences preferences = request.getPreferences();
        final ProxyPortletForm form = new ProxyPortletForm();

        form.setContentService(preferences.getValue(ProxyPortletController.CONTENT_SERVICE_KEY, null));
        form.setLocation(preferences.getValue(GenericContentRequestImpl.CONTENT_LOCATION_PREFERENCE, null));
        form.setMaxLocation(preferences.getValue(GenericContentRequestImpl.CONTENT_LOCATION_MAXIMIZED_PREFERENCE, null));
        form.setWhitelistRegexes(preferences.getValue(URLRewritingFilter.WHITELIST_REGEXES_KEY, null));
        form.setPageCharacterEncodingFormat(preferences.getValue(ProxyPortletController.PREF_CHARACTER_ENCODING,
                ProxyPortletController.CHARACTER_ENCODING_DEFAULT));
        form.setClippingSelector(preferences.getValue(ContentClippingFilter.SELECTOR_KEY, null));
        form.setHeader(preferences.getValue(HeaderFooterFilter.HEADER_KEY, null));
        form.setFooter(preferences.getValue(HeaderFooterFilter.FOOTER_KEY, null));

        form.setGsaHost(preferences.getValue("gsaHost", null));
        form.setGsaCollection(preferences.getValue("gsaCollection", null));
        form.setGsaFrontend(preferences.getValue("gsaFrontend",  null));
        form.setGsaWhitelistRegex(preferences.getValue("gsaWhitelistRegex",  null));

        form.setAnchorWhitelistRegex(preferences.getValue("anchorWhitelistRegex", null));
        form.setSearchStrategies(preferences.getValues("searchStrategies", new String[]{}));

        String authTypeForm = preferences.getValue(ProxyPortletForm.AUTHENTICATION_TYPE, null);
        ProxyPortletForm.ProxyAuthType authType = ProxyPortletForm.ProxyAuthType.NONE;
        if (!StringUtils.isBlank(authTypeForm)) {
            authType = ProxyPortletForm.ProxyAuthType.valueOf(authTypeForm);
            if (ProxyPortletForm.ProxyAuthType.BASIC_PORTLET_PREFERENCES.equals(authType)) {
                final String basicAuthPreferencesUsername = preferences.getValue(
                        PortletPreferencesBasicAuthenticationPreInterceptor.USERNAME_PREFERENCE, "");
                form.setBasicAuthPreferencesUsername(basicAuthPreferencesUsername);
                final String basicAuthPreferencesPassword = preferences.getValue(
                        PortletPreferencesBasicAuthenticationPreInterceptor.PASSWORD_PREFERENCE, "");
                form.setBasicAuthPreferencesPassword(basicAuthPreferencesPassword);
            }
        }

        form.setAuthType(authType);

        return form;
    }

}
