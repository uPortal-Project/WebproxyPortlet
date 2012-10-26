package org.jasig.portlet.proxy.service.web;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

public class RedirectTrackingResponseInterceptor implements
		HttpResponseInterceptor {

	public static final String FINAL_URL_KEY = "finalUrl";
	
	@Override
	public void process(HttpResponse response, HttpContext context)
			throws HttpException, IOException {
        if (response.containsHeader("Location")) {
            Header[] locations = response.getHeaders("Location");
            if (locations.length > 0) {
            	context.setAttribute(FINAL_URL_KEY, locations[0].getValue());
            }
        }
	}
	
}
