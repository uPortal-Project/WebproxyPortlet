package org.jasig.portlet.proxy.mvc.portlet.gateway;

import java.util.LinkedHashMap;
import java.util.List;

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;

public class GatewayEntry {

	private String name;
	private String iconUrl;
	private LinkedHashMap<HttpContentRequestImpl, List<String>> contentRequests = new LinkedHashMap<HttpContentRequestImpl, List<String>>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public LinkedHashMap<HttpContentRequestImpl, List<String>> getContentRequests() {
		return contentRequests;
	}

	public void setContentRequests(LinkedHashMap<HttpContentRequestImpl, List<String>> contentRequests) {
		this.contentRequests = contentRequests;
	}

}
