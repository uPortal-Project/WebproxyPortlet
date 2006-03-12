<%@ page 
	import="javax.portlet.*, 
	        edu.wisc.my.webproxy.beans.config.*"%>
<%@ taglib uri='/WEB-INF/tld/portlet.tld' prefix='portlet'%>

<portlet:defineObjects/>

<%
    final PortletPreferences prefs = renderRequest.getPreferences(); 

	final String sStaticHeader = prefs.getValue(StaticHtmlConfigImpl.STATIC_HEADER, "");
    final String sStaticFooter = prefs.getValue(StaticHtmlConfigImpl.STATIC_FOOTER, "");
%>
<p>
	Static Header:
	<br/> 
	<textarea cols="100" rows="15" name="<%=StaticHtmlConfigImpl.STATIC_HEADER%>"><%=sStaticHeader%></textarea>
</p>
<p>
	Static Footer:
	<br/>
	<textarea cols="100" rows="15" name="<%=StaticHtmlConfigImpl.STATIC_FOOTER%>"><%=sStaticFooter%></textarea>
</p>
