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
package org.jasig.portlet.proxy.service.web.interceptor;

import java.net.URISyntaxException;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * ProxyCASAuthenticationPreInterceptor provides CAS authentication for HTTP
 * requests.  This implementation requests a CAS proxy ticket from the portal, then
 * uses this to obtain a proxy ticket for the target URL.  The URL is then
 * modified to contain the CAS ticket.  The ticket is only added when the 
 * base class believes no session exists on the target.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Service("proxyCASAuthenticationPreInterceptor")
public class ProxyCASAuthenticationPreInterceptor extends AuthenticationPreInterceptor {

    private String      serviceUrl;

    /**
     * Set the base URL of the CAS server
     * 
     * @param serviceUrl
     */
    @Value("${portal.server.base.url}/${portlet.context}")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    private TicketValidator ticketValidator;

    /**
     * Set the ticket validator
     * 
     * @param ticketValidator
     */
    @Autowired(required = true)
    public void setTicketValidator(TicketValidator ticketValidator) {
        this.ticketValidator = ticketValidator;
    }

    @Override
    protected void prepareAuthentication(HttpContentRequestImpl contentRequest, PortletRequest portletRequest) {

        // retrieve the CAS ticket from the UserInfo map
        @SuppressWarnings("unchecked")
        Map<String, String> userinfo = (Map<String, String>) portletRequest.getAttribute(PortletRequest.USER_INFO);
        String ticket = (String) userinfo.get("casProxyTicket");

        if (ticket == null) {
            log.warn("No CAS ticket found in the UserInfo map. Is 'casProxyTicket' user-attribute declared in the portlet configuration?");
            return;
        }

        log.debug("serviceURL: {}, ticket: {}", this.serviceUrl, ticket);

        /* contact CAS and validate */
        try {

            // validate the ticket provided by the portal
            final Assertion assertion = this.ticketValidator.validate(ticket, this.serviceUrl);

            // get a proxy ticket for the target URL
            final String proxyTicket = assertion.getPrincipal().getProxyTicketFor(contentRequest.getProxiedLocation());
            if (proxyTicket == null) {
                log.error("Failed to retrieve proxy ticket for assertion [{}]. Is the PGT still valid?", assertion.toString());
                return;
            }
            log.trace("returning from proxy ticket request with proxy ticket [{}]", proxyTicket);
            
            // update the URL to include the proxy ticket
            final URIBuilder builder = new URIBuilder(contentRequest.getProxiedLocation());
            builder.addParameter("ticket", proxyTicket);
            
            String proxiedLocation = builder.build().toString();
            log.debug("Set final proxied location to be {}", proxiedLocation);
            contentRequest.setProxiedLocation(proxiedLocation);

        } catch (TicketValidationException e) {
            log.warn("Failed to validate proxy ticket", e);
            return;
        } catch (URISyntaxException e) {
            log.warn("Failed to parse proxy URL", e);
            return;
        }

    }

	@Override
	public boolean validate(HttpContentRequestImpl proxyRequest,
			PortletRequest portletRequest) {
		return true;
	}

}
