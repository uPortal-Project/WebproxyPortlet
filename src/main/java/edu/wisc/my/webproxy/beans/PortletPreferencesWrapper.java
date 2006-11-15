/*******************************************************************************
 * Copyright 2004, The Board of Regents of the University of Wisconsin System.
 * All rights reserved.
 *
 * A non-exclusive worldwide royalty-free license is granted for this Software.
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose is granted
 * provided that such redistribution and use in source and binary forms, with or
 * without modification meets the following conditions:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *c
 * 3. Redistributions of any form whatsoever must retain the following
 * acknowledgement:
 *
 * "This product includes software developed by The Board of Regents of
 * the University of Wisconsin System."
 *
 *THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
 *WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
 *BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS OF
 *THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
 *INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 *OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

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