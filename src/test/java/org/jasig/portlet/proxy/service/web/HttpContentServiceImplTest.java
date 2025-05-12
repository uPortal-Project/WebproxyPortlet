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
package org.jasig.portlet.proxy.service.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.http.client.methods.HttpUriRequest;
import org.jasig.portlet.proxy.service.GenericContentRequestImpl;
import org.jasig.portlet.spring.SpringELProcessor;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class HttpContentServiceImplTest {

	@Spy HttpContentServiceImpl service;
	Map<String, String[]> params = new LinkedHashMap<String, String[]>();;
	@Mock PortletRequest request;
	@Mock PortletPreferences preferences;
	@Mock SpringELProcessor processor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(request.getParameterMap()).thenReturn(params);

		when(request.getPreferences()).thenReturn(preferences);
  		when(preferences.getValue(GenericContentRequestImpl.CONTENT_LOCATION_PREFERENCE, null)).thenReturn("http://somewhere.com/path/page.html");

		when(request.getParameter("proxy.url")).thenReturn(null);
	}


	@Test
	public void testPostFormNoParams() {
		when(request.getParameter(HttpContentServiceImpl.IS_FORM_PARAM)).thenReturn("true");
		when(request.getParameter(HttpContentServiceImpl.FORM_METHOD_PARAM)).thenReturn("POST");
		when(processor.process(any(String.class), any(PortletRequest.class))).thenReturn("http://somewhere.com/path/page.html");
		HttpContentRequestImpl proxyRequest = new HttpContentRequestImpl(request, processor);

		final HttpUriRequest httpRequest = service.getHttpRequest(proxyRequest, request);
		assertEquals("POST", httpRequest.getMethod());
		assertEquals("http://somewhere.com/path/page.html", httpRequest.getURI().toString());

	}

	@Test
	public void testGetForm() {

		params.put("param1", new String[]{"value1"});
		params.put("param2", new String[]{"value3", "value4"});

		when(request.getParameter(HttpContentServiceImpl.IS_FORM_PARAM)).thenReturn("true");
		when(request.getParameter(HttpContentServiceImpl.FORM_METHOD_PARAM)).thenReturn("GET");
		when(processor.process(any(String.class), any(PortletRequest.class))).thenReturn("http://somewhere.com/path/page.html");
		HttpContentRequestImpl proxyRequest = new HttpContentRequestImpl(request, processor);

		final HttpUriRequest httpRequest = service.getHttpRequest(proxyRequest, request);
		assertEquals("GET", httpRequest.getMethod());
		assertEquals("http://somewhere.com/path/page.html?param1=value1&param2=value3&param2=value4", httpRequest.getURI().toString());

	}

	@Test
	public void testGetFormNoParams() {
		when(request.getParameter(HttpContentServiceImpl.IS_FORM_PARAM)).thenReturn("true");
		when(request.getParameter(HttpContentServiceImpl.FORM_METHOD_PARAM)).thenReturn("GET");
		when(processor.process(any(String.class), any(PortletRequest.class))).thenReturn("http://somewhere.com/path/page.html");
		HttpContentRequestImpl proxyRequest = new HttpContentRequestImpl(request, processor);

		final HttpUriRequest httpRequest = service.getHttpRequest(proxyRequest, request);
		assertEquals("GET", httpRequest.getMethod());
		assertEquals("http://somewhere.com/path/page.html", httpRequest.getURI().toString());

	}


	@Test
	public void testNonForm() {

		when(request.getParameter("proxy.formMethod")).thenReturn(null);
		when(request.getParameter(HttpContentServiceImpl.IS_FORM_PARAM)).thenReturn("false");
		when(processor.process(any(String.class), any(PortletRequest.class))).thenReturn("http://somewhere.com/path/page.html");
		HttpContentRequestImpl proxyRequest = new HttpContentRequestImpl(request, processor);

		final HttpUriRequest httpRequest = service.getHttpRequest(proxyRequest, request);
		assertEquals("GET", httpRequest.getMethod());
		assertEquals("http://somewhere.com/path/page.html", httpRequest.getURI().toString());

	}


}
