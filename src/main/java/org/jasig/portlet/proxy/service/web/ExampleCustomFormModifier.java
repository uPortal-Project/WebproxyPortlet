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
package org.jasig.portlet.proxy.service.web;

import lombok.extern.slf4j.Slf4j;

import javax.portlet.PortletPreferences;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>ExampleCustomFormModifier class.</p>
 *
 * @author bjagg
 * @version $Id: $Id
 */
@Slf4j
public class ExampleCustomFormModifier implements IAuthenticationFormModifier {

    /**
     * {@inheritDoc}
     *
     * Example of modifications to a login form that invokes the system to gateway to in order to obtain a token
     * from that system that must be submitted with the login form and inserts the token into the url to submit.
     * Also demonstrates that you can modify the url to submit to.
     */
	@Override
    public void modifyHttpContentRequest(HttpContentRequestImpl contentRequest, PortletPreferences preferences) throws IOException {
        String urlSource ="https://prod.sdbor.edu/WebAdvisor/webadvisor?&TYPE=M&PID=CORE-WBMAIN&TOKENIDX="; //must be secure
        URL xmlURLToOpen = new URL(urlSource);

        // Set timeout values to insure a poor network connection doesn't lock up this thread indefinitely.
        URLConnection connection = xmlURLToOpen.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(10000);
        String headerInfo = connection.getHeaderFields().toString();
        int headerInfoBegin = headerInfo.indexOf("LASTTOKEN=");
        String tokenID = headerInfo.substring(headerInfoBegin+10,headerInfoBegin+20);
        tokenID = tokenID.replaceAll("=","");
        tokenID = tokenID.replaceAll(",","");
        contentRequest.addParameter("tokenID", tokenID); // For use by javascript
        log.debug("urlSource: " + urlSource);
        String postUrl = urlSource+tokenID+"&SS=LGRQ&URL=https%3A%2F%2Fwa-usd.prod.sdbor.edu%2FWebAdvisor%2Fwebadvisor%3F%26TYPE%3DM%26PID%3DCORE-WBMAIN%26TOKENIDX%3D"+tokenID;
        log.debug("postURL: " + postUrl);
        contentRequest.setProxiedLocation(postUrl);
    }
	
}
