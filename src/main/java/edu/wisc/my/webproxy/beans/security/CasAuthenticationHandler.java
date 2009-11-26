/**
 * Copyright (c) 2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */
package edu.wisc.my.webproxy.beans.security;

import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;

import edu.wisc.my.webproxy.beans.config.GeneralConfigImpl;
import edu.wisc.my.webproxy.portlet.WebProxyPortlet;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;

public class CasAuthenticationHandler {
  
  public static final String CAS_AUTHENTICATED_SESSION_FLAG = "edu.wisc.my.webproxy.beans.security.CasAuthenticationHandler.CAS_AUTHENTICATED_SESSION_FLAG";
  private static final Log log = LogFactory.getLog(CasAuthenticationHandler.class);
  protected Cas20ProxyTicketValidator ticketValidator;
  protected String myService;

  /**
   * @return the ticketValidator
   */
  public Cas20ProxyTicketValidator getTicketValidator() {
    return this.ticketValidator;
  }

  /**
   * @param ticketValidator the ticketValidator to set
   */
  public void setTicketValidator(Cas20ProxyTicketValidator ticketValidator) {
    this.ticketValidator = ticketValidator;
  }

  /**
   * @return the myService
   */
  public String getMyService() {
    return this.myService;
  }

  /**
   * @param myService the myService to set
   */
  public void setMyService(String myService) {
    this.myService = myService;
  }

  public String authenticate(PortletRequest request, String myProxyTicket) {
    PortletPreferences myPreferences = request.getPreferences();
    
    // The WebProxyPortlet's initial URL becomes the CAS service
    String service = myPreferences.getValue(GeneralConfigImpl.BASE_URL, null);
    Assertion assertion = null;
    try {
      assertion = this.ticketValidator.validate(myProxyTicket, this.myService);
    } catch (TicketValidationException ex) {
      log.error("Unable to validate my proxy ticket: " + myProxyTicket + " for service: " + this.myService, ex);
      return null;
    }
    // Request a proxy ticket for the service, and make a new URL for WebProxyPortlet to use
    String proxyTicket = assertion.getPrincipal().getProxyTicketFor(service);
    String newServiceUrl = service + "?ticket=" + proxyTicket;
    return newServiceUrl;
  }
}
