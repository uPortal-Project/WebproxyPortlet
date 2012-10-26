package org.jasig.portlet.proxy.service.web;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MultiRequestHttpClientServiceImplTest {

	@Mock PortletRequest request;
	@Mock PortletPreferences preferences;
	@Mock PortletSession session;
	@Mock DefaultHttpClient client;
	MultiRequestHttpClientServiceImpl service;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		service = new MultiRequestHttpClientServiceImpl();
		
		when(request.getPreferences()).thenReturn(preferences);
		when(request.getPortletSession()).thenReturn(session);
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
