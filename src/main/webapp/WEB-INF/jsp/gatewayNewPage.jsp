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
<portlet:resourceURL var="requestsUrl" escapeXml="false"/>
<c:set var="n"><portlet:namespace/></c:set>

<!DOCTYPE html>
<html>
<head>
  <title>Gateway</title>

  <link type="text/css" rel="stylesheet" href="<c:url value="/css/webproxy.css"/>"/>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery-1.8.3.min.js" ></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/webproxy.js" ></script>
  <script type="text/javascript">
    $(document).ready(function () {

      $( document ).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
        $(".documentBody").html("<p class='portlet-msg error text-danger'>An Error occurred.  See stack trace in log file for more information</p>" + jqXHR.responseText);
      });

      $.get(
          "${ requestsUrl }",
          { index: "${index}" },
          function (data) {
              if (data.contentRequests === undefined) {
                  <spring:message var="launchErrorMessage" code="error.message.invalid.beanName"/>
                  <spring:message var="launchErrorIndexName" code="error.message.invalid.index"/>
                  $(".documentBody").html("<p class='portlet-msg error text-danger'>${launchErrorMessage}. ${launchErrorIndexName}</p>");
              } else {
                webproxyGatewayHandleRequest($, data, 0, "${n}form");
              }
          },
          "json"
      );
    });
  </script>
</head>

<body class="documentBody gateway-portlet">
<p><spring:message code="portlet.logging.in.display"/><img src="${pageContext.request.contextPath}/images/loading.gif"/></p>
</body>
</html>
