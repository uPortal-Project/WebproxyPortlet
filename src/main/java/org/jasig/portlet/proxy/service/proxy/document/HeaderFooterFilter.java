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

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.proxy.service.IContentResponse;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * HeaderFooterFilter can add static HTML header and footer content to any 
 * proxied content.
 * 
 * @author Jen Bourey
 */
@Service("contentWrappingFilter")
public class HeaderFooterFilter implements IDocumentFilter {

    public static final String HEADER_KEY = "headerHtml";
    public static final String FOOTER_KEY = "footerHtml";

	@Override
	public void filter(Document document, IContentResponse proxyResponse,
			RenderRequest portletRequest, RenderResponse portletResponse) {
		
		// get any configured header / footer content from the portlet preferences
		final PortletPreferences preferences = portletRequest.getPreferences();
		final String header = preferences.getValue(HEADER_KEY, null);
		final String footer = preferences.getValue(FOOTER_KEY, null);

		// If both a header and footer have been specified, there's some chance they 
		// could be intended to wrap the proxied content.  We can't wrap content 
		// using the prepend / append methods, since those would automatically 
		// close partial elements in each header / footer
		if (StringUtils.isNotBlank(header) && StringUtils.isNotBlank(footer)) {
			document.html(header.concat(document.html()).concat(footer));
		} 
		
		else if (StringUtils.isNotBlank(header)) {
			document.prepend(header);
		} 
		
		else if (StringUtils.isNotBlank(footer)) {
			document.append(footer);
		}
		
	}

		
	
}
