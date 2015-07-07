package org.jasig.portlet.proxy.search;

import javax.portlet.EventRequest;

import org.jasig.portal.search.SearchRequest;
import org.jasig.portal.search.SearchResults;

public interface ISearchService {
    
    public void addSearchStrategy(ISearchStrategy strategy);
    
    public String[] getStrategyNames();

    public SearchResults search(SearchRequest searchQuery, EventRequest request);

}
