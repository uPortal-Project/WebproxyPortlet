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

	final String[] headerNames  = prefs.getValues(HttpHeaderConfigImpl.HEADER_NAME, new String[] {});
	final String[] headerValues = prefs.getValues(HttpHeaderConfigImpl.HEADER_VALUE, new String[] {});
%>
<p>
	HTTP Headers
	<table>
		<tr>
			<td>Name</td>
			<td>Value</td>
		</tr>
<%
	for (int index = 0; index < 5; index++) {
%>
		<tr>
			<td><input type="text" name="<%=HttpHeaderConfigImpl.HEADER_NAME%>" value="<%=(index < headerNames.length ? headerNames[index] : "")%>"></td>
			<td><input type="text" name="<%=HttpHeaderConfigImpl.HEADER_VALUE%>" value="<%=(index < headerValues.length ? headerValues[index] : "")%>"></td>
		</tr>
<%
	}
%>
	</table>
</p>