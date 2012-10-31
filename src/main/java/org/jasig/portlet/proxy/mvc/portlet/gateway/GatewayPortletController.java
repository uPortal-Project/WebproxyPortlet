package org.jasig.portlet.proxy.mvc.portlet.gateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

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
	
	@Autowired(required=true)
	private ApplicationContext applicationContext;

	@RenderMapping
	public ModelAndView getView(RenderRequest request){
		final ModelAndView mv = new ModelAndView(viewName);
		final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
		mv.addObject("entries", entries);
		return mv;
	}
	
	@ResourceMapping()
	public ModelAndView showTarget(ResourceRequest portletRequest, @RequestParam("index") int index) throws IOException {		
		final ModelAndView mv = new ModelAndView("json");
		
		final List<GatewayEntry> entries =  (List<GatewayEntry>) applicationContext.getBean("gatewayEntries", List.class);
		final GatewayEntry entry = entries.get(index);
		
		final List<HttpContentRequestImpl> contentRequests = new ArrayList<HttpContentRequestImpl>();
		for (Map.Entry<HttpContentRequestImpl, List<String>> requestEntry : entry.getContentRequests().entrySet()){
			final HttpContentRequestImpl contentRequest = requestEntry.getKey();
			for (String interceptorKey : requestEntry.getValue()) {
				final IPreInterceptor interceptor = applicationContext.getBean(interceptorKey, IPreInterceptor.class);
				interceptor.intercept(contentRequest, portletRequest);
			}
			contentRequests.add(contentRequest);
		}
		mv.addObject("contentRequests", contentRequests);
		
		return mv;
	}
}
