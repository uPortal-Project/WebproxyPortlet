<%@ page 
	import="javax.portlet.*, 
	        java.util.*, 
	        edu.wisc.my.webproxy.beans.config.*"%>
<%@ taglib uri='/WEB-INF/tld/portlet.tld' prefix='portlet'%>

<portlet:defineObjects/>

<style type="text/css">
	.workbox {
		border-style: solid;
		border-width: thin;
		height: 50px;
		width: 1%;
		align: center;
		padding-left: 9px;
		padding-right: 9px;
	}

	.line {
		height: 3px;
		width: 100%;
		padding: 0px;
		margin: 0px;
	}

	.arrow {
		padding: 0px;
		margin: 0px;
	}

	.linebox {
		vertical-align: middle;
		padding: 0px;
		margin: 0px;
	}

	.arrowbox {
		vertical-align: middle;
		padding: 0px;
		margin: 0px;
		width: 1%;
	}

	.selected {
		color: rgb(75, 0, 0);
		font-weight: bold;
	}

	.highlight {
		background: rgb(255, 100, 100);
	}

	a.page:link {
		color: #000000;
		text-decoration: none;
	}
	a.page:visited {
		color: #000000;
		text-decoration: none;
	}
	a.page:hover {
		color: #000000;
		text-decoration: none;
	}
	a.page:active {
		color: #000000;
		text-decoration: none;
	}
</style>
<div>
	<table>
		<tr>
		 <% 
    		PortletSession pSession = renderRequest.getPortletSession(); 

		 	Integer configPlacer = new Integer(0);
		 	Integer oldConfigPlacer = new Integer(0);
		 	
		 	if (pSession.getAttribute("configPlacer") != null)
		 		oldConfigPlacer = (Integer) pSession.getAttribute("configPlacer");
		 	
		 	List configList = (List) pSession.getAttribute("configList");
		 	for (Iterator configIterator = configList.listIterator(); configIterator.hasNext(); ) {
				ConfigPage tempConfig = (ConfigPage) configIterator.next();
        		
        		PortletURL myUrl = renderResponse.createActionURL();
				configPlacer = new Integer(configList.indexOf(tempConfig));
  				myUrl.setParameter("configPlacer", configPlacer.toString());
  				String sName = tempConfig.getName();
  				if (oldConfigPlacer.intValue() != configPlacer.intValue()) {
%>
			<td class="workbox"><a class="page" href="<%=myUrl.toString()%>"><%=sName%></a></td>
<%
                }
                else {
%>
			<td class="workbox"><span class="selected"><%=sName%></a></td>
<%
                }
                if(configIterator.hasNext()) {
%>		
            <td class="linebox"><img class="line"</td>
<%
                }
            }
%>			
		</tr>
	</table>
</div>
<br/>
<hr/>
<%
	String errorMsg = renderRequest.getParameter("msg");
	if (errorMsg != null) {
%>
<h1><%=errorMsg%></h1>
<br/>
<hr/>
<%
    }
%>
<form name="beanConfigForm" action="<portlet:actionURL/>" method="post">
