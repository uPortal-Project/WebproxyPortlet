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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jasig.portlet.proxy.service.GenericContentResponseImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
public class URLRewritingFilterTest {
    
    URLRewritingFilter filter;
    GenericContentResponseImpl proxyResponse;
    @Mock RenderRequest request;
    @Mock RenderResponse response;
    @Mock PortletSession session;    
    @Mock PortletPreferences preferences;
    @Mock PortletURL portletURL;
    @Mock Writer writer;
    @Mock ConcurrentMap<String, String> rewrittenUrls;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(request.getPortletSession()).thenReturn(session);
        when(session.getAttribute(URLRewritingFilter.REWRITTEN_URLS_KEY)).thenReturn(rewrittenUrls);
        when(request.getPreferences()).thenReturn(preferences);
        when(preferences.getValues(URLRewritingFilter.WHITELIST_REGEXES_KEY, new String[]{})).thenReturn(new String[]{});

        filter = spy(new URLRewritingFilter());
        
        proxyResponse = new GenericContentResponseImpl();
        proxyResponse.setProxiedLocation("http://external.site.com/somewhere/index.html?q=a&b=t");
        
        final Map<String, Set<String>> urlAttributes = new HashMap<String, Set<String>>();
        urlAttributes.put("a", Collections.singleton("href"));
        urlAttributes.put("img", Collections.singleton("src"));
        urlAttributes.put("form", Collections.singleton("action"));
        filter.setActionElements(urlAttributes);
        
        filter.setResourceElements(new HashMap<String, Set<String>>());
    }
    
    @Test
    public void testRelativeUrls() {
        final Document document = Jsoup.parse("<div><a href=\"/link/with/slash.html\">Link</a><a href=\"link/without/slash.html\">Link</a></div>");
        filter.filter(document, proxyResponse, request, response);
        final String result = "<div><ahref=\"http://external.site.com/link/with/slash.html\">Link</a><ahref=\"http://external.site.com/somewhere/link/without/slash.html\">Link</a></div>";
        final String expected = document.body().html().replace(" ", "").replace("\n", "");
        assertEquals(result, expected);
    }
    
    @Test
    public void testProxiedUrls() {
        when(preferences.getValues(URLRewritingFilter.WHITELIST_REGEXES_KEY, new String[]{})).thenReturn(new String[]{"^http://external.site.com"});
        doReturn("portletUrl").when(filter).createActionUrl(any(RenderResponse.class), any(String.class));        
        doReturn("portletUrl").when(filter).createResourceUrl(any(RenderResponse.class), any(String.class));
        
        final Document document = Jsoup.parse("<div><a href=\"/link/with/slash.html\">Link</a><a href=\"link/without/slash.html\">Link</a></div>");
        filter.filter(document, proxyResponse, request, response);
        final String result = "<div><ahref=\"portletUrl\">Link</a><ahref=\"portletUrl\">Link</a></div>";
        final String expected = document.body().html().replace(" ", "").replace("\n", "");
        assertEquals(result, expected);
    }
    
    @Test
    public void testGetBaseUrl() throws URISyntaxException {
        final String result = filter.getBaseServerUrl("http://somewhere.com/some/path?query=nothing");
        assertEquals(result, "http://somewhere.com");
    }
    
}
