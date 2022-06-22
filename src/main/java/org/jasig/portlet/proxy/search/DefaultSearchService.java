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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.PortletPreferences;
import org.apereo.portal.search.SearchRequest;
import org.apereo.portal.search.SearchResult;
import org.apereo.portal.search.SearchResults;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>DefaultSearchService class.</p>
 *
 * @author bjagg
 * @version $Id: $Id
 */
public class DefaultSearchService implements ISearchService {

    /** Constant <code>SEARCH_STRATEGIES_PREFERENCE="searchStrategies"</code> */
    public static final String SEARCH_STRATEGIES_PREFERENCE = "searchStrategies";

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<String, ISearchStrategy> strategies = new HashMap<String, ISearchStrategy>();
    /** {@inheritDoc} */
    @Override
    public void addSearchStrategy(ISearchStrategy strategy) {
        strategies.put(strategy.getStrategyName(), strategy);
    }

    /**
     * <p>getStrategyNames.</p>
     *
     * @return an array of {@link java.lang.String} objects
     */
    public String[] getStrategyNames() {
        return (String[]) strategies.keySet().toArray(new String[]{});
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults search(final EventRequest request, final Document document) {

        final Event event = request.getEvent();
        final SearchRequest searchQuery = (SearchRequest) event.getValue();

        final SearchResults searchResults = new SearchResults();
        searchResults.setQueryId(searchQuery.getQueryId());
        searchResults.setWindowId(request.getWindowID());

        final PortletPreferences preferences = request.getPreferences();
        String[] strategyNames = preferences.getValues(SEARCH_STRATEGIES_PREFERENCE, new String[]{});
        for (int i = 0; i < strategyNames.length; i++) {
            ISearchStrategy strategy = strategies.get(strategyNames[i]);
            if (strategy != null) {
                List<SearchResult> results = strategy.search(searchQuery, request, document);
                searchResults.getSearchResult().addAll(results);
            }
        }

        return searchResults;

    }

}
