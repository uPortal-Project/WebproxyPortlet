/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.proxy.service.proxy.document;


import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jasig.portlet.proxy.service.IContentResponse;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Service("contentClippingFilter")
public class ContentClippingFilter implements IDocumentFilter {

    public static final String SELECTOR_KEY = "clippingSelector";
    
    @Override
    public void filter(final Document document,
            final IContentResponse proxyResponse, final RenderRequest request,
            final RenderResponse response) {
        
        // get the clipping selector for this portlet configuration
        final PortletPreferences preferences = request.getPreferences();
        final String selector = preferences.getValue(SELECTOR_KEY, null);
        
        // locate the matching element in the document and replace the document
        // with just that node subtree
        final Elements elements = document.select(selector);        
        if (elements.size() > 0) {
            document.html("").appendChild(elements.get(0));
        }
    }
    
}
