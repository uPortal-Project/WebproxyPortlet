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

<div id="${n}" class="portlet">
    <div class="portlet-content" data-role="content">
        <ul data-role="listview">
            <c:forEach items="${ entries }" var="entry">
                <li class="entry">                    
                    <a href="javascript:;" target="_blank">
                        <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>
                        <h3>${ entry.name }</h3>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>

<script type="text/javascript">
    up.jQuery(function () {
        
        var $ = up.jQuery;
        
        $(document).ready(function () {
            
            var finalRequest = function (contentRequest) {
                if (contentRequest.form) {
                    var form = $(document.createElement("form"))
                        .attr("action", contentRequest.proxiedLocation)
                        .attr("method", contentRequest.method);
                    
                    $.each(contentRequest.parameters, function (key, values) {
                        $(values).each(function (idx, value) {
                            form.append($(document.createElement("input")).attr("name", key).attr("value", value));
                        });
                    });
                    
                    form.submit();
                } else {
                    window.location = contentRequest.proxiedLocation;
                }
            };
            
            $("#${n} .entry a").each(function (idx, link) {
                $(link).click(function () {
                    $.get(
                        "${ requestsUrl }", 
                        { index: idx }, 
                        function (data) { 
                            var contentRequests = data.contentRequests;
                            // TODO: handle multiple requests
                            finalRequest(contentRequests[0]);
                        }, 
                        "json"
                    );
                });
            });
        });
    });
</script>
