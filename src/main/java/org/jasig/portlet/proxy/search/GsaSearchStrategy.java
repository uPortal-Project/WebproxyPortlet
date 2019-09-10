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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.portlet.EventRequest;
import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apereo.portal.search.PortletUrl;
import org.apereo.portal.search.PortletUrlParameter;
import org.apereo.portal.search.PortletUrlType;
import org.apereo.portal.search.SearchRequest;
import org.apereo.portal.search.SearchResult;
import org.jasig.portlet.proxy.search.util.SearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class GsaSearchStrategy implements ISearchStrategy {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private ISearchService contentSearchProvider;
    @Required
    @Resource(name="contentSearchProvider")
    public void setContentSearchProvider(ISearchService contentSearchProvider) {
        this.contentSearchProvider = contentSearchProvider;
    }
    
    @PostConstruct
    public void init() {
        contentSearchProvider.addSearchStrategy(this);
    }

    @Override
    public String getStrategyName() {
        return "GSA";
    }

    @Override
    public List<SearchResult> search(SearchRequest searchQuery,
            EventRequest request, org.jsoup.nodes.Document ignore) {
        
        List<SearchResult> searchResults = new ArrayList<SearchResult>();
        
        String searchBaseURL = this.buildGsaUrl(searchQuery, request);
        HttpClient client = new DecompressingHttpClient(new DefaultHttpClient());         
        HttpGet get = new HttpGet(searchBaseURL);

        try {
            
            HttpResponse httpResponse = client.execute(get);
            log.debug("STATUS CODE :: "+httpResponse.getStatusLine().getStatusCode());
            
            InputStream in = httpResponse.getEntity().getContent();
                        
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            
            Document doc = builder.parse(in);
            
            log.debug(("GOT InputSource"));
            XPathFactory  factory=XPathFactory.newInstance();
            XPath xPath=factory.newXPath();
                    
            Integer maxCount = Integer.parseInt(xPath.evaluate("count(/GSP/RES/R)", doc));

            final String[] whitelistRegexes = request.getPreferences().getValues("gsaWhitelistRegex", new String[] {});

            log.debug(maxCount + " -- Results");
            for (int count = 1; count <= maxCount; count++ ) {
                String u = xPath.evaluate("/GSP/RES/R["+count+"]/U/text()", doc);
                String t = xPath.evaluate("/GSP/RES/R["+count+"]/T/text()", doc);
                String s = xPath.evaluate("/GSP/RES/R["+count+"]/S/text()", doc);
                
                log.debug("title: [" + t + "]");
                SearchResult result = new SearchResult();
                result.setTitle(t);
                result.setSummary(s);
                
                PortletUrl pUrl = new PortletUrl();
                pUrl.setPortletMode(PortletMode.VIEW.toString());
                pUrl.setType(PortletUrlType.RENDER);
                pUrl.setWindowState(WindowState.MAXIMIZED.toString());
                PortletUrlParameter param = new PortletUrlParameter();
                param.setName("proxy.url");
                param.getValue().add(u);
                pUrl.getParam().add(param);
                result.setPortletUrl(pUrl);
                
                new SearchUtil().updateUrls(u, request, whitelistRegexes);                
                searchResults.add(result);
            }
        
        } catch (IOException ex) {
            log.error(ex.getMessage(),ex);
        } catch (XPathExpressionException ex) {
            log.error(ex.getMessage(),ex);
        } catch (ParserConfigurationException ex) {
            log.error(ex.getMessage(),ex);
        } catch (SAXException ex) {
            log.error(ex.getMessage(),ex);
        }
        
        return searchResults;
        
    }    
    
    private String buildGsaUrl(SearchRequest searchQuery, EventRequest request) {
        String searchTerms = "";
        try {
            searchTerms = URLEncoder.encode(searchQuery.getSearchTerms(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("Search term cannot be converted to UTF-8",e);
        }
        
        String gsa = request.getPreferences().getValue("gsaHost", "");
        String collection = request.getPreferences().getValue("gsaCollection", "");
        String frontend = request.getPreferences().getValue("gsaFrontend", "");
        if (gsa.equals("") || collection.equals("") || frontend.equals("")) {
            log.info("NOT Configured for search -- GSA:"+gsa+" -- COLLECTION:"+collection+" -- frontend:"+frontend);
        }
                
        String searchBaseURL = "http://"+gsa+"/search?q="+searchTerms+"&site="+ collection +"&client="+frontend+"&output=xml_no_dtd";
        log.debug(searchBaseURL); 
        return searchBaseURL;
    }
}
