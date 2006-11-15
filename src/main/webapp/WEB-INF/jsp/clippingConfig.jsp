<%@ page 
	import="javax.portlet.*, 
	        edu.wisc.my.webproxy.beans.config.*"%>
<%@ taglib uri='/WEB-INF/tld/portlet.tld' prefix='portlet'%>

<portlet:defineObjects/>

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