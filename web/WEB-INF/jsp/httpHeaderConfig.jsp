<%@ page 
	import="javax.portlet.*, 
	        edu.wisc.my.webproxy.beans.config.*"%>
<%@ taglib uri='/WEB-INF/tld/portlet.tld' prefix='portlet'%>

<portlet:defineObjects/>

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