package org.jasig.portlet.proxy.search;

import java.util.List;

import javax.portlet.EventRequest;

import org.jasig.portal.search.SearchRequest;
import org.jasig.portal.search.SearchResult;
import org.jsoup.nodes.Document;

public interface ISearchStrategy {

    public String getStrategyName();
    
    public List<SearchResult> search(SearchRequest searchQuery, EventRequest request, Document document);

}
