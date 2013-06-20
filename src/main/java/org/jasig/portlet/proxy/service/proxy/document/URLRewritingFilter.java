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
package org.jasig.portlet.proxy.service.proxy.document;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jasig.portlet.proxy.service.IContentResponse;
import org.jasig.portlet.proxy.service.web.HttpContentServiceImpl;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.portlet.util.PortletUtils;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Service("urlRewritingFilter")
public class URLRewritingFilter implements IDocumentFilter {

    public final static String REWRITTEN_URLS_KEY = "rewrittenUrls";
    public final static String WHITELIST_REGEXES_KEY = "whitelistRegexes";

    protected static final String JAVASCRIPT_PREFIX = "JAVASCRIPT:";
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Map<String, Set<String>> actionElements;

    @Resource(name = "urlRewritingActionElements")
    public void setActionElements(Map<String, Set<String>> actionElements) {
        this.actionElements = actionElements;
    }

    private Map<String, Set<String>> resourceElements;

    @Resource(name = "urlRewritingResourceElements")
    public void setResourceElements(Map<String, Set<String>> resourceElements) {
        this.resourceElements = resourceElements;
    }

    @Override
    public void filter(final Document document, final IContentResponse proxyResponse, final RenderRequest request, final RenderResponse response) {

        updateUrls(document, proxyResponse, actionElements, request, response, true);
        updateUrls(document, proxyResponse, resourceElements, request, response, false);

    }

    protected void updateUrls(final Document document, final IContentResponse proxyResponse, final Map<String, Set<String>> elementSet,
            final RenderRequest request, final RenderResponse response, boolean action) {

        // attempt to retrieve the list of rewritten URLs from the session
        final PortletSession session = request.getPortletSession();
        ConcurrentMap<String, String> rewrittenUrls;
        synchronized (PortletUtils.getSessionMutex(session)) {
            rewrittenUrls = (ConcurrentMap<String, String>) session.getAttribute(REWRITTEN_URLS_KEY);

            // if the rewritten URLs list doesn't exist yet, create it
            if (rewrittenUrls == null) {
                rewrittenUrls = new ConcurrentHashMap<String, String>();
                session.setAttribute(REWRITTEN_URLS_KEY, rewrittenUrls);
            }
        }

        // get the list of configured whitelist regexes
        final PortletPreferences preferences = request.getPreferences();
        final String[] whitelistRegexes = preferences.getValues("whitelistRegexes", new String[] {});

        // If we're proxying a remote website (as opposed to a local file system 
        // resources, we'll need to transform any relative URLs.  To do this,
        // we first compute the base and relative URLs for the page.
        String baseUrl = null;
        String relativeUrl = null;
        try {
            baseUrl = getBaseServerUrl(proxyResponse.getProxiedLocation());
            relativeUrl = getRelativePathUrl(proxyResponse.getProxiedLocation());
            LOG.trace("Computed base url {} and relative url {} for proxied url {}", baseUrl, relativeUrl, proxyResponse.getProxiedLocation());
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        }

        for (final Map.Entry<String, Set<String>> elementEntry : elementSet.entrySet()) {
            for (final String attributeName : elementEntry.getValue()) {

                // get a list of elements for this element type and iterate through
                // them, updating the relevant URL attribute
                final Elements elements = document.getElementsByTag(elementEntry.getKey());
                for (Element element : elements) {

                    String attributeUrl = element.attr(attributeName);
                    LOG.trace("Considering element {}  with URL attribute {} of value {}", element, attributeName, attributeUrl);
                    
                    // don't adjust or filter javascript url targets
                    if (StringUtils.isNotBlank(attributeUrl) && !attributeUrl.startsWith(JAVASCRIPT_PREFIX) && 
                        !attributeUrl.startsWith(JAVASCRIPT_PREFIX.toLowerCase())) {

                        // if we're proxying a remote website, adjust any 
                        // relative URLs into absolute URLs
                        if (baseUrl != null) {

                            // if the URL is relative to the server base, prepend
                            // the base URL
                            if (attributeUrl.startsWith("/") && !attributeUrl.startsWith("//")) {
                                attributeUrl = baseUrl.concat(attributeUrl);
                            }

                            // if the URL contains no path information, use
                            // the full relative path
                            else if (!attributeUrl.contains("://")) {
                                attributeUrl = relativeUrl.concat(attributeUrl);
                            }

                        }

                        // if this URL matches our whitelist regex, rewrite it 
                        // to pass through this portlet
                        for (String regex : whitelistRegexes) {

                            if (StringUtils.isNotBlank(regex)) {
                              final Pattern pattern = Pattern.compile(regex);  // TODO share compiled regexes
                              if (pattern.matcher(attributeUrl).find()) {
  
                                  // record that we've rewritten this URL
                                  rewrittenUrls.put(attributeUrl, attributeUrl);
  
                                  // TODO: the value in the rewritten URLs map needs to 
                                  // be a resource URL.  we also want to key URLs by a short
                                  // string rather than the full URL
  
                                  if (elementEntry.getKey().equals("form")) {
                                      // the form action needs to be set to POST to
                                      // properly pass through our portlet
                                      boolean isPost = "POST".equalsIgnoreCase(element.attr("method"));
                                      if (!isPost) {
                                          element.attr("method", "POST");
                                      }
                                      attributeUrl = createFormUrl(response, isPost, attributeUrl);
                                  }
  
                                  else if (action) {
                                      attributeUrl = createActionUrl(response, attributeUrl);
                                  }
  
                                  else {
                                      attributeUrl = createResourceUrl(response, attributeUrl);
                                  }
                              }
                            }
                        }

                    }

                    element.attr(attributeName, attributeUrl.replace("&amp;", "&"));

                }

            }

        }

    }

    protected String createFormUrl(final RenderResponse response, final boolean isPost, final String url) {
        final PortletURL portletUrl = response.createActionURL();
        portletUrl.setParameter(HttpContentServiceImpl.URL_PARAM, url);
        portletUrl.setParameter(HttpContentServiceImpl.IS_FORM_PARAM, "true");
        portletUrl.setParameter(HttpContentServiceImpl.FORM_METHOD_PARAM, isPost ? "POST" : "GET");
        return portletUrl.toString();
    }

    protected String createActionUrl(final RenderResponse response, final String url) {
        final PortletURL portletUrl = response.createActionURL();
        portletUrl.setParameter(HttpContentServiceImpl.URL_PARAM, url);
        return portletUrl.toString();
    }

    protected String createResourceUrl(final RenderResponse response, final String url) {
        final ResourceURL resourceUrl = response.createResourceURL();
        resourceUrl.setParameter(HttpContentServiceImpl.URL_PARAM, url);
        return resourceUrl.toString();
    }

    protected String getBaseServerUrl(final String fullUrl) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(fullUrl);
        uriBuilder.removeQuery();
        uriBuilder.setPath("");
        return uriBuilder.build().toString();
    }

    protected String getRelativePathUrl(final String fullUrl) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(fullUrl);
        uriBuilder.removeQuery();
        final String path = uriBuilder.getPath();
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash < 0) {
            uriBuilder.setPath("");
        } else {
            uriBuilder.setPath(path.substring(0, lastSlash + 1));
        }
        return uriBuilder.build().toString();
    }

}
