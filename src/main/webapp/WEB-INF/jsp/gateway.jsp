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

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:resourceURL var="requestsUrl" escapeXml="false"/>
<portlet:resourceURL id="showTargetInNewWindow" var="newPageUrl" escapeXml="false"/>
<c:set var="n"><portlet:namespace/></c:set>

<div id="${n}">
    <c:forEach items="${ entries }" var="entry">
        <p class="entry">
        <c:set var="validation" value="${validations.get(entry.name)}" />
        <c:if test="${validation == true}">      
            <a href="javascript:;" target="_self">
                <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>${ entry.name }
            </a>
        </c:if>
        <c:if test="${validation == false}">      
            <a href="javascript:;" target="_self"></a>
            <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>${ entry.name }
            <spring:message code="portlet.preferences.missing"/>
        </c:if>
        </p>
    </c:forEach>
    
</div>


<div class="edit-link">
    <portlet:renderURL var="editUrl"  portletMode="EDIT" />
    <a href="${editUrl}"><spring:message code="edit.proxy.show.preferences.link"/></a>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/webproxy.js" ></script>
<script type="text/javascript">
    up.jQuery(function () {
    	
    	var $ = up.jQuery;
    	
    	$(document).ready(function () {
    		
            $("#${n} .entry a").each(function (idx, link) {
	            $(link).click(function () {
   	                <c:choose>
   	                	<c:when test="${openInNewPage}">
            	            window.open("${newPageUrl}?index="+idx);
                	    </c:when>
                    	<c:otherwise>
                        	$.get(
                            	"${ requestsUrl }",
                            	{ index: idx },
                            	function (data) {
                            	    var contentRequests = data.contentRequests;
                        	        webproxyGatewayHandleRequest($, contentRequests, 0, "${n}form");
                    	            },
                	            "json"
            	                );
        	            </c:otherwise>
    	            </c:choose>
	            });
    		});
    	});
    });
</script>
