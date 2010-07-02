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

        final boolean insertDocType  = new Boolean(prefs.getValue(HtmlParserConfigImpl.INSERTDOCTYPE, null)).booleanValue();
        final boolean balanceTags  = new Boolean(prefs.getValue(HtmlParserConfigImpl.BALANCETAGS, null)).booleanValue();
        final boolean stripScriptComments  = new Boolean(prefs.getValue(HtmlParserConfigImpl.SCRIPTSTRIPCOMMENT, null)).booleanValue();
        final boolean stripComments  = new Boolean(prefs.getValue(HtmlParserConfigImpl.STRIPCOMMENTS, null)).booleanValue();
        final boolean reportErrors  = new Boolean(prefs.getValue(HtmlParserConfigImpl.REPORTERRORS, null)).booleanValue();
%>
<p>
    <input type="checkbox" name="<%=HtmlParserConfigImpl.INSERTDOCTYPE%>" value="true" <%=(insertDocType ? "checked=\"checked\"" : "")%>/>
    Insert Document Type
</p>
<p>
    <input type="checkbox" name="<%=HtmlParserConfigImpl.BALANCETAGS%>" value="true" <%=(balanceTags ? "checked=\"checked\"" : "")%>/>
    Balance Tags
</p>
<p>
    <input type="checkbox" name="<%=HtmlParserConfigImpl.SCRIPTSTRIPCOMMENT%>" value="true" <%=(stripScriptComments ? "checked=\"checked\"" : "")%>/>
    Strip JavaScript commenting
</p>
<p>
    <input type="checkbox" name="<%=HtmlParserConfigImpl.STRIPCOMMENTS%>" value="true" <%=(stripComments ? "checked=\"checked\"" : "")%>/>
    Strip Comments
</p>
<p>
    <input type="checkbox" name="<%=HtmlParserConfigImpl.REPORTERRORS%>" value="true" <%=(reportErrors ? "checked=\"checked\"" : "")%>/>
    Report Errors
</p>
