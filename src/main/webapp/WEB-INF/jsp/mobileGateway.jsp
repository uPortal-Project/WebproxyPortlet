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
<link type="text/css" rel="stylesheet" href="<c:url value="/css/webproxy-mobile.css"/>" media="screen, projection"/>
<portlet:resourceURL var="requestsUrl" escapeXml="false"/>
<portlet:resourceURL id="showTargetInNewWindow" var="newPageUrl" escapeXml="false"/>
<c:set var="n"><portlet:namespace/></c:set>

<div id="${n}" class="portlet gateway-portlet">
    <div class="portlet-content" data-role="content">
        <ul data-role="listview">
            <c:forEach items="${ entries }" var="entry">
                <li class="entry">
                    <portlet:renderURL var="editUrl" portletMode="EDIT"><portlet:param name="entryName" value="${entry.name}"/></portlet:renderURL>
                    <c:choose>
                        <c:when test="${validations.get(entry.name)}">
                            <a href="javascript:;" target="_self" index="${entry.name}">
                                <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>
                                <h3>${ entry.name }</h3>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <c:set var="buttonUrl" value="${editUrl}"/>
                            <c:if test="${!entry.operations.enterCredentialsAllowed}">
                                <c:set var="editIconDisabled" value="data-icon='false'"/>
                                <c:set var="buttonUrl" value="javascript:;"/>
                            </c:if>
                            <a href="${buttonUrl}" target="_self" index="${entry.name}" ${editIconDisabled}>
                                <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>
                                <h3>${ entry.name } <spring:message code="portlet.preferences.missing"/></h3>
                            </a>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${entry.operations.enterCredentialsAllowed}">
                        <a href="${editUrl}" class="gateway-edit-link"
                           title="<spring:message code='portlet.preferences.missing.title'/>" data-icon="gear">
                        </a>
                    </c:if>
                </li>
            </c:forEach>
        </ul>
    </div>
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
                        window.open("${newPageUrl}?index=" + link.getAttribute("index"));
                        </c:when>
                        <c:otherwise>
                        $.get(
                                "${ requestsUrl }",
                                { index: link.getAttribute("index") },
                                function (data) {
                                    if (data.contentRequests === undefined) {
                                        <spring:message var="launchErrorMessage" code="error.message.invalid.beanName"/>
                                        $(link).parent().append("<p class='portlet-msg error text-danger'>${launchErrorMessage}. BeanName: " + link.getAttribute("index") + "</p>");
                                    } else {
                                        $(link).parent().append('<div><spring:message code="portlet.logging.in.display"/></div>');
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
        });
    </script>
