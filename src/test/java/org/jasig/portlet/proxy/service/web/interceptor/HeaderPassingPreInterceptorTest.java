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
package org.jasig.portlet.proxy.service.web.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletPreferences;

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HeaderPassingPreInterceptorTest {

  HeaderPassingPreInterceptor preprocessor;
  @Mock PortletRequest portletRequest;
  HttpContentRequestImpl proxyRequest;
  Map<String, String> userInfoMap;
  @Mock PortletPreferences prefs;
  String[] headerNames;
  String[] headerValues;

  @Before
  public void setUp() {
    preprocessor = new HeaderPassingPreInterceptor();

    headerNames = new String[]{"uid", "ApplicationSpecificCN", "displayName"};
    headerValues = new String[]{"uid", "cn", "displayName"};
    when(prefs.getValues(HeaderPassingPreInterceptor.HEADER_PREFERENCE_NAMES, new String[0])).thenReturn(headerNames);
    when(prefs.getValues(HeaderPassingPreInterceptor.HEADER_PREFERENCE_VALUES, new String[0])).thenReturn(headerValues);
    when(portletRequest.getPreferences()).thenReturn(prefs);

    userInfoMap = new HashMap<String, String>();
    userInfoMap.put("uid", "Admin");
    userInfoMap.put("telephone", "6081234567");
    userInfoMap.put("cn", "AmyTheGreat");
    userInfoMap.put("ApplicationSpecificCN", "Amy");
    when(portletRequest.getAttribute(PortletRequest.USER_INFO)).thenReturn(userInfoMap);


    proxyRequest = new HttpContentRequestImpl();
    proxyRequest.setProxiedLocation("http://somewhere.com/rest/test/id");

  }

  @Test
  public void testHeaderPassing() {
    preprocessor.intercept(proxyRequest, portletRequest);
    assertEquals(proxyRequest.getHeaders().get("uid"), "Admin");
    assertEquals(proxyRequest.getHeaders().get("ApplicationSpecificCN"), "AmyTheGreat");
  }

  /*
  * Displayname should be there but have no value since no user preference
  * User preferences should not pass just because they're there
  */
  @Test
  public void testHeaderNotPassing() {
    preprocessor.intercept(proxyRequest, portletRequest);
    assertTrue(proxyRequest.getHeaders().containsKey("displayName"));
    assertEquals(proxyRequest.getHeaders().get("displayName"), null);
    assertEquals(proxyRequest.getHeaders().get("telephone"), null);
    assertEquals(proxyRequest.getHeaders().get("madeUpHeader"), null);
  }

  @Test
  public void testNoHeaderToPass() {
    preprocessor.intercept(proxyRequest, portletRequest);
    assertFalse(proxyRequest.getHeaders().containsKey("cn"));
    assertEquals(proxyRequest.getHeaders().get("cn"), null);
  }

  @Test
  public void testIncorrectPreferencesNaming(){
    //when def isn't found, def is returned for portletprefernce.getValues
    when(prefs.getValues(HeaderPassingPreInterceptor.HEADER_PREFERENCE_NAMES, new String[0])).thenReturn(new String[0]);
    preprocessor.intercept(proxyRequest, portletRequest);
    assertEquals(proxyRequest.getHeaders().get("uid"), null);
  }

  @Test
  public void testIncorrectPreferencesLength(){
    when(prefs.getValues(HeaderPassingPreInterceptor.HEADER_PREFERENCE_VALUES, new String[0])).thenReturn(new String[2]);
    preprocessor.intercept(proxyRequest, portletRequest);
    assertEquals(proxyRequest.getHeaders().get("uid"), null);
  }

}
