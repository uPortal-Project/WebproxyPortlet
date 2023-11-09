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
package org.jasig.portlet.proxy.search.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import com.liferay.portletmvc4spring.util.PortletUtils;

/**
 * <p>SearchUtil class.</p>
 *
 * @author bjagg
 * @version $Id: $Id
 */
@Slf4j
public class SearchUtil {

    /**
     * <p>updateUrls.</p>
     *
     * @param searchResultUrl a {@link java.lang.String} object
     * @param request a {@link javax.portlet.PortletRequest} object
     * @param whitelistRegexes an array of {@link java.lang.String} objects
     */
    public void updateUrls(final String searchResultUrl,
            final PortletRequest request, String[] whitelistRegexes) {
        final String REWRITTEN_URLS_KEY = "rewrittenUrls";
        
        // attempt to retrieve the list of rewritten URLs from the session
        final PortletSession session = request.getPortletSession();
        ConcurrentMap<String, String> rewrittenUrls;
        synchronized (PortletUtils.getSessionMutex(session)) {
            rewrittenUrls = (ConcurrentMap<String, String>) session
                    .getAttribute(REWRITTEN_URLS_KEY);

            // if the rewritten URLs list doesn't exist yet, create it
            if (rewrittenUrls == null) {
                rewrittenUrls = new ConcurrentHashMap<String, String>();
                session.setAttribute(REWRITTEN_URLS_KEY, rewrittenUrls);
            }
        }

        // if this URL matches our whitelist regex, rewrite it
        // to pass through this portlet
        for (String regex : whitelistRegexes) {
            if (StringUtils.isNotBlank(regex)) {
                final Pattern pattern = Pattern.compile(regex); // TODO share
                                                                // compiled
                                                                // regexes

                if (pattern.matcher(searchResultUrl).find()) {
                    // record that we've rewritten this URL
                    rewrittenUrls.put(searchResultUrl, searchResultUrl);
                    log.debug("added [" + searchResultUrl + "] to rewrittenUrls");
                }
            }
        }
    }
}
