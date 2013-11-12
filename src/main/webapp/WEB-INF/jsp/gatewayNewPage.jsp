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
<c:set var="n"><portlet:namespace/></c:set>

<!DOCTYPE html>
<html>
<head>
  <title>Gateway</title>

  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/jquery-1.8.3.min.js" ></script>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/webproxy.js" ></script>
  <script type="text/javascript">
    $(document).ready(function () {

      $( document ).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
        $(".documentBody").html("<p>An Error occurred.  See stack trace in log file for more information</p>" + jqXHR.responseText);
      });

      $.get(
          "${ requestsUrl }",
          { index: ${index} },
          function (data) {
            var contentRequests = data.contentRequests;
            webproxyGatewayHandleRequest($, data, 0, "${n}form");
          },
          "json"
      );
    });
  </script>
</head>

<body class="documentBody">
</body>
</html>
