package org.jasig.portlet.proxy.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.EventRequest;
import javax.portlet.PortletPreferences;
import org.jasig.portal.search.SearchRequest;
import org.jasig.portal.search.SearchResult;
import org.jasig.portal.search.SearchResults;
import org.jasig.portlet.proxy.service.GenericContentRequestImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSearchService implements ISearchService {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private Map<String, ISearchStrategy> strategies = new HashMap<String, ISearchStrategy>();
    @Override
    public void addSearchStrategy(ISearchStrategy strategy) {
        strategies.put(strategy.getStrategyName(), strategy);
    }
    
    public String[] getStrategyNames() {
        return (String[]) strategies.keySet().toArray(new String[]{});
    }
    
    @Override
    public SearchResults search(SearchRequest searchQuery, EventRequest request) {
        final SearchResults searchResults = new SearchResults();
        searchResults.setQueryId(searchQuery.getQueryId());
        searchResults.setWindowId(request.getWindowID());
        
        String baseUrl = request.getPreferences().getValue(
                GenericContentRequestImpl.CONTENT_LOCATION_PREFERENCE, "/");
        try {
            Document document = Jsoup.connect(baseUrl).get();
            final PortletPreferences preferences = request.getPreferences();
            String[] strategyNames = preferences.getValues("searchStrategies", new String[]{});
            for (int i = 0; i < strategyNames.length; i++) {
                ISearchStrategy strategy = strategies.get(strategyNames[i]);
                if (strategy != null) {
                    List<SearchResult> results = strategy.search(searchQuery, request, document);
                    searchResults.getSearchResult().addAll(results);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        
        return searchResults;

    }
}
