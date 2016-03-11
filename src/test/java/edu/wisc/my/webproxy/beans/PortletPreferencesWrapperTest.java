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
*
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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.mock.web.portlet.MockPortletPreferences;


/**
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class PortletPreferencesWrapperTest extends TestCase {
    private PortletPreferencesWrapper wrapper = null;
    
    protected void setUp() throws Exception {
        final Map<String, String> userAttrs = new HashMap<String, String>();
        userAttrs.put("user.name.given", "John");
        userAttrs.put("user.name.middle", "Paul");
        userAttrs.put("user.name.last", "Doe");
        userAttrs.put("uid", "123456789");
        userAttrs.put("email", "john.doe@uportal.edu");
        userAttrs.put("user.address.line1", "1234 Any St.");
        userAttrs.put("user.address.line2", "Apt. 5");
        userAttrs.put("state", "CA");
        userAttrs.put("zip", "90210");
        
        final MockPortletPreferences mockPrefs = new MockPortletPreferences();
        mockPrefs.setValue("displayName", "${user.name.given} ${user.name.middle} ${user.name.last}");
        mockPrefs.setValue("emailAddr", "${email}");
        mockPrefs.setValue("SSN", "test ${foo${uid}bar} test");
        mockPrefs.setValue("HomeTown", "${city}");
        mockPrefs.setValues("Address", new String[] {"${user.address.line1}", "${user.address.line2}", "${user.address.line3}"});
        mockPrefs.setValue("AddressLine3", "${city} ${state} ${zip}");
        
        this.wrapper = new PortletPreferencesWrapper(mockPrefs, userAttrs);
    }

    public void testSingleAttribute() {
        final String value = wrapper.getValue("emailAddr", null);
        assertEquals("john.doe@uportal.edu", value);
    }
    
    public void testEmbeddedAttribute() {
        final String value = wrapper.getValue("SSN", null);
        assertEquals("test ${foo123456789bar} test", value);
    }

    public void testMultiAttribute() {
        final String value = wrapper.getValue("displayName", null);
        assertEquals("John Paul Doe", value);
    }
    
    public void testMultiMissingAttribute() {
        final String value = wrapper.getValue("AddressLine3", null);
        assertEquals(" CA 90210", value);
    }
    
    public void testArrayAttribute() {
        final String[] values = wrapper.getValues("Address", null);
        final String[] expected = new String[] {"1234 Any St.", "Apt. 5", ""};
        assertEquals(expected.length, values.length);
        
        for (int index = 0; index < expected.length; index++)
            assertEquals(expected[index], values[index]);
    }
}
