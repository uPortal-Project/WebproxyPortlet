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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jasig.portlet.proxy.service.IContentResponse;
import org.jasig.portlet.proxy.service.web.HttpContentServiceImpl;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import com.liferay.portletmvc4spring.util.PortletUtils;

/**
 * <p>URLRewritingFilter class.</p>
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
@Service("urlRewritingFilter")
@Slf4j
public class URLRewritingFilter implements IDocumentFilter {

    /** Constant <code>REWRITTEN_URLS_KEY="rewrittenUrls"</code> */
    public final static String REWRITTEN_URLS_KEY = "rewrittenUrls";
    /** Constant <code>WHITELIST_REGEXES_KEY="whitelistRegexes"</code> */
    public final static String WHITELIST_REGEXES_KEY = "whitelistRegexes";

    private Map<String, Set<String>> actionElements;

    /**
     * <p>Setter for the field <code>actionElements</code>.</p>
     *
     * @param actionElements a {@link java.util.Map} object
     */
    @Resource(name = "urlRewritingActionElements")
    public void setActionElements(Map<String, Set<String>> actionElements) {
        this.actionElements = actionElements;
    }

    private Map<String, Set<String>> resourceElements;

    /**
     * <p>Setter for the field <code>resourceElements</code>.</p>
     *
     * @param resourceElements a {@link java.util.Map} object
     */
    @Resource(name = "urlRewritingResourceElements")
    public void setResourceElements(Map<String, Set<String>> resourceElements) {
        this.resourceElements = resourceElements;
    }

    /**
     * This filter is for rewriting URLs inside the proxy, but some protocols
     * should be ignored (NOT rewritten), e.g. 'javascript:' and 'mailto:'
     */
    private enum IgnorableProtocol {

        JAVASCRIPT("javascript:"),

        MAILTO("mailto:");

        private final String prefix;

        private IgnorableProtocol(String prefix) {
            this.prefix = prefix;
        }

        /**
         * Always in lower case
         */
        public String getPrefix() {
            return prefix;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void filter(final Document document, final IContentResponse proxyResponse, final RenderRequest request, final RenderResponse response) {

        updateUrls(document, proxyResponse, actionElements, request, response, true);
        updateUrls(document, proxyResponse, resourceElements, request, response, false);

    }

    /**
     * <p>updateUrls.</p>
     *
     * @param document a {@link org.jsoup.nodes.Document} object
     * @param proxyResponse a {@link org.jasig.portlet.proxy.service.IContentResponse} object
     * @param elementSet a {@link java.util.Map} object
     * @param request a {@link javax.portlet.RenderRequest} object
     * @param response a {@link javax.portlet.RenderResponse} object
     * @param action a boolean
     */
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
            log.trace("Computed base url {} and relative url {} for proxied url {}", baseUrl, relativeUrl, proxyResponse.getProxiedLocation());
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }

        for (final Map.Entry<String, Set<String>> elementEntry : elementSet.entrySet()) {
            for (final String attributeName : elementEntry.getValue()) {

                // get a list of elements for this element type and iterate through
                // them, updating the relevant URL attribute
                final Elements elements = document.getElementsByTag(elementEntry.getKey());
                for (Element element : elements) {

                    String attributeUrl = element.attr(attributeName);
                    log.trace("Considering element {}  with URL attribute {} of value {}", element, attributeName, attributeUrl);

                    // Ignore blank
                    if (StringUtils.isBlank(attributeUrl)) {
                        continue;
                    }

                    // DON'T adjust (i.e. ignore) ignorable protocols
                    boolean ignorable = false;  // default
                    for (IgnorableProtocol p : IgnorableProtocol.values()) {
                        if (attributeUrl.toLowerCase().startsWith(p.getPrefix())) {
                            ignorable = true;
                        }
                    }
                    if (ignorable) {
                        continue;
                    }

                    // if we're proxying a remote website, adjust any
                    // relative URLs into absolute URLs
                    if (baseUrl != null) {

                        // (1) do not prefix absolute URLs
                        if (attributeUrl.contains("://") || attributeUrl.startsWith("//")) {
                            // do nothing...
                        }

                        // (2) if the URL is relative to the server base,
                        // prepend the base URL
                        else if (attributeUrl.startsWith("/")) {
                            attributeUrl = baseUrl.concat(attributeUrl);
                        }

                        // (3) otherwise use the full relative path
                        else {
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
                              rewrittenUrls.put(attributeUrl, createResourceUrl(response, attributeUrl));

                              if (elementEntry.getKey().equals("form")) {
                                  // the form action needs to be set to POST to
                                  // properly pass through our portlet
                                  boolean isPost = "POST".equalsIgnoreCase(element.attr("method"));
                                  if (!isPost) {
                                      element.attr("method", "POST");
                                  }
                                  attributeUrl = createFormUrl(response, isPost, attributeUrl);
                              } else if (action) {
                                  attributeUrl = createActionUrl(response, attributeUrl);
                              } else {
                                  attributeUrl = createResourceUrl(response, attributeUrl);
                              }
                          }
                        }
                    }

                    element.attr(attributeName, attributeUrl.replace("&amp;", "&"));

                }

            }

        }

    }

    /**
     * <p>createFormUrl.</p>
     *
     * @param response a {@link javax.portlet.RenderResponse} object
     * @param isPost a boolean
     * @param url a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    protected String createFormUrl(final RenderResponse response, final boolean isPost, final String url) {
        final PortletURL portletUrl = response.createActionURL();
        portletUrl.setParameter(HttpContentServiceImpl.URL_PARAM, url);
        portletUrl.setParameter(HttpContentServiceImpl.IS_FORM_PARAM, "true");
        portletUrl.setParameter(HttpContentServiceImpl.FORM_METHOD_PARAM, isPost ? "POST" : "GET");
        return portletUrl.toString();
    }

    /**
     * <p>createActionUrl.</p>
     *
     * @param response a {@link javax.portlet.RenderResponse} object
     * @param url a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    protected String createActionUrl(final RenderResponse response, final String url) {
        final PortletURL portletUrl = response.createRenderURL();
        portletUrl.setParameter(HttpContentServiceImpl.URL_PARAM, url);
        return portletUrl.toString();
    }

    /**
     * <p>createResourceUrl.</p>
     *
     * @param response a {@link javax.portlet.RenderResponse} object
     * @param url a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    protected String createResourceUrl(final RenderResponse response, final String url) {
        final ResourceURL resourceUrl = response.createResourceURL();
        resourceUrl.setParameter(HttpContentServiceImpl.URL_PARAM, url);
        return resourceUrl.toString();
    }

    /**
     * <p>getBaseServerUrl.</p>
     *
     * @param fullUrl a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     * @throws java.net.URISyntaxException if any.
     */
    protected String getBaseServerUrl(final String fullUrl) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(fullUrl);
        uriBuilder.removeQuery();
        uriBuilder.setPath("");
        return uriBuilder.build().toString();
    }

    /**
     * <p>getRelativePathUrl.</p>
     *
     * @param fullUrl a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     * @throws java.net.URISyntaxException if any.
     */
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
