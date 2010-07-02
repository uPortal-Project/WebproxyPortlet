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
	        edu.wisc.my.webproxy.beans.config.*"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%
    final PortletPreferences prefs = renderRequest.getPreferences(); 

	final String baseUrl                    = prefs.getValue(GeneralConfigImpl.BASE_URL, "");
    final String editUrl                    = prefs.getValue(GeneralConfigImpl.EDIT_URL, "");

    final String[] portletUrlMasks          = prefs.getValues(GeneralConfigImpl.PORTLET_URL_REWRITE_MASKS, new String[] {});
    final String[] portletUrlStates         = prefs.getValues(GeneralConfigImpl.PORTLET_URL_REWRITE_STATES, new String[] {});
    final String portletUrlRewriteListType  = prefs.getValue(GeneralConfigImpl.PORTLET_URL_LIST_TYPE, GeneralConfigImpl.URL_LIST_TYPE_INCLUDE);   

    final String[] fNameUrlMasks          	= prefs.getValues(GeneralConfigImpl.FNAME_URL_REWRITE_MASKS, new String[] {});
    final String[] fNameUrlStates         	= prefs.getValues(GeneralConfigImpl.FNAME_URL_REWRITE_STATES, new String[] {});
    final String fNameUrlRewriteListType  	= prefs.getValue(GeneralConfigImpl.FNAME_URL_LIST_TYPE, GeneralConfigImpl.URL_LIST_TYPE_INCLUDE);   
    final String fNameTarget  				= prefs.getValue(GeneralConfigImpl.FNAME_TARGET, "");   

    final String preInterceptorClassName    = prefs.getValue(GeneralConfigImpl.PRE_INTERCEPTOR_CLASS, "");   
    final String postInterceptorClassName   = prefs.getValue(GeneralConfigImpl.POST_INTERCEPTOR_CLASS, "");   
%>
<p>
	Base Url (including protocol): <input type="text" name="<%=GeneralConfigImpl.BASE_URL%>" value="<%=baseUrl%>">
</p>
<p>
	Edit Url (including protocol): <input type="text" name="<%=GeneralConfigImpl.EDIT_URL%>" value="<%=editUrl%>">
</p>
<p>
	Portlet URL Rewrite Masks:
	<table>
		<tr>
			<td>Portlet URL (including protocol)</td>
			<td>WindowState  (optional)</td>
		</tr>
<%
	for (int index = 0; index < 4; index++) {
		final String urlState = (index < portletUrlStates.length ? portletUrlStates[index] : "");
%>
		<tr>
			<td><input type="text" name="<%=GeneralConfigImpl.PORTLET_URL_REWRITE_MASKS%>" value="<%=(index < portletUrlMasks.length ? portletUrlMasks[index] : "")%>"></td>
			<td>
			    <select name="<%=GeneralConfigImpl.PORTLET_URL_REWRITE_STATES%>">
					<option value="NORMAL" <%=("NORMAL".equals(urlState) ? "selected=\"selected\"" : "")%>>Normal</option>
					<option value="MINIMIZED" <%=("MINIMIZED".equals(urlState) ? "selected=\"selected\"" : "")%>>Minimized</option>
					<option value="MAXIMIZED" <%=("MAXIMIZED".equals(urlState) ? "selected=\"selected\"" : "")%>>Maximized</option>
					<option value="EXCLUSIVE" <%=("EXCLUSIVE".equals(urlState) ? "selected=\"selected\"" : "")%>>Exclusive</option>
			    </select>
			</td>
		</tr>
<%
	}
%>
	</table>
	<br>
	Portlet URL List Type(Include or Exclude): 
    <select name="<%=GeneralConfigImpl.PORTLET_URL_LIST_TYPE%>">
        <option value="<%=GeneralConfigImpl.URL_LIST_TYPE_INCLUDE%>" <%=(GeneralConfigImpl.URL_LIST_TYPE_INCLUDE.equals(portletUrlRewriteListType) ? "selected=\"selected\"" : "")%>>Include</option>
        <option value="<%=GeneralConfigImpl.URL_LIST_TYPE_EXCLUDE%>" <%=(GeneralConfigImpl.URL_LIST_TYPE_EXCLUDE.equals(portletUrlRewriteListType) ? "selected=\"selected\"" : "")%>>Exclude</option>
    </select>
</p>
<p>
	FunctionalName URL Rewrite Masks:<br>
	Target FName: <input type="text" name="<%=GeneralConfigImpl.FNAME_TARGET%>" value="<%=fNameTarget%>">
	<table>
		<tr>
			<td>FName URL (including protocol)</td>
			<td>WindowState  (optional)</td>
		</tr>
<%
	for (int index = 0; index < 4; index++) {
		final String urlState = (index < fNameUrlStates.length ? fNameUrlStates[index] : "");
%>
		<tr>
			<td><input type="text" name="<%=GeneralConfigImpl.FNAME_URL_REWRITE_MASKS%>" value="<%=(index < fNameUrlMasks.length ? fNameUrlMasks[index] : "")%>"></td>
			<td>
			    <select name="<%=GeneralConfigImpl.FNAME_URL_REWRITE_STATES%>">
					<option value="NORMAL" <%=("NORMAL".equals(urlState) ? "selected=\"selected\"" : "")%>>Normal</option>
					<option value="MINIMIZED" <%=("MINIMIZED".equals(urlState) ? "selected=\"selected\"" : "")%>>Minimized</option>
					<option value="MAXIMIZED" <%=("MAXIMIZED".equals(urlState) ? "selected=\"selected\"" : "")%>>Maximized</option>
					<option value="EXCLUSIVE" <%=("EXCLUSIVE".equals(urlState) ? "selected=\"selected\"" : "")%>>Exclusive</option>
			    </select>
			</td>
		</tr>
<%
	}
%>
	</table>
	<br>
	FName URL List Type(Include or Exclude): 
    <select name="<%=GeneralConfigImpl.FNAME_URL_LIST_TYPE%>">
        <option value="<%=GeneralConfigImpl.URL_LIST_TYPE_INCLUDE%>" <%=(GeneralConfigImpl.URL_LIST_TYPE_INCLUDE.equals(fNameUrlRewriteListType) ? "selected=\"selected\"" : "")%>>Include</option>
        <option value="<%=GeneralConfigImpl.URL_LIST_TYPE_EXCLUDE%>" <%=(GeneralConfigImpl.URL_LIST_TYPE_EXCLUDE.equals(fNameUrlRewriteListType) ? "selected=\"selected\"" : "")%>>Exclude</option>
    </select>
</p>
<p>
	Pre-Interceptor class: <input type="text" name="GeneralConfigImpl.PRE_INTERCEPTOR_CLASS" value="<%=preInterceptorClassName%>">
</p>
<p>
	Post-Interceptor class: <input type="text" name="GeneralConfigImpl.POST_INTERCEPTOR_CLASS" value="<%=postInterceptorClassName%>">
</p>