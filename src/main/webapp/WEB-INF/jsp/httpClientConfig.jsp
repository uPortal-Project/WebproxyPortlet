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
    final PortletPreferences prefs = renderRequest.getPreferences(); 

	final String httpTimeout         		= prefs.getValue(HttpClientConfigImpl.HTTP_TIMEOUT, "");
    final boolean authEnable         		= new Boolean(prefs.getValue(HttpClientConfigImpl.AUTH_ENABLE, null)).booleanValue();
    final boolean sessionPersistenceEnable	= new Boolean(prefs.getValue(HttpClientConfigImpl.SESSION_PERSISTENCE_ENABLE, null)).booleanValue();
	final String maxRedirects        		= prefs.getValue(HttpClientConfigImpl.MAX_REDIRECTS, "");
	final boolean circularRedirects         = new Boolean(prefs.getValue(HttpClientConfigImpl.CIRCULAR_REDIRECTS, null)).booleanValue();
    final String authType            		= prefs.getValue(HttpClientConfigImpl.AUTH_TYPE, "");
    final String authUrl             		= prefs.getValue(HttpClientConfigImpl.AUTH_URL, "");
    final String sessionKey	  		 		= prefs.getValue(HttpClientConfigImpl.SHARED_SESSION_KEY, "");
    
	final String userName            = prefs.getValue(HttpClientConfigImpl.USER_NAME, "");
    final boolean promptUserName     = new Boolean(prefs.getValue(HttpClientConfigImpl.PROMPT_USER_NAME, null)).booleanValue();
	final boolean persistUserName    = new Boolean(prefs.getValue(HttpClientConfigImpl.PERSIST_USER_NAME, null)).booleanValue();
    final String password            = prefs.getValue(HttpClientConfigImpl.PASSWORD, "");
    final boolean promptPassword     = new Boolean(prefs.getValue(HttpClientConfigImpl.PROMPT_PASSWORD, null)).booleanValue();
	final boolean persistPassword    = new Boolean(prefs.getValue(HttpClientConfigImpl.PERSIST_PASSWORD, null)).booleanValue();
	
	final String sessionTimeout          = prefs.getValue(HttpClientConfigImpl.SESSION_TIMEOUT, "");
    final String[] dynamicParamNames     = prefs.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_NAMES, new String[] {});
	final String[] dynamicParamPersist   = prefs.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_PERSIST, new String[] {});
	final Set dynamicParamPersistSet     = new HashSet(Arrays.asList(dynamicParamPersist)); 
	final String[] dynamicParamSensitive = prefs.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_SENSITIVE, new String[] {});
	final Set dynamicParamSensitiveSet   = new HashSet(Arrays.asList(dynamicParamSensitive)); 
 
    final String[] staticParamNames  = prefs.getValues(HttpClientConfigImpl.STATIC_PARAM_NAMES, new String[] {});
	final String[] staticParamValues = prefs.getValues(HttpClientConfigImpl.STATIC_PARAM_VALUES, new String[] {});
%>
<p>
	HTTP Timeout (sec): <input type="text" name="<%=HttpClientConfigImpl.HTTP_TIMEOUT%>" value="<%=httpTimeout%>">
</p>
<p>
	Maximum Redirects: <input type="text" name="<%=HttpClientConfigImpl.MAX_REDIRECTS%>" value="<%=maxRedirects%>">
    <br/>
    <input type="checkbox" value="true" name="<%=HttpClientConfigImpl.CIRCULAR_REDIRECTS%>" <%=(circularRedirects ? "checked=\"checked\"" : "")%>/>
    Allow Circular Redirects
</p>
<p>
    <input type="checkbox" value="true" name="<%=HttpClientConfigImpl.AUTH_ENABLE%>" <%=(authEnable ? "checked=\"checked\"" : "")%>/>
	Enable Authentication
</p>
<p>
	Type of Authentication:
    <select name="<%=HttpClientConfigImpl.AUTH_TYPE%>">
      <option value="<%=HttpClientConfigImpl.AUTH_TYPE_BASIC%>" <%=(HttpClientConfigImpl.AUTH_TYPE_BASIC.equals(authType) ? "selected=\"selected\"" : "")%>>BASIC</option>
      <option value="<%=HttpClientConfigImpl.AUTH_TYPE_NTLM%>" <%=(HttpClientConfigImpl.AUTH_TYPE_NTLM.equals(authType) ? "selected=\"selected\"" : "")%>>NTLM</option>
      <option value="<%=HttpClientConfigImpl.AUTH_TYPE_FORM%>" <%=(HttpClientConfigImpl.AUTH_TYPE_FORM.equals(authType) ? "selected=\"selected\"" : "")%>>FORM</option>
      <option value="<%=HttpClientConfigImpl.AUTH_TYPE_SHIBBOLETH%>" <%=(HttpClientConfigImpl.AUTH_TYPE_FORM.equals(authType) ? "selected=\"selected\"" : "")%>>SHIBBOLETH</option>
      <option value="<%=HttpClientConfigImpl.AUTH_TYPE_CAS%>" <%=(HttpClientConfigImpl.AUTH_TYPE_FORM.equals(authType) ? "selected=\"selected\"" : "")%>>CAS</option>
    </select>
</p>
<p>
    <input type="checkbox" value="true" name="<%=HttpClientConfigImpl.SESSION_PERSISTENCE_ENABLE%>" value="true" <%=(sessionPersistenceEnable ? "checked=\"checked\"" : "")%>/>
	Enable Session Persistance
</p>
<p>
	Shared Session Key:
    <input name="<%=HttpClientConfigImpl.SHARED_SESSION_KEY%>" type="text" value="<%=sessionKey%>" size="20"/><br/>
    A key/identifier for storing session to be shared between other webproxy portlets for a user. (leave blank to disable shared sessions)
</p>

<br/>
<h3>For BASIC Authentication Only</h3>
<p>
	Enter User Name <input type="text" name="<%=HttpClientConfigImpl.USER_NAME%>" value="<%=userName%>"> or 
	<input type="checkbox" value="true" name="<%=HttpClientConfigImpl.PROMPT_USER_NAME%>" <%=(promptUserName ? "checked=\"checked\"" : "")%>/>Prompt for User Name - 
	<input type="checkbox" value="true" name="<%=HttpClientConfigImpl.PERSIST_USER_NAME%>" <%=(persistUserName ? "checked=\"checked\"" : "")%>/> Persist prompted value
	<br/>
	Enter Password <input type="text" name="<%=HttpClientConfigImpl.PASSWORD%>" value="<%=password%>"> or
	<input type="checkbox" value="true" name="<%=HttpClientConfigImpl.PROMPT_PASSWORD%>" <%=(promptPassword ? "checked=\"checked\"" : "")%>/> Prompt for Password -
	<input type="checkbox" value="true" name="<%=HttpClientConfigImpl.PERSIST_PASSWORD%>" <%=(persistPassword ? "checked=\"checked\"" : "")%>/> Persist prompted value
</p>

<br/>
<h3>For FORM Authentication Only</h3>
<p>
	Session Timeout (min): <input type="text" name="<%=HttpClientConfigImpl.SESSION_TIMEOUT%>" value="<%=sessionTimeout%>">
</p>
<p>
	Authentication URL: <input type="text" name="<%=HttpClientConfigImpl.AUTH_URL%>" value="<%=authUrl%>"><br/>
</p>
<p>
	Additional Dynamic Authentication parameters (User will be prompted)
	<table>
		<tr>
			<td>Parameter Name</td>
			<td>Persist Value</td>
			<td>Sensitive Value</td>
		</tr>
<%
	for (int index = 0; index < 10; index++) {
%>
		<tr>
			<td><input type="text" name="<%=HttpClientConfigImpl.DYNAMIC_PARAM_NAMES%>" value="<%=(index < dynamicParamNames.length ? dynamicParamNames[index] : "")%>"></td>
			<td><input type="checkbox" name="<%=HttpClientConfigImpl.DYNAMIC_PARAM_PERSIST%>" value="<%=index%>" <%=(dynamicParamPersistSet.contains(Integer.toString(index)) ? "checked=\"checked\"" : "")%>/></td>
			<td><input type="checkbox" name="<%=HttpClientConfigImpl.DYNAMIC_PARAM_SENSITIVE%>" value="<%=index%>" <%=(dynamicParamSensitiveSet.contains(Integer.toString(index)) ? "checked=\"checked\"" : "")%>/></td>
		</tr>
<%
	}
%>
	</table>
</p>
<p>	
	Additional Static Form Authentication parameters:
	<table>
		<tr>
			<td>Parameter Name</td>
			<td>Parameter Value</td>
		</tr>
<%
	for (int index = 0; index < 10; index++) {
%>
		<tr>
			<td><input type="text" name="<%=HttpClientConfigImpl.STATIC_PARAM_NAMES%>" value="<%=(index < staticParamNames.length ? staticParamNames[index] : "")%>"></td>
			<td><input type="text" name="<%=HttpClientConfigImpl.STATIC_PARAM_VALUES%>" value="<%=(index < staticParamValues.length ? staticParamValues[index] : "")%>"></td>
		</tr>
<%
	}
%>
	</table>
</p>
