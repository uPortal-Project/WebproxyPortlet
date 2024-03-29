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
package org.jasig.portlet.proxy.service.proxy.document;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jasig.portlet.proxy.service.IContentResponse;
import org.jsoup.nodes.Document;

/**
 * IDocumentFilter provides an interface for modifying a retrieved document
 * prior to displaying it to an end user.
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
public interface IDocumentFilter {

    /**
     * Filter a provided document.
     *
     * @param document a {@link org.jsoup.nodes.Document} object
     * @param proxyResponse a {@link org.jasig.portlet.proxy.service.IContentResponse} object
     * @param portletRequest a {@link javax.portlet.RenderRequest} object
     * @param portletResponse a {@link javax.portlet.RenderResponse} object
     */
    public void filter(Document document, IContentResponse proxyResponse, RenderRequest portletRequest,
            RenderResponse portletResponse);
    
}
