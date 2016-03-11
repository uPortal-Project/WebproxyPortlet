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
package edu.wisc.my.webproxy.beans;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

/**
 * @author dgrimwood
 *  
 */
public class PortletPreferencesWrapper implements PortletPreferences {
    /* pattern is a regular expression that matches anything inside ${xyz} */
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("\\$\\{([^\\$\\}]+)\\}");

    private PortletPreferences portletPrefs = null;

    private Map userInfo = null;

    public PortletPreferencesWrapper(PortletPreferences pp, Map userInfo) {
        this.portletPrefs = pp;
        this.userInfo = userInfo;
    }

    /**
     * @param userInfo
     * @throws ReadOnlyException
     */

    public boolean isReadOnly(String arg0) {
        return this.portletPrefs.isReadOnly(arg0);
    }

    public String getValue(String attribute, String def) {
        String value = this.portletPrefs.getValue(attribute, def);
        if (value!=null)
            value = rewriteValue(value);
        return value;
    }

    private String rewriteValue(final String value) {
        if (value == null)
            return null;

        final StringBuffer valueBuffer = new StringBuffer(value);
        
        Matcher contentMatcher = ATTRIBUTE_PATTERN.matcher(valueBuffer);
        
        int searchIndex = 0;
        while (searchIndex < valueBuffer.length() && contentMatcher.find(searchIndex)) {
            final int start = contentMatcher.start();
            final int end = contentMatcher.end();
            final String attrName = contentMatcher.group(1);
            String attrValue = (String)userInfo.get(attrName);
            
            if (attrValue == null)
                attrValue = "";

            valueBuffer.replace(start, end, attrValue);
            searchIndex = start + attrValue.length() + 1;
            contentMatcher = ATTRIBUTE_PATTERN.matcher(valueBuffer);
        }
        
        return valueBuffer.toString();
    }

    public String[] getValues(String attribute, String[] def) {
        String sTemp = null;
        String[] values = this.portletPrefs.getValues(attribute, def);
        
        if (values == null)
            return null;
        
        String[] newValues = new String[values.length];
        for (int count = 0; count < values.length; count++) {
            sTemp = this.rewriteValue(values[count]);
            newValues[count] = sTemp;
        }

        return newValues;
    }

    public void setValue(String arg0, String arg1) throws ReadOnlyException {
        this.portletPrefs.setValue(arg0, arg1);
    }

    public void setValues(String arg0, String[] arg1) throws ReadOnlyException {
        this.portletPrefs.setValues(arg0, arg1);
    }

    public Enumeration getNames() {
        return this.portletPrefs.getNames();
    }

    public Map getMap() {
        return this.portletPrefs.getMap();
    }

    public void reset(String arg0) throws ReadOnlyException {
        this.portletPrefs.reset(arg0);
    }

    public void store() throws IOException, ValidatorException {
        this.portletPrefs.store();
    }

}