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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.http.client.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MultiRequestHttpClientServiceImplTest {

	@Mock PortletRequest request;
	@Mock PortletPreferences preferences;
	@Mock PortletSession session;
	@Mock HttpClient client;
	MultiRequestHttpClientServiceImpl service;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		service = new MultiRequestHttpClientServiceImpl();
		
		when(request.getPreferences()).thenReturn(preferences);
		when(request.getPortletSession()).thenReturn(session);
		when(preferences.getValue(MultiRequestHttpClientServiceImpl.HTTP_CLIENT_CONNECTION_TIMEOUT,
				String.valueOf(MultiRequestHttpClientServiceImpl.DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT)))
				.thenReturn(String.valueOf(MultiRequestHttpClientServiceImpl.DEFAULT_HTTP_CLIENT_CONNECTION_TIMEOUT));
		when(preferences.getValue(MultiRequestHttpClientServiceImpl.HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT,
				String.valueOf(MultiRequestHttpClientServiceImpl.DEFAULT_HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT)))
				.thenReturn(String.valueOf(MultiRequestHttpClientServiceImpl.DEFAULT_HTTP_CLIENT_CONNECTION_REQUEST_TIMEOUT));
		when(preferences.getValue(MultiRequestHttpClientServiceImpl.HTTP_CLIENT_SOCKET_TIMEOUT,
				String.valueOf(MultiRequestHttpClientServiceImpl.DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT)))
				.thenReturn(String.valueOf(MultiRequestHttpClientServiceImpl.DEFAULT_HTTP_CLIENT_SOCKET_TIMEOUT));
	}

	
	@Test
	public void testGetSharedClient() {
		when(preferences.getValue(MultiRequestHttpClientServiceImpl.SHARED_SESSION_KEY, null)).thenReturn("sharedSession");
		when(session.getAttribute("sharedSession", PortletSession.APPLICATION_SCOPE)).thenReturn(client);
		
		HttpClient response = service.getHttpClient(request);
		assertSame(client, response);
	}
	
	@Test
	public void testGetUnsharedClient() {
		when(session.getAttribute(MultiRequestHttpClientServiceImpl.CLIENT_SESSION_KEY, PortletSession.PORTLET_SCOPE)).thenReturn(client);
		
		HttpClient response = service.getHttpClient(request);
		assertSame(client, response);
	}
	
	@Test
	public void testCreateSharedClient() {
		when(preferences.getValue(MultiRequestHttpClientServiceImpl.SHARED_SESSION_KEY, null)).thenReturn("sharedSession");
		HttpClient response = service.getHttpClient(request);
		assertNotNull(response);
		verify(session).setAttribute("sharedSession", response, PortletSession.APPLICATION_SCOPE); 
	}
	
	@Test
	public void testCreateUnsharedClient() {
		HttpClient response = service.getHttpClient(request);
		assertNotNull(response);
		verify(session).setAttribute(MultiRequestHttpClientServiceImpl.CLIENT_SESSION_KEY, response, PortletSession.PORTLET_SCOPE);
	}
	
}
