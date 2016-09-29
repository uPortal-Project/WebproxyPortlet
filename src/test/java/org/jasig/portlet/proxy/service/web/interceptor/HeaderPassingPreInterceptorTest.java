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

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletPreferences;

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;

public class HeaderPassingPreInterceptorTest {

  HeaderPassingPreInterceptor preprocessor;
  @Mock PortletRequest portletRequest;
  HttpContentRequestImpl proxyRequest;
  Map<String, String> userInfoMap;
  @Mock PortletPreferences prefs;
  String[] headerNames;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    preprocessor = new HeaderPassingPreInterceptor();

    headerNames = new String[]{"uid", "cn", "displayName"};
    when(prefs.getValues(HeaderPassingPreInterceptor.HEADER_PREFERENCE, new String[0])).thenReturn(headerNames);
    when(portletRequest.getPreferences()).thenReturn(prefs);

    userInfoMap = new HashMap<String, String>();
    userInfoMap.put("uid", "Admin");
    userInfoMap.put("telephone", "6081234567");
    when(portletRequest.getAttribute(PortletRequest.USER_INFO)).thenReturn(userInfoMap);


    proxyRequest = new HttpContentRequestImpl();
    proxyRequest.setProxiedLocation("http://somewhere.com/rest/test/id");

  }

  @Test
  public void testHeaderPassing() {
    preprocessor.intercept(proxyRequest, portletRequest);
    assertEquals(proxyRequest.getHeaders().get("uid"), "Admin");
  }

  @Test
  public void testHeaderNotPassing() {
    preprocessor.intercept(proxyRequest, portletRequest);
    assertTrue(proxyRequest.getHeaders().containsKey("cn"));
    assertEquals(proxyRequest.getHeaders().get("cn"), null);
    assertEquals(proxyRequest.getHeaders().get("telephone"), null);
    assertEquals(proxyRequest.getHeaders().get("madeUpHeader"), null);
  }

  @Test
  public void testNoHeaderToPass() {
    preprocessor.intercept(proxyRequest, portletRequest);
    assertFalse(proxyRequest.getHeaders().containsKey("madeUpHeader"));
  }

  @Test
  public void testIncorrectPreferences(){
    //when def isn't found, def is returned for portletprefernce.getValues
    when(prefs.getValues(HeaderPassingPreInterceptor.HEADER_PREFERENCE, new String[0])).thenReturn(new String[0]);
    preprocessor.intercept(proxyRequest, portletRequest);
    assertEquals(proxyRequest.getHeaders().get("uid"), null);
  }

}
