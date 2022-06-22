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

import javax.portlet.EventRequest;

import org.apereo.portal.search.SearchResults;
import org.jsoup.nodes.Document;

/**
 * <p>ISearchService interface.</p>
 *
 * @author bjagg
 * @version $Id: $Id
 */
public interface ISearchService {

    /**
     * <p>addSearchStrategy.</p>
     *
     * @param strategy a {@link org.jasig.portlet.proxy.search.ISearchStrategy} object
     */
    void addSearchStrategy(ISearchStrategy strategy);

    /**
     * <p>getStrategyNames.</p>
     *
     * @return an array of {@link java.lang.String} objects
     */
    String[] getStrategyNames();

    /**
     * <p>search.</p>
     *
     * @param request a {@link javax.portlet.EventRequest} object
     * @param document a {@link org.jsoup.nodes.Document} object
     * @return a {@link org.apereo.portal.search.SearchResults} object
     */
    SearchResults search(EventRequest request, Document document);

}
