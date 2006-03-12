<%@ page 
	import="javax.portlet.*, 
	        edu.wisc.my.webproxy.beans.config.*"%>
<%@ taglib uri='/WEB-INF/tld/portlet.tld' prefix='portlet'%>

<portlet:defineObjects/>

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
