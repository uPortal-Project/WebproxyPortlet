package org.jasig.portlet.proxy.search.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.portlet.util.PortletUtils;

public class SearchUtil {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

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
