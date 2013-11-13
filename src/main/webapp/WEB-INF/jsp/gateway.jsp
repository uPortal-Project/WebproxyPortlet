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
<c:set var="n"><portlet:namespace/></c:set>
<portlet:defineObjects/>

<link href="${pageContext.request.contextPath}/css/webproxy.css" type="text/css" rel="stylesheet"/>
<script src="<rs:resourceURL value="/rs/jquery/1.8.3/jquery-1.8.3.min.js"/>" type="text/javascript"></script>
<script src="<rs:resourceURL value="/rs/jqueryui/1.8.24/jquery-ui-1.8.24.min.js"/>" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/scripts/webproxy.js" type="text/javascript"></script>

<portlet:resourceURL var="requestsUrl" escapeXml="false"/>
<portlet:resourceURL id="showTargetInNewWindow" var="newPageUrl" escapeXml="false"/>

<div id="${n}" class="gateway-portlet">
    <div>
        <c:forEach items="${ entries }" var="entry">
            <p class="entry">
            <c:set var="validation" value="${validations.get(entry.name)}" />
            <c:if test="${validation == true}">
                <a href="javascript:;" target="_self" index="${entry.name}">
                    <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>${ entry.name }
                </a>
            </c:if>
            <c:if test="${validation == false}">
                <a href="javascript:;" target="_self" index="${entry.name}"></a>
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

    <spring:message var="loadingTitle" code="portlet.logging.in.title"/>
    <div class="logging-in-message hidden" title="${loadingTitle}">
        <spring:message code="portlet.logging.in.display"/>
        <img src="${pageContext.request.contextPath}/images/loading.gif"/>
    </div>

</div>

<script type="text/javascript">
    var ${n} = ${n} || {}; // create a unique variable for our JS namespace
    ${n}.jQuery = jQuery.noConflict(true); // assign jQuery to this namespace

    /*  runs when the document is finished loading. */
    ${n}.jQuery(function () {
        $ = ${n}.jQuery; // assign jQuery to this namespace

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
                                    $(".logging-in-message").dialog();
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
