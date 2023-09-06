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
package org.jasig.portlet.proxy.mvc.portlet.json;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.jasig.portlet.proxy.mvc.IViewSelector;
import org.jasig.portlet.proxy.service.ClasspathResourceContentService;
import org.jasig.portlet.proxy.service.IContentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
public class JsonPortletControllerTest {

    JsonPortletController controller;
    IContentService contentService;

    @Mock PortletRequest request;
    @Mock PortletResponse response;
    @Mock PortletPreferences preferences;
    @Mock IViewSelector viewSelector;
    @Mock ApplicationContext applicationContext;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        controller = new JsonPortletController();
        controller.setViewSelector(viewSelector);
        controller.setApplicationContext(applicationContext);
        
        contentService = new ClasspathResourceContentService();
        
        when(request.getPreferences()).thenReturn(preferences);
        when(viewSelector.isMobile(request)).thenReturn(false);
        when(preferences.getValue(JsonPortletController.MAIN_VIEW_KEY, null)).thenReturn("main");
        when(preferences.getValue(JsonPortletController.MOBILE_VIEW_KEY, null)).thenReturn("main-jQM");
        when(preferences.getValue(JsonPortletController.CONTENT_LOCATION_KEY, null)).thenReturn("test.json");
        when(preferences.getValue(JsonPortletController.CONTENT_SERVICE_KEY, null)).thenReturn("classpath");
        when(applicationContext.getBean("classpath", IContentService.class)).thenReturn(contentService);
        
    }
    
    @Test
    public void test() {
        final ModelAndView mv = controller.showContent(request);
        assertEquals(mv.getViewName(), "main");
        
        final Map<String, Object> model = mv.getModel();
        @SuppressWarnings("unchecked")
        final List<Object> list = (List<Object>) model.get("things");
        assertEquals(list.size(), 2);
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> thing = (Map<String, Object>) list.get(0);
        assertEquals("thing1", thing.get("name"));
    }
    
    @Test
    public void testMobileView() {
        when(viewSelector.isMobile(request)).thenReturn(true);
        final ModelAndView mv = controller.showContent(request);
        assertEquals(mv.getViewName(), "main-jQM");
    }
    
    @Test
    public void testNoMobileView() {
        when(viewSelector.isMobile(request)).thenReturn(true);
        when(preferences.getValue(JsonPortletController.MOBILE_VIEW_KEY, null)).thenReturn(null);
        final ModelAndView mv = controller.showContent(request);
        assertEquals(mv.getViewName(), "main");
    }
    
}
