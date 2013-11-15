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

<script src="<rs:resourceURL value='/rs/jquery/1.8.3/jquery-1.8.3.min.js'/>" type="text/javascript"></script>
<script src="<c:url value="/scripts/webproxy.js" />" type="text/javascript"></script>

<portlet:resourceURL var="requestsUrl" escapeXml="false"/>
<portlet:resourceURL id="showTargetInNewWindow" var="newPageUrl" escapeXml="false"/>

<c:set var="n"><portlet:namespace/></c:set>

<div id="${n}">
    <c:forEach items="${ entries }" var="entry">
        <p class="entry">
        <a href="javascript:;" class="gateway-link" target="_self">
            <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>
            <c:out value="${entry.name}" />
        </a>
        <c:if test="${entry.operations.enterCredentialsAllowed}">
            <a href="<portlet:renderURL portletMode="EDIT"><portlet:param name="entryName" value="${entry.name}"/></portlet:renderURL>">
                <img src="<rs:resourceURL value="/rs/famfamfam/silk/1.3/application_edit.png" />" title="<spring:message code="portlet.preferences.missing"/>" />
            </a>
        </c:if>
        </p>
    </c:forEach>

</div>

<script type="text/javascript">

    var ${n} = {};
    ${n}.jQuery = jQuery.noConflict(true);

    ${n}.jQuery(function() {

        var $ = ${n}.jQuery;

        $("#${n} gateway-link").each(function(idx, link) {
            $(link).click(function() {
                <c:choose>
                    <c:when test="${openInNewPage}">
                        window.open("${newPageUrl}?index="+idx);
                    </c:when>
                    <c:otherwise>
                        $.get(
                            "${requestsUrl}",
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
</script>
