<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<%@ page 
	import="javax.portlet.*,
	        java.util.*, 
	        edu.wisc.my.webproxy.beans.config.*"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%
    PortletURL actionUrl = renderResponse.createActionURL();
    
    PortletPreferences prefs = renderRequest.getPreferences();
    PortletSession portletSession = renderRequest.getPortletSession();
    
	final String sAuthType = prefs.getValue(HttpClientConfigImpl.AUTH_TYPE, "");
%>
<form action="<%=actionUrl%>" method="post">
<%
	if (HttpClientConfigImpl.AUTH_TYPE_BASIC.equals(sAuthType)) {
        String userName = (String)portletSession.getAttribute(HttpClientConfigImpl.USER_NAME);
        if (userName == null)
            userName = prefs.getValue(HttpClientConfigImpl.USER_NAME, "");
        
        String password = (String)portletSession.getAttribute(HttpClientConfigImpl.PASSWORD);
        if (password == null)
            password = prefs.getValue(HttpClientConfigImpl.PASSWORD, "");
%>
	Please enter your username and password:<br/>
	Username: <input type="text" name="<%=HttpClientConfigImpl.USER_NAME%>" value="<%=userName%>"><br/>
	Password: <input TYPE="PASSWORD" name="<%=HttpClientConfigImpl.PASSWORD%>" value="<%=password%>"><br/>
<%
	}
	else if (HttpClientConfigImpl.AUTH_TYPE_FORM.equals(sAuthType)) {
		final String[] dynamicParamNames  = prefs.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_NAMES, new String[] {});
		final String[] sessionDynamicParamValues = (String[])portletSession.getAttribute(HttpClientConfigImpl.DYNAMIC_PARAM_VALUES);

		final String[] dynamicParamSensitive = prefs.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_SENSITIVE, new String[] {});
		final Set dynamicParamSensitiveSet   = new HashSet(Arrays.asList(dynamicParamSensitive)); 
	
		for (int index = 0; index < dynamicParamNames.length; index++) {
			String prefVal = "";
			if (index < sessionDynamicParamValues.length && sessionDynamicParamValues[index] != null)
				prefVal = sessionDynamicParamValues[index];
%>
	<%=dynamicParamNames[index]%>: 
	<input type="<%=(dynamicParamSensitiveSet.contains(Integer.toString(index)) ? "password" : "text")%>" name="<%=HttpClientConfigImpl.DYNAMIC_PARAM_VALUES%>" value="<%=prefVal%>"/><br/>
<%
		}
	}
	else {
%>
	<h3>Error, invalid authentication type '<%=sAuthType%>'</h3>
<%
	}
%>
	<input type="submit" name="AUTH_CREDS" value="Submit">
</form>