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

package org.jasig.portlet.proxy.mvc;

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThemeNameViewSelectorImpl implements IViewSelector {
    
    private static final String THEME_NAME_PROPERTY = "themeName";
    private static final String MOBILE_THEMES_KEY = "mobileThemes";
    private String[] mobileThemesDefault = new String[]{ "UniversalityMobile" };

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public boolean isMobile(PortletRequest request) {
        
        String[] mobileThemes = request.getPreferences().getValues(MOBILE_THEMES_KEY, mobileThemesDefault);
        String themeName = request.getProperty(THEME_NAME_PROPERTY);
        
        // if no theme name can be found, just assume the request is for a 
        // desktop client
        if (themeName == null) {
            logger.debug("No theme name found, assuming desktop environment");
            return false;
        }

        // otherwise, determine if the theme name matches one of the known 
        // mobile themes
        for (String theme : mobileThemes) {
            if (themeName.equals(theme)) {
                logger.debug("Theme name {} matches configured list of mobile themes", themeName);
                return true;
            }
        }
        
        logger.debug("No match found for theme name {}, assuming desktop environment", themeName);
        return false;
    }

    public String[] getMobileThemesDefault() {
        return mobileThemesDefault;
    }

    public void setMobileThemesDefault(String[] mobileThemesDefault) {
        this.mobileThemesDefault = mobileThemesDefault;
    }
}
