<%@ page import="javax.portlet.*"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.wisc.my.webproxy.beans.config.*"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
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
    <input type="submit" name="previous" value="Back">
<%
    }    
    if(configPlacer.intValue() != configList.size()-1) {
%>
    <input type="submit" name="next" value="Next">
<%
    }
%>
	<input type="submit" name="apply" value="Complete">
    <input type="submit" name="cancel" value="Cancel">
</form>
<p>
    Do not use the buttons bellow until you have used the '<< Previous' 'Apply'
    or 'Next >>' buttons above to submit any newly entered data.
</p>