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
package org.jasig.portlet.proxy.service.proxy.document;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jasig.portlet.proxy.service.IContentResponse;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HeaderFooterFilterTest {

	private HeaderFooterFilter filter;
	String testContent = "<div>Hi I'm a div! And I have <a href=\"links\">links</a>!</div>";
	@Mock PortletPreferences preferences;
	@Mock RenderRequest portletRequest;
	@Mock RenderResponse portletResponse;
	@Mock IContentResponse proxyResponse;
	Document document;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);		
		when(portletRequest.getPreferences()).thenReturn(preferences);
		
		filter = new HeaderFooterFilter();
		document = new Document("http://somewhere.com");
		document.html(testContent);
	}
	
	@Test
	public void doNothing() {
		filter.filter(document, proxyResponse, portletRequest, portletResponse);
		
		assertEquals(testContent.replaceAll("\\s", ""), document.html().replaceAll("\\s", ""));
	}

	@Test
	public void addHeader() {
		String header = "<div>A header</div>";
		when(preferences.getValue(HeaderFooterFilter.HEADER_KEY, null)).thenReturn(header);
	
		filter.filter(document, proxyResponse, portletRequest, portletResponse);
		
		assertEquals(header.concat(testContent).replaceAll("\\s", ""), document.html().replaceAll("\\s", ""));
	}
	
	@Test
	public void addFooter() {
		
		String footer = "<div>A footer</div>";
		when(preferences.getValue(HeaderFooterFilter.FOOTER_KEY, null)).thenReturn(footer);
		
		filter.filter(document, proxyResponse, portletRequest, portletResponse);
		
		assertEquals(testContent.concat(footer).replaceAll("\\s", ""), document.html().replaceAll("\\s", ""));
	}
	
	@Test
	public void wrapContent() {
		
		String header = "<div>wrapped content: <span>header</span>";
		String footer = "<span>footer</span></div>";
		when(preferences.getValue(HeaderFooterFilter.HEADER_KEY, null)).thenReturn(header);
		when(preferences.getValue(HeaderFooterFilter.FOOTER_KEY, null)).thenReturn(footer);
		
		filter.filter(document, proxyResponse, portletRequest, portletResponse);
		
		assertEquals(header.concat(testContent).concat(footer).replaceAll("\\s", ""), document.html().replaceAll("\\s", ""));
	}
	

}
