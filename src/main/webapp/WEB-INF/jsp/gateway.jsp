<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<c:set var="n"><portlet:namespace/></c:set>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/webproxy.css" type="text/css"/>
<script src="<rs:resourceURL value='/rs/jquery/1.8.3/jquery-1.8.3.min.js'/>" type="text/javascript"></script>
<script src="<rs:resourceURL value="/rs/jqueryui/1.8.24/jquery-ui-1.8.24.min.js"/>" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/scripts/webproxy.js" type="text/javascript"></script>

<portlet:resourceURL var="requestsUrl" escapeXml="false"/>
<portlet:resourceURL id="showTargetInNewWindow" var="newPageUrl" escapeXml="false"/>

<div id="${n}" class="gateway-portlet">
    <div>
        <c:forEach items="${ entries }" var="entry">
            <p class="entry">
            <c:choose>
                <c:when test="${validations.get(entry.name)}">
                    <a href="javascript:;" class="gateway-link" target="_self" index="${entry.name}">
                        <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>
                        <c:out value="${entry.name}" />
                    </a>
                </c:when>
                <c:otherwise>
                    <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>
                    <c:out value="${entry.name}" />
                </c:otherwise>
            </c:choose>
            <c:if test="${entry.operations.enterCredentialsAllowed}">
                <a href="<portlet:renderURL portletMode="EDIT" windowState="maximized"><portlet:param name="entryName" value="${entry.name}"/></portlet:renderURL>"
                        class="gateway-edit-link" title="<spring:message code='portlet.preferences.missing.title'/>">
                    <img src="<rs:resourceURL value="/rs/famfamfam/silk/1.3/application_edit.png" />"/>
                </a>
            </c:if>
            </p>
        </c:forEach>
    </div>

    <spring:message var="loadingTitle" code="portlet.logging.in.title"/>
    <div class="logging-in-message hidden" title="${loadingTitle}">
        <spring:message code="portlet.logging.in.display"/>
        <img src="${pageContext.request.contextPath}/images/loading.gif"/>
    </div>

</div>

<script type="text/javascript">

    var ${n} = {};
    ${n}.jQuery = jQuery.noConflict(true);

    ${n}.jQuery(function() {

        var $ = ${n}.jQuery;

        $("#${n} .gateway-link").each(function(idx, link) {
            $(link).click(function() {
                <c:choose>
                    <c:when test="${openInNewPage}">
                        window.open("${newPageUrl}?index=" + link.getAttribute("index"));
                    </c:when>
                    <c:otherwise>
                        $.get(
                            "${requestsUrl}",
                            { index:  link.getAttribute("index") },
                            function (data) {
                                if (data.contentRequests === undefined) {
                                    <spring:message var="launchErrorMessage" code="error.message.invalid.beanName"/>
                                    $(link).parent().append("<p class='portlet-msg error text-danger'>${launchErrorMessage}. BeanName: " + link.getAttribute("index") + "</p>");
                                } else {
                                    var dialog = $(".logging-in-message");
                                    dialog.dialog();
                                    dialog.removeClass("hidden");
                                    webproxyGatewayHandleRequest($, data, 0, "${n}form");
                                }
                            },
                            "json"
                        );
                    </c:otherwise>
                </c:choose>
            });
        });
    });
</script>
