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
package org.jasig.portlet.proxy.mvc.portlet.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.proxy.service.web.ContentClippingFilter;
import org.jasig.portlet.proxy.service.web.URLRewritingFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
	final Log log = LogFactory.getLog(getClass());
	
	@RenderMapping
	public String getEditView() {
		return "editProxyPortlet";
	}
	
	@ActionMapping
	public void updatePortlet(@ModelAttribute("form") ProxyPortletForm form, ActionRequest request, ActionResponse response) {
		try {

			final PortletPreferences preferences = request.getPreferences();

			preferences.setValue(ProxyPortletController.CONTENT_LOCATION_KEY, form.getLocation());
			preferences.setValue(ProxyPortletController.CONTENT_SERVICE_KEY, form.getContentService());
			preferences.setValue(URLRewritingFilter.WHITELIST_REGEXES_KEY, form.getWhitelistRegexes());
			
			final List<String> filters = new ArrayList<String>();
			
			// if the HTTP content service is in use, we need to rewrite the 
			// proxied URLs
			if ("httpContentService".equals(form.getContentService())) {
				filters.add("urlRewritingFilter");
			}
			
			// if a clipping selector has been specified, add the content clipping
			// filter
			if (StringUtils.isNotBlank(form.getClippingSelector())) {
				filters.add("contentClippingFilter");
			}
			preferences.setValue(ContentClippingFilter.SELECTOR_KEY, form.getClippingSelector());

			preferences.setValues(ProxyPortletController.FILTER_LIST_KEY, filters.toArray(new String[]{}));

			preferences.store();
			
			response.setPortletMode(PortletMode.VIEW);
			
		} catch (ValidatorException e) {
			log.error("Unable to update web proxy portlet configuration", e);
		} catch (IOException e) {
			log.error("Unable to update web proxy portlet configuration", e);
		} catch (ReadOnlyException e) {
			log.error("Unable to update web proxy portlet configuration", e);
		} catch (PortletModeException e) {
			log.error("Unable to update web proxy portlet configuration", e);
		}
	}

	@ModelAttribute("form")
	public ProxyPortletForm getForm(PortletRequest request) {
		final PortletPreferences preferences = request.getPreferences();
		final ProxyPortletForm form = new ProxyPortletForm();
		
		form.setContentService(preferences.getValue(ProxyPortletController.CONTENT_SERVICE_KEY, null));
		form.setLocation(preferences.getValue(ProxyPortletController.CONTENT_LOCATION_KEY, null));
		form.setWhitelistRegexes(preferences.getValue(URLRewritingFilter.WHITELIST_REGEXES_KEY, null));
		form.setClippingSelector(preferences.getValue(ContentClippingFilter.SELECTOR_KEY, null));
		
		return form;
	}
	
}
