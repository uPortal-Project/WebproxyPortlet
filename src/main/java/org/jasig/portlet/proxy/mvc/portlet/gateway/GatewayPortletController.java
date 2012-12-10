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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.jasig.portlet.proxy.mvc.IViewSelector;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.jasig.portlet.proxy.service.web.interceptor.IPreInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

@Controller
@RequestMapping("VIEW")
public class GatewayPortletController {
	
	@Autowired(required=false)
	private String viewName = "gateway";
	
	@Autowired(required=false)
	private String mobileViewName = "mobileGateway";
	
	@Autowired(required=true)
	private ApplicationContext applicationContext;
	
	@Autowired(required=true)
	private IViewSelector viewSelector;

	@RenderMapping
	public ModelAndView getView(RenderRequest request){
		final ModelAndView mv = new ModelAndView();
		final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
		mv.addObject("entries", entries);
		
		final String view = viewSelector.isMobile(request) ? mobileViewName : viewName;
		mv.setView(view);
		return mv;
	}
	
	@ResourceMapping()
	public ModelAndView showTarget(ResourceRequest portletRequest, ResourceResponse portletResponse, @RequestParam("index") int index) throws IOException {		
		final ModelAndView mv = new ModelAndView("json");
		
		// get the requested gateway link entry from the list configured for
		// this portlet
		final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
		final GatewayEntry entry = entries.get(index);

		// build a list of content requests
		final List<HttpContentRequestImpl> contentRequests = new ArrayList<HttpContentRequestImpl>();
		for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
			
			// run each content request through any configured preinterceptors
			// before adding it to the list
			final HttpContentRequestImpl contentRequest = requestEntry.getKey();
			for (String interceptorKey : requestEntry.getValue()) {
				final IPreInterceptor interceptor = applicationContext.getBean(interceptorKey, IPreInterceptor.class);
				interceptor.intercept(contentRequest, portletRequest);
			}
			contentRequests.add(contentRequest);
		}
		mv.addObject("contentRequests", contentRequests);

		// we don't want this response to be cached by the browser since it may
		// include one-time-only authentication tokens
        portletResponse.getCacheControl().setExpirationTime(1);
        portletResponse.getCacheControl().setUseCachedContent(false);

		return mv;
	}
}
