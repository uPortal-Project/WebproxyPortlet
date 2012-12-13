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

package org.jasig.portlet.cas;

import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;

public class CASProxyTicketServiceUserInfoImpl implements ICASProxyTicketService {
	
	protected final Log log = LogFactory.getLog(this.getClass());

	private String serviceUrl;
	
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	private TicketValidator ticketValidator;
	
	public void setTicketValidator(TicketValidator ticketValidator) {
		this.ticketValidator = ticketValidator;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.portlet.cas.ICASProxyTicketService#getProxyTicket(javax.portlet.PortletRequest)
	 */
	public Assertion getProxyTicket(PortletRequest request) {

		// retrieve the CAS ticket from the UserInfo map
		@SuppressWarnings("unchecked")
		Map<String,String> userinfo = (Map<String,String>) request.getAttribute(PortletRequest.USER_INFO);
		String ticket = (String) userinfo.get("casProxyTicket");
		
		if (ticket == null) {
			log.debug("No CAS ticket found in the UserInfo map");
			return null;
		}
		
		log.debug("serviceURL: " + this.serviceUrl + ", ticket: " + ticket);
		
		/* contact CAS and validate */
		
		try {
			Assertion assertion = ticketValidator.validate(ticket, this.serviceUrl);
			return assertion;
		} catch (TicketValidationException e) {
			log.warn("Failed to validate proxy ticket", e);
			return null;
		}

	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jasig.portlet.cas.ICASProxyTicketService#getCasServiceToken(edu.yale.its.tp.cas.client.CASReceipt, java.lang.String)
	 */
	public String getCasServiceToken(Assertion assertion, String target) {
        final String proxyTicket = assertion.getPrincipal().getProxyTicketFor(target);
        if (proxyTicket == null){
            log.error("Failed to retrieve proxy ticket for assertion [" + assertion.toString() + "].  Is the PGT still valid?");
            return null;
        }
        if (log.isTraceEnabled()) {
            log.trace("returning from getCasServiceToken(), returning proxy ticket ["
                    + proxyTicket + "]");
        }
        return proxyTicket;
	}

}
