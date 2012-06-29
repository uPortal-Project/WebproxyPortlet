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
package org.jasig.portlet.proxy.service.web;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Service("urlRewritingFilter")
public class URLRewritingFilter implements IDocumentFilter {
    
    final public static String REWRITTEN_URLS_KEY = "rewrittenUrls";
    final public static String WHITELIST_REGEXES_KEY = "whitelistRegexes";
    final public static String BASE_URL_KEY = "baseUrl";
    
    final protected Log log = LogFactory.getLog(getClass());
    
    private Map<String, Set<String>> actionElements;
    
    @Resource(name="urlRewritingActionElements")
    public void setActionElements(Map<String, Set<String>> actionElements) {
        this.actionElements = actionElements;
    }

    private Map<String, Set<String>> resourceElements;
    
    @Resource(name="urlRewritingResourceElements")
    public void setResourceElements(Map<String, Set<String>> resourceElements) {
        this.resourceElements = resourceElements;
    }

    @Override
    public void filter(final Document document, final RenderRequest request,
            final RenderResponse response) {
        
        updateUrls(document, actionElements, request, response, true);
        updateUrls(document, resourceElements, request, response, false);

    }
    
    protected void updateUrls(Document document,
            Map<String, Set<String>> elementSet, final RenderRequest request,
            final RenderResponse response, boolean action) {
        
        // attempt to retrieve the list of rewritten URLs from the session
        final PortletSession session = request.getPortletSession();
        @SuppressWarnings("unchecked")
        List<String> rewrittenUrls = (List<String>) session.getAttribute(REWRITTEN_URLS_KEY);
        
        // if the rewritten URLs list doesn't exist yet, create it
        if (rewrittenUrls == null) {
            rewrittenUrls = new ArrayList<String>();
            session.setAttribute(REWRITTEN_URLS_KEY, rewrittenUrls);
        }
        
        // get the list of configured whitelist regexes
        final PortletPreferences preferences = request.getPreferences();
        final String[] whitelistRegexes = preferences.getValues("whitelistRegexes", new String[]{});
        
        // TODO: this base URL needs to be automatically computed for the 
        // individual page, rather than hard-coded for the entire portlet definition
        final String baseUrl = preferences.getValue(BASE_URL_KEY, "");
        
        for (final Map.Entry<String, Set<String>> elementEntry : elementSet.entrySet()) {
            for (final String attributeName : elementEntry.getValue()) {

            // get a list of elements for this element type and iterate through
            // them, updating the relevant URL attribute
            final Elements elements = document.getElementsByTag(elementEntry.getKey());
                for (Element element : elements) {
    
                    String url = element.attr(attributeName);
                    if (StringUtils.isNotBlank(url)) {
                        
                        // transform any relative URLs into absolute URLs
                        // TODO: base URL needs to be dynamically inferred from
                        // request rather than hardcoded per-portlet
                        if (url.startsWith("/") && !url.startsWith("//")) {
                            url = baseUrl.concat(url);
                        } else if (!url.contains("://")) {
                            final StringBuffer buf = new StringBuffer();
                            buf.append(baseUrl);
                            buf.append(request.getContextPath());
                            buf.append("/");
                            buf.append(url);
                            url = buf.toString();
                        }
    
                        // if this URL matches our whitelist regex, rewrite it 
                        // to pass through this portlet
                        for (String regex : whitelistRegexes) {
                            final Pattern pattern = Pattern.compile(regex);  // TODO share compiled regexes
                            if (pattern.matcher(url).find()) {
                                rewrittenUrls.add(url);
                                if (action) {
                                    url = createActionUrl(response, url);
                                } else {
                                    url = createResourceUrl(response, url);
                                }
                            }
                        }
                        
                    }
                    
                    element.attr(attributeName, url);
                    
                    if (elementEntry.getKey().equals("form")) {
                        element.attr("method", "POST");
                    }
                }
                
            }

        }

    }

    protected String createActionUrl(final RenderResponse response, final String url) {
        final PortletURL portletUrl = response.createActionURL();
        portletUrl.setParameter("url", url);
        final StringWriter writer = new StringWriter();
        try {
            portletUrl.write(writer);
            writer.flush();
            return writer.getBuffer().toString();
        } catch (IOException e) {
            log.error("Failed to write portlet URL");
        }

        return null;
    }

    protected String createResourceUrl(final RenderResponse response, final String url) {
        final ResourceURL resourceUrl = response.createResourceURL();
        resourceUrl.setParameter("url", url);
        final StringWriter writer = new StringWriter();
        try {
            resourceUrl.write(writer);
            writer.flush();
            return writer.getBuffer().toString();
        } catch (IOException e) {
            log.error("Failed to write portlet URL");
        }

        return null;
    }

}
