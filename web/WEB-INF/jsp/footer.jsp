<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.wisc.my.webproxy.beans.config.*"%>
<%@ taglib uri='/WEB-INF/tld/portlet.tld' prefix='portlet'%>
<portlet:defineObjects/>
<br/>
<hr/>
<%
    PortletSession pSession = renderRequest.getPortletSession();    
    List configList = (List) pSession.getAttribute("configList");
    Integer configPlacer = new Integer(0);
    if (pSession.getAttribute("configPlacer") != null) {
        configPlacer = (Integer) pSession.getAttribute("configPlacer");
    }
    
    if(configPlacer.intValue() != 0){    
%>
    <input type="submit" name="previous" value="<< Previous">
<%
    }    
%>
	<input type="submit" name="apply" value="Apply">
<%
    if(configPlacer.intValue() != configList.size()-1) {
%>
    <input type="submit" name="next" value="Next >>">
<%
    }
%>
</form>
<p>
    Do not use the buttons bellow until you have used the '<< Previous' 'Apply'
    or 'Next >>' buttons above to submit any newly entered data.
</p>