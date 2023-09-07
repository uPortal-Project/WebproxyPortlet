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
package org.jasig.portlet.proxy.mvc.portlet.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apereo.portal.search.SearchConstants;
import org.apereo.portal.search.SearchResults;
import org.jasig.portlet.proxy.search.ISearchService;
import org.jasig.portlet.proxy.service.IContentRequest;
import org.jasig.portlet.proxy.service.IContentResponse;
import org.jasig.portlet.proxy.service.IContentService;
import org.jasig.portlet.proxy.service.proxy.document.IDocumentFilter;
import org.jasig.portlet.proxy.service.proxy.document.URLRewritingFilter;
import org.jasig.portlet.proxy.service.web.HttpContentResponseImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.EventMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;

/**
 * ProxyPortletController is the main view controller for web proxy portlets.
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
@Controller
@RequestMapping("VIEW")
@Slf4j
public class ProxyPortletController {

    /** Constant <code>PREF_CHARACTER_ENCODING="sourcePageCharacterEncoding"</code> */
    public static final String PREF_CHARACTER_ENCODING = "sourcePageCharacterEncoding";
    /** Constant <code>CHARACTER_ENCODING_DEFAULT="UTF-8"</code> */
    public static final String CHARACTER_ENCODING_DEFAULT = "UTF-8";
    /** Constant <code>CONTENT_SERVICE_KEY="contentService"</code> */
    protected static final String CONTENT_SERVICE_KEY = "contentService";
    /** Constant <code>FILTER_LIST_KEY="filters"</code> */
    protected static final String FILTER_LIST_KEY = "filters";
    private static final String PROXY_RESPONSE_KEY = "proxyResponse";
    @Autowired
    private ApplicationContext applicationContext;
    private final List<Pattern> knownHtmlContentTypes = new ArrayList<Pattern>();
    @Resource(name = "contentSearchProvider")
    private ISearchService searchService;

    /**
     * <p>Setter for the field <code>knownHtmlContentTypes</code>.</p>
     *
     * @param contentTypes a {@link java.util.List} object
     */
    @Required
    @Resource(name = "knownHtmlContentTypes")
    public void setKnownHtmlContentTypes(List<String> contentTypes) {
        knownHtmlContentTypes.clear();
        for (String contentType : contentTypes) {
            knownHtmlContentTypes.add(Pattern.compile(contentType));
        }
    }

    /**
     * <p>showContent.</p>
     *
     * @param request a {@link javax.portlet.RenderRequest} object
     * @param response a {@link javax.portlet.RenderResponse} object
     */
    @RenderMapping
    public void showContent(final RenderRequest request, final RenderResponse response) {
        log.debug("Entering render mapping");
        IContentResponse proxyResponse = null;
        try {
            // From action phase?
            proxyResponse = (IContentResponse) request.getPortletSession().getAttribute(PROXY_RESPONSE_KEY);
            if (proxyResponse == null) {
                // retrieve the HTML content
                log.debug("proxyResponse not found in request attribute proxyResponse -- invoking proxy method");
                proxyResponse = invokeProxy(request);
            } else {
                log.debug("proxyResponse found(!) in request attribute proxyResponse -- using it on this render and removing it from session");
                request.getPortletSession().removeAttribute(PROXY_RESPONSE_KEY);
            }
            final List<IDocumentFilter> filters = prepareFilters(request);
            final Document document = parseDocument(request, proxyResponse);

            // apply each of the document filters in order
            for (final IDocumentFilter filter : filters) {
                filter.filter(document, proxyResponse, request, response);
            }

            // write out the final content
            OutputStream out = null;
            try {
                out = response.getPortletOutputStream();
                IOUtils.write(document.html(), out);
                out.flush();
            } catch (IOException e) {
                log.error("Exception writing proxy content", e);
            } finally {
                IOUtils.closeQuietly(out);
            }

        } catch (IOException e) {
            log.error("Error parsing HTML content", e);
        } finally {
            if (proxyResponse != null) {
                proxyResponse.close();
            }
        }

    }

    /**
     * <p>proxyTarget.</p>
     *
     * @param url a {@link java.lang.String} object
     * @param request a {@link javax.portlet.ActionRequest} object
     * @param response a {@link javax.portlet.ActionResponse} object
     * @throws java.io.IOException if any.
     */
    @ActionMapping
    public void proxyTarget(final @RequestParam("proxy.url") String url, final ActionRequest request,
                            final ActionResponse response) throws IOException {
        log.debug("Entering action mapping");
        IContentResponse proxyResponse = null;
        try {
            proxyResponse = invokeProxy(request);
            assert proxyResponse != null;
            request.getPortletSession().setAttribute(PROXY_RESPONSE_KEY, proxyResponse);

            // TODO: this probably can only be an HTTP content type
            if (proxyResponse instanceof HttpContentResponseImpl) {

                // Determine the content type of the proxied response.  If this is
                // not an HTML type, we need to construct a resource URL instead
                final HttpContentResponseImpl httpContentResponse = (HttpContentResponseImpl) proxyResponse;
                final String responseContentType = httpContentResponse.getHeaders().get("Content-Type");
                log.debug("content-type: {}", responseContentType);
                for (Pattern contentType : knownHtmlContentTypes) {
                    if (responseContentType != null && contentType.matcher(responseContentType).matches()) {
                        final Map<String, String[]> params = request.getParameterMap();
                        response.setRenderParameters(params);
                        log.debug("found an HTML content type match, so leaving action mapping now");
                        return;
                    }
                }

            }

            // if this is not an HTML content type, use the corresponding resource
            // URL in the session
            log.warn("We should not reach this code unless the TODO note is wrong");
            final PortletSession session = request.getPortletSession();
            @SuppressWarnings("unchecked") final ConcurrentMap<String, String> rewrittenUrls = (ConcurrentMap<String, String>) session.getAttribute(URLRewritingFilter.REWRITTEN_URLS_KEY);
            response.sendRedirect(rewrittenUrls.get(url));
        } finally {
            // closed in Render phase
            /*
            if (proxyResponse != null) {
                proxyResponse.close();
            }
             */
        }
    }

    /**
     * <p>proxyResourceTarget.</p>
     *
     * @param url a {@link java.lang.String} object
     * @param request a {@link javax.portlet.ResourceRequest} object
     * @param response a {@link javax.portlet.ResourceResponse} object
     */
    @ResourceMapping
    public void proxyResourceTarget(final @RequestParam("proxy.url") String url, final ResourceRequest request, final ResourceResponse response) {
        log.debug("Entering resource mapping");
        IContentResponse proxyResponse = null;
        OutputStream out = null;

        try {
            proxyResponse = invokeProxy(request);

            // TODO: find a cleaner way to handle this.  we probably can't ever
            // have anything except an HTTP response
            if (proxyResponse instanceof HttpContentResponseImpl) {
                // replay any response headers from the proxied target
                HttpContentResponseImpl httpProxyResponse = (HttpContentResponseImpl) proxyResponse;
                for (Map.Entry<String, String> header : httpProxyResponse.getHeaders().entrySet()) {
                    response.setProperty(header.getKey(), header.getValue());
                }
            }

            // write out all proxied content
            out = response.getPortletOutputStream();
            IOUtils.copyLarge(proxyResponse.getContent(), out);
            out.flush();

        } catch (IOException e) {
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, String.valueOf(HttpServletResponse.SC_UNAUTHORIZED));
            log.error("Exception writing proxied content", e);
        } finally {
            if (proxyResponse != null) {
                proxyResponse.close();
            }
            IOUtils.closeQuietly(out);
        }

    }

    /**
     * <p>searchRequest.</p>
     *
     * @param request a {@link javax.portlet.EventRequest} object
     * @param response a {@link javax.portlet.EventResponse} object
     */
    @EventMapping(SearchConstants.SEARCH_REQUEST_QNAME_STRING)
    public void searchRequest(EventRequest request, EventResponse response) {
        log.debug("EVENT HANDLER -- START");

        try {
            // retrieve the HTML content
            final IContentResponse proxyResponse = invokeProxy(request);
            final Document document = parseDocument(request, proxyResponse);
            SearchResults searchResults = searchService.search(request, document);
            response.setEvent(SearchConstants.SEARCH_RESULTS_QNAME, searchResults);
        } catch (IOException e) {
            throw new RuntimeException("Search request failed", e);
        }

        log.debug("EVENT HANDLER -- END");
    }

    /*
     * Implementation (private stuff)
     */

    /**
     * @throws NoSuchBeanDefinitionException If there is no such bean
     */
    private IContentService<IContentRequest, IContentResponse> selectContentService(final PortletRequest req) {
        final PortletPreferences prefs = req.getPreferences();
        final String contentServiceKey = prefs.getValue(CONTENT_SERVICE_KEY, null);
        @SuppressWarnings("unchecked") final IContentService<IContentRequest, IContentResponse> rslt = applicationContext.getBean(contentServiceKey, IContentService.class);
        return rslt;
    }

    private IContentResponse invokeProxy(final PortletRequest req) {
        log.debug("Entering invokeProxy()");
        // locate the content service to use to retrieve our HTML content
        final IContentService<IContentRequest, IContentResponse> contentService = selectContentService(req);

        final IContentRequest proxyRequest;
        try {
            proxyRequest = contentService.getRequest(req);
        } catch (Exception e) {
            throw new RuntimeException("URL was not in the proxy list", e);
        }

        // retrieve the HTML content
        final IContentResponse rslt;
        try {
            rslt = contentService.getContent(proxyRequest, req);
        } catch (Exception e) {
            throw new RuntimeException("Failed to proxy content", e);
        }

        log.debug("Leaving invokeProxy()");
        return rslt;
    }

    private List<IDocumentFilter> prepareFilters(final PortletRequest req) {
        final PortletPreferences preferences = req.getPreferences();
        final List<IDocumentFilter> filters = new ArrayList<IDocumentFilter>();
        final String[] filterKeys = preferences.getValues(FILTER_LIST_KEY, new String[]{});
        for (final String filterKey : filterKeys) {
            final IDocumentFilter filter = applicationContext.getBean(filterKey, IDocumentFilter.class);
            filters.add(filter);
        }
        return filters;
    }

    private Document parseDocument(final PortletRequest req, final IContentResponse proxyResponse) throws IOException {
        log.debug("Entering parseDocument()");
        final PortletPreferences preferences = req.getPreferences();
        String sourceEncodingFormat = preferences.getValue(PREF_CHARACTER_ENCODING, CHARACTER_ENCODING_DEFAULT);
        log.debug("encoding format: {}", sourceEncodingFormat);
        log.debug("proxyResponse content: {}", proxyResponse.getContent().toString());
        log.debug("proxyResponse location: {}", proxyResponse.getProxiedLocation());
        final Document rslt = Jsoup.parse(proxyResponse.getContent(), sourceEncodingFormat,
                proxyResponse.getProxiedLocation());
        log.debug("Leaving parseDocument()");
        return rslt;
    }

}
