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

import org.jasig.portlet.proxy.service.GenericContentResponseImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class ContentClippingFilterTest {

    ContentClippingFilter filter;
    
    @Mock RenderRequest request;
    @Mock RenderResponse response;
    @Mock PortletPreferences preferences;
    @Mock GenericContentResponseImpl proxyResponse;
    
    @Before
    public void setUp() {
        filter = new ContentClippingFilter();

        when(request.getPreferences()).thenReturn(preferences);
        when(preferences.getValue(ContentClippingFilter.SELECTOR_KEY, null)).thenReturn("div#mine");
    }
    
    @Test
    public void testFilter() {
        final String source = "<html><body><div id=\"mine\"><div>Some content</div></div></body></html>";
        final String expected = "<divid=\"mine\"><div>Somecontent</div></div>";
        
        final Document document = Jsoup.parse(source);
        filter.filter(document, proxyResponse, request, response);
        final String result = document.html().replace("\n", "").replace(" ", "");
        assertEquals(expected, result);
    }
    
}
