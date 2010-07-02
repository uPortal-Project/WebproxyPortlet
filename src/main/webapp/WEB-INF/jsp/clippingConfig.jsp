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

	final boolean useCache  = new Boolean(prefs.getValue(ClippingConfigImpl.DISABLE, null)).booleanValue();
    final String[] xpaths   = prefs.getValues(ClippingConfigImpl.XPATH, new String[] {});
    final String[] comments = prefs.getValues(ClippingConfigImpl.COMMENT, new String[] {});
    final String[] elements = prefs.getValues(ClippingConfigImpl.ELEMENT, new String[] {});
%>
<p>
    <input type="checkbox" name="<%=ClippingConfigImpl.DISABLE%>" value="true" <%=(useCache ? "checked=\"checked\"" : "")%>/> Do Clipping
</p>
<p>
	Absolute Element Path to begin and end clipping: <br/>
<%
	for (int index = 0; index < 4; index++) {
%>
		<input type="text" name="<%=ClippingConfigImpl.XPATH%>" value="<%=(index < xpaths.length ? xpaths[index] : "")%>"><br/>
<%
	}
%>
</p>
<p>
	Comment to begin and end clipping: <br/>
<%
	for (int index = 0; index < 4; index++) {
%>
		<input type="text" name="<%=ClippingConfigImpl.COMMENT%>" value="<%=(index < comments.length ? comments[index] : "")%>"><br/>
<%
	}
%>
</p>
<p>
	Element to begin and end clipping: (e.g., script) <br/>
<%
	for (int index = 0; index < 4; index++) {
%>
		<input type="text" name="<%=ClippingConfigImpl.ELEMENT%>" value="<%=(index < elements.length ? elements[index] : "")%>"><br/>
<%
	}
%>
</p>