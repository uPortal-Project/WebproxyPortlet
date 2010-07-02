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