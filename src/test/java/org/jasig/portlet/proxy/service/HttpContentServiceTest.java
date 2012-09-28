package org.jasig.portlet.proxy.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.http.client.methods.HttpUriRequest;
import org.jasig.portlet.proxy.mvc.portlet.proxy.ProxyPortletController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class HttpContentServiceTest {

	@Spy HttpContentService service;
	Map<String, String[]> params = new LinkedHashMap<String, String[]>();;
	@Mock PortletRequest request;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(request.getParameterMap()).thenReturn(params);
		
	}
	
	
	@Test
	public void testPostFormNoParams() {
		when(request.getParameter(ProxyPortletController.IS_FORM_PARAM)).thenReturn("true");
		when(request.getParameter(ProxyPortletController.FORM_METHOD_PARAM)).thenReturn("POST");
		
		final HttpUriRequest httpRequest = service.getHttpRequest("http://somewhere.com/path/page.html", request);
		assertEquals("POST", httpRequest.getMethod());
		assertEquals("http://somewhere.com/path/page.html", httpRequest.getURI().toString());
		
	}

	@Test
	public void testGetForm() {
		
		params.put("param1", new String[]{"value1"});
		params.put("param2", new String[]{"value3", "value4"});
		
		when(request.getParameter(ProxyPortletController.IS_FORM_PARAM)).thenReturn("true");
		when(request.getParameter(ProxyPortletController.FORM_METHOD_PARAM)).thenReturn("GET");
		
		final HttpUriRequest httpRequest = service.getHttpRequest("http://somewhere.com/path/page.html", request);
		assertEquals("GET", httpRequest.getMethod());
		assertEquals("http://somewhere.com/path/page.html?param1=value1&param2=value3&param2=value4", httpRequest.getURI().toString());
		
	}

	@Test
	public void testGetFormNoParams() {
		when(request.getParameter(ProxyPortletController.IS_FORM_PARAM)).thenReturn("true");
		when(request.getParameter(ProxyPortletController.FORM_METHOD_PARAM)).thenReturn("GET");
		
		final HttpUriRequest httpRequest = service.getHttpRequest("http://somewhere.com/path/page.html", request);
		assertEquals("GET", httpRequest.getMethod());
		assertEquals("http://somewhere.com/path/page.html", httpRequest.getURI().toString());
		
	}


	@Test
	public void testNonForm() {
		
		when(request.getParameter(ProxyPortletController.IS_FORM_PARAM)).thenReturn("false");
		
		final HttpUriRequest httpRequest = service.getHttpRequest("http://somewhere.com/path/page.html", request);
		assertEquals("GET", httpRequest.getMethod());
		assertEquals("http://somewhere.com/path/page.html", httpRequest.getURI().toString());
		
	}


}
