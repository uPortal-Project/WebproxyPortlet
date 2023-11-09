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
package org.jasig.portlet.proxy.search;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.portlet.EventRequest;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import lombok.extern.slf4j.Slf4j;
import org.apereo.portal.search.PortletUrl;
import org.apereo.portal.search.PortletUrlParameter;
import org.apereo.portal.search.PortletUrlType;
import org.apereo.portal.search.SearchRequest;
import org.apereo.portal.search.SearchResult;
import org.jasig.portlet.proxy.search.util.SearchUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Required;

/**
 * <p>AnchorSearchStrategy class.</p>
 *
 * @author bjagg
 * @version $Id: $Id
 */
@Slf4j
public class AnchorSearchStrategy implements ISearchStrategy {
    private ISearchService contentSearchProvider;
    /**
     * <p>Setter for the field <code>contentSearchProvider</code>.</p>
     *
     * @param contentSearchProvider a {@link org.jasig.portlet.proxy.search.ISearchService} object
     */
    @Required
    @Resource(name="contentSearchProvider")
    public void setContentSearchProvider(ISearchService contentSearchProvider) {
        this.contentSearchProvider = contentSearchProvider;
    }
    
    /**
     * <p>init.</p>
     */
    @PostConstruct
    public void init() {
        contentSearchProvider.addSearchStrategy(this);
    }

    /** {@inheritDoc} */
    @Override
    public String getStrategyName() {
        return "Anchor";
    }

    /** {@inheritDoc} */
    @Override
    public List<SearchResult> search(SearchRequest searchQuery,
            EventRequest request, Document document) {
        List<SearchResult> results = new ArrayList<SearchResult>();
        final String[] whitelistRegexes = request.getPreferences().getValues("anchorWhitelistRegex", new String[] {});
        String searchTerms = searchQuery.getSearchTerms().toLowerCase();
                
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String linkUrl = link.attr("abs:href");
            for (String searchTerm: searchTerms.split(" ")) {
                if (link.text().toLowerCase().contains(searchTerm)) {
                    log.debug("found a match, term: [" + searchTerm + "], anchor URL: [" + linkUrl + "], anchor text: [" + link.text() + "]");
                    SearchResult result = new SearchResult();
                    result.setTitle(link.text());
                    result.setSummary(link.text());
                    
                    PortletUrl pUrl = new PortletUrl();
                    pUrl.setPortletMode(PortletMode.VIEW.toString());
                    pUrl.setType(PortletUrlType.RENDER);
                    pUrl.setWindowState(WindowState.MAXIMIZED.toString());
                    PortletUrlParameter param = new PortletUrlParameter();
                    param.setName("proxy.url");
                    param.getValue().add(linkUrl);
                    pUrl.getParam().add(param);

                    new SearchUtil().updateUrls(linkUrl, request, whitelistRegexes);
     
                    result.setPortletUrl(pUrl);
                    results.add(result);
                }
            }            
        }        
        return results;
    }    
}
