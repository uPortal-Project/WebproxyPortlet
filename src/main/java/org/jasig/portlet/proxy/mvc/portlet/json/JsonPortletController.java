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

import java.io.IOException;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;
import org.jasig.portlet.proxy.mvc.IViewSelector;
import org.jasig.portlet.proxy.service.IContentRequest;
import org.jasig.portlet.proxy.service.IContentResponse;
import org.jasig.portlet.proxy.service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>JsonPortletController class.</p>
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
@Controller
@RequestMapping("VIEW")
@Slf4j
public class JsonPortletController {

    /** Constant <code>CONTENT_LOCATION_KEY="location"</code> */
    protected static final String CONTENT_LOCATION_KEY = "location";
    /** Constant <code>CONTENT_SERVICE_KEY="contentService"</code> */
    protected static final String CONTENT_SERVICE_KEY = "contentService";
    /** Constant <code>MAIN_VIEW_KEY="mainView"</code> */
    protected static final String MAIN_VIEW_KEY = "mainView";
    /** Constant <code>MOBILE_VIEW_KEY="mobileView"</code> */
    protected static final String MOBILE_VIEW_KEY = "mobileView";

    private ApplicationContext applicationContext;
    
    /**
     * <p>Setter for the field <code>applicationContext</code>.</p>
     *
     * @param applicationContext a {@link org.springframework.context.ApplicationContext} object
     */
    @Autowired(required = true)
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private IViewSelector viewSelector;
    
    /**
     * <p>Setter for the field <code>viewSelector</code>.</p>
     *
     * @param viewSelector a {@link org.jasig.portlet.proxy.mvc.IViewSelector} object
     */
    @Autowired(required = true)
    public void setViewSelector(IViewSelector viewSelector) {
        this.viewSelector = viewSelector;
    }

    /**
     * <p>showContent.</p>
     *
     * @param request a {@link javax.portlet.PortletRequest} object
     * @return a {@link org.springframework.web.servlet.ModelAndView} object
     */
    @RequestMapping
    public ModelAndView showContent(PortletRequest request) {
        final ModelAndView mv = new ModelAndView();
        
        final PortletPreferences preferences = request.getPreferences();
        
        // locate the content service to use to retrieve our JSON
        final String contentServiceKey = preferences.getValue(CONTENT_SERVICE_KEY, null);
        final IContentService contentService = applicationContext.getBean(contentServiceKey, IContentService.class);
        final IContentRequest proxyRequest = contentService.getRequest(request);

        // retrieve the JSON content
        final IContentResponse proxyResponse = contentService.getContent(proxyRequest, request);
        
        // parse our JSON into a map and add it to the model
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectReader reader = mapper.reader(Map.class);
        try {
            final Map<String, Object> map = reader.readValue(proxyResponse.getContent());
            mv.addAllObjects(map);
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON content", e);
        } catch (IOException e) {
            log.error("IOException reading JSON content", e);
        } finally {
            if (proxyResponse != null) {
                proxyResponse.close();
            }
        }
        
        // set the appropriate view name
        final String mainView = preferences.getValue(MAIN_VIEW_KEY, null);
        final String mobileView = preferences.getValue(MOBILE_VIEW_KEY, null);
        final String viewName;
        if (mobileView != null) {
            final boolean isMobile = viewSelector.isMobile(request);
            viewName = isMobile ? mobileView : mainView;
        } else {
            viewName = mainView;
        }
        mv.setViewName(viewName);
        
        return mv;
    }

}
