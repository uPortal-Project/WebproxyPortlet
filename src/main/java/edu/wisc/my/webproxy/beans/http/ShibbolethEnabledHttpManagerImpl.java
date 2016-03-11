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
package edu.wisc.my.webproxy.beans.http;

import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jasig.portal.security.provider.saml.SAMLSession;

import edu.wisc.my.webproxy.beans.PortletPreferencesWrapper;
import edu.wisc.my.webproxy.beans.config.ConfigUtils;
import edu.wisc.my.webproxy.beans.config.HttpClientConfigImpl;

/**
 * ShibbolethEnabledHttpManagerImpl subclasses HttpManagerImpl to configurably
 * provide a Shibboleth-authenticating HttpClient instance.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
public class ShibbolethEnabledHttpManagerImpl extends HttpManagerImpl {
	private final static String AUTH_TYPE_SHIBBOLETH = "SHIBBOLETH";

	private String spPrivateKey;
	private String spCertificate;
	private String portalEntityID;
	private boolean skipValidateIdp = false;

	/**
   * @return the portalEntityID
   */
  public String getPortalEntityID() {
    return portalEntityID;
  }

  /**
   * @param portalEntityID the portalEntityID to set
   */
  public void setPortalEntityID(String portalEntityID) {
    this.portalEntityID = portalEntityID;
  }

  public void setSpPrivateKey(String spPrivateKey) {
		this.spPrivateKey = spPrivateKey;
	}

	public void setSpCertificate(String spCertificate) {
		this.spCertificate = spCertificate;
	}
	
	public boolean isSkipValidateIdp() {
        return skipValidateIdp;
    }

	/**
	 * Set if the IDP host name validation step should be skipped during authentication. This
     * is generally needed when using a WAYF since the target site will reference the WAYF and
     * not the IDP in the response.
     * 
	 * @see SAMLSession#setSkipValidateIdp(boolean)
	 */
    public void setSkipValidateIdp(boolean skipValidateIdp) {
        this.skipValidateIdp = skipValidateIdp;
    }

    /**
	 * Create a new HttpClient which may potentially be pre-configured for
	 * Shibboleth authentication.  If the current portlet instance is configured
	 * to perform Shibboleth-based authentication, this implementation should 
	 * construct a new SAMLSession and return the associated DefaultHttpClient
	 * instance.  If the portlet is not using Shibboleth for authentication, or
	 * does not have authentication enabled, this method will delegate to the
	 * default parent impelmentation.
	 * 
	 * @param request portlet request
	 * @return new DefaultHttpClient instance
	 */
	@Override
	protected DefaultHttpClient createHttpClient(PortletRequest request) {
		// determine whether authentication is enabled, and if so, which type
        final PortletPreferences myPreferences = new PortletPreferencesWrapper(request.getPreferences(), (Map<?, ?>)request.getAttribute(PortletRequest.USER_INFO));
        final boolean authEnabled = new Boolean(myPreferences.getValue(HttpClientConfigImpl.AUTH_ENABLE, null)).booleanValue();
        final String authType = ConfigUtils.checkEmptyNullString(myPreferences.getValue(HttpClientConfigImpl.AUTH_TYPE, ""), "");

        // If this portlet instance is configured to use shibboleth authentication,
        // construct a new SAMLSession and return the HttpClient instance
        // returned by the SAMLSession.  
        if (authEnabled && AUTH_TYPE_SHIBBOLETH.equals(authType)) {
    		final String samlAssertion = getAssertion(request);
    		
    		final HttpParams params = new BasicHttpParams();
    		final ClientConnectionManager clientConnectionManager = this.createClientConnectionManager(request, params);
    		final SAMLSession samlSession = new SAMLSession(samlAssertion, clientConnectionManager, params);
    		
    		samlSession.setSkipValidateIdp(this.skipValidateIdp);
    		samlSession.setPortalEntityID(portalEntityID);

    		if (spPrivateKey != null && spCertificate != null) {
    			samlSession.setIdPClientPrivateKeyAndCert(spPrivateKey,
    					spCertificate);
    		}

    		String idpPublicKeys = getIdPPublicKeys(request);
    		if (idpPublicKeys != null) {
    			samlSession.setIdPServerPublicKeys(idpPublicKeys);
    		}

    		this.logger.debug("Returning new Shibbolized HttpClient instance");
    		DefaultHttpClient client = (DefaultHttpClient) samlSession.getHttpClient();
    		client = setHttpClientTimeouts(request, client);
    		return client;
        }
        
        // If the portlet is not using shibboleth authentication, call the 
        // parent method as usual.
		DefaultHttpClient client = super.createHttpClient(request);
		client = setHttpClientTimeouts(request, client);
		return client;
	}

	/**
	 * Get the SAML assertion from the UserInfo map.
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String getAssertion(PortletRequest request) {
		Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
		String samlAssertion = (String) userInfo.get("samlAssertion");
		return samlAssertion;
	}

	/**
	 * Get the IdP Public keys from the UserInfo map.
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String getIdPPublicKeys(PortletRequest request) {
		Map userInfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
		String idpPublicKeys = (String) userInfo.get("idpPublicKeys");
		return idpPublicKeys;
	}

}
