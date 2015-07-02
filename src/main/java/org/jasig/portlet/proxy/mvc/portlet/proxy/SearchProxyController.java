package org.jasig.portlet.proxy.mvc.portlet.proxy;

import javax.annotation.Resource;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jasig.portal.search.SearchConstants;
import org.jasig.portal.search.SearchRequest;
import org.jasig.portal.search.SearchResults;
import org.jasig.portlet.proxy.search.ISearchService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.EventMapping;

@Controller
@RequestMapping("VIEW")
public class SearchProxyController {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private ISearchService searchService;
    @Required
    @Resource(name="contentSearchProvider")
    public void setSearchService(ISearchService searchService) {
	    this.searchService = searchService;
    }
    
	@EventMapping(SearchConstants.SEARCH_REQUEST_QNAME_STRING) 
	public void searchRequest(EventRequest request, EventResponse response) {   
        log.debug("EVENT HANDLER -- START");
        final Event event = request.getEvent();
        final SearchRequest searchQuery = (SearchRequest)event.getValue();
        
        SearchResults searchResults = searchService.search(searchQuery, request);
        
        response.setEvent(SearchConstants.SEARCH_RESULTS_QNAME, searchResults);
        log.debug("EVENT HANDLER -- END");
    }
}
