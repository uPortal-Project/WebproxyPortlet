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
package org.jasig.portlet.proxy.mvc.portlet.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.proxy.service.IContentRequest;
import org.jasig.portlet.proxy.service.IContentResponse;
import org.jasig.portlet.proxy.service.IContentService;
import org.jasig.portlet.proxy.service.proxy.document.IDocumentFilter;
import org.jasig.portlet.proxy.service.proxy.document.URLRewritingFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 * ProxyPortletController is the main view controller for web proxy portlets.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Controller
@RequestMapping("VIEW")
public class ProxyPortletController {


    protected static final String CONTENT_SERVICE_KEY = "contentService";
    protected static final String FILTER_LIST_KEY = "filters";

    protected final Log log = LogFactory.getLog(getClass());
    
    private ApplicationContext applicationContext;
    
    @Autowired(required = true)
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @RenderMapping
    public void showContent(final RenderRequest request,
            final RenderResponse response) {

        final PortletPreferences preferences = request.getPreferences();
        
        // locate the content service to use to retrieve our HTML content
        final String contentServiceKey = preferences.getValue(CONTENT_SERVICE_KEY, null);
        final IContentService contentService = applicationContext.getBean(contentServiceKey, IContentService.class);

        final IContentRequest proxyRequest = contentService.getRequest(request);

        // retrieve the HTML content
        final IContentResponse proxyResponse = contentService.getContent(proxyRequest, request);
        
        // locate all filters configured for this portlet
        final List<IDocumentFilter> filters = new ArrayList<IDocumentFilter>();
        final String[] filterKeys = preferences.getValues(FILTER_LIST_KEY, new String[]{});
        for (final String filterKey : filterKeys) {
            final IDocumentFilter filter = applicationContext.getBean(filterKey, IDocumentFilter.class);
            filters.add(filter);
        }
        
        try {
            final Document document = Jsoup.parse(proxyResponse.getContent(), "UTF-8", proxyResponse.getProxiedLocation());
            
            
            // apply each of the document filters in order
            for (final IDocumentFilter filter : filters) {
                filter.filter(document, proxyResponse, request, response);
            }
            
            // write out the final content
            try {
                final OutputStream out = response.getPortletOutputStream();
                IOUtils.write(document.html(), out);
                out.flush();
                out.close();
            } catch (IOException e) {
                log.error("Exception writing proxied content", e);
            }
                        
        } catch (IOException e) {
            log.error("Error parsing HTML content", e);
        }
        
    }

    @ActionMapping
    public void proxyTarget(final @RequestParam("proxy.url") String url,
            final ActionRequest request, final ActionResponse response) {
        // TODO: ?
        final Map<String, String[]> params = request.getParameterMap();
        response.setRenderParameters(params);
    }
    
    @ResourceMapping
    public void proxyResourceTarget(final @RequestParam("proxy.url") String url,
            final ResourceRequest request, final ResourceResponse response) {
        
        final PortletSession session = request.getPortletSession();
        @SuppressWarnings("unchecked")
        final List<String> rewrittenUrls = (List<String>) session.getAttribute(URLRewritingFilter.REWRITTEN_URLS_KEY);
        if (!rewrittenUrls.contains(url)) {
            return;
        }

        final PortletPreferences preferences = request.getPreferences();
        
        // locate the content service to use to retrieve our HTML content
        final String contentServiceKey = preferences.getValue(CONTENT_SERVICE_KEY, null);
        final IContentService contentService = applicationContext.getBean(contentServiceKey, IContentService.class);

        final IContentRequest proxyRequest = contentService.getRequest(request);

        // retrieve the HTML content
        final IContentResponse proxyResponse = contentService.getContent(proxyRequest, request);
        
        // TODO: handle headers, etc.

        try {
            final OutputStream out = response.getPortletOutputStream();
            IOUtils.copyLarge(proxyResponse.getContent(), out);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("Exception writing proxied content", e);
        }
        
    }

}
