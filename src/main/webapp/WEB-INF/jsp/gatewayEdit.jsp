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
<link rel="stylesheet" href="<c:url value='/css/webproxy.css'/>" type="text/css"/>

<portlet:actionURL var="savePreferencesUrl">
    <portlet:param name="action" value="savePreferences"/>
    <portlet:param name="entryName" value="${gatewayEntry.name}"/>
</portlet:actionURL>
<portlet:actionURL var="clearPreferencesUrl">
    <portlet:param name="action" value="clearPreferences"/>
    <portlet:param name="entryName" value="${gatewayEntry.name}"/>
</portlet:actionURL>

<div id="${n}gatewayPortlet" class="portlet gateway-portlet">

    <div class="credentials">

        <h2><img src="${gatewayEntry.iconUrl}" style="vertical-align: middle; padding-right: 10px;"/><c:out value="${gatewayEntry.name}" /></h2>
        <form id="setCredentialsForm" class="set-credentials-form" action="${savePreferencesUrl}" method="post">
            <c:if test="${!empty error}">
                <div class="portlet-msg-error portlet-msg error"><spring:message code="${error}"/></div>
            </c:if>
            <table id="setCredentialsTable">
                <tbody>
                <c:forEach var="gatewayPreference" items="${gatewayPreferences}" varStatus="index">
                    <c:set var="autofocus" value=""/>
                    <c:if test="${index.first}">
                        <c:set var="autofocus" value="autofocus"/>
                    </c:if>
                    <tr>
                        <td>${gatewayPreference.value.logicalFieldName}</td>
                        <c:choose>
                            <c:when test="${gatewayPreference.value.secured == true}">
                                <td><input type="password" name="${gatewayPreference.value.preferenceName}" value="${gatewayPreference.value.fieldValue}" ${autofocus}/></td>
                            </c:when>
                            <c:otherwise>
                                <td><input type="text" name="${gatewayPreference.value.preferenceName}" value="${gatewayPreference.value.fieldValue}" ${autofocus}/></td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <input type="submit" value="<spring:message code="edit.proxy.save.preferences"/>" class="portlet-form-button"/>
            <a href="<portlet:renderURL portletMode="VIEW" />" style="margin: 0 0 0 12px;"><spring:message code="edit.proxy.cancel" /></a>
            <c:if test="${gatewayEntry.operations.clearCredentialsAllowed}">
                <a href="${clearPreferencesUrl}" style="margin: 0 0 0 12px;">
                    <img src="<rs:resourceURL value="/rs/famfamfam/silk/1.3/application_delete.png" />" />
                    <spring:message code="portlet.preferences.clear.credentials" />
                </a>
            </c:if>
        </form>
    </div>
</div>
