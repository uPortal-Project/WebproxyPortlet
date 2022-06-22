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
package org.jasig.portlet.proxy.mvc.portlet.proxy;

import java.beans.PropertyEditorSupport;


/**
 * <p>ProxyPortletForm class.</p>
 *
 * @author bjagg
 * @version $Id: $Id
 */
public class ProxyPortletForm {

    /** Constant <code>AUTHENTICATION_TYPE="authType"</code> */
    public static final String AUTHENTICATION_TYPE = "authType";

    enum ProxyAuthType {
        NONE, CAS, BASIC, BASIC_PORTLET_PREFERENCES;
    }

    public static final class ProxyAuthTypeEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) {
            ProxyAuthType value = ProxyAuthType.valueOf(text);
            setValue(value);
        }
    }

    private String contentService;
    private String location;
    private String maxLocation;
    private String pageCharacterEncodingFormat;

    // filters
    private String whitelistRegexes; // TODO: support multiple whitelist entries
    private String clippingSelector;
    private String header;
    private String footer;

    // authentication
    private ProxyAuthType authType = ProxyAuthType.NONE;
    private String usernameKey = "user.login.id";
    private String passwordKey = "password";
    private String basicAuthPreferencesUsername;
    private String basicAuthPreferencesPassword;

    // strategies
    private String[] searchStrategies;

    // gsa
    private String gsaHost;
    private String gsaCollection;
    private String gsaFrontend;
    private String gsaWhitelistRegex;

    // anchor
    private String anchorWhitelistRegex;

    /**
     * <p>Getter for the field <code>contentService</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getContentService() {
        return contentService;
    }

    /**
     * <p>Setter for the field <code>contentService</code>.</p>
     *
     * @param contentService a {@link java.lang.String} object
     */
    public void setContentService(String contentService) {
        this.contentService = contentService;
    }

    /**
     * <p>Getter for the field <code>location</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLocation() {
        return location;
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param location a {@link java.lang.String} object
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * <p>Getter for the field <code>maxLocation</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getMaxLocation() {
        return maxLocation;
    }

    /**
     * <p>Setter for the field <code>maxLocation</code>.</p>
     *
     * @param maxLocation a {@link java.lang.String} object
     */
    public void setMaxLocation(String maxLocation) {
        this.maxLocation = maxLocation;
    }

    /**
     * <p>Getter for the field <code>clippingSelector</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getClippingSelector() {
        return clippingSelector;
    }

    /**
     * <p>Getter for the field <code>whitelistRegexes</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getWhitelistRegexes() {
        return whitelistRegexes;
    }

    /**
     * <p>Setter for the field <code>whitelistRegexes</code>.</p>
     *
     * @param whitelistRegexes a {@link java.lang.String} object
     */
    public void setWhitelistRegexes(String whitelistRegexes) {
        this.whitelistRegexes = whitelistRegexes;
    }

    /**
     * <p>Setter for the field <code>clippingSelector</code>.</p>
     *
     * @param clippingSelector a {@link java.lang.String} object
     */
    public void setClippingSelector(String clippingSelector) {
        this.clippingSelector = clippingSelector;
    }

    /**
     * <p>Getter for the field <code>header</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getHeader() {
        return header;
    }

    /**
     * <p>Setter for the field <code>header</code>.</p>
     *
     * @param header a {@link java.lang.String} object
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * <p>Getter for the field <code>footer</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getFooter() {
        return footer;
    }

    /**
     * <p>Setter for the field <code>footer</code>.</p>
     *
     * @param footer a {@link java.lang.String} object
     */
    public void setFooter(String footer) {
        this.footer = footer;
    }

    /**
     * <p>Getter for the field <code>authType</code>.</p>
     *
     * @return a {@link org.jasig.portlet.proxy.mvc.portlet.proxy.ProxyPortletForm.ProxyAuthType} object
     */
    public ProxyAuthType getAuthType() {
        return authType;
    }

    /**
     * <p>Setter for the field <code>authType</code>.</p>
     *
     * @param authType a {@link org.jasig.portlet.proxy.mvc.portlet.proxy.ProxyPortletForm.ProxyAuthType} object
     */
    public void setAuthType(ProxyAuthType authType) {
        this.authType = authType;
    }

    /**
     * <p>Getter for the field <code>usernameKey</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUsernameKey() {
        return usernameKey;
    }

    /**
     * <p>Setter for the field <code>usernameKey</code>.</p>
     *
     * @param usernameKey a {@link java.lang.String} object
     */
    public void setUsernameKey(String usernameKey) {
        this.usernameKey = usernameKey;
    }

    /**
     * <p>Getter for the field <code>passwordKey</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPasswordKey() {
        return passwordKey;
    }

    /**
     * <p>Setter for the field <code>passwordKey</code>.</p>
     *
     * @param passwordKey a {@link java.lang.String} object
     */
    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    /**
     * <p>Getter for the field <code>basicAuthPreferencesUsername</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getBasicAuthPreferencesUsername() {
        return basicAuthPreferencesUsername;
    }

    /**
     * <p>Setter for the field <code>basicAuthPreferencesUsername</code>.</p>
     *
     * @param basicAuthPreferencesUsername a {@link java.lang.String} object
     */
    public void setBasicAuthPreferencesUsername(String basicAuthPreferencesUsername) {
        this.basicAuthPreferencesUsername = basicAuthPreferencesUsername;
    }

    /**
     * <p>Getter for the field <code>basicAuthPreferencesPassword</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getBasicAuthPreferencesPassword() {
        return basicAuthPreferencesPassword;
    }

    /**
     * <p>Setter for the field <code>basicAuthPreferencesPassword</code>.</p>
     *
     * @param basicAuthPreferencesPassword a {@link java.lang.String} object
     */
    public void setBasicAuthPreferencesPassword(String basicAuthPreferencesPassword) {
        this.basicAuthPreferencesPassword = basicAuthPreferencesPassword;
    }

    /**
     * <p>Getter for the field <code>pageCharacterEncodingFormat</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPageCharacterEncodingFormat() {
        return pageCharacterEncodingFormat;
    }

    /**
     * <p>Setter for the field <code>pageCharacterEncodingFormat</code>.</p>
     *
     * @param pageCharacterEncodingFormat a {@link java.lang.String} object
     */
    public void setPageCharacterEncodingFormat(String pageCharacterEncodingFormat) {
        this.pageCharacterEncodingFormat = pageCharacterEncodingFormat;
    }

    /**
     * <p>Getter for the field <code>gsaHost</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getGsaHost() {
        return gsaHost;
    }

    /**
     * <p>Setter for the field <code>gsaHost</code>.</p>
     *
     * @param gsaHost a {@link java.lang.String} object
     */
    public void setGsaHost(String gsaHost) {
        this.gsaHost = gsaHost;
    }

    /**
     * <p>Getter for the field <code>gsaCollection</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getGsaCollection() {
        return gsaCollection;
    }

    /**
     * <p>Setter for the field <code>gsaCollection</code>.</p>
     *
     * @param gsaCollection a {@link java.lang.String} object
     */
    public void setGsaCollection(String gsaCollection) {
        this.gsaCollection = gsaCollection;
    }

    /**
     * <p>Getter for the field <code>gsaFrontend</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getGsaFrontend() {
        return gsaFrontend;
    }

    /**
     * <p>Setter for the field <code>gsaFrontend</code>.</p>
     *
     * @param gsaFrontend a {@link java.lang.String} object
     */
    public void setGsaFrontend(String gsaFrontend) {
        this.gsaFrontend = gsaFrontend;
    }

    /**
     * <p>Getter for the field <code>searchStrategies</code>.</p>
     *
     * @return an array of {@link java.lang.String} objects
     */
    public String[] getSearchStrategies() {
        return searchStrategies;
    }

    /**
     * <p>Setter for the field <code>searchStrategies</code>.</p>
     *
     * @param searchStrategies an array of {@link java.lang.String} objects
     */
    public void setSearchStrategies(String[] searchStrategies) {
        this.searchStrategies = searchStrategies;
    }

    /**
     * <p>Getter for the field <code>gsaWhitelistRegex</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getGsaWhitelistRegex() {
        return gsaWhitelistRegex;
    }

    /**
     * <p>Setter for the field <code>gsaWhitelistRegex</code>.</p>
     *
     * @param gsaWhitelistRegex a {@link java.lang.String} object
     */
    public void setGsaWhitelistRegex(String gsaWhitelistRegex) {
        this.gsaWhitelistRegex = gsaWhitelistRegex;
    }

    /**
     * <p>Getter for the field <code>anchorWhitelistRegex</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getAnchorWhitelistRegex() {
        return anchorWhitelistRegex;
    }

    /**
     * <p>Setter for the field <code>anchorWhitelistRegex</code>.</p>
     *
     * @param anchorWhitelistRegex a {@link java.lang.String} object
     */
    public void setAnchorWhitelistRegex(String anchorWhitelistRegex) {
        this.anchorWhitelistRegex = anchorWhitelistRegex;
    }
}
