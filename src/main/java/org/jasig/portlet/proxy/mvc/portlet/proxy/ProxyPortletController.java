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

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

import java.io.IOException;
import java.io.InputStream;
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
import org.jasig.portlet.proxy.service.HttpContentService;
import org.jasig.portlet.proxy.service.IContentService;
import org.jasig.portlet.proxy.service.web.GenericProxyRequest;
import org.jasig.portlet.proxy.service.web.HttpProxyRequest;
import org.jasig.portlet.proxy.service.web.IDocumentFilter;
import org.jasig.portlet.proxy.service.web.IUrlPreProcessingFilter;
import org.jasig.portlet.proxy.service.web.ProxyRequest;
import org.jasig.portlet.proxy.service.web.URLRewritingFilter;
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

    public final static String PROXY_PORTLET_PARAM_PREFIX = "proxy.";
    public final static String URL_PARAM = PROXY_PORTLET_PARAM_PREFIX.concat("url");
    public final static String IS_FORM_PARAM = PROXY_PORTLET_PARAM_PREFIX.concat("isForm");
    public final static String FORM_METHOD_PARAM = PROXY_PORTLET_PARAM_PREFIX.concat("formMethod");


    protected static final String CONTENT_LOCATION_KEY = "location";
    protected static final String CONTENT_SERVICE_KEY = "contentService";
    protected static final String FILTER_LIST_KEY = "filters";
    protected static final String PREPROCESSOR_LIST_KEY = "preprocessors";

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

        // If a URL parameter has been specified, check to make sure that it's 
        // one that the portlet rewrote (we want to prevent this portlet from
        // acting as an open proxy).  If we did rewrite this URL, set the URL
        // to be proxied to the requested one
        final String url;
        final String urlParam = request.getParameter(URL_PARAM);
        if (urlParam != null) {
            final PortletSession session = request.getPortletSession();
            @SuppressWarnings("unchecked")
            final List<String> rewrittenUrls = (List<String>) session.getAttribute("rewrittenUrls");
            if (!rewrittenUrls.contains(urlParam)) {
                return;
            }
            url = urlParam;
        } 
        
        // otherwise use the default starting URL for this proxy portlet
        else {
            // locate all pre-processing filters configured for this portlet
            final List<IUrlPreProcessingFilter> filters = new ArrayList<IUrlPreProcessingFilter>();
            final String[] preprocessorKeys = preferences.getValues(PREPROCESSOR_LIST_KEY, new String[]{});
            for (final String preprocessorKey : preprocessorKeys) {
                final IUrlPreProcessingFilter filter = applicationContext.getBean(preprocessorKey, IUrlPreProcessingFilter.class);
                filters.add(filter);
            }
            
        	String workingUrl = preferences.getValue(CONTENT_LOCATION_KEY, null);
            // apply each of the url preprocessing filters in order
            for (final IUrlPreProcessingFilter filter : filters) {
                workingUrl = filter.filter(workingUrl, request, response);
            }
            
            url = workingUrl;
        }

        // locate the content service to use to retrieve our HTML content
        final String contentServiceKey = preferences.getValue(CONTENT_SERVICE_KEY, null);
        final IContentService contentService = applicationContext.getBean(contentServiceKey, IContentService.class);

        // locate all filters configured for this portlet
        final List<IDocumentFilter> filters = new ArrayList<IDocumentFilter>();
        final String[] filterKeys = preferences.getValues(FILTER_LIST_KEY, new String[]{});
        for (final String filterKey : filterKeys) {
            final IDocumentFilter filter = applicationContext.getBean(filterKey, IDocumentFilter.class);
            filters.add(filter);
        }
        
        // retrieve the HTML content
        final InputStream stream = contentService.getContent(url, request);
        
        try {
            final Document document = Jsoup.parse(stream, "UTF-8", url);
            
            final ProxyRequest proxyRequest;
            if (contentService instanceof HttpContentService) {
                // TODO: we really need the final URL here, not the requested one
                // to properly handle forwarded requests
                final HttpProxyRequest httpProxyRequest = new HttpProxyRequest();
                httpProxyRequest.setProxiedUrl(url);
                proxyRequest = httpProxyRequest;
            } else {
                proxyRequest = new GenericProxyRequest();
            }
            
            // apply each of the document filters in order
            for (final IDocumentFilter filter : filters) {
                filter.filter(document, proxyRequest, request, response);
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

        // retrieve the HTML content
        final InputStream stream = contentService.getContent(url, request);
        
        // TODO: handle headers, etc.

        try {
            final OutputStream out = response.getPortletOutputStream();
            IOUtils.copyLarge(stream, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("Exception writing proxied content", e);
        }
        
    }

}
