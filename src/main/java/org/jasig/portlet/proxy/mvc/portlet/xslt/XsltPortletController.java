/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.proxy.mvc.portlet.xslt;

import java.io.InputStream;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.proxy.mvc.IViewSelector;
import org.jasig.portlet.proxy.service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.servlet.view.xslt.XsltView;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Controller
@RequestMapping("VIEW")
public class XsltPortletController {

    protected static final String CONTENT_LOCATION_KEY = "location";
    protected static final String CONTENT_SERVICE_KEY = "contentService";
    protected static final String MAIN_XSLT_KEY = "mainXslt";
    protected static final String MOBILE_XSLT_KEY = "mobileXslt";

    protected final Log log = LogFactory.getLog(getClass());
    
    private ApplicationContext applicationContext;
    
    @Autowired(required = true)
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private IViewSelector viewSelector;
    
    @Autowired(required = true)
    public void setViewSelector(IViewSelector viewSelector) {
        this.viewSelector = viewSelector;
    }

    @RequestMapping
    public ModelAndView showContent(PortletRequest request) {
        final ModelAndView mv = new ModelAndView();
        
        final PortletPreferences preferences = request.getPreferences();
        
        // locate the content service to use to retrieve our XML
        final String contentServiceKey = preferences.getValue(CONTENT_SERVICE_KEY, null);
        final IContentService contentService = applicationContext.getBean(contentServiceKey, IContentService.class);

        // retrieve the XML content
        final String location = preferences.getValue(CONTENT_LOCATION_KEY, null);
        final InputStream stream = contentService.getContent(location, request);
        mv.addObject("xml", stream);

        // set the appropriate view name
        final String mainXslt = preferences.getValue(MAIN_XSLT_KEY, null);
        final String mobileXslt = preferences.getValue(MOBILE_XSLT_KEY, null);
        final String xslt;
        if (mobileXslt != null) {
            final boolean isMobile = viewSelector.isMobile(request);
            xslt = isMobile ? mobileXslt : mainXslt;
        } else {
            xslt = mainXslt;
        }
        
        // create an XSLT view using the configured source
        final XsltView view = new XsltView();
        view.setUrl(xslt);
        view.setSourceKey("xml");
        view.setApplicationContext(this.applicationContext);
        mv.setView(view);

        return mv;
    }

}
