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

<div id="${n}">
    <c:forEach items="${ entries }" var="entry">
        <p class="entry">
            
            <a href="javascript:;" target="_blank">
                <img src="${entry.iconUrl}" style="vertical-align: middle; text-decoration: none; padding-right: 10px;"/>${ entry.name }
            </a>
        </p>
    </c:forEach>
</div>

<script type="text/javascript">
    up.jQuery(function () {
    	
    	var $ = up.jQuery;
    	
    	$(document).ready(function () {
    		
            var handleRequest = function (contentRequests, index) {
            	var contentRequest = contentRequests[index];
            	// final request
            	if (index == contentRequests.length-1) {
                    if (contentRequest.form) {
                        var form = $(document.createElement("form"))
                            .attr("action", contentRequest.proxiedLocation)
                            .attr("method", contentRequest.method);
                        
                        $.each(contentRequest.parameters, function (key, values) {
                            $(values).each(function (idx, value) {
                                form.append($(document.createElement("input")).attr("name", key).attr("value", value));
                            });
                        });
                        console.log(form);

                        form.submit();
                    } else {
                        window.location = contentRequest.proxiedLocation;
                    }
            	}
            	
            	else if (contentRequest.form) {
                	// TODO
                } else {
                    var iframe = $(document.createElement("iframe"));
                    iframe.load(function () { 
                        handleRequest(contentRequests, index+1);
                    });
                    iframe.attr("src", contentRequest.proxiedLocation);
                }
            };
            
    		$("#${n} .entry a").each(function (idx, link) {
    			$(link).click(function () {
    				$.get(
						"${ requestsUrl }", 
						{ index: idx }, 
						function (data) { 
							var contentRequests = data.contentRequests;
							handleRequest(contentRequests, 0);
						}, 
						"json"
    			    );
    			});
    		});
    	});
    });
</script>
